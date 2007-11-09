package com.silvermindsoftware.hitch.validation;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
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
    private static Logger s_logger = Logger.getLogger(
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
        m_listener.setValid((JComponent) binding.getTargetObject());
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
    }

    /** {@inheritDoc} */
    public void synced(Binding binding) {
        //s_logger.info("Validate");
        if (binding.getSourceObject() instanceof ValidationCapability) {
            ValidationCapability source = (ValidationCapability) binding
                    .getSourceObject();
            if (binding.getSourceProperty() instanceof BeanProperty) {
                BeanProperty beanProp = (BeanProperty) binding
                        .getSourceProperty();
                m_listener.setValid((JComponent) binding.getTargetObject(),
                        source.isValid(beanProp.getPropertyName()));

            } else {
                m_listener.setValid((JComponent) binding.getTargetObject(),
                        source.isValid());
            }
        }
    }
}
