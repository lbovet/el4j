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

import java.io.IOException;

import javax.faces.application.ViewExpiredException;
import javax.servlet.ServletException;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.SessionException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sun.facelets.tag.TagException;


/**
 * Seam component for generic error handling. This class is used by
 * the custom error page to "translate" the handled exception into
 * a description that can be displayed to a normal user.
 * 
 * <script type="text/javascript"> 
 * printFileStatus
 *      ("$URL$", 
 *      "$Revision$", "$Date$", "$Author$" ); </script>
 * 
 * @author Frank Bitzer (FBI)
 */
@Name("errorResolver")
@Scope(ScopeType.CONVERSATION)
public class ErrorResolver {
	
	/**
	 * Returns a funny text describing the root cause of 
	 * specified exception.
	 * 
	 * Only implemented for some kinds of Exceptions.
	 * 
	 * TODO: use internationalised messages...
	 * 
	 * @param ex
	 * @return
	 */
	public String getErrorMessage(Exception exception){
		
		//get root cause at first
		Throwable ex = exception;
		while (ex.getCause() != null){
			ex = ex.getCause();
		}
		
		String result = "Unknown problem.";
		
		if (ex.getClass().equals(IOException.class)) {
			
			result = "An input/output operation failed.";
		}
		
		if (ex.getClass().equals(NullPointerException.class)) {
			
			result = "An internal object could not be found or created.";
		}
		
		if (ex.getClass().equals(AssertionError.class)) {
			
			result = "An internal assertion was violenced.";
		}
		
		if (ex.getClass().equals(HibernateException.class)) {
			
			result = "An internal database error occured.";
		}
		
		if (ex.getClass().equals(JDBCException.class)) {
			
			result = "An error in a database query was detected.";
			
		}
		
		if (ex.getClass().equals(ViewExpiredException.class)) {
			
			result = "Your view expired. Please reload this page.";
			
		}
		
		if (ex.getClass().equals(SessionException.class)) {
			
			result = "Your database session expired. "
				+ "Please reload this page.";
			
		}
		
		if (ex.getClass().equals(ServletException.class)) {
			
			result = "Your request could not be processed due to "
				+ "an internal servlet error.";
			
		}
		if (ex.getClass().equals(TagException.class)) {
			
			result = "Your request could not be processed due to "
				+ "an unknown or wrong tag in this page.";
			
		}
		
		if (ex.getClass().equals(StaleObjectStateException.class)) {
			
			result = "You tried to edit or delete an object"
				+ ", but its state has changed in meantime.";
			
		}
		
		
		return result;
		
	}
	
	

}
