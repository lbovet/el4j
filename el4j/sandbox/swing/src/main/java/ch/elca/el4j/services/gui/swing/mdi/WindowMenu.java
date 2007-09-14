package ch.elca.el4j.services.gui.swing.mdi;


import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

public class WindowMenu extends JMenu {
    private WindowManager windowManager;
    
    public WindowMenu() {
        String[] windowMenuActionNames = { "closeAll" };
        createMenu("windowMenu", windowMenuActionNames);
    }
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }
   
    private void createMenu(String menuName, String[] actionNames) {
        setName(menuName);
        for (String actionName : actionNames) {
            if (actionName.equals("---")) {
                add(new JSeparator());
            } else {
               JMenuItem menuItem = new JMenuItem();
               menuItem.setAction(getAction(actionName));
               menuItem.setIcon(null);
               add(menuItem);
            }
        }
   }
   
   private javax.swing.Action getAction(String actionName) {
       ApplicationContext ac = Application.getInstance().getContext();
       return ac.getActionMap(this).get(actionName);
   }
   
   @Action
   public void closeAll() {
       windowManager.closeAll();
   }
   
   // TODO more actions

}
