/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.tcpforwarder;

import java.net.InetSocketAddress;

import org.springframework.util.StringUtils;

// Checkstyle: UncommentedMain off
/**
 * Used to start the tcp forwarder from command line.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class TcpForwarderRunner {
    /**
     * Hide default constructor.
     */
    protected TcpForwarderRunner() { }
    
    /**
     * Runs a tcp forwarder on a given port and forwards the tcp messages to 
     * the given target address.
     * 
     * @param args
     *            The first argument is the local listening port and the second
     *            argument is the local port or remote target with its port to
     *            forward to.
     */
    public static void main(String[] args) {
        int listeningPort = 0;
        InetSocketAddress targetAddress = null;
        if (args.length != 2) {
            printUsage();
        } else {
            try {
                listeningPort = Integer.parseInt(args[0]);
                
                String target = args[1];
                String targetHost = null;
                int targetPort = 0;
                if (StringUtils.hasText(target)) {
                    int i = target.indexOf(':');
                    if (i > 0) {
                        targetHost = target.substring(0, i);
                    }
                    if (i < target.length() - 1) {
                        targetPort = Integer.parseInt(target.substring(i + 1));
                    }
                }
                if (targetHost == null) {
                    targetAddress = new InetSocketAddress(targetPort);
                } else {
                    targetAddress 
                        = new InetSocketAddress(targetHost, targetPort);
                }
            } catch (Exception e) {
                e.printStackTrace();
                printUsage();
            }
        }
        
        if (listeningPort <= 0 || targetAddress == null) {
            return;
        }
        
        new TcpForwarder(listeningPort, targetAddress);
    }

    /**
     * Prints the usage of this main class.
     */
    protected static void printUsage() {
        System.out.println(
            "Usage: java TcpForwarderRunner listeningPort targetAddress");
        System.out.println(
            "   Example: java TcpForwarderRunner 6786 tulipe.elca.ch:1521");
    }
}
// Checkstyle: UncommentedMain on
