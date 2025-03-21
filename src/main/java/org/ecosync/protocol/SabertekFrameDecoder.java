/*
 * Copyright 2018 Manah Shivaya (shivaya@ecosync.org)
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
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.ecosync.BaseFrameDecoder;

public class SabertekFrameDecoder extends BaseFrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        int beginIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0x02);
        if (beginIndex >= 0) {
            int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0x03);
            if (endIndex >= 0) {
                buf.readerIndex(beginIndex + 1);
                ByteBuf frame = buf.readRetainedSlice(endIndex - beginIndex - 1);
                buf.readerIndex(endIndex + 1);
                buf.skipBytes(2); // end line
                return frame;
            }
        }

        return null;
    }

}
