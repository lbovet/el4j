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
package ch.elca.el4j.gui.swing.exceptions;

/**
 * The interface Exception handlers must implement. It has two methods that
 * contain the logic for each of its 2 concerns: (1) recognizing Exceptions
 * it can handle and (2) handling those Exceptions appropriately.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public interface Handler {
    /**
     * @param e    the thrown exception
     * @return     <tt>true</tt> if this Handler can handle the given
     *             Exception; <tt>false</tt> otherwise.
     */
    public boolean recognize(Exception e);

    /**
     * React to the given Exception in any way seen fit. This may include
     * notifying the user, writing to a log, or any other valid logic.
     * 
     * @param e    the thrown exception
     */
    public void handle(Exception e);
}