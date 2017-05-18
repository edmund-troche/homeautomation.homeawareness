package org.openhab.binding.homeawareness.sensor;


public class PowerSensor extends Sensor
{
    /**
     * Power Sensor - As configured, this sensor simply reports whether or not a load is active on
     * the device plugged in. Device class is reported as (id: 0004). State reports (state: 1) when
     * load is off and (state: 2) when load is turned on
     */
    public static final DeviceClass DEVICE_CLASS = DeviceClass.POWER_SENSOR;

    protected PowerSensor(final String deviceId, final String bindingId, final String deviceName,
            final String version)
    {
        super(deviceId, bindingId, DEVICE_CLASS, deviceName, version);
    }

    @Override
    public String getSensorType()
    {
        return DEVICE_CLASS.className;
    }

    @Override
    protected void updateSensorState(final String stateData)
    {
        previousState = currentState;

        if (stateData.equals(State.ON.getStateValue()))
        {
            currentState = State.ON;
        }
        else if (stateData.equals(State.OFF.getStateValue()))
        {
            currentState = State.OFF;
        }
        else
        {
            currentState = State.UNKNOWN;
        }
    }
}
