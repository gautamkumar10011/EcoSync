/*
 * Copyright 2017 - 2024 Manah Shivaya (shivaya@ecosync.org)
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
import org.ecosync.helper.model.PositionUtil;
import org.ecosync.model.Event;
import org.ecosync.model.Position;
import org.ecosync.session.cache.CacheManager;

public class DriverEventHandler extends BaseEventHandler {

    private final CacheManager cacheManager;

    @Inject
    public DriverEventHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        if (!PositionUtil.isLatest(cacheManager, position)) {
            return;
        }
        String driverUniqueId = position.getString(Position.KEY_DRIVER_UNIQUE_ID);
        if (driverUniqueId != null) {
            String oldDriverUniqueId = null;
            Position lastPosition = cacheManager.getPosition(position.getDeviceId());
            if (lastPosition != null) {
                oldDriverUniqueId = lastPosition.getString(Position.KEY_DRIVER_UNIQUE_ID);
            }
            if (!driverUniqueId.equals(oldDriverUniqueId)) {
                Event event = new Event(Event.TYPE_DRIVER_CHANGED, position);
                event.set(Position.KEY_DRIVER_UNIQUE_ID, driverUniqueId);
                callback.eventDetected(event);
            }
        }
    }

}
