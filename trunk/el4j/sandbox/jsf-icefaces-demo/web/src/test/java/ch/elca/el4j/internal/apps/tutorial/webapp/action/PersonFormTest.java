//this testclass was part of the AppFuse tutorial but is not used any more
//------------------------------------------------------------------------

//package ch.elca.el4j.internal.apps.tutorial.webapp.action;
//
//import ch.elca.el4j.internal.apps.service.GenericManager;
//import ch.elca.el4j.internal.apps.tutorial.model.Person;
//import ch.elca.el4j.internal.apps.webapp.action.BasePageTestCase;
//
//public class PersonFormTest extends BasePageTestCase {
//    private PersonForm bean;
//    private GenericManager<Person, Long> personManager;
//        
//    public void setPersonManager(GenericManager<Person, Long> personManager) {
//        this.personManager = personManager;
//    }
//
//    @Override
//    protected void onSetUp() throws Exception {
//        super.onSetUp();
//        bean = new PersonForm();
//        bean.setPersonManager(personManager);
//    }
//
//    @Override
//    protected void onTearDown() throws Exception {
//        super.onTearDown();
//        bean = null;
//    }
//
//    public void testAdd() throws Exception {
//        Person person = new Person();
//
//        // enter all required fields
//        bean.setPerson(person);
//
//        assertEquals("list", bean.save());
//        //assertFalse(bean.hasErrors());
//    }
//
//    public void testEdit() throws Exception {
//        log.debug("testing edit...");
//        bean.setId(2L);
//
//        assertEquals("edit", bean.edit());
//        assertNotNull(bean.getPerson());
//        //assertFalse(bean.hasErrors());
//    }
//
//    public void testSave() {
//        log.debug("testing save...");
//        bean.setId(2L);
//
//        assertEquals("edit", bean.edit());
//        assertNotNull(bean.getPerson());
//        Person person = bean.getPerson();
//
//        // update required fields
//        bean.setPerson(person);
//
//        assertEquals("edit", bean.save());
//        //assertFalse(bean.hasErrors());
//    }
//
//    public void testRemove() throws Exception {
//        log.debug("testing remove...");
////        Person person = new Person();
////        person.setId(4L);
////        bean.setPerson(person);
////        
////        //bean.save();
////        
////
////        assertEquals("list", bean.delete());
////        assertFalse(bean.hasErrors());
//    }
//}