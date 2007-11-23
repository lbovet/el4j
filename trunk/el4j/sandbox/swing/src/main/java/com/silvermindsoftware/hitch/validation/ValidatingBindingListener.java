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
package com.silvermindsoftware.hitch.validation;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.swingbinding.validation.ValidationCapability;

import com.silvermindsoftware.hitch.validation.response.ValidationResponder;

/**
 * This class listens to a binding and performs a validation on sync.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
@SuppressWarnings("unchecked")
public class ValidatingBindingListener implements BindingListener {
    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(
        ValidatingBindingListener.class);
        
    /**
     * The validataion responder.
     */
    private ValidationResponder m_listener;
    
    /**
     * Create a new validating binding listener.
     * @param listener     the validataion responder
     */
    public ValidatingBindingListener(ValidationResponder listener) {
        m_listener = listener;
    }
    
    /** {@inheritDoc} */
    public void bindingBecameBound(Binding binding) {
        //s_logger.info("bound");
    }
    
    /** {@inheritDoc} */
    public void bindingBecameUnbound(Binding binding) {
        //s_logger.info("unbound");
        m_listener.setValid(binding.getSourceObject(),
            (JComponent) binding.getTargetObject());
    }
    
    /** {@inheritDoc} */
    public void sourceChanged(Binding binding, PropertyStateEvent event) {
        //s_logger.info("source changed");
    }
    
    /** {@inheritDoc} */
    public void targetChanged(Binding binding, PropertyStateEvent event) {
        //s_logger.info("target changed");
    }

    /** {@inheritDoc} */
    public void syncFailed(Binding binding, SyncFailure failure) {
        //s_logger.info("failed");
        if (binding.getTargetObject() instanceof JComponent) {
            m_listener.setInvalid(binding.getSourceObject(),
                (JComponent) binding.getTargetObject(),
                failure.toString());
        }
        /*if (binding.getSourceObject() instanceof JComponent) {
            m_listener.setInvalid(binding.getTargetObject(),
                (JComponent) binding.getSourceObject(),
                failure.toString());
        }*/
    }

    /** {@inheritDoc} */
    public void synced(Binding binding) {
        //s_logger.info("Validate");
        ValidationCapability source = null;
        BeanProperty beanProperty  = null;
        JComponent target = null;
        
        if (binding.getSourceObject() instanceof ValidationCapability) {
            source = (ValidationCapability) binding.getSourceObject();
            target = (JComponent) binding.getTargetObject();
            if (binding.getSourceProperty() instanceof BeanProperty) {
                beanProperty = (BeanProperty) binding.getSourceProperty();
            }
        } else if (binding.getTargetObject() instanceof ValidationCapability) {
            source = (ValidationCapability) binding.getTargetObject();
            target = (JComponent) binding.getSourceObject();
            if (binding.getTargetProperty() instanceof BeanProperty) {
                beanProperty = (BeanProperty) binding.getTargetProperty();
            }
        }
        if (source != null) {
            if (beanProperty != null) {
                m_listener.setValid(source, target,
                    source.isValid(beanProperty.getPropertyName()));
            } else {
                m_listener.setValid(source, target, source.isValid());
            }
        } /*else {
            m_listener.setValid(null, (JComponent) binding.getTargetObject());
        }*/
    }
}
