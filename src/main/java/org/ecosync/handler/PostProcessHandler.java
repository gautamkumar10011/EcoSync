/*
 * Copyright 2024 Manah Shivaya (shivaya@ecosync.org)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ecosync.helper.model.PositionUtil;
import org.ecosync.model.Device;
import org.ecosync.model.Position;
import org.ecosync.session.ConnectionManager;
import org.ecosync.session.cache.CacheManager;
import org.ecosync.storage.Storage;
import org.ecosync.storage.StorageException;
import org.ecosync.storage.query.Columns;
import org.ecosync.storage.query.Condition;
import org.ecosync.storage.query.Request;

public class PostProcessHandler extends BasePositionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostProcessHandler.class);

    private final CacheManager cacheManager;
    private final Storage storage;
    private final ConnectionManager connectionManager;

    @Inject
    public PostProcessHandler(CacheManager cacheManager, Storage storage, ConnectionManager connectionManager) {
        this.cacheManager = cacheManager;
        this.storage = storage;
        this.connectionManager = connectionManager;
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        try {
            if (PositionUtil.isLatest(cacheManager, position)) {
                Device updatedDevice = new Device();
                updatedDevice.setId(position.getDeviceId());
                updatedDevice.setPositionId(position.getId());
                storage.updateObject(updatedDevice, new Request(
                        new Columns.Include("positionId"),
                        new Condition.Equals("id", updatedDevice.getId())));

                cacheManager.updatePosition(position);
                connectionManager.updatePosition(true, position);
            }
        } catch (StorageException error) {
            LOGGER.warn("Failed to update device", error);
        }
        callback.processed(false);
    }

}
