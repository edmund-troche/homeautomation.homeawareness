package org.openhab.binding.homeawareness.internal;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;

import org.openhab.binding.homeawareness.sensor.InvalidStateException;
import org.openhab.binding.homeawareness.sensor.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusProcessor extends Thread
{
    private static final String STATE_TABLE_END = "STATE=DONE";
    private static final String STATE_ENTRY_PREFIX = "STATE=\"";
    private static final String GET_STATE_TABLE_COMMAND = "S";
    private static final String NEW_STATE_INDICATOR = "STATE=NEW";
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusProcessor.class);
    private final SensorsModel sensorsModel;
    private BufferedReader reader;
    private PrintStream writer;
    private boolean stop = false;
    private SerialPort port;

    public StatusProcessor() throws Exception
    {
        setName("Status Processor");
        setDaemon(true);

        // TODO make configurable
        final String serialPortName = "/dev/cu.usbserial-000011FD";

        //
        // Find the correct port identifier for the serial port name
        //
        @SuppressWarnings("unchecked")
        final Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier
                .getPortIdentifiers();

        CommPortIdentifier portIdentifier = null;
        while (portIdentifiers.hasMoreElements())
        {
            final CommPortIdentifier currentPortIdentifier = portIdentifiers.nextElement();
            if (currentPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL
                    && currentPortIdentifier.getName().equals(serialPortName))
            {
                portIdentifier = currentPortIdentifier;
                break;
            }
        }

        if (portIdentifier == null)
        {
            LOGGER.error("Could not find serial port " + serialPortName);
            // TODO throw custom exception
            throw new Exception("Unable to find serial port " + serialPortName);
        }

        try
        {
            port = (SerialPort) portIdentifier.open("Home Heartbeat Base", 10000);
        }
        catch (PortInUseException e)
        {
            LOGGER.error("Port already in use: " + serialPortName);
            // TODO throw custom exception
            throw new Exception("Port already in use: " + serialPortName);
        }

        try
        {
            port.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
        catch (UnsupportedCommOperationException ucoExc)
        {
            LOGGER.error("Unable to set serial port parameters", ucoExc);

            // TODO refactor
            throw new RuntimeException(ucoExc);
        }

        try
        {
            reader = new BufferedReader(new InputStreamReader(port.getInputStream()));
        }
        catch (IOException e)
        {
            LOGGER.error("Unable to open input stream");

            // TODO handle error
            reader = null;
        }

        try
        {
            writer = new PrintStream(port.getOutputStream(), true);
        }
        catch (IOException e)
        {
            LOGGER.error("Unable to open output stream");

            // TODO handle error
            writer = null;
        }

        sensorsModel = new SensorsModel(new Sensor[]{});
    }

    @Override
    public void run()
    {
        while (!stop)
        {
            String currentTextLine = null;

            try
            {
                currentTextLine = reader.readLine();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (currentTextLine != null && currentTextLine.equals(NEW_STATE_INDICATOR))
            {
                /**
                 * Request a sensor state update and process the values
                 */
                processStateTable();
            }
        }
    }

    private void processStateTable()
    {
        /**
         * Send the state table request/command
         */
        writer.println(GET_STATE_TABLE_COMMAND);
        writer.flush();

        /**
         * Process the state table values
         */
        try
        {
            String currentTextLine = reader.readLine();

            while (currentTextLine != null && currentTextLine.startsWith(STATE_ENTRY_PREFIX)
                    && !currentTextLine.equals(STATE_TABLE_END))
            {
                final Sensor sensor = Sensor.getSensorFromData(currentTextLine, null);
                if (sensor != null)
                {
                    sensorsModel.updateSensors(new Sensor[]{sensor});
                }

                currentTextLine = reader.readLine();
            }
        }
        catch (IOException ioExc)
        {
            // TODO log warning
        }
        catch (InvalidStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
