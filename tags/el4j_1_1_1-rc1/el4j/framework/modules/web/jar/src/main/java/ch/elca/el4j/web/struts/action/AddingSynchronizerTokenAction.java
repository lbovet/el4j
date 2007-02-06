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

package ch.elca.el4j.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.elca.el4j.web.struts.form.SynchronizerForm;

/**
 * This action is used to save a synchronizer token. The synchronizer token
 * pattern is mainly used to prevent double submission of a form.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public class AddingSynchronizerTokenAction extends Action {

    /** The logger. */
    protected final Log s_logger = LogFactory.getLog(getClass());
    
    /**
     * {@inheritDoc}
     */
    public ActionForward execute(ActionMapping actionMapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        
        SynchronizerForm synchronizerForm = (SynchronizerForm) form;
        
        // save the token in order to avoid double submission
        saveToken(request);
        
        // set the token in the jsp page manually since Struts does not write
        // in xml pages. The jsp page to which "success" is leading must contain
        // a hidden field with name="org.apache.struts.taglib.html.TOKEN" whose
        // value is refering to synchronizerForm.token
        String token = (String) request.getSession().getAttribute(
                Globals.TRANSACTION_TOKEN_KEY);
        synchronizerForm.setToken(token);
        
        return actionMapping.findForward("success");
        
    }

}