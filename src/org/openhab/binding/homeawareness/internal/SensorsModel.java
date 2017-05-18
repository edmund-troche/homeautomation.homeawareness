package org.openhab.binding.homeawareness.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openhab.binding.homeawareness.sensor.Sensor;
import org.openhab.binding.homeawareness.sensor.StateChangeListener;
import org.openhab.binding.homeawareness.sensor.StatusChangeListener;

public class SensorsModel
{
    private final Map<String, Sensor> sensors;
    private final List<StateChangeListener> stateChangeListeners = Collections
            .synchronizedList(new ArrayList<StateChangeListener>());
    private final List<StatusChangeListener> statusChangeListeners = Collections
            .synchronizedList(new ArrayList<StatusChangeListener>());

    public SensorsModel(final Sensor initialSensors[])
    {
        sensors = new HashMap<String, Sensor>();

        updateSensors(initialSensors);
    }

    public void updateSensors(final Sensor updatedSensors[])
    {
        int changes = 0;

        for (Sensor sensor : updatedSensors)
        {
            if (sensors.containsKey(sensor.getDeviceId()))
            {
                final Sensor existingSensor = sensors.get(sensor.getDeviceId());
                changes = existingSensor.update(sensor);
            }
            else
            {
                sensors.put(sensor.getDeviceId(), sensor);
                changes = Sensor.STATE_CHANGED & Sensor.STATUS_CHANGED;
            }

            /**
             * If we have a new sensor or the state/status of an existing sensor changed, then call
             * the change listeners
             */
            if ((changes & Sensor.STATE_CHANGED) == Sensor.STATE_CHANGED)
            {
                for (StateChangeListener listener : stateChangeListeners)
                {
                    listener.stateChanged(sensors.get(sensor.getDeviceId()));
                }
            }

            if ((changes & Sensor.STATUS_CHANGED) == Sensor.STATUS_CHANGED)
            {
                for (StatusChangeListener listener : statusChangeListeners)
                {
                    listener.statusChanged(sensors.get(sensor.getDeviceId()));
                }
            }
        }
    }

    public void addStatusChangeListener(final StatusChangeListener listener)
    {
        if (statusChangeListeners.contains(listener))
        {
            return;
        }

        statusChangeListeners.add(listener);
    }

    public void addStateChangeListener(final StateChangeListener listener)
    {
        if (stateChangeListeners.contains(listener))
        {
            return;
        }

        stateChangeListeners.add(listener);
    }

    public Sensor getSensor(final String sensorId)
    {
        // TODO Should return a read-only copy of the sensor object, the only thing that should
        // be able to change the state/status of a sensor it the status processor.
        return sensors.get(sensorId);
    }
}
