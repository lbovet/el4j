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

//import ch.elca.el4j


package ch.elca.el4j.internal.apps.webapp.action;

import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.util.StringUtils;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.LikeCriteria;



/**
 * Backing bean for keyword page.
 * 
 * Using ICEfaces resembles to programming desktop GUI applications in some kind.
 * The properties of the formular/icefaces-view are set in the backing bean
 * and rendered automatically by the ViewHandler.
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
public class Keywords extends BasePage {
    
    /**
     * the keyword currently viewed/edited
     */
    private Keyword currentKeyword = new Keyword();
    
    /**
     * Index of currently selected tab of ICEfaces tabbedPabel
     */
    private int selectedTab = 0;
    
    /**
     * Caption for second tab
     */
    private String detailCaption = "keywords.tab.create";
    
    /**
     * currently searched name
     */
    private String searchName = "";
    
    /**
     * currently searched Description
     */
    private String searchDescription = "";
    
    /**
     * currently loaded keywords
     */
    private List<Keyword> keywords;
    

    private DaoRegistry m_daoRegistry = null;
    
    
    public Keywords(){
     // sets the default sort column
        setSortColumn("name"); 
        setAscending(true);
    }
    
    
    /**
     * @return The DAO registry
     */
    public DaoRegistry getDaoRegistry() {
        
        return m_daoRegistry;
    }
    
    /**
     * @param reg
     *            The DaoRegistry to set
     */
    public void setDaoRegistry(DaoRegistry reg) {
        m_daoRegistry = reg;
    }
//    
    /**
     * Returns the DAO for keywords.
     * 
     * @return The DAO for keywords
     */
    protected KeywordDao getKeywordDao() {
       return (KeywordDao) getDaoRegistry().getFor(Keyword.class);
    }
       
   /**
    * Getter for keyword list
    * 
    * Keywords are chached in memory.
    * when a save or delete operation is executed,
    * search() must be called to refresh list
    * 
    * @return list of keywords
    */
    public List getKeywords() {
        
        
         if (keywords != null) {
                
                return sort(keywords);
            
         } else {
                
                return null;
         }
        
       
    }
    
    
    public void setKeywords(List kw){
        keywords = kw;
    }
   
    public Keyword getCurrentKeyword() {
        return currentKeyword;
    }

    public void setCurrentKeyword(Keyword currentKeyword) {
        this.currentKeyword = currentKeyword;
    }

    
    public int getSelectedTab() {
        return this.selectedTab;
    }

    public void setSelectedTab(int val) {
        this.selectedTab = val;
    }
    
    public String getDetailCaption() {
        return detailCaption;
    }


    public void setDetailCaption(String detailCaption) {
        this.detailCaption = detailCaption;
    }
    
    public String getSearchName() {
        return searchName;
    }


    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }


    public String getSearchDescription() {
        return searchDescription;
    }


    public void setSearchDescription(String searchDescription) {
        this.searchDescription = searchDescription;
    }

    
    /**
     * Click on add-button to create new keyword.
     */
    public void add() {
        
        currentKeyword = new Keyword();
        
        this.detailCaption = getText("keywords.tab.create");
        
        
        this.selectedTab = 1;
        
    }
    
    /**
     * Edit imagebutton in table was pushed
     * @param event
     */
    public void edit(ActionEvent event){    
        
       Integer currentId = getIntKeyFromParam(event,"currentId");
  
        
        if (currentId != null) {
            
            
            currentKeyword = getKeywordDao().findById(currentId);
            
            this.detailCaption = getText("keywords.tab.edit");
            
            this.selectedTab = 1;
            
           
            
        }
        
        
    }
    
    
    /**
     * Click on delete imagebutton in table
     * @param event
     */
    public void delete(ActionEvent event){  
        
        Integer currentId = this.getIntKeyFromParam(event, "currentId");
        
        if (currentId != null) {
            
          
            
            getKeywordDao().delete(currentId);
            
            
            //reload list of keywords
            search();
            
        }
        
        
    }
       
    
    
    /**
     * Save button on detail tab was pushed.
     */
    public String save() {
        
       
        getKeywordDao().saveOrUpdate(currentKeyword);
        
        this.selectedTab = 0;
       
        //reload list of keywords
        search();
        
        //here a return value is necessary to call the navigation case
        //in order to clear the input fields
        return "cancelKeyword";
        
        
    }
    
    
    /**
     * Click on cancel button on detail tab.
     */
    public String cancelDetail() {
        
        currentKeyword.setName("");
        currentKeyword.setDescription("");
        
        this.selectedTab = 0;
        

        //here a return value is necessary to call the navigation case
        //in order to clear the input fields
        return "cancelKeyword";
        
    }
    
    
    /**
     * Click on search button
     */
    public void search(){
        
        
        setKeywords(findKeywords());
        
        
    }

    /**
     * Find keywords by query created from current values 
     * of textboxes
     * 
     * @return
     */
    private List<Keyword> findKeywords() {
        //find keywords by query
          
        QueryObject query = new QueryObject();
        if (StringUtils.hasText(searchName)) {
            query.addCriteria(LikeCriteria.caseInsensitive("name", searchName));
        }
        if (StringUtils.hasText(searchDescription)) {
            query.addCriteria(LikeCriteria.caseInsensitive("description", 
                searchDescription));
        }
          
        List<Keyword> kl = getKeywordDao().findByQuery(query);
          
        
        
        return kl;
      
    }


  

  
    

   

}
