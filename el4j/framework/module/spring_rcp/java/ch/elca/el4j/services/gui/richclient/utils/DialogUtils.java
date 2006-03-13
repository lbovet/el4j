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
package ch.elca.el4j.services.gui.richclient.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Util class for dialog boxes.
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
public class DialogUtils {
    /**
     * Is the property for dialog messages.
     */
    public static final String DIALOG_MESSAGE_PROPERTY = "message";
    
    /**
     * Is the property for dialog titles.
     */
    public static final String DIALOG_TITLE_PROPERTY = "title";
    
    /**
     * Default error code.
     */
    public static final String DEFAULT_ERROR_CODE = "Exception";
    
    /**
     * Multiplicity code for zero beans.
     */
    public static final String MULTIPLICITY_ZEROBEANS = "zerobeans";
    
    /**
     * Multiplicity code for one bean.
     */
    public static final String MULTIPLICITY_ONEBEAN = "onebean";

    /**
     * Multiplicity code for multiple beans.
     */
    public static final String MULTIPLICITY_MULTIPLEBEANS = "multiplebeans";

    /**
     * Default constructor hidden.
     */
    protected DialogUtils() { }
    
    /**
     * Shows an error message dialog with the given message and title, blocking
     * the given parent component.
     * 
     * @param message
     *            Is the dialog message.
     * @param title
     *            Is the dialog title.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String message, String title,
        Component parentComponent) {
        JOptionPane.showMessageDialog(parentComponent, message, 
            title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an error message dialog with the given message and title, blocking
     * the given parent component.
     * 
     * @param titleMessageObject
     *            Contains the title and the message.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(TitleMessage titleMessageObject,
        Component parentComponent) {
        Reject.ifNull(titleMessageObject);
        showErrorMessageDialog(titleMessageObject.getMessage(), 
            titleMessageObject.getTitle(), parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param errorCode
     *            Is the error code for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     * @param appendix
     *            Will be appended to the code in every case.
     * @param showExceptionDetails
     *            If stack trace of given exception should be appended on
     *            message.
     * @param multiplicity
     *            Is used to show a specific message according to the
     *            multiplicity.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, String errorCode, Exception e, String appendix,
        boolean showExceptionDetails, int multiplicity,
        Component parentComponent) {
        
        String message = null;
        String title = null;
        boolean unusableTexts = true;
        
        if (StringUtils.hasText(errorCode)) {
            String code = errorCode;
            if (StringUtils.hasText(appendix)) {
                code += "." + appendix;
            }
            String decoratedCode 
                = decorateCode(code, multiplicity);
            message = MessageUtils.getMessage(parentSchema, schema, 
                decoratedCode + "." + DIALOG_MESSAGE_PROPERTY);
            title = MessageUtils.getMessage(parentSchema, schema, 
                decoratedCode + "." + DIALOG_TITLE_PROPERTY);

            unusableTexts = areMessageAndTitleNotUsable(message, title);
            if (unusableTexts && !code.equals(decoratedCode)) {
                message = MessageUtils.getMessage(parentSchema, schema, 
                    code + "." + DIALOG_MESSAGE_PROPERTY);
                title = MessageUtils.getMessage(parentSchema, schema, 
                    code + "." + DIALOG_TITLE_PROPERTY);
                unusableTexts = areMessageAndTitleNotUsable(message, title);
            }
        }
        if (unusableTexts && e != null) {
            Class exceptionClass = e.getClass();
            while (Exception.class.isAssignableFrom(exceptionClass)
                && unusableTexts) {
                TitleMessage titleMessageObject = getTitleMessageByException(
                    parentSchema, schema, exceptionClass, appendix, 
                    multiplicity);
                if (titleMessageObject != null) {
                    message = titleMessageObject.getMessage();
                    title = titleMessageObject.getTitle();
                    unusableTexts = areMessageAndTitleNotUsable(message, title);
                }
                exceptionClass = exceptionClass.getSuperclass();
                // TODO Go through the interfaces of execptions too.
            }
            
            
            if (unusableTexts) {
                title = ClassUtils.getShortName(e.getClass());
                message = e.getLocalizedMessage();
            }
            
            if (showExceptionDetails) {
                String stackTrace = ExceptionUtils.getStackTrace(e);
                if (message != null) {
                    message += "\n\n";
                }
                message += stackTrace;
            }
        }
        showErrorMessageDialog(message, title, parentComponent);
    }
    
    /**
     * Returns the title-message-object for the given exception or 
     * <code>null</code> if title or message is not usable.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param exceptionClass
     *            Is the exception class the message dialog is for.
     * @param appendix
     *            Will be appended to the code in every case.
     * @param multiplicity
     *            Is used to show a specific message according to the
     *            multiplicity.
     * @return Returns the title-message-object for the given exception or 
     * <code>null</code> if title or message is not usable.
     */
    public static TitleMessage getTitleMessageByException(String parentSchema, 
        String schema, Class exceptionClass, String appendix, 
        int multiplicity) {
        
        String shortExceptionName = ClassUtils.getShortName(exceptionClass);
        String code = shortExceptionName;
        if (StringUtils.hasText(appendix)) {
            code += "." + appendix;
        }
        String decoratedCode = decorateCode(code, multiplicity);
        String message = MessageUtils.getMessage(parentSchema, schema, 
            decoratedCode + "." + DIALOG_MESSAGE_PROPERTY);
        String title = MessageUtils.getMessage(parentSchema, schema, 
            decoratedCode + "." + DIALOG_TITLE_PROPERTY);
        
        boolean unusableTexts = areMessageAndTitleNotUsable(message, title);
        if (unusableTexts && !code.equals(decoratedCode)) {
            message = MessageUtils.getMessage(parentSchema, schema, 
                decoratedCode + "." + DIALOG_MESSAGE_PROPERTY);
            title = MessageUtils.getMessage(parentSchema, schema, 
                decoratedCode + "." + DIALOG_TITLE_PROPERTY);
            unusableTexts = areMessageAndTitleNotUsable(message, title);
        }
        
        if (unusableTexts) {
            return null;
        } else {
            TitleMessage result = new TitleMessage();
            result.setMessage(message);
            result.setTitle(title);
            return result;
        }
    }
    
