package org.openhab.binding.homeawareness.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openhab.binding.homeawareness.sensor.InvalidStateException;
import org.openhab.binding.homeawareness.sensor.Sensor;

public class StateParser
{
    public static List<Sensor> getSensors(final String[] stateTableData)
            throws InvalidStateException
    {
        if (stateTableData.length == 0)
        {
            return Collections.emptyList();
        }

        final List<Sensor> sensors = new ArrayList<Sensor>();
        for (final String stateTableEntry : stateTableData)
        {
            final String versionTableEntry = getVersionTableEntry(stateTableEntry);
            final Sensor sensor = Sensor.getSensorFromData(stateTableEntry, versionTableEntry);
            if (sensor != null)
            {
                sensors.add(sensor);
            }
        }

        return sensors;
    }

    private static String getVersionTableEntry(String stateTableEntry)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
