package ch.elca.el4j.internal.apps.tutorial.webapp.action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import ch.elca.el4j.internal.apps.webapp.action.BasePage;
import ch.elca.el4j.internal.apps.tutorial.model.Person;
import ch.elca.el4j.internal.apps.service.GenericManager;

//import com.icesoft.faces.webapp.xmlhttp.*;
//import com.icesoft.faces.async.render.*;

public class PersonList extends BasePage implements Serializable {
    private GenericManager<Person, Long> personManager;

    
    private boolean popupVisible=false;
    
   
    private Person currentPerson = new Person();
    
    private Long currentId;
    
    
    
    
    
    public Long getCurrentId() {
//        if (currentId == null) {
//            currentId = (Long) expressionResolver("#{param.foo}");
//        }
        return currentId;
    }

    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }
    
    public Person getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(Person currentPerson) {
        this.currentPerson = currentPerson;
    }

    public boolean getPopupVisible() {
        return popupVisible;
    }

    public void setPopupVisible(boolean popupVisible) {
        this.popupVisible = popupVisible;
    }

    public void setPersonManager(GenericManager<Person, Long> personManager) {
        this.personManager = personManager;
    }

    public PersonList() {
        setSortColumn("id"); // sets the default sort column
    }

    public List getPersons() {
        
        //Person p = personManager.get(1L);
        
        List<Person> pl = personManager.getAll();
        return sort(pl);
      
    }
    
    public void edit(ActionEvent event){    
       
        Long currentId = Long.parseLong(Integer.toString(this.getIntKeyFromParam(event, "currentId")));
  
        if (currentId != null) {
            currentPerson = personManager.get(currentId);
        } else {            
            currentPerson = new Person();
            currentPerson.setFirstName("Neu");
        }
        
        popupVisible = true;
        
        //return "edit";
        
    }
    
    public String cancelPopup(){
        
        popupVisible=false;
        
       
        
        return "cancel";
    }
    
    public String save(){
        
      boolean isNew = (currentPerson.getId() == null);
      personManager.save(currentPerson);

      
      
      String key = (isNew) ? "person.added" : "person.updated";
      addMessage(key);

      this.popupVisible = false;
      
      
      
      return "cancel";
        
    }
    
    public String delete() {
      personManager.remove(currentPerson.getId());
      addMessage("person.deleted");

      this.popupVisible = false;
      
      return "cancel";
    
    }
    
    
    public void add(){
      
      currentPerson = new Person();
         
      popupVisible = true;
      
    }
    
    
    
//    private Object expressionResolver(String expression) {
//        Object value = null;
//    
//        if ((expression.indexOf("#{") != -1) && (expression.indexOf("#{") < expression.indexOf('}'))) {
//            value =  getFacesContext().getApplication().createValueBinding(expression).getValue(getFacesContext());
//        } else {
//            value = expression;
//        }
//        return value;
//    }

    
}

