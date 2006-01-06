/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.remoting.protocol.ejb.exception;

import java.rmi.RemoteException;

/**
 * This exception replaces java.rmi.RemoteException.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 */
public class RemoteRTException extends BaseRTException {

    /**
     * Constructor with exception.
     *
     * @param re the mother RemoteException
     */
    public RemoteRTException(RemoteException re) {
        super("Remote exception {0} with message :\"{1}\"",
              new Object[] {re.getClass().getName(),
                            re.getMessage()},
              re);
    }

    /**
     * Package private method to construct an exception when only the
     * original class name and stack trace are available.
     */
    RemoteRTException(String className, String stackTrace) {
        //the message and stack trace formatting is made to be symmetrical
        //and easy readable
        super("Remote exception {0} with message/stacktrace:\n"
                + "\"\n{1}" + /*assume \n here*/ "\"\n"
                + "local stacktrace: ", /*assume \n here*/
            new Object[] {className, stackTrace},
            null);
    }
}
