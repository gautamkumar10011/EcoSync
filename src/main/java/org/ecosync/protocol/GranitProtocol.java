/*
 * Copyright 2016 - 2019 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.protocol;

import org.ecosync.BaseProtocol;
import org.ecosync.PipelineBuilder;
import org.ecosync.TrackerServer;
import org.ecosync.config.Config;
import org.ecosync.model.Command;

import jakarta.inject.Inject;

public class GranitProtocol extends BaseProtocol {

    @Inject
    public GranitProtocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_IDENTIFICATION,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_POSITION_SINGLE);
        setTextCommandEncoder(new GranitProtocolSmsEncoder(this));
        setSupportedTextCommands(
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_POSITION_PERIODIC);
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new GranitFrameDecoder());
                pipeline.addLast(new GranitProtocolEncoder(GranitProtocol.this));
                pipeline.addLast(new GranitProtocolDecoder(GranitProtocol.this));
            }
        });
    }

}
