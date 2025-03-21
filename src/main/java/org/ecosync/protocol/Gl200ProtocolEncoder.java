/*
 * Copyright 2016 - 2019 Manah Shivaya (shivaya@ecosync.org)
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

public class Gl200ProtocolEncoder extends StringProtocolEncoder {

    public Gl200ProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object encodeCommand(Command command) {

        initDevicePassword(command, "");

        return switch (command.getType()) {
            case Command.TYPE_POSITION_SINGLE -> formatCommand(
                    command, "AT+GTRTO=%s,1,,,,,,FFFF$", Command.KEY_DEVICE_PASSWORD);
            case Command.TYPE_ENGINE_STOP -> formatCommand(
                    command, "AT+GTOUT=%s,1,,,0,0,0,0,0,0,0,,,,,,,FFFF$", Command.KEY_DEVICE_PASSWORD);
            case Command.TYPE_ENGINE_RESUME -> formatCommand(
                    command, "AT+GTOUT=%s,0,,,0,0,0,0,0,0,0,,,,,,,FFFF$", Command.KEY_DEVICE_PASSWORD);
            case Command.TYPE_IDENTIFICATION -> formatCommand(
                    command, "AT+GTRTO=%s,8,,,,,,FFFF$", Command.KEY_DEVICE_PASSWORD);
            case Command.TYPE_REBOOT_DEVICE -> formatCommand(
                    command, "AT+GTRTO=%s,3,,,,,,FFFF$", Command.KEY_DEVICE_PASSWORD);
            default -> null;
        };
    }

}
