/*
 * Copyright 2016 - 2024 Manah Shivaya (shivaya@ecosync.org)
 * Copyright 2016 Shree Rama (rama@ecosync.org)
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
import org.ecosync.helper.model.PositionUtil;
import org.ecosync.model.Device;
import org.ecosync.model.Event;
import org.ecosync.model.Position;
import org.ecosync.session.cache.CacheManager;

public class IgnitionEventHandler extends BaseEventHandler {

    private final CacheManager cacheManager;

    @Inject
    public IgnitionEventHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        Device device = cacheManager.getObject(Device.class, position.getDeviceId());
        if (device == null || !PositionUtil.isLatest(cacheManager, position)) {
            return;
        }

        if (position.hasAttribute(Position.KEY_IGNITION)) {
            boolean ignition = position.getBoolean(Position.KEY_IGNITION);

            Position lastPosition = cacheManager.getPosition(position.getDeviceId());
            if (lastPosition != null && lastPosition.hasAttribute(Position.KEY_IGNITION)) {
                boolean oldIgnition = lastPosition.getBoolean(Position.KEY_IGNITION);

                if (ignition && !oldIgnition) {
                    callback.eventDetected(new Event(Event.TYPE_IGNITION_ON, position));
                } else if (!ignition && oldIgnition) {
                    callback.eventDetected(new Event(Event.TYPE_IGNITION_OFF, position));
                }
            }
        }
    }

}
