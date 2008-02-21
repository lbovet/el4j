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

package ch.elca.el4j.seam.generic.errorhandling;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Seam component for generic error handling. This class is used by the custom
 * error page to "translate" the handled exception into a description that can
 * be displayed to a normal user.
 * 
 * <script type="text/javascript"> printFileStatus ("$URL:
 * https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/sandbox/seam-demo/jar/src/main/java/ch/elca/el4j/seam/generic/errorhandling/ErrorResolver.java
 * $", "$Revision$", "$Date: 2008-02-04 16:34:48 +0100 (Mo, 04 Feb 2008)
 * $", "$Author$" ); </script>
 * 
 * @author Frank Bitzer (FBI)
 * @author Stefan Wismer (SWI)
 */
@Name("errorResolver")
@Scope(ScopeType.CONVERSATION)
public class ErrorResolver implements Serializable {

    /**
     * Returns the simple name of the root cause of the specified exception.
     * This is used in error.xhtml and error_??.properties
     * 
     * @param exception    the exception
     * @return             the simple name
     */
    public String getErrorMessage(Exception exception) {

        // get root cause at first
        Throwable ex = exception;
        while (ex.getCause() != null) {
            ex = ex.getCause();
        }

        return ex.getClass().getSimpleName();
    }

}
