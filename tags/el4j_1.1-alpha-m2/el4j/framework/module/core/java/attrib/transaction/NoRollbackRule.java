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
package attrib.transaction;

import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;

/**
 * This class is a no rollback rule attribute. This must be used in combination
 * with a <code>*RuleBased</code> attribute to work.
 * 
 * <script type="text/javascript">printFileStatus
 * ("$URL$",
 *  "$Revision$",
 *  "$Date$", 
 *  "$Author$" ); </script>
 * 
 * @author Martin Zeltner (MZE)
 */
public class NoRollbackRule extends NoRollbackRuleAttribute {

    /**
     * Constructor.
     * 
     * @param clazz
     *            Is the class of a throwable. When this kind of throwable will
     *            be thrown, then the current transaction should not be rolled
     *            back.
     */
    public NoRollbackRule(Class clazz) {
        super(clazz);
    }
}