package org.openhab.binding.homeawareness.sensor;

public abstract class Sensor
{
    private static final String STATE_ELEMENT_DELIMITER = ",";
    public static final int STATE_CHANGED = 0x01;
    public static final int STATUS_CHANGED = 0x02;
    protected final DeviceClass deviceClass;
    protected final String bindingId;
    protected final String deviceId;
    protected final String deviceName;
    protected final String swVersion;
    protected long lastUpdateTime;
    protected State currentState = State.UNKNOWN;
    protected State previousState = State.UNKNOWN;
    protected int currentStatus;
    private final int IN_ALARM_STATUS = 0x01;
    private final int COMM_FAILURE_STATUS = 0x02;
    private final int LOW_BATTERY_STATUS = 0x04;
    private static final String STATE_PREFIX = "STATE=";
    private static final int TOTAL_NUMBER_OF_STATE_ELEMENTS = 17;
    private static final int STATE_DATA_INDEX = 1;
    private static final int DEVICE_CLASS_INDEX = 3;
    private static final int DEVICE_SENSOR_STATE_INDEX = 4;
    private static final int DEVICE_SENSOR_STATUS_INDEX = 6;
    // private static final int DEVICE_AWARENESS_CONFIG_INDEX = 8;
    private static final int DEVICE_ID_INDEX = 15;
    private static final int DEVICE_NAME_INDEX = 16;
    private static final int DEVICE_BINDING_INDEX = 1;

    public enum DeviceClass
    {
        POWER_SENSOR
        {
            {
                classNumber = "0004";
                className = "PowerSensor";
            }
        },

        MOTION_SENSOR
        {
            {
                classNumber = "0017";
                className = "MotionSensor";
            }
        },

        WATER_SENSOR
        {
            {
                classNumber = "0005";
                className = "WaterSensor";
            }
        },

        DOOR_SENSOR
        {
            {
                classNumber = "0003";
                className = "DoorSensor";
            }
        },

        GARAGE_DOOR_SENSOR
        {
            {
                classNumber = "0018";
                className = "GarageDoorSensor";
            }
        };

        public String getClassNumber()
        {
            return classNumber;
        }

        public String toString()
        {
            switch (this)
            {
                case POWER_SENSOR:
                    return POWER_SENSOR.classNumber;
                case MOTION_SENSOR:
                    return MOTION_SENSOR.classNumber;
                case WATER_SENSOR:
                    return WATER_SENSOR.classNumber;
                case DOOR_SENSOR:
                    return DOOR_SENSOR.classNumber;
                case GARAGE_DOOR_SENSOR:
                    return GARAGE_DOOR_SENSOR.classNumber;
            }

            return "";
        }

        String classNumber;
        String className;
    }

    protected Sensor(final String deviceId, final String bindingId, final DeviceClass deviceClass,
            final String deviceName, final String swVersion)
    {
        if (deviceId == null || deviceId.trim().equals(""))
        {
            throw new IllegalArgumentException("deviceId must not be null or empty");
        }

        if (bindingId == null || bindingId.trim().equals(""))
        {
            throw new IllegalArgumentException("bindingId must not be null or empty");
        }

        if (deviceClass == null)
        {
            throw new IllegalArgumentException("deviceClass must not be null or empty");
        }

        if (deviceName == null || deviceName.trim().equals(""))
        {
            throw new IllegalArgumentException("deviceName must not be null or empty");
        }

        /**
         * if (swVersion == null || swVersion.trim().equals("")) { throw new
         * IllegalArgumentException("swVersion must not be null or empty"); }
         */

        this.deviceId = deviceId;
        this.bindingId = bindingId;
        this.deviceClass = deviceClass;
        this.deviceName = deviceName;
        this.swVersion = swVersion;
    }

    public abstract String getSensorType();

    public String toString()
    {
        return getSensorType() + " Sensor: Device id [" + deviceId + "], deviceName[" + deviceName
                + "], bindingId[" + bindingId + "], state[" + currentState + "], software version["
                + swVersion + "]";
    }

    public DeviceClass getDeviceClass()
    {
        return deviceClass;
    }

    public static DeviceClass[] getDeviceClasses()
    {
        return DeviceClass.values();
    }