    /**
     * @param message Is the message of a dialog.
     * @param title Is the title of a dialog.
     * @return Returns <code>true</code> if message and title are not usable.
     */
    protected static boolean areMessageAndTitleNotUsable(
        String message, String title) {
        return message == null || title == null; 
    }
    
    /**
     * @param titleMessageObject Contains the title and the message.
     * @return Returns <code>true</code> if message and title are not usable.
     */
    protected static boolean areMessageAndTitleNotUsable(
        TitleMessage titleMessageObject) {
        return titleMessageObject == null 
            || areMessageAndTitleNotUsable(titleMessageObject.getMessage(), 
                titleMessageObject.getTitle()); 
    }

    /**
     * Decorates the code by considering the muliplicity.
     * 
     * <dl>
     * <dt>multiplicity &lt; 0</dt>
     *   <dd>decoratedCode = code</dd>
     * <dt>multiplicity == 0</dt>
     *   <dd>decoratedCode = code + "." + MULTIPLICITY_ZEROBEANS</dd>
     * <dt>multiplicity == 1</dt>
     *   <dd>decoratedCode = code + "." + MULTIPLICITY_ONEBEAN</dd>
     * <dt>multiplicity > 1</dt>
     *   <dd>decoratedCode = code + "." + MULTIPLICITY_MULTIPLEBEANS</dd>
     * </dl>
     * 
     * @param code Is the one to decorate.
     * @param multiplicity Is used to decide how to decorate the code.
     * @return Returns the decorated code by considering the muliplicity.
     */
    public static String decorateCode(String code, int multiplicity) {
        String decoratedCode;
        if (multiplicity < 0) {
            decoratedCode = code;
        } else if (multiplicity == 0) {
            decoratedCode = code + "." + MULTIPLICITY_ZEROBEANS;
        } else if (multiplicity == 1) {
            decoratedCode = code + "." + MULTIPLICITY_ONEBEAN;
        } else {
            decoratedCode = code + "." 
                + MULTIPLICITY_MULTIPLEBEANS;
        }
        return decoratedCode;
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param errorCode
     *            Is the error code for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     * @param showExceptionDetails
     *            If stack trace of given exception should be appended on
     *            message.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, String errorCode, Exception e,
        boolean showExceptionDetails, Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, errorCode, e, null,
            showExceptionDetails, -1, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param errorCode
     *            Is the error code for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, String errorCode, Exception e,
        Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, errorCode, e, 
            false, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param errorCode
     *            Is the error code for text lookup.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, String errorCode, Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, errorCode, null, false,
            parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param errorCode
     *            Is the error code for text lookup.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(
        String errorCode, Component parentComponent) {
        showErrorMessageDialog(null, null, errorCode, null, false, 
            parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     *            message.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, Exception e, Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, null, e, null,
            false, -1, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     *            message.
     * @param multiplicity
     *            Is used to show a specific message according to the
     *            multiplicity.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, Exception e, int multiplicity,
        Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, null, e, null,
            false, multiplicity, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     *            message.
     * @param appendix
     *            Will be appended to the code in every case.
     * @param multiplicity
     *            Is used to show a specific message according to the
     *            multiplicity.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, Exception e, String appendix, int multiplicity,
        Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, null, e, appendix,
            false, multiplicity, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param parentSchema
     *            Is the parent schema for text lookup.
     * @param schema
     *            Is the current schema for text lookup.
     * @param e
     *            Is the exception the message dialog is for.
     *            message.
     * @param appendix
     *            Will be appended to the code in every case.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(String parentSchema, 
        String schema, Exception e, String appendix,
        Component parentComponent) {
        showErrorMessageDialog(parentSchema, schema, null, e, appendix,
            false, -1, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param e
     *            Is the exception the message dialog is for.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialog(
        Exception e, Component parentComponent) {
        showErrorMessageDialog(null, null, null, e, false, parentComponent);
    }
    
    /**
     * Shows an error message dialog with the given parameters.
     * 
     * @param e
     *            Is the exception the message dialog is for.
     * @param parentComponent
     *            Is the component to display the dialog over it.
     */
    public static void showErrorMessageDialogDetails(
        Exception e, Component parentComponent) {
        showErrorMessageDialog(null, null, null, e, true, parentComponent);
    }
    
    /**
     * Small data container for a title and a message text.
     * 
     * @author Martin Zeltner (MZE)
     */
    public static class TitleMessage {
        /**
         * Is the message.
         */
        private String m_message;
        
        /**
         * Is the title.
         */
        private String m_title;

        /**
         * @return Returns the message.
         */
        public final String getMessage() {
            return m_message;
        }

        /**
         * @param message Is the message to set.
         */
        public final void setMessage(String message) {
            m_message = message;
        }

        /**
         * @return Returns the title.
         */
        public final String getTitle() {
            return m_title;
        }

        /**
         * @param title Is the title to set.
         */
        public final void setTitle(String title) {
            m_title = title;
        }
    }
}
