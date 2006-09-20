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
package ch.elca.el4j.services.richclient.config;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.View;
import org.springframework.richclient.application.ViewDescriptor;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl.AbstractGroupPageComponentDescriptor;
import ch.elca.el4j.services.gui.richclient.views.descriptors.impl.AbstractViewDescriptor;
import ch.elca.el4j.services.i18n.MessageProvider;
import ch.elca.el4j.services.richclient.context.AwakingContext;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.dom.reflect.EntityType;




/**
 * A generic view is a generic component that displays one or more beans of the
 * given type.
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
public abstract class AbstractGenericView extends AbstractComponent {
    /** this component's schema, i.e. its identifier for message lookups. */
    public String schema = defaultSchema();
    
    /** the type of entities displayed by this view. */
    EntityType m_type;
    
    /**
     * The awaking context to be used to waken beans.
     */
    AwakingContext m_awaker;
    
    /***/
    ViewDescriptor m_descriptor;
    
    /**
     * creates a view for entities represented by type {@code c}.
     * @param c .
     */
    AbstractGenericView(Class<?> c) {
        Reject.ifNull(c);
        m_type = EntityType.get(c);        
    }
    
    
    /** {@inheritDoc} */
    @Override
    void init(AwakingContext context) {
        m_awaker = context;
        m_descriptor = createDescriptor(m_type.clazz);
    }
    
    /** labels and wakens <code>d</code>. Also returns it (for convenience)
     * @param d .
     * @param <T> .
     * @return .
     **/
    protected <T extends AbstractGroupPageComponentDescriptor>
    T configure(T d) {
        d.setTitle(
            MessageProvider.instance().forView(schema, m_type).get("title")
        );
        d.setImage(m_awaker.getImages().getImage(m_type, schema, "image"));
        m_awaker.awaken(d);
        return d;
    }
    
    /**
     * A descriptor returning a pre-created view.
     */
    class Descriptor extends AbstractViewDescriptor {
        /***/
        protected View m_view;
        
        /**
         * creates a descriptor for {@code v}. {@code v} is awakened.
         * @param v .
         */
        public Descriptor(View v) {
            v.setDescriptor(this);
            m_awaker.awaken(v);
            m_view = v;            
        }
        
        /**
         * returns the view.
         * {@inheritDoc}
         */
        public PageComponent createPageComponent() {
            return m_view;
        }
    }
    
    /** creates and returns the descriptor holding the View represented by 
     * this object.
     * @param clazz the domain class for this view's entity type.
     * @return . */
    protected abstract <T> ViewDescriptor createDescriptor(Class<T> clazz);
    
    /** {@inheritDoc} */
    ViewDescriptor getDescriptor(AwakingContext context) {
        return m_descriptor;
    }
}
