/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.web.struts.form;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for replacing xml entity references in order to avoid xml parsing
 * errors.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class XmlTagTransformer {

    /** The static logger. */
    protected static Log s_logger = LogFactory.getLog(XmlTagTransformer.class);
    
    /**
     * This method replaces xml entity references by the corresponding
     * expression.
     * 
     * @param input the original string
     * @return the string with the replacements
     */
    public String transform(String input) {
        
        if (input != null) {
            input = input.replaceAll("&", "&amp;");
            input = input.replaceAll("<", "&lt;");
            input = input.replaceAll(">", "&gt;");
            input = input.replaceAll("'", "&apos;");
            input = input.replaceAll("\"", "&quot;");
        }
        
        return input;
    }
}