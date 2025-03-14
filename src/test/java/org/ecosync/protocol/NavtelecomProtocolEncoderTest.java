package org.ecosync.protocol;

import org.junit.jupiter.api.Test;
import org.ecosync.ProtocolTest;
import org.ecosync.model.Command;

public class NavtelecomProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        var encoder = inject(new NavtelecomProtocolEncoder(null));

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "*!SETOUT 1Y");

        verifyCommand(encoder, command, binary("404e544300000000010000000b004f5c2a215345544f5554203159"));

    }

}
