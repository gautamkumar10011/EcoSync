/*
 * Copyright 2016 Nyash (nyashh@gmail.com)
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

public class JpKorjarFrameDecoder extends BaseFrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        if (buf.readableBytes() < 80) {
            return null;
        }

        int spaceIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ' ');
        if (spaceIndex == -1) {
            return null;
        }

        int endIndex = buf.indexOf(spaceIndex, buf.writerIndex(), (byte) ',');
        if (endIndex == -1) {
            return null;
        }

        return buf.readRetainedSlice(endIndex + 1);
    }

}
