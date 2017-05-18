package org.openhab.binding.homeawareness.sensor;

public class WaterSensor extends Sensor
{
    public static final DeviceClass DEVICE_CLASS = DeviceClass.WATER_SENSOR;

    public WaterSensor(final String deviceId, final String bindingId, final String deviceName,
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

        if (stateData.equals(State.WATER_DETECTED.getStateValue()))
        {
            currentState = State.WATER_DETECTED;
        }
        else if (stateData.equals(State.DRY.getStateValue()))
        {
            currentState = State.DRY;
        }
        else
        {
            currentState = State.UNKNOWN;
        }
    }
}
