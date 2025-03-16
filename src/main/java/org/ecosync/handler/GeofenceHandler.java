/*
 * Copyright 2023 - 2024 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.handler;

import jakarta.inject.Inject;
import org.ecosync.config.Config;
import org.ecosync.helper.model.GeofenceUtil;
import org.ecosync.model.Position;
import org.ecosync.session.cache.CacheManager;

import java.util.List;

public class GeofenceHandler extends BasePositionHandler {

    private final Config config;
    private final CacheManager cacheManager;

    @Inject
    public GeofenceHandler(Config config, CacheManager cacheManager) {
        this.config = config;
        this.cacheManager = cacheManager;
    }

    @Override
    public void onPosition(Position position, Callback callback) {

        List<Long> geofenceIds = GeofenceUtil.getCurrentGeofences(config, cacheManager, position);
        if (!geofenceIds.isEmpty()) {
            position.setGeofenceIds(geofenceIds);
        }
        callback.processed(false);
    }

}
