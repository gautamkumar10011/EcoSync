/*
 * Copyright 2016 - 2022 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.geofence;

import java.text.ParseException;
import java.util.ArrayList;

import org.ecosync.config.Config;
import org.ecosync.config.Keys;
import org.ecosync.helper.DistanceCalculator;
import org.ecosync.model.Geofence;

public class GeofencePolyline extends GeofenceGeometry {

    private ArrayList<Coordinate> coordinates;

    public GeofencePolyline() {
    }

    public GeofencePolyline(String wkt) throws ParseException {
        fromWkt(wkt);
    }

    @Override
    public boolean containsPoint(Config config, Geofence geofence, double latitude, double longitude) {
        double distance = geofence.getDouble("polylineDistance");
        if (distance == 0) {
            distance = config.getDouble(Keys.GEOFENCE_POLYLINE_DISTANCE);
        }
        for (int i = 1; i < coordinates.size(); i++) {
            if (DistanceCalculator.distanceToLine(
                    latitude, longitude, coordinates.get(i - 1).getLat(), coordinates.get(i - 1).getLon(),
                    coordinates.get(i).getLat(), coordinates.get(i).getLon()) <= distance) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double calculateArea() {
        return 0;
    }

    @Override
    public String toWkt() {
        StringBuilder buf = new StringBuilder();
        buf.append("LINESTRING (");
        for (Coordinate coordinate : coordinates) {
            buf.append(coordinate.getLat());
            buf.append(" ");
            buf.append(coordinate.getLon());
            buf.append(", ");
        }
        return buf.substring(0, buf.length() - 2) + ")";
    }

    @Override
    public void fromWkt(String wkt) throws ParseException {
        if (coordinates == null) {
            coordinates = new ArrayList<>();
        } else {
            coordinates.clear();
        }

        if (!wkt.startsWith("LINESTRING")) {
            throw new ParseException("Mismatch geometry type", 0);
        }
        String content = wkt.substring(wkt.indexOf("(") + 1, wkt.indexOf(")"));
        if (content.isEmpty()) {
            throw new ParseException("No content", 0);
        }
        String[] commaTokens = content.split(",");
        if (commaTokens.length < 2) {
            throw new ParseException("Not valid content", 0);
        }

        for (String commaToken : commaTokens) {
            String[] tokens = commaToken.trim().split("\\s");
            if (tokens.length != 2) {
                throw new ParseException("Here must be two coordinates: " + commaToken, 0);
            }
            Coordinate coordinate = new Coordinate();
            try {
                coordinate.setLat(Double.parseDouble(tokens[0]));
            } catch (NumberFormatException e) {
                throw new ParseException(tokens[0] + " is not a double", 0);
            }
            try {
                coordinate.setLon(Double.parseDouble(tokens[1]));
            } catch (NumberFormatException e) {
                throw new ParseException(tokens[1] + " is not a double", 0);
            }
            coordinates.add(coordinate);
        }

    }

}
