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

package ch.elca.el4j.web.struts.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

/**
 * General exception handler for Struts actions. Prints an exception message
 * and the stack trace on the page to which this action is forwarded.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Jacques-Olivier Haenni (JOH)
 */
public class GeneralExceptionHandler extends ExceptionHandler {
    
    /** The static logger. */
    protected static Log s_logger = LogFactory.
        getLog(GeneralExceptionHandler.class);
    
    /**
     * {@inheritDoc}
     */
    public ActionForward execute(Exception exception, ExceptionConfig config,
            ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        
        // Construct the exception message to be printed on the error page
        String message = exception.getMessage();
        List messages = new ArrayList();
        if (message != null) {
            StringTokenizer tokenizer = new StringTokenizer(message, "\n\r");
            while (tokenizer.hasMoreTokens()) {
                messages.add(tokenizer.nextToken());
            }
        }

        // Get and save the stack trace in a printable form 
        Throwable t = exception;
        StringBuffer sb = new StringBuffer();
        while (t != null) {
            sb.append(t.toString() + "\n");
            StackTraceElement[] stacktrace = t.getStackTrace();
            for (int i = 0; i < stacktrace.length; i++) {
                sb.append(stacktrace[i].toString() + "\n");
            }
            t = t.getCause();
            if (t != null) {
                sb.append("Caused by:\n");
            }
        }

        // Add the exception message and the stack trace to the request
        // to have them available on the error page
        request.setAttribute("exceptionMessage", messages);
        request.setAttribute("exceptionText", sb.toString());

        return mapping.findForward("error");
    }
}