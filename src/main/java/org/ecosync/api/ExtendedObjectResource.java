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
package org.ecosync.api;

import org.ecosync.model.BaseModel;
import org.ecosync.model.Device;
import org.ecosync.model.Group;
import org.ecosync.model.User;
import org.ecosync.storage.StorageException;
import org.ecosync.storage.query.Columns;
import org.ecosync.storage.query.Condition;
import org.ecosync.storage.query.Order;
import org.ecosync.storage.query.Request;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import java.util.Collection;
import java.util.LinkedList;

public class ExtendedObjectResource<T extends BaseModel> extends BaseObjectResource<T> {

    private  final String sortField;

    public ExtendedObjectResource(Class<T> baseClass, String sortField) {
        super(baseClass);
        this.sortField = sortField;
    }

    @GET
    public Collection<T> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId,
            @QueryParam("groupId") long groupId, @QueryParam("deviceId") long deviceId) throws StorageException {

        var conditions = new LinkedList<Condition>();

        if (all) {
            if (permissionsService.notAdmin(getUserId())) {
                conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
            }
        } else {
            if (userId == 0) {
                conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
            } else {
                permissionsService.checkUser(getUserId(), userId);
                conditions.add(new Condition.Permission(User.class, userId, baseClass).excludeGroups());
            }
        }

        if (groupId > 0) {
            permissionsService.checkPermission(Group.class, getUserId(), groupId);
            conditions.add(new Condition.Permission(Group.class, groupId, baseClass).excludeGroups());
        }
        if (deviceId > 0) {
            permissionsService.checkPermission(Device.class, getUserId(), deviceId);
            conditions.add(new Condition.Permission(Device.class, deviceId, baseClass).excludeGroups());
        }

        return storage.getObjects(baseClass, new Request(
                new Columns.All(), Condition.merge(conditions), sortField != null ? new Order(sortField) : null));
    }

}
