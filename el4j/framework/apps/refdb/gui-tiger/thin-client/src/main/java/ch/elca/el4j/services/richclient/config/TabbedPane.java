/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.richclient.config;

import org.springframework.richclient.application.PageComponentDescriptor;

import ch.elca.el4j.services.richclient.context.AwakingContext;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;




@Preliminary
public class TabbedPane extends AbstractComponent {
    ExtendedWritableList<AbstractComponent> components 
        = new ExtendedArrayList<AbstractComponent>();
    
    /** {@inheritDoc} */
    @Override
    void init(AwakingContext context) {
        Reject.ifEmpty(components);
    }

    /** {@inheritDoc} */
    @Override
    PageComponentDescriptor getDescriptor(AwakingContext awaker) {
        // TODO Auto-generated method stub
        return null;
    }
}
