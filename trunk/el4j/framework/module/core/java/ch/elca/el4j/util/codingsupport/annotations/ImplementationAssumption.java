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
package ch.elca.el4j.util.codingsupport.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * States an assumption made by the annotated element's implementation. 
 * Assumptions are statements that are considered true for the purpose of
 * writing code but whose truth is unknown. 
 * 
 * <p>The long term vision is that an automatic tool can process the source
 * code to collect the assumptions made, permitting efficient review in case
 * requirements change. Even in the absence of such a tool, source code
 * information systems such as eclipse can efficiently search assumptions. 
 * 
 * <p> Assumption bear some similarity with "to do" items, but differ
 * in that they are conditional: An assumption only needs to be removed if it is
 * false. In contrast, a "to do" item needs to be taken care of unconditionally.
 * 
 * <p>Assumptions that can easily be formulated as Java expressions should be
 * stated as assertions/preconditions instead (to enable runtime checking).
 * 
 * <p>Assumptions document code, not behavior, and are therefore not included
 * in the API documentation. Like comments, they are discarded by the compiler.
 * 
 * <h4>Example</h4>
 *  <pre>
 *  &#64;assumes("the name uniquely identifies a person") 
 *  Object getKey() {return this.name;}  
 *  </pre>
 * 
 *  <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @see Reject
 * @author Adrian Moos (AMS)
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ImplementationAssumption {
    /** text describing the assumption. */
    String value();
}