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

import org.ecosync.BaseProtocol;
import org.ecosync.PipelineBuilder;
import org.ecosync.TrackerServer;
import org.ecosync.config.Config;
import org.ecosync.model.Command;

import jakarta.inject.Inject;

public class MeiligaoProtocol extends BaseProtocol {

    @Inject
    public MeiligaoProtocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_POSITION_SINGLE,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_OUTPUT_CONTROL,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_ALARM_GEOFENCE,
                Command.TYPE_SET_TIMEZONE,
                Command.TYPE_REQUEST_PHOTO,
                Command.TYPE_REBOOT_DEVICE);
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new MeiligaoFrameDecoder());
                pipeline.addLast(new MeiligaoProtocolEncoder(MeiligaoProtocol.this));
                pipeline.addLast(new MeiligaoProtocolDecoder(MeiligaoProtocol.this));
            }
        });
        addServer(new TrackerServer(config, getName(), true) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new MeiligaoProtocolEncoder(MeiligaoProtocol.this));
                pipeline.addLast(new MeiligaoProtocolDecoder(MeiligaoProtocol.this));
            }
        });
    }

}
