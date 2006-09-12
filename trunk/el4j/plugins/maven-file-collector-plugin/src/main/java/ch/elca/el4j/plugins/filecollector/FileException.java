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
package ch.elca.el4j.plugins.filecollector;

/**
 * 
 * This exception will be thrown when an abnormal situation is detected during
 * file access or modification.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class FileException extends BaseException {
    
    /**
     * Default message if no reason for the exception is provided. Should only
     * be used with a wrapped exception, and only if the wrapped exception
     * contains sufficient information to explain what happened.
     */
    private static final String EXCEPTION_MESSAGE_FILE_SUPPORT
        = "An exception occurred during file access or modification."; 
    
    /**
     * @see BaseException for comment of constructor.
     */
    public FileException(String message) {
        super(message);
    }
    
    /**
     * @see BaseException for comment of constructor.
     */
    public FileException(String message, Object[] parameters) {
        super(message, parameters);
    }
    
    /**
     * @see BaseException for comment of constructor.
     */
    public FileException(String message, Object[] parameters,
            Throwable wrappedException) {
        super(message, parameters, wrappedException);
    }
    
    /**
     * @see BaseException for comment of constructor.
     */
    public FileException(String message, Throwable wrappedException) {
        super(message, wrappedException);
    }
    
    /**
     * Constructor with wrapped exception.
     * 
     * @param wrappedException
     *            The exception that occured during file access or modification.
     */
    public FileException(Throwable wrappedException) {
        this(EXCEPTION_MESSAGE_FILE_SUPPORT, wrappedException);
    }
}
