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
package ch.elca.el4j.jsf.component.html;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.richfaces.component.html.HtmlDatascroller;

import ch.elca.el4j.jsf.model.PagedListDataModel;
import ch.elca.el4j.seam.generic.PagedEntityManager;

/**
 * Extended HtmlDataScroller to support paging. Always belongs to a
 * PagedRichHtmlDataTable, by which is it created.<br/> Reloads data only for
 * the currently displayed page. Uses the {@link PagedEntityManager} interface 
 * to get correct data from database, just in time it is required.
 * 
 * @see PagedRichHtmlDataTable 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Frank Bitzer (FBI)
 */
public class PagedRichHtmlDatascroller extends HtmlDatascroller {

    /**
     * Component type for this component.
     */
    public static final String COMPONENT_TYPE 
        = "ch.elca.el4j.PagedRichHtmlDatascroller";

    /**
     * Retrieves the PagedRichHtmlDataTable to which this scroller belongs.
     * 
     * @return PagedRichHtmlDataTable
     */
    public PagedRichHtmlDataTable getTable() {

        PagedRichHtmlDataTable t = (PagedRichHtmlDataTable) this.getParent()
            .findComponent(this.getFor());

        return t;

    }

    /**
     * Handles reloading of required data. 
     * 
     * {@inheritDoc}
     */
    public void encodeBegin(FacesContext context) {

        //get bound table and its PagedEntityManager
        PagedRichHtmlDataTable t = getTable();

        PagedEntityManager<Object> p = t.getCurrentPagedEntityManager();

        //calculate first recordset to display
        //depending on whether a new page was requested or the
        //complete underlying data was changed (then, p.isViewReset 
        //must be true)
        int first = t.getFirst();

        boolean wasReset = false;

        if (p.isViewReset()) {
            //view was reset, so set first record to zero
            first = 0;

            wasReset = true;

            p.setViewReset(false);
        }

        //total number of available entities
        int size = p.getEntityCount(t.getCurrentEntityName());

        //set range to load entities within
        p.setRange(first, t.getRows());

        //now get entities
        List<Object> ent = p.getEntities(t.getCurrentEntityName());

        //and set model for datatable
        PagedListDataModel m = new PagedListDataModel(ent, size, t.getRows());

        t.setValue(m);

        //view was reset so show first page
        if (wasReset) {
            this.setPage("1");
        }

        
        try {
            super.encodeBegin(context);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }

    }

}
