/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tcpred;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ConcurrentModificationException;
import java.util.Date;

//Checkstyle: UncommentedMain off

/**
 * This class is a simple user interface for using the tcp forwarder.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Florian Suess (FLS)
 * @author Alex Mathey (AMA)
 */
public class TcpForwarderTool {

    /**
     * Delay between the single test steps (in milliseconds).
     */
    static final int DELAY = 1000;

    /**
     * New input port -> Forwarder between INPUT_PORT and DEST_PORT.
     */
    static final int INPUT_PORT = 6789;

    /**
     * Original port of the application to test (Derby-DB: 1527).
     */
    static final int DEST_PORT = 1521;

    /**
     * Destination URL.
     */
    static final String DEST_URL = "tulipe.elca.ch";

    /**
     * Help variable to test if an exception occured.
     */
    int m_gotException = 0;

    /**
     * Hide default constructor.
     */
    protected TcpForwarderTool() { }

    /**
     * User interface to plug / unplug the described connection.
     *
     * @param args Starting arguments, currently no arguments supportet.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        /**
         * User input.
         */
        String input;

        /**
         * Status of the connection (0: unplugged, 1: connected).
         */
        int status = 0;

        Date dt = new Date();

        BufferedReader userIn = new BufferedReader(new InputStreamReader(
            System.in));

        SocketAddress target = new InetSocketAddress(Inet4Address
            .getByName(DEST_URL), DEST_PORT);
        TcpForwarder ti = new TcpForwarder(INPUT_PORT, target);

        System.out.println("Forwarder started...");

        status = 1;

        Thread.sleep(DELAY);

        while (true) {

            dt.getTime();

            if (status == 1) {
                System.out.println("\n\n[" + dt.getHours() + ":"
                    + dt.getMinutes() + ":" + dt.getSeconds()
                    + "] Connection between ports " + INPUT_PORT + " and "
                    + DEST_PORT + " is currently up.");
            } else {
                System.out.println("\n\n[" + dt.getHours() + ":"
                    + dt.getMinutes() + ":" + dt.getSeconds()
                    + "] Connection between ports " + INPUT_PORT + " and "
                    + DEST_PORT + " is currently down.");
            }

            System.out.println("Select Action:  1: unplug connection\n"
                + "                2: plug connection\n"
                + "                3: abort");
            input = userIn.readLine();
            System.out.println("\nyour choice: " + input);

            if (input.equals("3")) {
                System.out.println("exiting...");
                System.exit(1);
                break;
            }
            if (input.equals("1")) {
                if (status == 0) {
                    System.out.println("Connection already unplugged, "
                        + "try again...");
                    continue;
                }
                try {
                    ti.unplug();
                } catch (ConcurrentModificationException e) {
                    System.out.println("Caught Exception:" + e);
                }
                status = 0;
                System.out.println("Connection unplugged");
                Thread.sleep(DELAY);
                continue;
            }
            if (input.equals("2")) {
                if (status == 1) {
                    System.out.println("Connection already plugged, "
                        + "try again...");
                    continue;
                }
                status = 1;
                ti.plug();
                System.out.println("Connection restored");
                Thread.sleep(DELAY);
                continue;
            }
            System.out.println("Illegal input, one more chance");
            Thread.sleep(DELAY);
        }
        System.exit(1);
    }
}

//Checkstyle: UncommentedMain on