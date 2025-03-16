/*
 * Copyright 2017 - 2022 Manah Shivaya (shivaya@ecosync.org)
 * Copyright 2017 - 2018 Shree Rama (rama@ecosync.org)
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
package org.ecosync.api.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.ecosync.api.ExtendedObjectResource;
import org.ecosync.model.Attribute;
import org.ecosync.model.Device;
import org.ecosync.model.Position;
import org.ecosync.handler.ComputedAttributesHandler;
import org.ecosync.session.cache.CacheManager;
import org.ecosync.storage.query.Columns;
import org.ecosync.storage.query.Condition;
import org.ecosync.storage.query.Request;

@Path("attributes/computed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttributeResource extends ExtendedObjectResource<Attribute> {

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ComputedAttributesHandler.Late computedAttributesHandler;

    public AttributeResource() {
        super(Attribute.class, "description");
    }

    @POST
    @Path("test")
    public Response test(@QueryParam("deviceId") long deviceId, Attribute entity) throws Exception {
        permissionsService.checkAdmin(getUserId());
        permissionsService.checkPermission(Device.class, getUserId(), deviceId);

        Position position = storage.getObject(Position.class, new Request(
                new Columns.All(),
                new Condition.LatestPositions(deviceId)));

        var key = new Object();
        try {
            cacheManager.addDevice(position.getDeviceId(), key);
            Object result = computedAttributesHandler.computeAttribute(entity, position);
            if (result != null) {
                return switch (entity.getType()) {
                    case "number", "boolean" -> Response.ok(result).build();
                    default -> Response.ok(result.toString()).build();
                };
            } else {
                return Response.noContent().build();
            }
        } finally {
            cacheManager.removeDevice(position.getDeviceId(), key);
        }
    }

    @POST
    public Response add(Attribute entity) throws Exception {
        permissionsService.checkAdmin(getUserId());
        return super.add(entity);
    }

    @Path("{id}")
    @PUT
    public Response update(Attribute entity) throws Exception {
        permissionsService.checkAdmin(getUserId());
        return super.update(entity);
    }

    @Path("{id}")
    @DELETE
    public Response remove(@PathParam("id") long id) throws Exception {
        permissionsService.checkAdmin(getUserId());
        return super.remove(id);
    }

}
