/*
 * Copyright 2019 Manah Shivaya (shivaya@ecosync.org)
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

import org.ecosync.StringProtocolEncoder;
import org.ecosync.model.Command;
import org.ecosync.Protocol;

public class ItsProtocolEncoder extends StringProtocolEncoder {

    public ItsProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object encodeCommand(Command command) {
        return switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP -> "@SET#RLP,OP1,";
            case Command.TYPE_ENGINE_RESUME -> "@CLR#RLP,OP1,";
            default -> null;
        };
    }

}
