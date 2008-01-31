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
package ch.elca.el4j.jsf.model;


import java.util.List;

import javax.faces.model.DataModel;


/**
 * Extended DataModel for DataTable that implements "real" paging. 
 * 
 * <script
 * type="text/javascript">
 * printFileStatus 
 * ("$URL$",
 *  "$Revision$", "$Date$",
 * "$Author$" );</script>
 * 
 * @author Frank Bitzer (FBI)
 */
public class PagedListDataModel extends DataModel {

    /**
     * Index of selected row.
     */
    private int m_rowIndex = -1;

    /**
     * total number of rows.
     */
    private int m_totalNumRows;

    /**
     * rows per page.
     */
    private int m_pageSize;

    /**
     * List of underlying data.
     */
    private List<Object> m_list;

    

    /**
     * Constructor takes arguments to initialize the model.
     * 
     * @param list
     *            list containing only items for current page
     * @param totalNumRows
     *            total number of rows of underlying data (used to determine how
     *            many pages can be displayed and to show forward/backward
     *            buttons, if a dataScroller is provided)
     * @param pageSize
     *            number of items to be displayed on one page (should be equal
     *            to <code>DataTable.getRows()</code>)
     */
    public PagedListDataModel(List<Object> list, int totalNumRows, 
        int pageSize) {
        
        super();
        setWrappedData(list);
        this.m_totalNumRows = totalNumRows;
        this.m_pageSize = pageSize;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public boolean isRowAvailable() {
        boolean result = false;
        
        if (m_list == null) {
            return false;
        }

        int rowIndex = getRowIndex();
        
        if (rowIndex >= 0 && rowIndex < m_list.size()) {
            result =  true;
        } 
        
        return result;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public int getRowCount() {
        return m_totalNumRows;
        
    }
    
    /**
     * 
     * @param value value to set
     */
    public void setRowCount(int value) {
        m_totalNumRows = value;
    }

    
    /**
     * 
     * {@inheritDoc}
     */
    public Object getRowData() {
        
        if (m_list == null) {
            
            return null;
            
        } else if (!isRowAvailable()) {
            
            throw new IllegalArgumentException();
        
        } else {
            
            int dataIndex = getRowIndex();
            return m_list.get(dataIndex);
            
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public int getRowIndex() {
        return (m_rowIndex % m_pageSize);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setRowIndex(int rowIndex) {
        this.m_rowIndex = rowIndex;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Object getWrappedData() {
        return m_list;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setWrappedData(Object list) {
        this.m_list = (List<Object>) list;
    }

}
