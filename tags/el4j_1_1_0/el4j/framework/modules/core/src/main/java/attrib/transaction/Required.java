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
package attrib.transaction;

import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

/**
 * This class specifies a required transaction attribute.
 * It must be used standalone.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @deprecated Use Java 5 annotation 
 *             {@link org.springframework.transaction.annotation.Transactional}
 *             instead.
 * @see <a href="http://static.springframework.org/spring/docs/2.0.x/reference/transaction.html#transaction-declarative-annotations">
 *      Using @Transactional</a> (Spring reference doc).
 */
public class Required extends DefaultTransactionAttribute {

    /**
     * Default constructor.
     */
    public Required() {
        super(PROPAGATION_REQUIRED);
    }
}