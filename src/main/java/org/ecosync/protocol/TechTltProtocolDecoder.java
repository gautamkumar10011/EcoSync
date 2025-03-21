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

import io.netty.channel.Channel;
import org.ecosync.BaseProtocolDecoder;
import org.ecosync.session.DeviceSession;
import org.ecosync.Protocol;
import org.ecosync.helper.Parser;
import org.ecosync.helper.PatternBuilder;
import org.ecosync.helper.UnitsConverter;
import org.ecosync.model.CellTower;
import org.ecosync.model.Network;
import org.ecosync.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class TechTltProtocolDecoder extends BaseProtocolDecoder {

    public TechTltProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN_STATUS = new PatternBuilder()
            .number("(d+),")                     // id
            .text("INFOGPRS,")
            .number("V Bat=(d+.d),")             // battery
            .number("TEMP=(d+),")                // temperature
            .expression("[^,]*,")
            .number("(d+)")                      // rssi
            .compile();

    private static final Pattern PATTERN_POSITION = new PatternBuilder()
            .number("(d+)")                      // id
            .text("*POS=Y,")
            .number("(dd):(dd):(dd),")           // time
            .number("(dd)/(dd)/(dd),")           // date
            .number("(dd)(dd.d+)")               // latitude
            .expression("([NS]),")
            .number("(ddd)(dd.d+)")              // longitude
            .expression("([EW]),")
            .number("(d+.d+),")                  // speed
            .number("(d+.d+),")                  // course
            .number("(d+.d+),")                  // altitude
            .number("(d+),")                     // satellites
            .number("(d+),")                     // lac
            .number("(d+)")                      // cid
            .compile();

    private Position decodeStatus(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_STATUS, sentence);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        getLastLocation(position, null);

        position.set(Position.KEY_BATTERY, parser.nextDouble());
        position.set(Position.KEY_DEVICE_TEMP, parser.nextInt());
        position.set(Position.KEY_RSSI, parser.nextInt());

        return position;
    }

    private Position decodeLocation(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_POSITION, sentence);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setValid(true);
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.HMS_DMY));
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));
        position.setCourse(parser.nextDouble());
        position.setAltitude(parser.nextDouble());

        position.set(Position.KEY_SATELLITES, parser.nextInt());

        position.setNetwork(new Network(CellTower.fromLacCid(getConfig(), parser.nextInt(), parser.nextInt())));

        return position;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = ((String) msg).trim();
        if (sentence.contains("INFO")) {
            return decodeStatus(channel, remoteAddress, sentence);
        } else if (sentence.contains("POS")) {
            return decodeLocation(channel, remoteAddress, sentence);
        } else {
            return null;
        }
    }

}
