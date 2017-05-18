package org.openhab.binding.homeawareness.sensor;

public class DoorSensor extends Sensor
{
    public static final DeviceClass DEVICE_CLASS = DeviceClass.DOOR_SENSOR;

    public DoorSensor(final String deviceId, final String bindingId, final String deviceName,
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
    public void updateSensorState(final String stateData)
    {
        previousState = currentState;

        if (stateData.equals(State.CLOSED.getStateValue()))
        {
            currentState = State.CLOSED;
        }
        else if (stateData.equals(State.OPEN.getStateValue()))
        {
            currentState = State.OPEN;
        }
        else
        {
            currentState = State.UNKNOWN;
        }
    }
}
