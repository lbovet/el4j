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


import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.jsf.SeamApplication;
import org.richfaces.component.html.HtmlDataTable;

import ch.elca.el4j.seam.generic.PagedEntityManager;

/**
 * A custom component adding paging to RichFaces DataTable. The paging works
 * with any dataScroller bound to this DataTable. It requires the following
 * attributes to be specified:
 * <ul>
 * <li> entityName <br/> Fully Qualified Class Name of the entity to be
 * displayed in the table.
 * <li> pagedEntityManager <br/> Bean implementing the interface
 * <code>PagedEntityManager</code>. This is used to fetch data from db. Hint:
 * if you don't want your dao's to implement this interface directly, you may
 * write a wrapper around them.
 * </ul>
 * Example of usage:
 * 
 * <pre>
 * &lt;d:pagedRichDataTable id=&quot;myTable&quot; 
 *                   entityName=&quot;ch.elca.el4j.apps.refdb.dom.Reference&quot;
 *                   pagedEntityManager=&quot;#{referenceList}&quot;
 *                   var=&quot;reference&quot;
 *                   rows=&quot;5&quot;&gt;
 *                   
 *   &lt;!-- column defs go here as normal using var &quot;reference&quot;--&gt;
 *         
 * &lt;/d:pagedRichDataTable&gt;
 * &lt;!-- you do not need a datascroller to enable switching between the pages;
 * it is rendered automatically by the table  --&gt;         
 * </pre>
 * 
 * <script type="text/javascript">printFileStatus ("$URL:
 * https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/sandbox/seam-demo/jar/src/main/java/ch/elca/el4j/jsf/component/html/PagedRichHtmlDataTable.java
 * $", "$Revision$", "$Date: 2008-01-17 15:40:29 +0100 (Do, 17 Jan 2008)
 * $", "$Author$" );</script>
 * 
 * @author Frank Bitzer (FBI)
 */
public class PagedRichHtmlDataTable extends HtmlDataTable {

    /**
     * value for MaxPages property of datascroller.
     */
    private static final int MAX_PAGES = 10;
    
    /**
     * Provides access to the current PagedEntityManager which is used to 
     * fetch data from the database.
     * 
     * @return current instance of PagedEntityManager specified as attribute
     * for this table
     */
    public PagedEntityManager getCurrentPagedEntityManager() {
        PagedEntityManager pagedEntityManager 
            = (PagedEntityManager) getAttributes().get("pagedEntityManager");

        return pagedEntityManager;
    }

    /**
     * Provides access to the fully qualified classname of the currently 
     * displayed entity.
     * 
     * @return entityName specified via attribute
     */
    public String getCurrentEntityName() {
        String em = (String) getAttributes().get("entityName");

        return em;
    }

    
    /**
     * {@inheritDoc} encodeBegin is called before the response is returned to
     * the client. 
     * We intercept the lifecycle and initiate the loading of the
     * entities by setting up a PagedRichHtmlDatascroller.
     */
    public void encodeBegin(FacesContext context) {

        
        createDataScroller(context);
        

        try {
            super.encodeBegin(context);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, 
                "error in super.encodeBegin: " + ex.getMessage());
        }

    }

    

    
    /**
     * creates and configures a datascroller for the table and adds it as 
     * footer facet.
     * 
     * @param ctx FacesContext
     */
    private void createDataScroller(FacesContext ctx) {

        SeamApplication a = (SeamApplication) ctx.getApplication();

        PagedRichHtmlDatascroller s = (PagedRichHtmlDatascroller) a
            .createComponent(PagedRichHtmlDatascroller.COMPONENT_TYPE);

        s.setId(this.getId() + "Scroller");
        s.setFor(this.getId());
        s.setMaxPages(MAX_PAGES);

        s.setRenderIfSinglePage(true);

        this.setFooter(s);
        
//        Map<String, UIComponent> facets = this.getFacets();
//        facets.put("footer", s);
        
        
    }

}
