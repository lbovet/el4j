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
package ch.elca.el4j.services.dom.info;

import java.lang.reflect.AnnotatedElement;

import ch.elca.el4j.services.dom.annotations.Description;


/**
 * Utility functions for reflecting the ch.elca.el4j.services.dom.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
final class InternalUtil {
    /** 
     * does nothing useful, just like the checkstyle directive that requires
     * this comment.
     */
    private InternalUtil() { }
    
    // TODO: inherit annotations?
    static String getDescription(AnnotatedElement a) {
        Description d = a.getAnnotation(Description.class);
        return d != null ? d.value() : null;
    }
    
    /** 
     * @param as the elements to check. nulls are permitted.
     * @return the value of the first description annotation on elements of 
     * {@code as} or null, if no such annotation is present. 
     */
    static String getDescription(AnnotatedElement... as) {
        String d;
        for (AnnotatedElement a : as) {
            if (a != null) {
                d = getDescription(a);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }
}
