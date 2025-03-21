/*
 * Copyright 2015 - 2019 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.protocol;

import io.netty.handler.codec.string.StringEncoder;
import org.ecosync.BaseProtocol;
import org.ecosync.PipelineBuilder;
import org.ecosync.TrackerServer;
import org.ecosync.config.Config;
import org.ecosync.model.Command;

import jakarta.inject.Inject;

public class Pt502Protocol extends BaseProtocol {

    @Inject
    public Pt502Protocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_SET_TIMEZONE,
                Command.TYPE_ALARM_SPEED,
                Command.TYPE_OUTPUT_CONTROL,
                Command.TYPE_REQUEST_PHOTO);
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new Pt502FrameDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new Pt502ProtocolEncoder(Pt502Protocol.this));
                pipeline.addLast(new Pt502ProtocolDecoder(Pt502Protocol.this));
            }
        });
    }

}
