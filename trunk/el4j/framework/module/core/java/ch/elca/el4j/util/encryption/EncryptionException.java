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

package ch.elca.el4j.util.encryption;

import ch.elca.el4j.core.exceptions.BaseException;

/**
 * Exception that is thrown when an encryption or decryption exception occured.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class EncryptionException extends BaseException {

    /**
     * Default message if no reason for the exception is provided. Should only
     * be used with a wrapped exception, and only if the wrapped exception
     * contains sufficient information to explain what happened.
     */
    private static final String EXCEPTION_MESSAGE_ENCRYPTION_FAILED 
        = "The password could not be encrypted due " 
            + "to a low level exception.{0}";

    /**
     * @see BaseException for comment of constructor.
     */
    protected EncryptionException(String message, Object[] parameters,
            Throwable wrappedException) {
        super(message, parameters, wrappedException);
    }

    /**
     * Constructor with wrapped exception.
     * 
     * @param wrappedException
     *            The exception that occured during encryption.
     */
    public EncryptionException(Throwable wrappedException) {
        this(EXCEPTION_MESSAGE_ENCRYPTION_FAILED, new Object[] {""},
                wrappedException);
    }

    /**
     * Constructor with message.
     * 
     * @param cause
     *            The reason for this exception.
     */
    public EncryptionException(String cause) {
        this(EXCEPTION_MESSAGE_ENCRYPTION_FAILED, new Object[] {cause}, null);
    }
}
