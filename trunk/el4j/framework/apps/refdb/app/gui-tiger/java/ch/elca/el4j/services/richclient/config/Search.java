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

import java.util.Map;

import org.springframework.richclient.application.ViewDescriptor;

import ch.elca.el4j.services.dom.info.EntityType;
import ch.elca.el4j.services.dom.info.Property;
import ch.elca.el4j.services.gui.search.AbstractSearchItem;
import ch.elca.el4j.services.gui.search.ComparisonSearchItem;
import ch.elca.el4j.services.gui.search.LikeSearchItem;
import ch.elca.el4j.services.i18n.SimpleFieldFaceSource;
import ch.elca.el4j.services.richclient.components.SearchView;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.collections.helpers.Function;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;
import ch.elca.el4j.util.registy.impl.StringMapBackedRegistry;


/**
 * A search form for entities of a specific type.
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
// TODO: Why is this a View?
public class Search extends AbstractGenericView {
    /** holds the query represented by this search form. */
    public final SettableObservableValue<QueryObject> query;
    
    /** the searchable properties. */
    @Preliminary
    public DisplayablePropertyList properties;
    
    /** @param c .*/
    public Search(Class c) {
        super(c);
        query = new SettableObservableValue<QueryObject>(new QueryObject(c));
        properties = new EditablePropertyList(EntityType.get(c));
    }

    /***/
    class GenericComponent extends SearchView {
        /***/
        GenericComponent() {
            setSearchItems(
                properties.m_eprops.filtered(DisplayablePropertyList.s_visibles).mapped(
                    new Function<EditableProperty, AbstractSearchItem>() {
                        public AbstractSearchItem apply(EditableProperty d) {
                            return getDefaultSearchItem(d.prop);
                        }
                    }
                ).toArray(AbstractSearchItem.class)
            );
        }

        /** @param prop .
         * @return .
         **/
        AbstractSearchItem getDefaultSearchItem(Property prop) {
            // Kludge: Autodetect proper search item. 
            // Check correctness of mapping.
            AbstractSearchItem i;
            Class pt = prop.type;
            if (pt.equals(String.class)) {
                i = new LikeSearchItem();
            } else {
                // Kludge: box primitive types to satisfy DynaBean
                if (pt.isPrimitive()) {
                    if (pt.equals(int.class)) {
                        pt = Integer.class;
                    } else {
                        String n = pt.getName();
                        try {
                            pt = Class.forName(
                                "java.lang."
                                + n.substring(0, 1).toUpperCase()
                                + n.substring(1)
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                
                ComparisonSearchItem csi = new ComparisonSearchItem();
                csi.setType(pt);
                i = csi;
            }
            i.setTargetProperty(prop.name);
            i.setTargetBeanClass(m_type.clazz);
                        
            return i;
        }
        
        /**
         * {@inheritDoc}
         * install custom message provider.
         */
        @Override
        protected void initializeFormModel() {
            super.initializeFormModel();
            m_formModel.setFieldFaceSource(
                SimpleFieldFaceSource.instance()            
            );
        }

        /** {@inheritDoc} */
        @Override
        protected void onQueryChanged(QueryObject newQuery) {
            query.set(newQuery);
        }

        /** {@inheritDoc} */
        @Override
        protected Map<String, Map<String, Object>> buildPropertyUserMetadata() {
            Map<String, Map<String, Object>> pum 
                = super.buildPropertyUserMetadata();
            for (AbstractSearchItem si : getSearchItems()) {
                StringMapBackedRegistry reg = new StringMapBackedRegistry(
                    pum.get(getPropertyName(si))
                );
                reg.set(AbstractGenericView.class, Search.this);
            }
            return pum;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected <T> ViewDescriptor createDescriptor(Class<T> clazz) {
        return configure(new Descriptor(new GenericComponent()));
    }
}
