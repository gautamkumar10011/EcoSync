/*
 * Copyright 2020 Manah Shivaya (shivaya@ecosync.org)
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

import org.ecosync.Protocol;
import org.ecosync.StringProtocolEncoder;
import org.ecosync.model.Command;

public class PortmanProtocolEncoder extends StringProtocolEncoder {

    public PortmanProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object encodeCommand(Command command) {

        return switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP -> formatCommand(command, "&&%s,XA5\r\n", Command.KEY_UNIQUE_ID);
            case Command.TYPE_ENGINE_RESUME -> formatCommand(command, "&&%s,XA6\r\n", Command.KEY_UNIQUE_ID);
            default -> null;
        };
    }

}
