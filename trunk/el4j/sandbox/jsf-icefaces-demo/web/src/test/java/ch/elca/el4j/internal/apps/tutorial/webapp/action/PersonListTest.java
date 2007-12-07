//package ch.elca.el4j.internal.apps.tutorial.webapp.action;
//
//import ch.elca.el4j.internal.apps.webapp.action.BasePageTestCase;
//import ch.elca.el4j.internal.apps.service.GenericManager;
//import ch.elca.el4j.internal.apps.tutorial.model.Person;
//
//public class PersonListTest extends BasePageTestCase {
//    private PersonList bean;
//    private GenericManager<Person, Long> personManager;
//
//    public void setPersonManager(GenericManager<Person, Long> personManager) {
//        this.personManager = personManager;
//    }
//        
//    @Override @SuppressWarnings("unchecked")
//    protected void onSetUp() throws Exception {
//        super.onSetUp();
//        bean = new PersonList();
//        bean.setPersonManager(personManager);
//        
//        // add a test person to the database
//        Person person = new Person();
//
//        // enter all required fields
//        person.setFirstName("Frank");
//        person.setLastName("Bitzer");
//
//        personManager.save(person);
//    }
//
//    @Override
//    protected void onTearDown() throws Exception {
//        super.onTearDown();
//        bean = null;
//    }
//
//    public void testSearch() throws Exception {
//        assertTrue(bean.getPersons().size() >= 1);
//        //assertFalse(bean.hasErrors());
//    }
//}