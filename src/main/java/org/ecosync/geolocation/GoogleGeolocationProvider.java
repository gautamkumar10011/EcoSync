/*
 * Copyright 2016 - 2022 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.geolocation;

import jakarta.ws.rs.client.Client;

public class GoogleGeolocationProvider extends UniversalGeolocationProvider {

    private static final String URL = "https://www.googleapis.com/geolocation/v1/geolocate";

    public GoogleGeolocationProvider(Client client, String key) {
        super(client, URL, key);
    }

}
