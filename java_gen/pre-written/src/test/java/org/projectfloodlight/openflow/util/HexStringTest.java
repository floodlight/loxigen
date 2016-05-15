/**
*    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
*    University
*
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package org.projectfloodlight.openflow.util;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Random;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Does hexstring conversion work?
 *
 * @author Rob Sherwood (rob.sherwood@stanford.edu)
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class HexStringTest {

    @Test
    public void testMarshalling() throws Exception {
        String dpidStr = "00:00:00:23:20:2d:16:71";
        long dpid = HexString.toLong(dpidStr);
        String testStr = HexString.toHexString(dpid);
        assertEquals(dpidStr, testStr);
    }

    @Test
    public void testToLong() {
        String dpidStr = "3e:1f:01:fc:72:8c:63:31";
        long valid = 0x3e1f01fc728c6331L;
        long testLong = HexString.toLong(dpidStr);
        assertEquals("was: " + Long.toHexString(testLong), valid, testLong);
    }

    @Test
    public void testToLong2() {
        String dpidStr = "1f:1:fc:72:3:f:31";
        long valid = 0x1f01fc72030f31L;
        long testLong = HexString.toLong(dpidStr);
        assertEquals("was: " + Long.toHexString(testLong), valid, testLong);
    }

    @Test
    public void testToLongMSB() {
        String dpidStr = "ca:7c:5e:d1:64:7a:95:9b";
        long valid = -3856102927509056101L;
        long testLong = HexString.toLong(dpidStr);
        assertEquals(valid, testLong);
    }

    @Test(expected=NumberFormatException.class)
    public void testToLongErrorTooManyBytes() {
        HexString.toLong("09:08:07:06:05:04:03:02:01");
    }

    @Test(expected=NumberFormatException.class)
    public void testToLongErrorByteValueTooLong() {
        HexString.toLong("234:01");
    }

    @Test(expected=NumberFormatException.class)
    public void testToLongErrorEmptyByte() {
        HexString.toLong("03::01");
    }

    @Test(expected=NumberFormatException.class)
    public void testToLongErrorColonAtEnd() {
        HexString.toLong("03:01:");
    }

    @Test(expected=NumberFormatException.class)
    public void testToLongErrorInvalidHexDigit() {
        HexString.toLong("ss:01");
    }

    public void testToLongErrorEmptyString() {
        assertThat(HexString.toLong(""), equalTo(0L));
    }


    @Test
    public void testToStringBytes() {
        byte[] dpid = { 0, 0, 0, 0, 0, 0, 0, -1 };
        String valid = "00:00:00:00:00:00:00:ff";
        String testString = HexString.toHexString(dpid);
        assertEquals(valid, testString);
    }

    @Test
    public void testToStringBytes2() {
        byte[] dpid = { 1, 2, 3, 4 };
        String valid = "01:02:03:04";
        String testString = HexString.toHexString(dpid);
        assertEquals(valid, testString);
    }

    @Test
    public void testToStringBytes3() {
        byte[] dpid = { (byte) 0xff };
        String valid = "ff";
        String testString = HexString.toHexString(dpid);
        assertEquals(valid, testString);
    }

    @Test
    public void testToStringEmpty() {
        byte[] dpid = { };
        String valid = "";
        String testString = HexString.toHexString(dpid);
        assertEquals(valid, testString);
    }

    @Test
    public void testToStringLong() {
        byte[] dpid = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        String valid = "01:02:03:04:05:06:07:08:09:0a:0b:0c:0d:0e:0f";
        String testString = HexString.toHexString(dpid);
        assertEquals(valid, testString);
    }

    @Test
    public void testToZero() {
        byte[] dpid = { 0, 0, 0, 0};
        String valid = "00:00:00:00";
        String testString = HexString.toHexString(dpid);
        assertEquals(valid, testString);
    }

    @Test
    public void testToStringFromLong() {
        assertThat(HexString.toHexString(0x0L), equalTo("00:00:00:00:00:00:00:00"));
        assertThat(HexString.toHexString(0x00_00_00_00_00_00_00_01L), equalTo("00:00:00:00:00:00:00:01"));
        assertThat(HexString.toHexString(0x01_02_03_04_05_06_07_08L), equalTo("01:02:03:04:05:06:07:08"));
        assertThat(HexString.toHexString(0x00_00_ff_fe_fd_fc_fb_fa_f9_f8L), equalTo("ff:fe:fd:fc:fb:fa:f9:f8"));
        assertThat(HexString.toHexString(0x80_70_60_50_40_30_20_10L), equalTo("80:70:60:50:40:30:20:10"));
    }

    @Test
    public void testToStringFromLongPad6() {
        assertThat(HexString.toHexString(0x0L, 6), equalTo("00:00:00:00:00:00"));
        assertThat(HexString.toHexString(0x00_00_00_00_00_00_00_01L, 6), equalTo("00:00:00:00:00:01"));
        assertThat(HexString.toHexString(0x00_00_01_02_03_04_05_06L, 6), equalTo("01:02:03:04:05:06"));
        assertThat(HexString.toHexString(0x00_00_ff_fe_fd_fc_fb_faL, 6), equalTo("ff:fe:fd:fc:fb:fa"));
        // when supplying a value longer than 6, it is displayed completely
        assertThat(HexString.toHexString(0x80_70_60_50_40_30_20_10L, 6), equalTo("80:70:60:50:40:30:20:10"));
    }

    @Test
    public void testToBytes() {
        assertThat(HexString.toBytes(""), equalTo(new byte[]{}));
        assertThat(HexString.toBytes("1"), equalTo(new byte[]{0x01}));
        assertThat(HexString.toBytes("f"), equalTo(new byte[]{0x0f}));
        assertThat(HexString.toBytes("10"), equalTo(new byte[]{(byte) 0x10}));
        assertThat(HexString.toBytes("80"), equalTo(new byte[]{(byte) 0x80}));
        assertThat(HexString.toBytes("ff"), equalTo(new byte[]{(byte) 0xff}));
        assertThat(HexString.toBytes("0:0"), equalTo(new byte[]{(byte) 0x00, 0x00}));
        assertThat(HexString.toBytes("00:00"), equalTo(new byte[]{(byte) 0x00, 0x00}));
        assertThat(HexString.toBytes("0:1"), equalTo(new byte[]{(byte) 0x00, 0x01}));
        assertThat(HexString.toBytes("00:1"), equalTo(new byte[]{(byte) 0x00, 0x01}));
        assertThat(HexString.toBytes("0:01"), equalTo(new byte[]{(byte) 0x00, 0x01}));
        assertThat(HexString.toBytes("1:0"), equalTo(new byte[]{(byte) 0x01, 0x00}));
        assertThat(HexString.toBytes("01:00"), equalTo(new byte[]{(byte) 0x01, 0x00}));
        assertThat(HexString.toBytes("01:02:03:04"), equalTo(new byte[]{(byte) 0x01, 0x02, 03, 04}));
        assertThat(HexString.toBytes("ff:fe:03:04"), equalTo(new byte[]{(byte) 0xff, (byte) 0xfe, 03, 04}));
        assertThat(HexString.toBytes("ff:fe:3:4"), equalTo(new byte[]{(byte) 0xff, (byte) 0xfe, 03, 04}));
        assertThat(HexString.toBytes("01:02:03:04:05:06"), equalTo(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06 }));
        assertThat(HexString.toBytes("ff:1:fe:2:fd"),
                equalTo(new byte[]{(byte) 0xff, 0x01, (byte) 0xfe, 0x02, (byte) 0xfd}));

    }
    @Test
    public void testToBytesRandom() {
        Random r = new Random();
        for(int length: ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 15, 16, 32, 63, 64, 128, 255, 256, 511, 512, 1023, 1024)) {
            StringBuilder build = new StringBuilder();
            byte[] bytes = new byte[length];
            for(int i=0; i<length; i++) {
                byte b = (byte) r.nextInt(256);
                build.append(String.format("%02x", b));
                if(i < length-1) {
                    build.append(":");
                }
                bytes[i] = b;
            }
            assertThat("For length "+ length + ", ",
                    HexString.toBytes(build.toString()),
                    equalTo(bytes));
        }
    }

    @Test(expected=NumberFormatException.class)
    public void testToBytesError() {
        String invalidStr = "00:00:00:00:00:00:ffff";
        HexString.toBytes(invalidStr);
    }


    @Test(expected=NumberFormatException.class)
    public void testToBytesError2() {
        String invalidStr = ":01:02:03";
        HexString.toBytes(invalidStr);
    }

    @Test(expected=NumberFormatException.class)
    public void testToBytesError3() {
        String invalidStr = "01::02:03";
        HexString.toBytes(invalidStr);
    }

    @Test(expected=NumberFormatException.class)
    public void testBoBytesError4() {
        String invalidStr = "01:02:03:";
        HexString.toBytes(invalidStr);
    }

    @Test(expected=NumberFormatException.class)
    public void testtoBytesError5() {
        String invalidStr = "01:0X";
        HexString.toBytes(invalidStr);
    }

}

