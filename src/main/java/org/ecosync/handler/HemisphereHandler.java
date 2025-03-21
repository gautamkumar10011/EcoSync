/*
 * Copyright 2016 - 2024 Manah Shivaya (shivaya@ecosync.org)
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
import org.ecosync.config.Keys;
import org.ecosync.model.Position;

public class HemisphereHandler extends BasePositionHandler {

    private int latitudeFactor;
    private int longitudeFactor;

    @Inject
    public HemisphereHandler(Config config) {
        String latitudeHemisphere = config.getString(Keys.LOCATION_LATITUDE_HEMISPHERE);
        if (latitudeHemisphere != null) {
            if (latitudeHemisphere.equalsIgnoreCase("N")) {
                latitudeFactor = 1;
            } else if (latitudeHemisphere.equalsIgnoreCase("S")) {
                latitudeFactor = -1;
            }
        }
        String longitudeHemisphere = config.getString(Keys.LOCATION_LONGITUDE_HEMISPHERE);
        if (longitudeHemisphere != null) {
            if (longitudeHemisphere.equalsIgnoreCase("E")) {
                longitudeFactor = 1;
            } else if (longitudeHemisphere.equalsIgnoreCase("W")) {
                longitudeFactor = -1;
            }
        }
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        if (latitudeFactor != 0) {
            position.setLatitude(Math.abs(position.getLatitude()) * latitudeFactor);
        }
        if (longitudeFactor != 0) {
            position.setLongitude(Math.abs(position.getLongitude()) * longitudeFactor);
        }
        callback.processed(false);
    }

}
