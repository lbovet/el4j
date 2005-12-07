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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

import ch.elca.el4j.services.gui.richclient.executors.BeanPropertiesExecutor;
import ch.elca.el4j.services.gui.richclient.models.BeanTableModel;
import ch.elca.el4j.services.gui.swing.table.LineSelectionTableCellRenderer;

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
     * Executor for bean properties.
     */
    private BeanPropertiesExecutor m_beanPropertiesExecutor;
    

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
        m_beanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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
                    && e.getButton() == MouseEvent.BUTTON1 
                    && m_beanPropertiesExecutor.isEnabled()) {
                    m_beanPropertiesExecutor.execute();
                }
            }
        });
    }
    
    /**
     * {@inheritDoc}
     * 
     * Registers the properties executor.
     */
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register(GlobalCommandIds.PROPERTIES, 
            m_beanPropertiesExecutor);
    }
    
    /**
     * Will be invoked if commands should be update.
     */
    protected void updateCommands() {
        // Enables the bean properties executer if a bean is selected.
        m_beanPropertiesExecutor.setEnabled(getSelectedBean() != null);
    }

    /**
     * @return Returns selected row index.
     */
    protected int getSelectedRow() {
        int rowIndexData = -1;
        if (isControlCreated()) {
            int rowIndexSorted = m_beanTable.getSelectedRow();
            if (rowIndexSorted >= 0) {
                rowIndexData 
                    = m_sortableTableModel.convertSortedIndexToDataIndex(
                        rowIndexSorted);
            }
        }
        return rowIndexData;
    }
    
    /**
     * @return Returns the selected object or <code>null</code>.
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
     * @param newBean Updates the selected row with the given bean.
     */
    public void updateSelectedBean(Object newBean) {
        int rowIndex = getSelectedRow();
        if (rowIndex >= 0 
            && newBean != null 
            && m_dataList.size() > rowIndex) {
            m_dataList.set(rowIndex, newBean);
            m_beanTableModel.fireTableDataChanged();
        }
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
     * @return Returns the beanPropertiesExecutor.
     */
    public final BeanPropertiesExecutor getBeanPropertiesExecutor() {
        return m_beanPropertiesExecutor;
    }

    /**
     * @param beanPropertiesExecutor The beanPropertiesExecutor to set.
     */
    public final void setBeanPropertiesExecutor(
        BeanPropertiesExecutor beanPropertiesExecutor) {
        m_beanPropertiesExecutor = beanPropertiesExecutor;
        m_beanPropertiesExecutor.setBeanView(this);
    }
}
