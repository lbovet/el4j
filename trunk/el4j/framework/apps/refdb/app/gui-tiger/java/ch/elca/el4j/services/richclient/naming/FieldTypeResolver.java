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
package ch.elca.el4j.services.richclient.naming;

import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.dom.info.EntityType;
import ch.elca.el4j.services.gui.context.support.MessageRewriter;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;

/**
 * Rewriting rule replacing a field's qualified name with the name of its type.
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
public class FieldTypeResolver implements MessageRewriter.Rule,
                                          InitializingBean {
    
    /** The list of packages containing DOM types (fully qualified names). */
    String[] m_domPackages;
    
    /**
     * Constructor.
     * @param domPackages see {@link #m_domPackages}
     */
    public void setDomPackages(String... domPackages) {
        m_domPackages = domPackages;
    }
    
    /**
     * Accepts keys of the form
     * 
     * <pre>Dom.field.<i>typename</i>.<i>fieldname</i>.type</pre>
     * 
     * and replaces them with the type of field {@code fieldname} declared in 
     * {@code typename}.
     */
    @ImplementationAssumption("There is only one dom package.")
    public StringBuffer rewrite(String key, Object[] arguments,
                                StringBuffer target) {
        
        String[] keyparts = MessageRewriter.qualifiers(key);

        // Checkstyle: MagicNumber off
        if (keyparts.length == 5 
            && keyparts[0].equals("Dom")
            && keyparts[1].equals("field")
            && keyparts[4].equals("type")
        ) {
            String tn = keyparts[2];
            String fn = keyparts[3];
            
            try {
                target.append(
                    EntityType.get(
                        Class.forName(m_domPackages[0] + "." + tn)
                    ).find(fn).type.getSimpleName()
                );
                return target;
            } catch (Exception e) {
                assert false : e;
                return null;
            }
        } else {
            return null;
        }
        // Checkstyle: MagicNumber on
    }

    /** 
     * Checks that domPackages has been set, throws an exception otherwise.
     */
    public void afterPropertiesSet() throws Exception {
        Reject.ifNull(m_domPackages);
    }
}
