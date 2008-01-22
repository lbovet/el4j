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

/**
 * Extended DataModel for DataTable that implements "real" paging. 
 * 
 * <script
 * type="text/javascript">printFileStatus ("$URL$", "$Revision$", "$Date$",
 * "$Author$" );</script>
 * 
 * @author Frank Bitzer (FBI)
 */

import java.util.List;

import javax.faces.model.DataModel;



public class PagedListDataModel extends DataModel {

    private int rowIndex = -1;

    private int totalNumRows;

    private int pageSize;

    private List list;

    public PagedListDataModel() {
        super();
    }

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
    public PagedListDataModel(List list, int totalNumRows, int pageSize) {
        super();
        setWrappedData(list);
        this.totalNumRows = totalNumRows;
        this.pageSize = pageSize;
    }

    public boolean isRowAvailable() {
        if (list == null)
            return false;

        int rowIndex = getRowIndex();
        if (rowIndex >= 0 && rowIndex < list.size())
            return true;
        else
            return false;
    }

    public int getRowCount() {
        return totalNumRows;
        
    }
    
    public void setRowCount(int value) {
        totalNumRows = value;
    }

    public Object getRowData() {
        if (list == null)
            return null;
        else if (!isRowAvailable())
            throw new IllegalArgumentException();
        else {
            int dataIndex = getRowIndex();
            return list.get(dataIndex);
        }
    }

    public int getRowIndex() {
        return (rowIndex % pageSize);
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Object getWrappedData() {
        return list;
    }

    public void setWrappedData(Object list) {
        this.list = (List) list;
    }

}
