/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.gui.richclient.views;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.services.gui.richclient.models.BeanTableModel;
import ch.elca.el4j.services.gui.swing.table.LineSelectionTableCellRenderer;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * Base class for bean views.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractBeanView extends AbstractView {
    /**
     * List with the displayed data.
     */
    private final List m_dataList 
        = Collections.synchronizedList(new ArrayList()); 
    
    /**
     * Bean table model.
     */
    private BeanTableModel m_beanTableModel;
    
    /**
     * Sortable table model.
     */
    private SortableTableModel m_sortableTableModel;
    
    /**
     * Table component.
     */
    private JTable m_beanTable;

    /**
     * Are the executors for this bean view.
     */
    private AbstractBeanExecutor[] m_beanExecutors;
    
    /**
     * Is the executor from executors above to change bean properties.
     */
    private AbstractBeanExecutor m_cachedBeanPropertiesExecutor;
    
    /**
     * {@inheritDoc}
     * 
     * Returns the root component for this view.
     */
    protected JComponent createControlOnce() {
        JPanel p = new JPanel(new BorderLayout());
        initializeSortedBeanTable();
        JScrollPane scrollableTable = new JScrollPane(getBeanTable());
        p.add(scrollableTable, BorderLayout.CENTER);
        return p;
    }

    /**
     * Creates and initializes a sorted bean table.
     */
    protected void initializeSortedBeanTable() {
        // Do not display a special column to show the row numbers.
        m_beanTableModel.setRowNumbers(false);
        m_beanTableModel.setRows(m_dataList);

        // Table that uses created model.      
        m_beanTable = new JTable(m_beanTableModel);
        
        // Make table sortable
        TableUtils.attachSorter(m_beanTable);
        m_sortableTableModel = (SortableTableModel) m_beanTable.getModel();
        
        // Line selection renderer so clicked cells will not be specially
        // hovered.
        LineSelectionTableCellRenderer lineSelectionRenderer 
            = new LineSelectionTableCellRenderer();
        m_beanTable.setDefaultRenderer(Object.class, lineSelectionRenderer);

        // Calculate prefered column sizes.
        TableUtils.sizeColumnsToFitRowData(m_beanTable);

        // Only one row is selectable. 
        m_beanTable.setRowSelectionAllowed(true);
        m_beanTable.setColumnSelectionAllowed(false);
        m_beanTable.setSelectionMode(
            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Register listener for table row selection.
        ListSelectionModel rowSelectionModel 
            = m_beanTable.getSelectionModel();
        rowSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else {
                    updateCommands();
                }
            }
        });
        
        // Register listener for double-click on a table row.
        m_beanTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 
                    && e.getButton() == MouseEvent.BUTTON1) {
                    AbstractBeanExecutor executor = getPropertiesExecutor();
                    if (executor.isEnabled()) {
                        executor.execute();
                    }
                }
            }
        });
    }
    
    /**
     * @return Returns the executor to change properties. The first executor
     *         from the executors array will be taken where
     *         <code>commandId</code> equals 
     *         <code>GlobalCommandIds.PROPERTIES</code>.
     */
    protected AbstractBeanExecutor getPropertiesExecutor() {
        if (m_cachedBeanPropertiesExecutor == null) {
            for (int i = 0; m_cachedBeanPropertiesExecutor == null 
                && m_beanExecutors != null 
                && i < m_beanExecutors.length; i++) {
                
                AbstractBeanExecutor executor = m_beanExecutors[i];
                String commandId = executor.getCommandId();
                if (GlobalCommandIds.PROPERTIES.equals(commandId)) {
                    m_cachedBeanPropertiesExecutor = executor;
                }
            }
        }
        return m_cachedBeanPropertiesExecutor;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Registers the properties executor.
     */
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        initializeExecutors();
        for (int i = 0; m_beanExecutors != null 
            && i < m_beanExecutors.length; i++) {
            AbstractBeanExecutor executor = m_beanExecutors[i];
            context.register(executor.getCommandId(), executor);
        }
        updateCommands();
    }
    
    /**
     * Initializes executors.
     */
    protected void initializeExecutors() {
        for (int i = 0; m_beanExecutors != null 
            && i < m_beanExecutors.length; i++) {
            AbstractBeanExecutor executor = m_beanExecutors[i];
            executor.setBeanView(this);
        }
    }
    
    /**
     * Will be invoked if commands should be update.
     */
    protected void updateCommands() {
        for (int i = 0; m_beanExecutors != null 
            && i < m_beanExecutors.length; i++) {
            AbstractBeanExecutor executor = m_beanExecutors[i];
            executor.updateState();
        }
    }

    /**
     * @return Returns selected row index if only one row is selected.
     */
    protected int getSelectedRow() {
        int rowIndexData = -1;
        if (isControlCreated()) {
            if (m_beanTable.getSelectedRowCount() == 1) {
                int rowIndexSorted = m_beanTable.getSelectedRow();
                if (rowIndexSorted >= 0) {
                    rowIndexData 
                        = m_sortableTableModel.convertSortedIndexToDataIndex(
                            rowIndexSorted);
                }
            }
        }
        return rowIndexData;
    }
    
    /**
     * @return Returns selected row indices or <code>null</code> if no rows are
     *         selected.
     */
    protected int[] getSelectedRows() {
        int[] rowIndicesData = null;
        if (isControlCreated()) {
            if (m_beanTable.getSelectedRowCount() >= 1) {
                int[] rowIndicesSorted = m_beanTable.getSelectedRows();
                if (rowIndicesSorted != null && rowIndicesSorted.length > 0) {
                    rowIndicesData 
                        = m_sortableTableModel
                            .convertSortedIndexesToDataIndexes(
                                rowIndicesSorted);
                }
            }
        }
        return rowIndicesData;
    }

    /**
     * @return Returns the selected object or <code>null</code>. If more than
     *         one object is selected also <code>null</code> will be returned.
     */
    public Object getSelectedBean() {
        Object result = null;
        int rowIndex = getSelectedRow();
        if (rowIndex >= 0) {
            result = m_dataList.get(rowIndex);
        }
        return result;
    }
    
    /**
     * @return Returns the selected objects or <code>null</code>.
     */
    public Object[] getSelectedBeans() {
        Object[] result = null;
        int[] rowIndices = getSelectedRows();
        if (rowIndices != null && rowIndices.length > 0) {
            result = new Object[rowIndices.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = m_dataList.get(rowIndices[i]);
            }
        }
        return result;
    }
    
    /**
     * Clears selection.
     */
    public void clearSelection() {
        if (isControlCreated()) {
            m_beanTable.requestFocusInWindow();
            m_beanTable.clearSelection();
        }
    }
    
    /**
     * Selects all rows.
     */
    public void selectAll() {
        if (isControlCreated()) {
            m_beanTable.requestFocusInWindow();
            m_beanTable.selectAll();
        }
    }
    
    /**
     * Additionally selects given bean.
     * 
     * @param bean Is the bean to select additionally to the existing selection.
     * @return Returns <code>true</code> if bean could be successfully selected.
     */
    public boolean selectBeanAdditionally(Object bean) {
        boolean success = false;
        int dataIndex;
        if (isControlCreated() 
            && bean != null
            && (dataIndex = m_dataList.indexOf(bean)) >= 0) {
            int sortedIndex = m_sortableTableModel
                .convertDataIndexesToSortedIndexes(new int[] {dataIndex})[0];
            m_beanTable.requestFocusInWindow();
            m_beanTable.addRowSelectionInterval(sortedIndex, sortedIndex);
            success = true;
        }
        return success;
    }
    
    /**
     * Focus the given bean.
     * 
     * @param bean Is the bean to set focus on.
     * @return Returns <code>true</code> if the bean could be successfully
     *         focused.
     */
    public boolean focusBean(Object bean) {
        boolean success = false;
        int dataIndex;
        if (isControlCreated() 
            && bean != null
            && (dataIndex = m_dataList.indexOf(bean)) >= 0) {
            final int SORTED_INDEX = m_sortableTableModel
                .convertDataIndexesToSortedIndexes(new int[] {dataIndex})[0];
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    m_beanTable.requestFocusInWindow();
                    m_beanTable.changeSelection(SORTED_INDEX, 0, false, false);
                }
            });
            success = true;
        }
        return success;
    }
    
    /**
     * Adds the given bean.
     * 
     * @param bean Is the bean to add.
     * @return Returns <code>true</code> if the given bean could be successfully
     *         added to list.
     */
    public boolean addBean(Object bean) {
        boolean success = false;
        if (isControlCreated()
            && bean != null) {
            success = m_dataList.add(bean);
            m_beanTableModel.fireTableDataChanged();
        }
        return success;
    }

    /**
     * Replaces the old with the new bean if the old bean exists in view.
     * 
     * @param oldBean Is the old bean that exists in data list.
     * @param newBean Is the new bean that replaces the old bean.
     * @return Return <code>true</code> if bean could be successfully replaced.
     */
    public boolean replaceBean(Object oldBean, Object newBean) {
        boolean success = false;
        int rowIndex;
        if (isControlCreated()
            && oldBean != null
            && newBean != null
            && (rowIndex = m_dataList.indexOf(oldBean)) >= 0) {
            m_dataList.set(rowIndex, newBean);
            m_beanTableModel.fireTableDataChanged();
            success = true;
        }
        return success;
    }

    /**
     * Removes given bean from data list.
     * 
     * @param bean Is the bean to remove.
     * @return Returns <code>true</code> if bean could be successfully removed.
     */
    public boolean removeBean(Object bean) {
        boolean success = false;
        if (isControlCreated()
            && bean != null) {
            success = m_dataList.remove(bean);
            m_beanTableModel.fireTableDataChanged();
        }
        return success;
    }
    
    /**
     * Removes given beans from data list.
     * 
     * @param beans Are the beans to remove.
     * @return Returns <code>true</code> if all beans could be successfully 
     *         removed.
     */
    public boolean removeBeans(Object[] beans) {
        boolean success = false;
        if (beans != null && beans.length > 0) {
            success = true;
            for (int i = 0; i < beans.length; i++) {
                Object bean = beans[i];
                success &= removeBean(bean);
            }
        } else {
            success = false;
        }
        return success;
    }
    
    /**
     * @return Returns the dataList.
     */
    public final List getDataList() {
        return m_dataList;
    }

    /**
     * @return Returns the beanTable.
     */
    public final JTable getBeanTable() {
        return m_beanTable;
    }

    /**
     * @return Returns the beanTableModel.
     */
    public final BeanTableModel getBeanTableModel() {
        return m_beanTableModel;
    }
    
    /**
     * @param beanTableModel Is the bean table model to set.
     */
    public final void setBeanTableModel(
        BeanTableModel beanTableModel) {
        m_beanTableModel = beanTableModel;
    }

    /**
     * @return Returns the sortableTableModel.
     */
    public final SortableTableModel getSortableTableModel() {
        return m_sortableTableModel;
    }

    /**
     * @return Returns the beanExecutors.
     */
    public final AbstractBeanExecutor[] getBeanExecutors() {
        return m_beanExecutors;
    }

    /**
     * @param beanExecutors The beanExecutors to set.
     */
    public final void setBeanExecutors(AbstractBeanExecutor[] beanExecutors) {
        m_beanExecutors = beanExecutors;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        CoreNotificationHelper.notifyIfEssentialPropertyIsEmpty(
            getBeanTableModel(), "beanTableModel", this);
    }
}
