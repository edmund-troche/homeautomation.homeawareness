package org.openhab.binding.homeawareness.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openhab.binding.homeawareness.sensor.InvalidStateException;
import org.openhab.binding.homeawareness.sensor.Sensor;

public class SerialPortStatusProcessor extends Thread implements SensorStatusProcessor, Runnable
{
    private boolean processing = true;
    private DataQueue inputData;
    private OutputStream outputStream;

    public SerialPortStatusProcessor(final OutputStream outputStream, final DataQueue inputData)
    {
        this.inputData = inputData;
        this.outputStream = outputStream;
    }

    public void run()
    {
        final StringBuilder currentData = new StringBuilder();

        while (processing)
        {
            currentData.append((char) inputData.remove().intValue());

            if (currentData.indexOf("\n\rSTATE=NEW\n\r") != -1)
            {
                System.out.println("Processing new state");

                // Get the input queue clean and ready for the status table data.
                inputData.clear();

                updateStatusTable();

                currentData.delete(0, currentData.length() - 1);
            }
        }
    }

    private void updateStatusTable()
    {
        try
        {
            requestCurrentStatusTable();

            processStatusTable();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void processStatusTable()
    {
        final StringBuilder currentData = new StringBuilder();
        final List<String> statusTable = new ArrayList<String>();

        System.out.println("Processing status table");

        while (true)
        {
            currentData.append((char) inputData.remove().intValue());

            if (currentData.indexOf("STATE=") != -1)
            {
                final StringBuilder currentLine = new StringBuilder("STATE=");
                boolean eol = false;

                while (!eol)
                {
                    final char currentChar = (char) inputData.remove().intValue();
                    currentLine.append(currentChar);

                    if (currentChar == '\n')
                    {
                        eol = true;
                    }
                }


                statusTable.add(currentLine.toString().trim());

                System.out.println("Adding line:" + currentLine.toString().trim());

                if (currentLine.indexOf("STATE=DONE") != -1)
                {
                    System.out.println("Done with status table processing");

                    break;
                }

                currentData.delete(0, currentData.length() -1);
            }
        }

        String[] status = statusTable.toArray(new String[statusTable.size()]);
        List<Sensor> sensors;
        try
        {
            sensors = StateParser.getSensors(status);
        }
        catch (InvalidStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();

            return;
        }

        assertEquals("Failed to find correct number of sensors", 7, sensors.size());

        for (Sensor sensor : sensors)
        {
            System.out.println(sensor);
        }
    }

    private void requestCurrentStatusTable() throws IOException
    {
        System.out.println("Requesting status table");

        outputStream.write("S".getBytes());
    }
}
