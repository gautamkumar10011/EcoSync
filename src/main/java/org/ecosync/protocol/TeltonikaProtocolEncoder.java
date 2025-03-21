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

import org.ecosync.BaseProtocolEncoder;
import org.ecosync.helper.Checksum;
import org.ecosync.helper.DataConverter;
import org.ecosync.model.Command;
import org.ecosync.Protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class TeltonikaProtocolEncoder extends BaseProtocolEncoder {

    public TeltonikaProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private ByteBuf encodeContent(byte[] content) {

        ByteBuf buf = Unpooled.buffer();

        buf.writeInt(0);
        buf.writeInt(content.length + 8);
        buf.writeByte(TeltonikaProtocolDecoder.CODEC_12);
        buf.writeByte(1); // quantity
        buf.writeByte(5); // type
        buf.writeInt(content.length);
        buf.writeBytes(content);
        buf.writeByte(1); // quantity
        buf.writeInt(Checksum.crc16(Checksum.CRC16_IBM, buf.nioBuffer(8, buf.writerIndex() - 8)));

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        if (command.getType().equals(Command.TYPE_CUSTOM)) {
            String data = command.getString(Command.KEY_DATA);
            if (data.matches("(\\p{XDigit}{2})+")) {
                return encodeContent(DataConverter.parseHex(data));
            } else {
                return encodeContent((data + "\r\n").getBytes(StandardCharsets.US_ASCII));
            }
        } else {
            return null;
        }
    }

}
