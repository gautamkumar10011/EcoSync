/*
 * Copyright 2023 Manah Shivaya (shivaya@ecosync.org)
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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.ecosync.BaseProtocolEncoder;
import org.ecosync.Protocol;
import org.ecosync.helper.Checksum;
import org.ecosync.model.Command;

import java.nio.charset.StandardCharsets;

public class Minifinder2ProtocolEncoder extends BaseProtocolEncoder {

    public Minifinder2ProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private ByteBuf encodeContent(ByteBuf content) {

        ByteBuf buf = Unpooled.buffer();

        buf.writeByte(0xAB); // header
        buf.writeByte(0x00); // properties
        buf.writeShortLE(content.readableBytes());
        buf.writeShortLE(Checksum.crc16(Checksum.CRC16_XMODEM, content.nioBuffer()));
        buf.writeShortLE(1); // index
        buf.writeBytes(content);

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        if (command.getType().equals(Command.TYPE_CONFIGURATION)) {
            ByteBuf content = Unpooled.buffer();
            content.writeByte(Minifinder2ProtocolDecoder.MSG_CONFIGURATION);
            content.writeByte(1); // length
            content.writeByte(0xF0); // type
        }

        if ("Nano".equalsIgnoreCase(getDeviceModel(command.getDeviceId()))) {
            ByteBuf content = Unpooled.buffer();
            if (command.getType().equals(Command.TYPE_FIRMWARE_UPDATE)) {
                String url = command.getString(Command.KEY_DATA);
                content.writeByte(Minifinder2ProtocolDecoder.MSG_SYSTEM_CONTROL);
                content.writeByte(1 + url.length());
                content.writeByte(0x30); // type
                content.writeCharSequence(url, StandardCharsets.US_ASCII);
                return encodeContent(content);
            }
        }

        return null;
    }

}
