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
package ch.elca.el4j.services.gui.richclient.executors.convenience;

import java.util.ArrayList;
import java.util.List;

import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.executors.AbstractConfirmBeanExecutor;
import ch.elca.el4j.services.gui.richclient.presenters.BeanPresenter;
import ch.elca.el4j.services.gui.richclient.utils.DialogUtils;
import ch.elca.el4j.services.persistence.generic.dto.PrimaryKeyObject;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Abstract executor to delete beans.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractBeanDeleteExecutor 
    extends AbstractConfirmBeanExecutor {
    /**
     * Are the beans that should be deleted.
     */
    private PrimaryKeyObject[] m_beans;

    /**
     * @return Returns the beans that should be deleted.
     */
    protected PrimaryKeyObject[] getBeans() {
        return m_beans;
    }
    
    /**
     * @param beans Are the beans to set.
     */
    public void setBeans(PrimaryKeyObject[] beans) {
        m_beans = beans;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean onFinishOrConfirm() {
        Object[] objects = getBeanPresenter().getSelectedBeans();
        Reject.ifNull(objects);
        PrimaryKeyObject[] beans = new PrimaryKeyObject[objects.length];
        System.arraycopy(objects, 0, beans, 0, beans.length);
        setBeans(beans);
        
        List keys = new ArrayList();
        for (int i = 0; i < beans.length; i++) {
            keys.add(beans[i].getKeyAsObject());
        }
        
        deleteBeansByKey(keys);
        
        BeanPresenter beanPresenter = getBeanPresenter();
        beanPresenter.removeBeans(beans);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onFinishOrConfirmException(Exception e) {
        BeanPresenter beanPresenter = getBeanPresenter();
        
        PrimaryKeyObject[] beans = getBeans();
        PrimaryKeyObject[] actualizedBeans = new PrimaryKeyObject[beans.length];
        for (int i = 0; i < beans.length; i++) {
            PrimaryKeyObject oldBean = beans[i];
            try {
                Object key = oldBean.getKeyAsObject();
                PrimaryKeyObject newBean = getBeanByKey(key);
                beanPresenter.replaceBean(oldBean, newBean);
                actualizedBeans[i] = newBean;
            } catch (Exception ex) {
                beanPresenter.removeBean(oldBean);
            }
        }
        
        // Selecting beans must be done in a seperate step, when no bean 
        // will be added to or removed from data list. Otherwise already 
        // made selections will be lost!
        beanPresenter.clearSelection();
        for (int i = 0; i < actualizedBeans.length; i++) {
            PrimaryKeyObject newBean = actualizedBeans[i];
            beanPresenter.selectBeanAdditionally(newBean);
        }
        
        // Show error message dialog.
        int multiplicity = beans.length;
        DialogUtils.showErrorMessageDialog(getId(), getSchema(), e, 
            multiplicity, getDisplayable().getMainComponent());
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandId() {
        String commandId = super.getCommandId();
        if (!StringUtils.hasText(commandId)) {
            commandId = GlobalCommandIds.DELETE;
            setCommandId(commandId);
        }
        return commandId;
    }
    
    /**
     * Deletes beans with the given keys.
     * 
     * @param keys Are the keys of beans to delete.
     */
    protected abstract void deleteBeansByKey(List keys);
    
    /**
     * @param key Used to lookup the bean.
     * @return Returns the bean with given key.
     * @throws Exception If bean could not be fetched.
     */
    protected abstract PrimaryKeyObject getBeanByKey(Object key) 
        throws Exception;
}
