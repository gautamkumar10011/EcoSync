/*
 * Copyright 2022 - 2024 Manah Shivaya (shivaya@ecosync.org)
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
import org.ecosync.model.Event;
import org.ecosync.model.Position;

import java.util.stream.Stream;

public class MediaEventHandler extends BaseEventHandler {

    @Inject
    public MediaEventHandler() {
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        Stream.of(Position.KEY_IMAGE, Position.KEY_VIDEO, Position.KEY_AUDIO)
                .filter(position::hasAttribute)
                .map(type -> {
                    Event event = new Event(Event.TYPE_MEDIA, position);
                    event.set("media", type);
                    event.set("file", position.getString(type));
                    return event;
                })
                .forEach(callback::eventDetected);
    }

}
