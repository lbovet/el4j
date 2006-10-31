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
package ch.elca.el4j.util.logging.appenders;

import java.io.IOException;

import org.apache.log4j.varia.ExternallyRolledFileAppender;

/**
 * This class applies more checks on the validityo of logfile path,
 * than the base class. Thefore this is a smarter appender.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 */
public class SmartExternallyRolledFileAppender 
    extends ExternallyRolledFileAppender {

    /**
     * {@inheritDoc}
     */
    public synchronized void setFile(String fileName, boolean append, 
        boolean bufferedIO, int bufferSize) throws IOException {
        
        super.setFile(SmartFileLibrary.createSmartLogPath(fileName), 
            append, bufferedIO, bufferSize);
    }
}
