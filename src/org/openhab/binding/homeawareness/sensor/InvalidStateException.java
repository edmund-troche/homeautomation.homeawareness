package org.openhab.binding.homeawareness.sensor;

public class InvalidStateException extends Exception
{
    private static final long serialVersionUID = 3919488356627442441L;

    public InvalidStateException()
    {
    }

    public InvalidStateException(String arg0)
    {
        super(arg0);
    }

    public InvalidStateException(Throwable arg0)
    {
        super(arg0);
    }

    public InvalidStateException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

}
