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

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.AbstractFormModel;
import org.springframework.binding.form.support.DefaultFieldMetadata;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.dialog.DialogPage;

import ch.elca.el4j.services.gui.richclient.executors.convenience.AbstractBeanPropertiesExecutor;
import ch.elca.el4j.services.gui.richclient.executors.displayable.ExecutorDisplayable;
import ch.elca.el4j.services.gui.richclient.forms.BeanPropertiesForm;
import ch.elca.el4j.services.gui.richclient.utils.Services;
import ch.elca.el4j.services.gui.richclient.views.DialogPageView;
import ch.elca.el4j.services.i18n.SimpleFieldFaceSource;
import ch.elca.el4j.services.persistence.generic.RepositoryAgency;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;
import ch.elca.el4j.services.persistence.generic.repo.ConvenientGenericRepository;
import ch.elca.el4j.services.persistence.generic.repo.SimpleGenericRepository;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;
import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.collections.ExtendedList;
import ch.elca.el4j.util.dom.reflect.Property;
import ch.elca.el4j.util.observer.ObservableValue;
import ch.elca.el4j.util.observer.ValueObserver;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;






/**
 * a form showing a specific bean's properties, supporting their editing.
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
public class Edit extends AbstractGenericView {
    /** specifies which properties are shown and which are changeable. */
    public EditablePropertyList properties;
    
    /** holds the domain object currently being edited. */
    public ObservableValue<?> current;

    /** the backing executor. */
    Executor m_executor;
    
    /** the list of visible properties. */
    ExtendedList<EditableProperty> m_visible;

    /**
     * creates a new edit view for entities represented by (a subtype of)
     * {@code c}.
     * @param c .
     */
    public Edit(Class<?> c) {
        super(c);
        properties = new EditablePropertyList(m_type);
        current = new SettableObservableValue<Object>(null);
    }
    
    /***/
    class Executor<T> extends AbstractBeanPropertiesExecutor {
        ///////////////////////
        // persistence logic
        ///////////////////////
        
        /** The repository to be used. */
        SimpleGenericRepository<T> m_repository;

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        protected PrimaryKeyObject saveBean(PrimaryKeyObject givenBean) {
            return (PrimaryKeyObject) m_repository.saveOrUpdate((T) givenBean);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected PrimaryKeyObject getBeanByKey(Object key) throws Exception {
            return (PrimaryKeyObject) 
                ((ConvenientGenericRepository<T, Integer>) m_repository)
                    .findById((Integer) key, false);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override public
        boolean onFinishAfterCommit(Object currentBean) throws Exception {
            PrimaryKeyObject givenBean = (PrimaryKeyObject) currentBean;
            PrimaryKeyObject savedBean = saveBean(givenBean);
            FormModel formModel = getFormModel();
            formModel.setFormObject(savedBean);
            return true;
        }
        
        
        ///////////////////
        // execution
        ///////////////////
        
        /** executes on the supplied bean. 
         * @see AbstractBeanPropertiesExecutor#execute() */
        // largely copied from super.execute(), but modified to use the supplied
        // bean or pull one out of the hat
        @ImplementationAssumption(
            "every dom type can be instantiated using a default constructor")
        protected void execute(Object bean) {
            FormModel formModel = getFormModel();
            DialogPage dialogPage = getDialogPage();
            if (formModel == null || dialogPage == null) {
                // Kludge: FormModels must be configured using instances,
                // not classes. We therefore create a temporary instance.
                Object exampleBean = bean;
                if (exampleBean == null) {
                    try {
                        exampleBean = m_type.clazz.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                initializeFormModelAndDialogPage(exampleBean);
            } else {
                formModel.setFormObject(bean);
            }
            
            ExecutorDisplayable displayable = getDisplayable();
            if (!displayable.isConfigured()) {
                displayable.configure(this);
            }
        }

        
        ///////////////////
        // temporary fixes
        ///////////////////
        
        @Override
        public String getSchema() {
            return m_type.name;
        }

        Executor() {
            setId(m_type.name + "Properties");
        }

        
        ///////////////////
        // localization
        ///////////////////
        
        /**
         * {@inheritDoc}
         **/
        @Override @Preliminary @ImplementationAssumption(
            "All form models are subtypes of AbstractFormModel. "
            + "Instances of FieldMetadata are always DefaultFieldMetadata")
        protected void initializeFormModelAndDialogPage(Object bean) {
            super.initializeFormModelAndDialogPage(bean);
            // HACK: implementation dependent downcast. Spring RCP's public API
            // does not permit to configure the localization provider used.
            AbstractFormModel fm = (AbstractFormModel) getFormModel();
            fm.setFieldFaceSource(
                SimpleFieldFaceSource.instance()
            );

            for (DisplayableProperty ep : m_visible) {
                // HACK: Implementation dependent downcast.
                //       The public api does not permit to set user metadata.
                DefaultFieldMetadata metadata 
                    = ((DefaultFieldMetadata) 
                        fm.getFieldMetadata(ep.prop.name));
                metadata.setUserMetadata(
                    AbstractGenericView.class.getName(),
                    Edit.this
                );
                metadata.setUserMetadata(
                    Property.class.getName(),
                    ep.prop
                );
            }
        }
    }

    /***/
    class GenericComponent extends DialogPageView 
                        implements ValueObserver<Object> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected JComponent createControl() {
            current.subscribe(this);
            return super.createControl();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void componentOpened() {
            super.componentOpened();
            updateControl();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
            current.unsubscribe(this);
            super.dispose();
        }

        /**
         * updates the component to reflect the current domain object.
         * @param newValue .
         */
        public void changed(Object newValue) {
            m_executor.execute(newValue);
            updateControl();
        }

        /** 
         * updates the control to reflect the current domain object 
         * (if the control has already been created). 
         */
        protected void updateControl() {
            if (isControlCreated()) {
                // possibly just disable? Is not trivial though, as enabled
                // does not propagate to child components
                getControl().setVisible(current.get() != null);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> ViewDescriptor createDescriptor(Class<T> clazz) {
        m_visible = properties.m_eprops
            .filtered(DisplayablePropertyList.s_visibles);

        BeanPropertiesForm form = new BeanPropertiesForm();
        form.setShownBeanProperties(
            m_visible.mapped(
                DisplayablePropertyList.s_toName
            ).toArray(String.class)
        );
        form.setReadOnlyBeanProperties(properties.getReadonly());
        m_awaker.awaken(form);
        
        GenericComponent gc = new GenericComponent();
        Executor<T> exec = new Executor<T>(); 
        exec.m_repository = Services.get(RepositoryAgency.class)
                                    .getFor(clazz);
        exec.setBeanPropertiesForms(new BeanPropertiesForm[] {form});
        exec.setDisplayable(gc);
        m_awaker.awaken(exec);
        m_executor = exec;
        
        return configure(new Descriptor(gc));
    }
}
