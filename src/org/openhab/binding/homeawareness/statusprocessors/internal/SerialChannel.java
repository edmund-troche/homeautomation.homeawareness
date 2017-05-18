/*
 * $Header: /usr/local/cvsroot/org.openhab.binding.homeawareness/src/org/openhab/binding/homeawareness/statusprocessors/internal/SerialChannel.java,v 1.2 2012/09/30 00:55:44 edmund Exp $
 *
 * Copyright (c) Home iPliance (2007-2012). All Rights Reserved.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * $Log: SerialChannel.java,v $
 * Revision 1.2  2012/09/30 00:55:44  edmund
 * Some refactoring, test, and sensor model updates
 *
 * Revision 1.1  2012/04/22 15:46:38  edmund
 * Created status processor abstraction with real communication channel and simulated channel
 *
 * Revision 1.3  2011/06/27 04:36:10  edmund
 * Trying other CVS keywords
 *
 *
 */
package org.openhab.binding.homeawareness.statusprocessors.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class SerialChannel
{
    public static final String CVS_ID = "$Id: SerialChannel.java,v 1.2 2012/09/30 00:55:44 edmund Exp $";
    private static final int MILLISECONDS_PER_SECOND = 1000;

    private static final String APPLICATION_NAME = "Home Heartbeat Homeawareness";

    /** How long to wait for the open to finish up. */
    public static final int TIMEOUT_IN_SECONDS = 30;

    /** The baud rate to use. */
    public static final int BAUD = 38400;

    /** The input stream */
    final protected DataInputStream is;

    /** The output stream */
    final protected DataOutputStream os;

    /** A flag to control debugging output. */
    protected boolean debug = true;

    /** The chosen Port Identifier */
    private final CommPortIdentifier commPortIdentifier;

    /** The chosen Port itself */
    final CommPort commPort;

    /** List of port names */
    HashMap<String, CommPortIdentifier> portNames;

    @SuppressWarnings("unchecked")
    protected void loadPortList()
    {
        final Enumeration<CommPortIdentifier> commPortIdentifiers = CommPortIdentifier
                .getPortIdentifiers();

        while (commPortIdentifiers.hasMoreElements())
        {
            final CommPortIdentifier cpi = commPortIdentifiers.nextElement();

            System.out.println("Found port: " + cpi.getName());

            portNames.put(cpi.getName(), cpi);

            if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
            }
        }
    }

    public SerialChannel(final String portName) throws IOException, NoSuchPortException,
            PortInUseException, UnsupportedCommOperationException, CommunicationException
    {
        portNames = new HashMap<String, CommPortIdentifier>();

        loadPortList();

        commPortIdentifier = (CommPortIdentifier) portNames.get(portName);

        if (commPortIdentifier != null
                && commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
        {
            if (debug)
            {
                System.out.println("Trying to open comm port " + commPortIdentifier.getName()
                        + "...");
            }

            commPort = commPortIdentifier.open(APPLICATION_NAME, TIMEOUT_IN_SECONDS
                    * MILLISECONDS_PER_SECOND);
            final SerialPort serialPort = (SerialPort) commPort;

            // set up the serial port
            serialPort.setSerialPortParams(BAUD, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            //serialPort.enableReceiveTimeout(2000);
        }
        else
        {
            throw new CommunicationException("Specified port must be a valid serial port: "
                    + portName);
        }

        if (debug)
        {
            System.out.println("Port " + commPortIdentifier.getName() + " succesfuly open");
        }

        // Get the input and output streams
        // Printers can be write-only
        try
        {
            is = new DataInputStream(commPort.getInputStream());
        }
        catch (IOException ioExc)
        {
            System.err.println("Can't open input stream: write-only");
            throw ioExc;
        }

        os = new DataOutputStream(commPort.getOutputStream());
    }

    public DataOutputStream getOutputStream()
    {
        return os;
    }

    public DataInputStream getInputStream()
    {
        return is;
    }
}
