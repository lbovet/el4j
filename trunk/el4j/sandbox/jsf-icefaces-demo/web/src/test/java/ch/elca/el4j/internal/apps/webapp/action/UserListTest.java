//package ch.elca.el4j.internal.apps.webapp.action;
//
//import ch.elca.el4j.internal.apps.service.UserManager;
//
//public class UserListTest extends BasePageTestCase {
//    private UserList bean;
//    private UserManager userManager;
//
//    public void setUserManager(UserManager userManager) {
//        this.userManager = userManager;
//    }
//
//    @Override
//    protected void onSetUp() throws Exception {    
//        super.onSetUp();
//        bean = new UserList();
//        bean.setUserManager(userManager);
//    }
//    
//    public void testSearch() throws Exception {
//        assertTrue(bean.getUsers().size() >= 1);
//        assertFalse(bean.hasErrors());
//    }
//
//}
