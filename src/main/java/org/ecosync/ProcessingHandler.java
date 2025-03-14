/*
 * Copyright 2024 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ecosync;

import com.google.inject.Injector;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.ecosync.config.Config;
import org.ecosync.database.BufferingManager;
import org.ecosync.database.NotificationManager;
import org.ecosync.handler.BasePositionHandler;
import org.ecosync.handler.ComputedAttributesHandler;
import org.ecosync.handler.CopyAttributesHandler;
import org.ecosync.handler.DatabaseHandler;
import org.ecosync.handler.DistanceHandler;
import org.ecosync.handler.DriverHandler;
import org.ecosync.handler.EngineHoursHandler;
import org.ecosync.handler.FilterHandler;
import org.ecosync.handler.GeocoderHandler;
import org.ecosync.handler.GeofenceHandler;
import org.ecosync.handler.GeolocationHandler;
import org.ecosync.handler.HemisphereHandler;
import org.ecosync.handler.MotionHandler;
import org.ecosync.handler.OutdatedHandler;
import org.ecosync.handler.PositionForwardingHandler;
import org.ecosync.handler.PostProcessHandler;
import org.ecosync.handler.SpeedLimitHandler;
import org.ecosync.handler.TimeHandler;
import org.ecosync.handler.events.AlarmEventHandler;
import org.ecosync.handler.events.BaseEventHandler;
import org.ecosync.handler.events.BehaviorEventHandler;
import org.ecosync.handler.events.CommandResultEventHandler;
import org.ecosync.handler.events.DriverEventHandler;
import org.ecosync.handler.events.FuelEventHandler;
import org.ecosync.handler.events.GeofenceEventHandler;
import org.ecosync.handler.events.IgnitionEventHandler;
import org.ecosync.handler.events.MaintenanceEventHandler;
import org.ecosync.handler.events.MediaEventHandler;
import org.ecosync.handler.events.MotionEventHandler;
import org.ecosync.handler.events.OverspeedEventHandler;
import org.ecosync.handler.network.AcknowledgementHandler;
import org.ecosync.helper.PositionLogger;
import org.ecosync.model.Position;
import org.ecosync.session.cache.CacheManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Stream;

@Singleton
@ChannelHandler.Sharable
public class ProcessingHandler extends ChannelInboundHandlerAdapter implements BufferingManager.Callback {

    private final CacheManager cacheManager;
    private final NotificationManager notificationManager;
    private final PositionLogger positionLogger;
    private final BufferingManager bufferingManager;
    private final List<BasePositionHandler> positionHandlers;
    private final List<BaseEventHandler> eventHandlers;
    private final PostProcessHandler postProcessHandler;

    private final Map<Long, Queue<Position>> queues = new HashMap<>();

    private synchronized Queue<Position> getQueue(long deviceId) {
        return queues.computeIfAbsent(deviceId, k -> new LinkedList<>());
    }

    @Inject
    public ProcessingHandler(
            Injector injector, Config config,
            CacheManager cacheManager, NotificationManager notificationManager, PositionLogger positionLogger) {
        this.cacheManager = cacheManager;
        this.notificationManager = notificationManager;
        this.positionLogger = positionLogger;
        bufferingManager = new BufferingManager(config, this);

        positionHandlers = Stream.of(
                ComputedAttributesHandler.Early.class,
                OutdatedHandler.class,
                TimeHandler.class,
                GeolocationHandler.class,
                HemisphereHandler.class,
                DistanceHandler.class,
                FilterHandler.class,
                GeofenceHandler.class,
                GeocoderHandler.class,
                SpeedLimitHandler.class,
                MotionHandler.class,
                ComputedAttributesHandler.Late.class,
                EngineHoursHandler.class,
                DriverHandler.class,
                CopyAttributesHandler.class,
                PositionForwardingHandler.class,
                DatabaseHandler.class)
                .map((clazz) -> (BasePositionHandler) injector.getInstance(clazz))
                .filter(Objects::nonNull)
                .toList();

        eventHandlers = Stream.of(
                MediaEventHandler.class,
                CommandResultEventHandler.class,
                OverspeedEventHandler.class,
                BehaviorEventHandler.class,
                FuelEventHandler.class,
                MotionEventHandler.class,
                GeofenceEventHandler.class,
                AlarmEventHandler.class,
                IgnitionEventHandler.class,
                MaintenanceEventHandler.class,
                DriverEventHandler.class)
                .map((clazz) -> (BaseEventHandler) injector.getInstance(clazz))
                .filter(Objects::nonNull)
                .toList();

        postProcessHandler = injector.getInstance(PostProcessHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Position position) {
            bufferingManager.accept(ctx, position);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void onReleased(ChannelHandlerContext context, Position position) {
        Queue<Position> queue = getQueue(position.getDeviceId());
        boolean queued;
        synchronized (queue) {
            queued = !queue.isEmpty();
            queue.offer(position);
        }
        if (!queued) {
            try {
                cacheManager.addDevice(position.getDeviceId(), position.getDeviceId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processPositionHandlers(context, position);
        }
    }

    private void processPositionHandlers(ChannelHandlerContext ctx, Position position) {
        var iterator = positionHandlers.iterator();
        iterator.next().handlePosition(position, new BasePositionHandler.Callback() {
            @Override
            public void processed(boolean filtered) {
                if (!filtered) {
                    if (iterator.hasNext()) {
                        iterator.next().handlePosition(position, this);
                    } else {
                        processEventHandlers(ctx, position);
                    }
                } else {
                    finishedProcessing(ctx, position, true);
                }
            }
        });
    }

    private void processEventHandlers(ChannelHandlerContext ctx, Position position) {
        eventHandlers.forEach(handler -> handler.analyzePosition(
                position, (event) -> notificationManager.updateEvents(Map.of(event, position))));
        finishedProcessing(ctx, position, false);
    }

    private void finishedProcessing(ChannelHandlerContext ctx, Position position, boolean filtered) {
        if (!filtered) {
            postProcessHandler.handlePosition(position, ignore -> {
                positionLogger.log(ctx, position);
                ctx.writeAndFlush(new AcknowledgementHandler.EventHandled(position));
                processNextPosition(ctx, position.getDeviceId());
            });
        } else {
            ctx.writeAndFlush(new AcknowledgementHandler.EventHandled(position));
            processNextPosition(ctx, position.getDeviceId());
        }
    }

    private void processNextPosition(ChannelHandlerContext ctx, long deviceId) {
        Queue<Position> queue = getQueue(deviceId);
        Position nextPosition;
        synchronized (queue) {
            queue.poll(); // remove current position
            nextPosition = queue.peek();
        }
        if (nextPosition != null) {
            processPositionHandlers(ctx, nextPosition);
        } else {
            cacheManager.removeDevice(deviceId, deviceId);
        }
    }

}
