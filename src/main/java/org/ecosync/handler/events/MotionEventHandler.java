/*
 * Copyright 2016 - 2024 Manah Shivaya (shivaya@ecosync.org)
 * Copyright 2017 Shree Rama (rama@ecosync.org)
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
package org.ecosync.handler.events;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ecosync.config.Keys;
import org.ecosync.helper.model.AttributeUtil;
import org.ecosync.helper.model.PositionUtil;
import org.ecosync.model.Device;
import org.ecosync.model.Position;
import org.ecosync.reports.common.TripsConfig;
import org.ecosync.session.cache.CacheManager;
import org.ecosync.session.state.MotionProcessor;
import org.ecosync.session.state.MotionState;
import org.ecosync.storage.Storage;
import org.ecosync.storage.StorageException;
import org.ecosync.storage.query.Columns;
import org.ecosync.storage.query.Condition;
import org.ecosync.storage.query.Request;

public class MotionEventHandler extends BaseEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotionEventHandler.class);

    private final CacheManager cacheManager;
    private final Storage storage;

    @Inject
    public MotionEventHandler(CacheManager cacheManager, Storage storage) {
        this.cacheManager = cacheManager;
        this.storage = storage;
    }

    @Override
    public void onPosition(Position position, Callback callback) {

        long deviceId = position.getDeviceId();
        Device device = cacheManager.getObject(Device.class, deviceId);
        if (device == null || !PositionUtil.isLatest(cacheManager, position)) {
            return;
        }
        boolean processInvalid = AttributeUtil.lookup(
                cacheManager, Keys.EVENT_MOTION_PROCESS_INVALID_POSITIONS, deviceId);
        if (!processInvalid && !position.getValid()) {
            return;
        }

        TripsConfig tripsConfig = new TripsConfig(new AttributeUtil.CacheProvider(cacheManager, deviceId));
        MotionState state = MotionState.fromDevice(device);
        MotionProcessor.updateState(state, position, position.getBoolean(Position.KEY_MOTION), tripsConfig);
        if (state.isChanged()) {
            state.toDevice(device);
            try {
                storage.updateObject(device, new Request(
                        new Columns.Include("motionStreak", "motionState", "motionTime", "motionDistance"),
                        new Condition.Equals("id", device.getId())));
            } catch (StorageException e) {
                LOGGER.warn("Update device motion error", e);
            }
        }
        if (state.getEvent() != null) {
            callback.eventDetected(state.getEvent());
        }
    }

}
