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
package ch.elca.el4j.seam.taghandler;

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
* 
*
* @author  Baeni Christoph (CBA) (APL licensed code from ninthavenue.com.au)
*/
/**
 * An implementation of c:set which evaluates(!!) the value expression when
 * the variable is set, creating a facelet-scoped attribute.
 * 
 * This code is derived from code taken from "ninth avenue"'s
 * "Seamless Java EE Utility Library"
 * The code was licensed under the Apache License Version 2.0.
 * See http://www.ninthavenue.com.au/extras/seamless for details
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Baeni Christoph (CBA) (APL licensed code from ninthavenue.com.au)
 */
public class SetTagHandler extends TagHandler {
    /**
     * The variable.
     */
    private final TagAttribute m_var;

    /**
     * The value to assign.
     */
    private final TagAttribute m_value;

    /**
     * @param config    the tag configuration
     */
    public SetTagHandler(TagConfig config) {
        super(config);
        m_value = this.getRequiredAttribute("value");
        m_var = this.getRequiredAttribute("var");
    }

    /** evaluate and set the attribute in the facelet scope */
    /** {@inheritDoc} */
    public void apply(FaceletContext ctx, UIComponent parent) {
        ctx.setAttribute(m_var.getValue(ctx), m_value.getObject(ctx));
    }
}