    public String getName()
    {
        return deviceName;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public String getVersion()
    {
        return swVersion;
    }

    protected void setCurrentState(String stateData)
    {
        updateSensorState(stateData);
    }

    private void  setCurrentState(final State newState)
    {
        currentState = newState;
    }

    protected abstract void updateSensorState(String stateData);

    public State getCurrentState()
    {
        return currentState;
    }

    public State getPreviousState()
    {
        return previousState;
    }

    /**
     * Set value for: alarm status, battery status, and communications status
     *
     * @param status
     */
    protected void setStatus(final String status)
    {
        this.currentStatus = Integer.parseInt(status, 16);
    }

    private void setStatus(final int newStatus)
    {
        this.currentStatus = newStatus;
    }

    public boolean isInAlarm()
    {
        return (currentStatus & IN_ALARM_STATUS) == IN_ALARM_STATUS;
    }

    public boolean hasLowBattery()
    {
        return (currentStatus & LOW_BATTERY_STATUS) == LOW_BATTERY_STATUS;
    }

    public boolean hasFailedComm()
    {
        return (currentStatus & COMM_FAILURE_STATUS) == COMM_FAILURE_STATUS;
    }

    /**
     * Sets the state and status of this sensor to the same values as the given sensor, if any of
     * them has changed.
     *
     * @return Ore'd value of <code>Sensor.STATE_CHANGED</code> and/or
     *         <code>Sensor.STATUS_CHANGED</code> depending on which one changed, or 0 if none
     *         changed.
     */
    public int update(final Sensor updatedSensor)
    {
        int changes = 0;

        if (this.getDeviceId().equalsIgnoreCase(updatedSensor.getDeviceId()))
        {
            if (getCurrentState() != updatedSensor.getCurrentState())
            {
                changes = Sensor.STATE_CHANGED;
                setCurrentState(updatedSensor.getCurrentState());
            }

            if (getCurrentStatus() != updatedSensor.getCurrentStatus())
            {
                changes &= Sensor.STATUS_CHANGED;
                setStatus(updatedSensor.getCurrentStatus());
            }
        }

        lastUpdateTime = System.currentTimeMillis();

        return changes;
    }

    public int getCurrentStatus()
    {
        return currentStatus;
    }

    /**
     * <code>
     * fields:  2 - binding id
     *          4 - device class
     *          5 - current state
     *          7 - sensor status; low battery, alarm indicator, communications:
     *                  0x01 alarm
     *                  0x02 no sensor update, communications failure, device missing
     *                  0x04 low battery
     *          8 - sensor name label number
     *          9  - call-me and in-home awareness
     *          13 - delay index or power sensitivity
     *          16 - device id (MAC address)
     *          17 - device name
     *
     *
     * </code> Field:
     *
     * 9 -> First two digits are for call-me awareness, second two are for in-home awareness
     *
     * @param stateData
     * @throws InvalidStateException
     */
    public static Sensor getSensorFromData(final String stateData, final String versionData)
            throws InvalidStateException
    {
        Sensor sensor = null;

        if (stateData.startsWith(STATE_PREFIX))
        {
            final String[] stateEntryElements = stateData.split("=");
            if (stateEntryElements.length == 2)
            {
                if (stateEntryElements[STATE_DATA_INDEX].equals("DONE"))
                {
                    return null;
                }

                // Substring removes leading and ending quotes (")
                final String state = stateEntryElements[STATE_DATA_INDEX].trim()
                        .substring(1, stateEntryElements[STATE_DATA_INDEX].length() - 1).trim();

                final String[] stateElements = state.split(STATE_ELEMENT_DELIMITER);
                if (stateElements.length == TOTAL_NUMBER_OF_STATE_ELEMENTS)
                {
                    final String deviceClass = stateElements[DEVICE_CLASS_INDEX];
                    final String deviceBinding = stateElements[DEVICE_BINDING_INDEX];
                    final String deviceId = stateElements[DEVICE_ID_INDEX];
                    final String deviceName = stateElements[DEVICE_NAME_INDEX];
                    final String deviceStatus = stateElements[DEVICE_SENSOR_STATUS_INDEX];
                    final String deviceState = stateElements[DEVICE_SENSOR_STATE_INDEX];

                    // TODO process the version information
                    final String version = "";

                    if (deviceClass.equals(PowerSensor.DEVICE_CLASS.getClassNumber()))
                    {
                        sensor = new PowerSensor(deviceId, deviceBinding, deviceName, version);
                    }
                    else if (deviceClass.equals(MotionSensor.DEVICE_CLASS.getClassNumber()))
                    {
                        sensor = new MotionSensor(deviceId, deviceBinding, deviceName, version);
                    }
                    else if (deviceClass.equals(DoorSensor.DEVICE_CLASS.getClassNumber()))
                    {
                        sensor = new DoorSensor(deviceId, deviceBinding, deviceName, version);
                    }
                    else if (deviceClass.equals(WaterSensor.DEVICE_CLASS.getClassNumber()))
                    {
                        sensor = new WaterSensor(deviceId, deviceBinding, deviceName, version);
                    }
                    else if (deviceClass.equals(GarageDoorSensor.DEVICE_CLASS.getClassNumber()))
                    {
                        sensor = new GarageDoorSensor(deviceId, deviceBinding, deviceName, version);
                    }

                    if (sensor != null)
                    {
                        sensor.setStatus(deviceStatus);
                        sensor.setCurrentState(deviceState);
                    }
                }
            }
        }

        return sensor;
    }
}
