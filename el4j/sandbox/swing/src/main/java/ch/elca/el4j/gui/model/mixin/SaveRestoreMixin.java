package ch.elca.el4j.gui.model.mixin;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 * Mixin to enable javaBeans to save and restore their properties.
 * This can be useful if objects are connected to the GUI and get modified
 * by the user, but the user discards the changes (e.g. by clicking on Cancel)
 * 
 * @author SWI
 */
public class SaveRestoreMixin extends DelegatingIntroductionInterceptor
        implements SaveRestoreCapability {

    /**
     * The wrapped object.
     */
    private Object m_subject;

    /**
     * The stored properties (a map containing the setter-method and its value).
     */
    private Map<Method, Object> m_backup = new HashMap<Method, Object>();

    private static Log s_logger = LogFactory.getLog(SaveRestoreMixin.class);

    /**
     * Wrap an object with the save/restore mixin.
     * 
     * @param object  the object to be wrapped
     * @return the same object wrapped with a spring proxy that has the
     *         {@link SaveRestoreMixin} as {@link Advisor}
     */
    public static Object addSaveRestoreMixin(Object object) {
        ProxyFactory pc = new ProxyFactory(object);
        IntroductionAdvisor ii = new DefaultIntroductionAdvisor(
                new SaveRestoreMixin());
        pc.setProxyTargetClass(true);
        pc.addAdvisor(0, ii);
        object = pc.getProxy();
        return object;
    }

    /** {@inheritDoc} */
    public void save() {
        if (m_subject == null) {
            s_logger.error("Subject not known yet. "
                    + "Call a getter/setter method before using save");
        }
        try {
            m_backup.clear();

            BeanInfo info = Introspector.getBeanInfo(m_subject.getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                Method r = pd.getReadMethod();
                Method w = pd.getWriteMethod();
                if (r != null && w != null) {
                    m_backup.put(pd.getWriteMethod(), r.invoke(m_subject));
                }
            }
        } catch (Exception e) {
            m_backup.clear();
        }
    }

    /** {@inheritDoc} */
    public void restore() {
        if (m_subject == null) {
            s_logger.error("Subject not known yet. "
                    + "Call a getter/setter method before using restore");
        }
        try {
            for (Method method : m_backup.keySet()) {
                method.invoke(m_subject, m_backup.get(method));
            }
        } catch (Exception e) {
            return;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (m_subject == null) {
            m_subject = invocation.getThis();
        }

        return super.invoke(invocation);
    }

}
