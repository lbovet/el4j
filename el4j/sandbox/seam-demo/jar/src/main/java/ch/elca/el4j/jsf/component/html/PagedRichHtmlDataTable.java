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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.richfaces.component.html.HtmlDataTable;

import ch.elca.el4j.jsf.model.PagedListDataModel;
import ch.elca.j4persist.generic.PagedEntityManager;

/**
 * A custom component adding paging to RichFaces DataTable.
 * 
 * The paging works with any dataScroller bound to this DataTable.
 * 
 * It requires the following attributes to be specified:
 * <ul>
 *  <li> entityName <br/>
 *  Fully Qualified Class Name of the entity to be displayed in the table.
 *  
 *  <li> pagedEntityManager <br/>
 *  Bean implementing the interface <code>PagedEntityManager</code>. 
 *  This is used to fetch data from db.
 *  Hint: if you don't want your dao's to implement this interface directly,
 *  you may write a wrapper around them.
 * </ul>
 *
 * Example of usage:
 * 
 * <pre>
 * &lt;d:pagedRichDataTable id="myTable" 
 *                   entityName="ch.elca.el4j.apps.refdb.dom.Reference"
 *                   pagedEntityManager="#{referenceList}"
 *                   var="reference"
 *                   rows="5"&gt;
 *                   
 *         &lt;!-- column defs go here as normal using var "reference"--&gt;
 *         
 * &lt;/d:pagedRichDataTable&gt;
 * &lt;!-- this is the datascroller to enable switching between the pages --&gt;         
 * &lt;rich:datascroller align="left"  for="myTable" maxPages="5" /&gt;
 * </pre>        
 * 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Frank Bitzer (FBI)
 */
public class PagedRichHtmlDataTable extends HtmlDataTable {

    
    /**
     * {@inheritDoc}
     * encodeBegin is called before the response is returned to the client.
     * 
     * We intercept the lifecycle and initiate the loading of the entities
     * for the currently displayed page of the datatable.
     */
    public void encodeBegin(FacesContext context){
        
        this.updateDataModel(getFirst(),getRows());
        
        try {
            super.encodeBegin(context);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, ex.getMessage());
        }
        
    }
    
    
    /**
     * Updates datamodel by loading <code>count</code> entities 
     * starting with <code>first</code>
     * 
     * @param first
     * @param count
     */
    private void updateDataModel(int first, int count){
        
        String entityName = (String)getAttributes().get("entityName");
        
        PagedEntityManager pagedEntityManager = 
            (PagedEntityManager)getAttributes().get("pagedEntityManager");
        
        
        if ((pagedEntityManager != null) && (entityName != null)){
            
            
            //total number of entities in database
            //this is used to calculate the number of pages
            int listSize = pagedEntityManager.getEntityCount(
                entityName);
    
//            //load entities for the page to be displayed
//            Object[] entities = pagedEntityManager.getEntities(
//                entityName, first, count);
//    
//            //transform array of objects into ArrayList
//            //maybe there is a more elegant way to do this?
//            ArrayList l = new ArrayList();
//            
//            for (int i = 0; i < entities.length; i++) {
//                l.add(entities[i]);
//            }
            
            
            pagedEntityManager.setRange(first, count);
            
            //now get the attribute that was magically updated by seam
           // DataModel model = (DataModel)getAttributes().get("value");
            
            DataModel model = (DataModel)FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("entities");           
            
            
            if (model == null){
                Logger.getAnonymousLogger().log(Level.SEVERE,"entities is null");
            } else {
                //create paged datamodel holding the entities of current page
                
               
                    
                PagedListDataModel dataModel = new PagedListDataModel(
                    (ArrayList)model.getWrappedData(),
                    listSize, count);
                
                this.setValue(dataModel);
            }
            
          
        
        
        }
    }
    
    
    
}
