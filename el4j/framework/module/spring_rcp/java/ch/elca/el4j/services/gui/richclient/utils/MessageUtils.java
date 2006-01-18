/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.utils;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.util.StringUtils;

/**
 * Util class to get application messages.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public final class MessageUtils {
    /**
     * Hide default constructor.
     */
    private MessageUtils() { }
    
    /**
     * Returns the requested message. Lookup codes are listed in order below:
     * <ol>
     *     <li><code>parentId.messageId</code></li>
     *     <li><code>messageId</code></li>
     * </ol>
     * 
     * @param parentId Is the id of the parent object
     * @param messageId Is the id of the message
     * @return Returns the looked up message or <code>null</code> if no could 
     *         be found.
     */
    public static String getMessage(String parentId, String messageId) {
        return getMessage(parentId, null, messageId);
    }
    
    /**
     * Returns the requested message. Lookup codes are listed in order below:
     * <ol>
     *     <li><code>parentId.childId.messageId</code></li>
     *     <li><code>childId.messageId</code></li>
     *     <li><code>parentId.messageId</code></li>
     *     <li><code>messageId</code></li>
     * </ol>
     * 
     * 
     * @param parentId Is the id of the parent object
     * @param childId Is the id of the child object
     * @param messageId Is the id of the message
     * @return Returns the looked up message or <code>null</code> if no could 
     *         be found.
     */
    public static String getMessage(String parentId, String childId, 
        String messageId) {
        boolean parentIdUsable = StringUtils.hasText(parentId);
        boolean childIdUsable = StringUtils.hasText(childId);
        boolean messageIdUsable = StringUtils.hasText(messageId);
        
        MessageSourceAccessor messages = getMessageSourceAccessor();
        if (messages == null || !messageIdUsable) {
            return null;
        }
        
        String result = null;
        if (parentIdUsable && childIdUsable && messageIdUsable) {
            String code = parentId + "." + childId + "." + messageId;
            result = getMessage(messages, code);
        }
        if (!StringUtils.hasText(result) && childIdUsable && messageIdUsable) {
            String code = childId + "." + messageId;
            result = getMessage(messages, code);
        }
        if (!StringUtils.hasText(result) && parentIdUsable && messageIdUsable) {
            String code = parentId + "." + messageId;
            result = getMessage(messages, code);
        }
        if (!StringUtils.hasText(result) && messageIdUsable) {
            String code = messageId;
            result = getMessage(messages, code);
        }
        return result;
    }

    /**
     * Get message or <code>null</code>.
     * 
     * @param messages Is the message source accessor.
     * @param code Is to code to lookup.
     * @return Returns looked up message or <code>null</code>.
     */
    private static String getMessage(MessageSourceAccessor messages, 
        String code) {
        String result;
        try {
            result = messages.getMessage(code);
        } catch (NoSuchMessageException e) {
            result = null;
        }
        return result;
    }
    
    /**
     * @return Returns the message source accessor.
     */
    private static MessageSourceAccessor getMessageSourceAccessor() {
        ApplicationServices services = Application.services();
        MessageSourceAccessor messages 
            = services != null ? services.getMessages() : null;
        return messages;
    }
}
