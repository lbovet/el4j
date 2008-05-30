/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.logging.html;

import ch.elca.el4j.maven.logging.AbstractFormattingLogger;

/**
 * Logger that outputs html colors around warn/error messages.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author your David Bernhard (DBD)
 */
public class HtmlLogger extends AbstractFormattingLogger {

    /**
     * Delegating constructor.
     * @param threshold The logger level.
     * @param name The logger name.
     */
    public HtmlLogger(int threshold, String name) {
        super(threshold, name);
    }

    /** {@inheritDoc} */
    protected String getPrefix(int level) {
        switch (level) {
            case LEVEL_WARN: 
                return "<font color=\"orange\">";
            case LEVEL_ERROR: 
                return "<font color=\"red\">";
            case LEVEL_FATAL: 
                return "<font color=\"red\"><b>";
            default: 
                return "";
        }
    }

    /** {@inheritDoc} */
    protected String getSuffix(int level) {
        switch(level) {
            // Fall through intended.
            case LEVEL_WARN:
            case LEVEL_ERROR:
                return "</font>";
            case LEVEL_FATAL:
                return "</b></font>";
            default:
                return "";
        }
    }
    
}
