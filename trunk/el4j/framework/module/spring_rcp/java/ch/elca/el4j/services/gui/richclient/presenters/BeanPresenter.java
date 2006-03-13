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
package ch.elca.el4j.services.gui.richclient.presenters;

import java.util.Collection;

/**
 * Interface to present beans.
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
public interface BeanPresenter {

    /**
     * @return Returns the selected object or <code>null</code>. If more than
     *         one object is selected also <code>null</code> will be returned.
     */
    public Object getSelectedBean();

    /**
     * @return Returns the selected objects or <code>null</code>.
     */
    public Object[] getSelectedBeans();

    /**
     * Clears selection.
     */
    public void clearSelection();

    /**
     * Selects all beans.
     */
    public void selectAllBeans();

    /**
     * Additionally selects given bean.
     * 
     * @param bean Is the bean to select additionally to the existing selection.
     * @return Returns <code>true</code> if bean could be successfully selected.
     */
    public boolean selectBeanAdditionally(Object bean);

    /**
     * Focus the given bean.
     * 
     * @param bean Is the bean to set focus on.
     * @return Returns <code>true</code> if the bean could be successfully
     *         focused.
     */
    public boolean focusBean(Object bean);

    /**
     * Adds the given bean.
     * 
     * @param bean Is the bean to add.
     * @return Returns <code>true</code> if the given bean could be successfully
     *         added to list.
     */
    public boolean addBean(Object bean);
    
    /**
     * Replaces all beans with the given.
     * 
     * @param beans
     *            Are the beans to set.
     * @return Returns <code>true</code> if bean presenter's collection has
     *         changed.
     */
    public boolean setBeans(Collection beans);

    /**
     * Replaces the old with the new bean if the old bean exists in view.
     * 
     * @param oldBean Is the old bean that exists in data list.
     * @param newBean Is the new bean that replaces the old bean.
     * @return Return <code>true</code> if bean could be successfully replaced.
     */
    public boolean replaceBean(Object oldBean, Object newBean);

    /**
     * Removes given bean from presenter.
     * 
     * @param bean Is the bean to remove.
     * @return Returns <code>true</code> if bean could be successfully removed.
     */
    public boolean removeBean(Object bean);

    /**
     * Removes given beans from presenter.
     * 
     * @param beans Are the beans to remove.
     * @return Returns <code>true</code> if all beans could be successfully 
     *         removed.
     */
    public boolean removeBeans(Object[] beans);
}