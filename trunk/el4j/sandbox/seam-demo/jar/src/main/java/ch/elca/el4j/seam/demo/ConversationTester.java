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
package ch.elca.el4j.seam.demo;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

/**
 * This class is a playground to experiment with conversations.
 * (see demos.xhtml) 
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
@Name("conversationTester")
@Scope(ScopeType.CONVERSATION)
public class ConversationTester implements Serializable {
    
    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(ConversationTester.class);
    
    /**
     * Counts number of page hits in a conversation.
     */
    @Out
    private int m_counter = 0;
    
    /**
     * Refresh the page.
     */
    public void refresh() {
        s_logger.debug("refresh");
        m_counter++;
    }
    
    /**
     * Begin a conversation.
     */
    @Begin
    public void begin() {
        s_logger.debug("begin");
        m_counter++;
    }
    
    /**
     * Join a conversation.
     */
    @Begin(join = true)
    public void beginJoin() {
        s_logger.debug("begin(join = true)");
        m_counter++;
    }
    
    /**
     * End a conversation.
     */
    @End
    public void end() {
        s_logger.debug("end");
        m_counter++;
    }

}
