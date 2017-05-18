package org.openhab.binding.homeawareness.sensor;

public class MotionSensor extends Sensor
{
    public static final DeviceClass DEVICE_CLASS = DeviceClass.MOTION_SENSOR;

    public MotionSensor(final String deviceId, final String bindingId, final String deviceName,
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

        if (stateData.equals(State.MOTION_DETECTED.getStateValue()))
        {
            currentState = State.MOTION_DETECTED;
        }
        else if (stateData.equals(State.MOTION_RESET.getStateValue()))
        {
            currentState = State.MOTION_RESET;
        }
        else
        {
            currentState = State.UNKNOWN;
        }
    }
}
