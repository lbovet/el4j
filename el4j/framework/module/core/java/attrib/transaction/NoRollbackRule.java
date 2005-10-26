/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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
package attrib.transaction;

import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;

/**
 * This class is a no rollback rule attribute. This must be used in combination
 * with a <code>*RuleBased</code> attribute to work.
 * 
 * <script type="text/javascript">printFileStatus
 * ("$Source$",
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