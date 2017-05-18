package org.openhab.binding.homeawareness.sensor;

public enum State
{
    ON("02"),
    OFF("01"),
    OPEN("02"),
    CLOSED("01"),
    MOTION_DETECTED("02"),
    MOTION_RESET("01"),
    WATER_DETECTED("01"),
    DRY("02"),
    UNKNOWN("FF");

    private State(final String stateValue)
    {
        this.stateValue = stateValue;
    }

    public String getStateValue()
    {
        return stateValue;
    }

    private final String stateValue;
}
