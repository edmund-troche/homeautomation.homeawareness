package org.openhab.binding.homeawareness.internal;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openhab.binding.homeawareness.sensor.InvalidStateException;
import org.openhab.binding.homeawareness.sensor.Sensor;

public class StateParserTest
{
    @SuppressWarnings("unused")
    private static final String BINDING_TABLE_DATA = "BIND=\"\n"
            + "BIND=00 00      L       0A      0A      000D6F000000B12D--FFFF\n"
            + "BIND=01 00      L       0A      0A      000D6F000000A435--FFFF\n"
            + "BIND=02 00      L       0A      0A      000D6F0000009938--FFFF\n"
            + "BIND=03 00      L       0A      0A      000D6F0000093791--FFFF\n"
            + "BIND=04 00      L       0A      0A      000D6F000000F3E4--FFFF\n"
            + "BIND=05 00      L       0A      0A      000D6F000000E3B3--FFFF\n"
            + "BIND=06 00      L       0A      0A      000D6F0000093B13--FFFF\n"
            + "BIND=07 00      L       0A      0A      000D6F000000BB35--FFFF\n"
            + "BIND=08 00      L       0A      0A      000D6F00000951ED--FFFF\n"
            + "BIND=09 00      U       0A      0A      000D6F000000A717--F240\n"
            + "BIND=10 00      L       0A      0A      000D6F000000A980--FFFF\n"
            + "BIND=11 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=12 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=13 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=14 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=15 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=16 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=17 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=18 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=19 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=20 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=21 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=22 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=23 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=24 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=25 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=26 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=27 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=28 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=29 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=30 00      x       FF      FF      FFFFFFFFFFFFFFFF--FFFF\n"
            + "BIND=31 00      M       09      09      00000103F200EEEE--FFFF\"";

    @SuppressWarnings("unused")
    private static final String[] STATE_TABLE_DATA = new String[]
    {
            "STATE=\"00,FF,0088,0001,00,00,00,00,0000,00,0000,00,00,00000000,00,,\"\n",
            "STATE=\"01,FF,0080,0010,00,00,00,00,0000,00,0000,00,00,00000000,00,,\"\n",
            "STATE=\"02,00,0040,0002,00,09,00,01,0000,09,0000,00,FF,00000000,00,000D6F000000B12D,Dad's \"\n",
            "STATE=\"03,01,0024,0007,02,90,00,00,0001,51,0000,00,FF,00000000,00,000D6F000000A435,Attent\"\n",
            "STATE=\"04,02,0034,0006,02,81,00,00,0001,54,0000,00,01,00000000,00,000D6F0000009938,Remind\"\n",
            "STATE=\"05,03,0034,0017,02,0B,01,00,0002,4F,0000,00,00,00000000,00,000D6F0000093791,Motion\"\n",
            "STATE=\"06,04,0024,0003,02,8F,05,01,0002,45,0000,00,FF,00000000,00,000D6F000000F3E4,Door\"\n",
            "STATE=\"07,05,0040,0002,00,26,00,02,0200,26,0000,00,FF,00000000,00,000D6F000000E3B3,Mom's \"\n",
            "STATE=\"08,06,0034,0017,02,20,00,00,0000,43,0000,00,00,00000000,00,000D6F0000093B13,Motion\"\n",
            "STATE=\"09,07,0024,0005,02,70,00,07,0001,53,0000,00,FF,00000000,00,000D6F000000BB35,Water \"\n",
            "STATE=\"0A,08,00B4,0004,01,6B,00,00,0002,17,0000,00,00,00000000,00,000D6F00000951ED,Power \"\n",
            "STATE=\"0B,09,00A6,0009,04,69,00,00,0002,18,0000,00,FF,00000000,00,000D6F000000A717,Range \"\n",
            "STATE=\"0C,0A,0024,0018,01,66,00,00,0002,48,0000,00,FF,00000000,00,000D6F000000A980,Garage\"\n",
            "STATE=DONE" };

    private static final String[] STATE_TABLE_DATA_1 = new String[]
    {
            "STATE=\"00,FF,0088,0001,00,00,00,00,0000,00,0000,00,00,00000000,00,,\"\n",
            "STATE=\"01,FF,0080,0010,00,00,00,00,0000,00,0000,00,00,00000000,00,,\"\n",
            "STATE=\"02,00,0040,0002,00,0A,00,01,0000,0A,0000,00,FF,00000000,00,000D6F000000B12D,Dad's \"\n",
            "STATE=\"03,01,0024,0007,02,B5,00,00,0001,44,0000,00,FF,00000000,00,000D6F000000A435,Attent\"\n",
            "STATE=\"04,02,0034,0006,02,A5,00,00,0001,4B,0000,00,01,00000000,00,000D6F0000009938,Remind\"\n",
            "STATE=\"05,03,0034,0017,01,05,00,05,0002,55,0000,00,00,00000000,00,000D6F0000093791,Bedroo\"\n",
            "STATE=\"06,04,0024,0003,01,81,00,01,0002,5D,0000,00,FF,00000000,00,000D6F000000F3E4,Door\"\n",
            "STATE=\"07,05,0040,0002,00,04,00,02,0200,04,0000,00,FF,00000000,00,000D6F000000E3B3,Mom's \"\n",
            "STATE=\"08,06,0034,0017,01,82,00,00,0000,4D,0000,00,00,00000000,00,000D6F0000093B13,Motion\"\n",
            "STATE=\"09,07,0024,0005,02,B5,00,07,0001,49,0000,00,FF,00000000,00,000D6F000000BB35,Water \"\n",
            "STATE=\"0A,08,00B4,0004,01,B5,00,00,0002,08,0000,00,01,00000000,00,000D6F00000951ED,Power \"\n",
            "STATE=\"0B,09,00A6,0009,04,B2,00,02,0002,16,0000,00,FF,00000000,00,000D6F000000A717,2nd Fl\"\n",
            "STATE=\"0C,0A,0024,0018,01,B5,04,00,0102,5C,0000,00,FF,00000000,00,000D6F000000A980,Garage\"\n",
            "STATE=\"0D,0B,0034,0017,01,84,00,06,0002,4E,0000,00,00,00000000,00,000D6F00000955FA,Child'\"\n",
            "STATE=DONE" };

    @SuppressWarnings("unused")
    private static final String[] SOFTWARE_VERSION_TABLE_DATA = new String[]
    {
            "VERSION=\"00,FF,010E,0001\"\n", "VERSION=\"01,FF,0101,0000\"\n",
            "VERSION=\"02,00,0109,0001\"\n", "VERSION=\"03,01,0108,0001\"\n",
            "VERSION=\"04,02,0108,0001\"\n", "VERSION=\"05,03,011C,0001\"\n",
            "VERSION=\"06,04,0108,0001\"\n", "VERSION=\"07,05,0109,0001\"\n",
            "VERSION=\"08,06,011C,0001\"\n", "VERSION=\"09,07,0109,0001\"\n",
            "VERSION=\"0A,08,0108,0001\"\n", "VERSION=\"0B,09,0108,0001\"\n",
            "VERSION=\"0C,0A,0108,0001\"\n", "VERSION=DONE\n" };

    @Test
    public void testParseData() throws InvalidStateException
    {
        final List<Sensor> sensors = StateParser.getSensors(STATE_TABLE_DATA_1);

        assertEquals("Failed to find correct number of sensors", 7, sensors.size());

        for (Sensor sensor : sensors)
        {
            System.out.println(sensor);
        }
    }
}
