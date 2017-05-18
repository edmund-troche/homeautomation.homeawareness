package org.openhab.binding.homeawareness.statusprocessors.internal;

public class CommunicationException extends Exception {
    private static final long serialVersionUID = -1472660652430075079L;

    public CommunicationException() {
    }

    public CommunicationException(String arg0) {
        super(arg0);
    }

    public CommunicationException(Throwable arg0) {
        super(arg0);
    }

    public CommunicationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
