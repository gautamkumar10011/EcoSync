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

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.ecosync.BaseProtocol;
import org.ecosync.PipelineBuilder;
import org.ecosync.TrackerServer;
import org.ecosync.config.Config;
import org.ecosync.model.Command;

import java.nio.ByteOrder;
import jakarta.inject.Inject;

public class CastelProtocol extends BaseProtocol {

    @Inject
    public CastelProtocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME);
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024, 2, 2, -4, 0, true));
                pipeline.addLast(new CastelProtocolEncoder(CastelProtocol.this));
                pipeline.addLast(new CastelProtocolDecoder(CastelProtocol.this));
            }
        });
        addServer(new TrackerServer(config, getName(), true) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new CastelProtocolEncoder(CastelProtocol.this));
                pipeline.addLast(new CastelProtocolDecoder(CastelProtocol.this));
            }
        });
    }

}
