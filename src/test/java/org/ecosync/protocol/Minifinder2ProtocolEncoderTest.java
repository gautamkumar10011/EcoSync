package org.ecosync.protocol;

import org.junit.jupiter.api.Test;
import org.ecosync.ProtocolTest;
import org.ecosync.model.Command;

public class Minifinder2ProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncodeNano() throws Exception {

        var encoder = inject(new Minifinder2ProtocolEncoder(null));

        encoder.setModelOverride("Nano");

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_FIRMWARE_UPDATE);
        command.set(Command.KEY_DATA, "https://example.com");

        verifyCommand(encoder, command, binary("ab00160059d2010004143068747470733a2f2f6578616d706c652e636f6d"));

    }

}
