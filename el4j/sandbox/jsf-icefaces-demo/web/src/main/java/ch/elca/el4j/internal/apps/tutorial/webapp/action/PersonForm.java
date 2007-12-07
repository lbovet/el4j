//this class was part of the AppFuse tutorial, but is not used any more, since
//this app uses ICEfaces. PersonForm was integrated in PersonList, because 
//the user always interacts with one form.
//-----------------------------------------------------------------------

//package ch.elca.el4j.internal.apps.tutorial.webapp.action;
//
//import java.io.Serializable;
//import java.util.Map;
//
//import javax.faces.component.UIParameter;
//import javax.faces.context.FacesContext;
//import javax.faces.event.ActionEvent;
//
//import ch.elca.el4j.internal.apps.tutorial.model.Person;
//import ch.elca.el4j.internal.apps.webapp.action.BasePage;
//import ch.elca.el4j.internal.apps.service.GenericManager;
//
//public class PersonForm extends BasePage implements Serializable {
//    private GenericManager<Person, Long> personManager;
//    private Person person = new Person();
//    private Long id;
//    
//    private boolean visible=false;
//
//    public boolean getVisible() {
//        return visible;
//    }
//
//    public void setVisible(boolean visible) {
//        this.visible = visible;
//    }
//
//    public void setPersonManager(GenericManager<Person, Long> personManager) {
//        this.personManager = personManager;
//    }
//
//    public Person getPerson() {
//        return person;
//    }
//
//    public void setPerson(Person person) {
//        this.person = person;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//    public Long getId() {
//        if (id == null) {
//            id = (Long) expressionResolver("#{param.foo}");
//        }
//        return id;
//    }
//
//    public Object expressionResolver(String expression) {
//        Object value = null;
//    
//        if ((expression.indexOf("#{") != -1) && (expression.indexOf("#{") < expression.indexOf('}'))) {
//            value =  getFacesContext().getApplication().createValueBinding(expression).getValue(getFacesContext());
//        } else {
//            value = expression;
//        }
//        return value;
//    }
//    
//
//    public String delete() {
//        personManager.remove(person.getId());
//        addMessage("person.deleted");
//
//        return "list";
//    }
//
//    
//    public void edit(ActionEvent event){    
//        
//        /*
//         * Holt sich die Session auf dem Externen Context
//         */
//        Map session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
//            
//        /* 
//         * Such die UIParameter Komponente anhand des Ausdrucks 
//         */
//        UIParameter component = (UIParameter) event.getComponent().findComponent("currentId");
//
//        /*
//         * Parse den Value der UIParameter Komponente
//         */
//        Long id = Long.parseLong(component.getValue().toString());
//  
//        if (id != null) {
//           person = personManager.get(id);
//        } else {            
//            person = new Person();
//            person.setFirstName("Neu");
//        }
//        
//        visible = true;
//        
//        //return "edit";
//        
//    }
//    
//    
//    
//    public String save() {
//        boolean isNew = (person.getId() == null);
//        personManager.save(person);
//
//        
//        
//        String key = (isNew) ? "person.added" : "person.updated";
//        addMessage(key);
//
//        if (isNew) {
//            return "list";
//        } else {
//            return "edit";
//        }
//        
//    }
//    
//    public String add(){
//        
//        person = new Person();
//           
//        return "add";
//    }
//    
//    
//} 