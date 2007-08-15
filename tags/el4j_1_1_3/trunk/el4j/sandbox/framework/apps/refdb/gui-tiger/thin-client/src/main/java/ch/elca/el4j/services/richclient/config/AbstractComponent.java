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
import ch.elca.el4j.util.observer.ObservableValue;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;



/**
 * Abstract superclass for page components.
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
public abstract class AbstractComponent {
    /** is this commponent presently visible? not yet implemented */
    public ObservableValue<Boolean> visible 
        = new SettableObservableValue<Boolean>(Boolean.TRUE);
    
    /**
     * Initializes and checks configuration consistency of this component.
     * May also pre-create descriptors and components.
     * @param context the Spring context surrogate to be used 
     *                for bean configuration
     */
    abstract void init(AwakingContext context);
    
    /**
     * @param awaker the Awaker to be used to awake created Spring beans
     * @return this component's descriptor
     */
    abstract PageComponentDescriptor getDescriptor(AwakingContext awaker);
    
    /** @return the default schema for this component's message lookups. */
    protected String defaultSchema() {
        Class<?> cc = getClass();
        while (cc.getEnclosingClass() != null) {
            cc = cc.getSuperclass();
        }
        return cc.getSimpleName();
    }
}