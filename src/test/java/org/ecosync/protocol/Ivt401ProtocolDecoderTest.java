package org.ecosync.protocol;

import org.junit.jupiter.api.Test;
import org.ecosync.ProtocolTest;

public class Ivt401ProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        var decoder = inject(new Ivt401ProtocolDecoder(null));

        verifyPosition(decoder, text(
                "(TLA,356917051007891,190118,090211,+16.986606,+82.242416,0,66,4,13,1,5,000,00,0.0,11.59,8.30,37.77,0.0,1,1.02,0,0,208,0,0,0,0,000000000,0,0,0,0,0,0,0,1,8654604,5,9,114)"));

        verifyPosition(decoder, text(
                "(TLN,862107032006249,230218,180500,+18.479728,+73.896339,30,0,944,13,1,5,111,11,0.00,10.88,6.31,29.55,0.00,0,0.99,66,0,0,88,95)"));

        verifyPosition(decoder, text(
                "(TLN,865933030026336,250118,063827,+18.598098,+73.806518,0,79,0,1,1,5,1200,0,0.0,11.50,4.00,36,0,0,1.00,0,0,12702,202,0)"));

        verifyPosition(decoder, text(
                "(TLA,865933030026336,250118,063838,+18.598110,+73.806518,0,334,0,1,1,5,1200,0,0.0,12.20,4.10,36,0,0,1.00,0,0,12704,0,0,0,0,0,0,0,0,0,0,0,0,1,0,3,203,0)"));

        verifyNull(decoder, text(
                "(TLB,356917050440515,0,0,+0.0,+0.0,0,0,0,0,0,0,000,00,0.00,12.21,7.95,26.57,0.0,1,1.12,0,0,0,1,90)"));

        verifyPosition(decoder, text(
                "(TLN,356917050440515,250118,094723,+0.0,+0.0,0,0,0,0,3,5,000,00,0.00,12.25,7.95,27.11,0.0,0,1.13,0,0,0,4,86)"));

        verifyPosition(decoder, text(
                "(TLA,356917050440515,250118,094733,+0.0,+0.0,0,0,0,0,3,5,000,00,0.00,12.25,7.95,27.20,0.0,0,1.12,0,0,0,0,0,0,0,000000000,0,1,0,0,0,0,0,1,0,0,5,70)"));

        verifyPosition(decoder, text(
                "(TLN,356917050440515,250118,094733,+0.0,+0.0,0,0,0,0,3,5,000,00,0.00,12.25,7.95,27.20,0.0,0,1.13,0,0,0,6,87)"));

        verifyPosition(decoder, text(
                "(TLN,356917050440515,250118,103541,+17.015546,+54.080841,0,0,31,12,1,5,000,00,0.0,0.00,8.04,28.59,M+0+0+0+0+0,0,1.12,0,0,2,48,30)"));

        verifyPosition(decoder, text(
                "(TLN,356917050291991,090315,133525,+12.990582,+77.589080,0,0,944,13,1,5,000,00,0.00,10.88,6.31,29.55,0.00,0,0.99,66,0,0,88,95)"));

        verifyPosition(decoder, text(
                "(TLN,356917050269732,061117,220046,+21.134126,+74.798561,51,28,204,14,1,5,100,00,0.0,13.92,7.82,23.74,0.0,1,1.33,-9,0,429,4848,67)"));

        verifyPosition(decoder, text(
                "(TLN,356917050269732,061117,220116,+21.137619,+74.800659,52,28,202,14,1,3,100,00,0.0,13.92,7.82,23.74,0.0,1,1.26,-23,0,445,4849,125)"));

        verifyPosition(decoder, text(
                "(TLA,356917050217335,190115,011336,+12.932403,+79.898887,0,0,71.7,08,3,10,000,00,0.00,10.41,7.07,26.84,0.00,0,0.99,63,0,0,0,0,0,0,000000000,0,0,0,0,0,0,0,2,0,0,14,86)"));

        verifyPosition(decoder, text(
                "(TLB,356917050291991,090315,133525,+12.990582,+77.589080,0,0,944,13,1,5,000,00,0.00,10.88,6.31,29.55,0.00,0,0.99,66,0,0,88,95)"));

        verifyPosition(decoder, text(
                "(TLL,356917050217335,190115,011336,+12.932403,+79.898887,0,0,71.7,08,3,10,000,00,0.00,10.41,7.07,26.84,0.00,0,0.99,63,0,0,0,0,0,0,000000000,0,0,0,0,0,0,0,2,0,0,14,86)"));

    }

}
