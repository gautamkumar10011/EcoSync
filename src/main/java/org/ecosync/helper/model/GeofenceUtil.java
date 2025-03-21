/*
 * Copyright 2022 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.helper.model;

import org.ecosync.config.Config;
import org.ecosync.model.Geofence;
import org.ecosync.model.Position;
import org.ecosync.session.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

public final class GeofenceUtil {

    private GeofenceUtil() {
    }

    public static List<Long> getCurrentGeofences(Config config, CacheManager cacheManager, Position position) {
        List<Long> result = new ArrayList<>();
        for (Geofence geofence : cacheManager.getDeviceObjects(position.getDeviceId(), Geofence.class)) {
            if (geofence.getGeometry().containsPoint(
                    config, geofence, position.getLatitude(), position.getLongitude())) {
                result.add(geofence.getId());
            }
        }
        return result;
    }

}
