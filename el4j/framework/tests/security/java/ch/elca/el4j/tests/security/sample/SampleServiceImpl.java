/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.tests.security.sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Sample service implementation.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *   "$Revision$", "$Date$", "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class SampleServiceImpl implements SampleService {
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(SampleServiceImpl.class);
    
    /**
     * {@inheritDoc}
     */
    public int addOne(int i) {
        int result = i + 1;
        s_logger.debug(i + " + 1 = " + result);
        return result;
    }

}
