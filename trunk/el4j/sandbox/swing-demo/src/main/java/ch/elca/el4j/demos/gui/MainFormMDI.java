/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.demos.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.exceptions.ExampleExceptionHandler;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.MDIApplication;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.splash.ImageSplashScreen;

/**
 * Sample MDI application that demonstrates how to use the framework.
 * 
 * See also associated MainFormMDI.properties file that contains resources
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class MainFormMDI extends MDIApplication {
    /**
     * A example popup menu.
     */
    private JPopupMenu m_popup;
    
    /**
     * Determines if user is admin (for activation demo).
     */
    private boolean m_admin = false;
    
    /**
     * Main definition of the GUI.
     *  This method is called back by the GUI framework
     */
    @Override
    protected void startup() {
        getMainFrame().setJMenuBar(createMenuBar());
        showMain(createMainPanel());
    }
    
    /**
     * @return    the created main panel
     */
    private JComponent createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createToolBar(), BorderLayout.NORTH);
        
        createDefaultDesktopPane();
        
        // show a popup menu consisting of menu items
        m_popup = createPopup(new String[] {
            "showDemo1", "showDemo2", "---", "quit"});
        m_desktopPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    m_popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // set default size (if size has not yet been saved by AppFW)
        getMainFrame().getContentPane().setPreferredSize(
            new Dimension(640, 480));
        
        panel.add(m_desktopPane, BorderLayout.CENTER);
        return panel;
    }

    @Action
    public void showDemo1() {
        show("ResourceInjectionDemoForm");
    }
    
    @Action
    public void showDemo2() {
        show("CancelableDemoForm");
    }

    @Action
    public void showDemo3() {
        show("MasterDetailDemoForm");
    }
    
    @Action
    public void showDemo4() {
        show("BindingDemoForm");
    }
    
    @Action
    public void showDemo5() {
        show("EventBusDemoForm");
    }
    
    @Action
    public void showSearch() {
        show("SearchDialog");
    }
    
    @Action
    public void showRefDB() {
        show("RefDBDemoForm");
    }
    
    @Action
    public void sendExampleEvent() {
        EventBus.publish(new ExampleEvent("I'm an Example Event!"));
    }
    

    /**
     * A "special" help only for admins (for demo purpose only).
     */
    @Action(enabledProperty = "admin")
    public void help() {
        /*
        JHelp helpViewer = null;
        try {
            // Get the classloader of this class.
            ClassLoader cl = MainForm.class.getClassLoader();
            // Use the findHelpSet method of HelpSet to create a URL referencing
            // the helpset file.
            // Note that in this example the location of the helpset is implied
            // as being in the same
            // directory as the program by specifying "jhelpset.hs" without any
            // directory prefix,
            // this should be adjusted to suit the implementation.
            URL url = HelpSet.findHelpSet(cl, "jhelpset.hs");
            // Create a new JHelp object with a new HelpSet.
            helpViewer = new JHelp(new HelpSet(cl, url));
            // Set the initial entry point in the table of contents.
            helpViewer.setCurrentID("Simple.Introduction");
        } catch (Exception e) {
            System.err.println("API Help Set not found");
        }
        */
    }
    
    /**
     * Toggle admin flag (for visibility of menu entry).
     */
    @Action
    public void toggleAdmin() {
        // enable help menuItem
        boolean oldAdmin = m_admin;
        m_admin = !m_admin;
        firePropertyChange("admin", oldAdmin, m_admin);
    }
    
    /**
     * Show the about dialog.
     */
    @Action
    public void about() {
        show("AboutDialog");
    }
    
    /**
     * Indicates whether permission "admin" is set
     *  (used via enabledProperty field of \@Action).
     *  @return    <code>true</code> if user has admin rights
     */
    public boolean isAdmin() {
        return hasRole("ROLE_SUPERVISOR");
    }
    
    /**
     * @param requestedRole    the role to check
     * @return                 <code>true</code> if user has specified role.
     */
    public boolean hasRole(String requestedRole) {
        /*  commented out for now (until acegi security is set up):
         *   
          GrantedAuthority[] authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals(requestedRole)) {
                return true;
            }
        }*/
        return m_admin;
    }  
 
    /**
     * @return    the created menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        String[] fileMenuActionNames = {"quit"};
        String[] editMenuActionNames = {"cut", "copy", "paste", "delete"};
        String[] demoMenuActionNames
            = {"showDemo1", "showDemo2", "showDemo3", "showDemo4", "---",
                "showSearch", "showRefDB", "---",
                "showDemo5", "sendExampleEvent"};
        String[] helpMenuActionNames = {"help", "toggleAdmin", "about"};
        menuBar.add(createMenu("fileMenu", fileMenuActionNames));
        menuBar.add(createMenu("editMenu", editMenuActionNames));
        menuBar.add(createMenu("demoMenu", demoMenuActionNames));
        menuBar.add(createMenu("helpMenu", helpMenuActionNames));
        return menuBar;
    }
    
    /**
     * @return    the created tool bar
     */
    private JToolBar createToolBar() {
        String[] toolbarActionNames = {"quit"};
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        Border border = new EmptyBorder(2, 9, 2, 9);
        for (String actionName : toolbarActionNames) {
            JButton button = new JButton();
            button.setBorder(border);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setAction(getAction(actionName));
            button.setFocusable(false);
            button.setText("");
            toolBar.add(button);
        }
        return toolBar;
    }
    
    /**
     * Sample main application for a GUI.
     * @param args command line arguments
     */
    public static void main(String[] args) {

        ImageSplashScreen splashScreen = null;
        try {
            // uses default splash screen
            splashScreen = new ImageSplashScreen();

            // add special exception handler
            Exceptions.getInstance().addHandler(new ExampleExceptionHandler());

            PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());

            String[] applicationContextPaths = {
                "classpath*:mandatory/*.xml",
                "classpath*:mandatory/refdb/*.xml",
                "classpath*:scenarios/db/raw/*.xml",
                "classpath*:scenarios/dataaccess/hibernate/*.xml",
                "classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
                "classpath*:optional/interception/transactionJava5Annotations.xml",
                "classpath:scenarios/swing/demo/*.xml"};
            
            ModuleApplicationContextConfiguration contextConfig
                = new ModuleApplicationContextConfiguration();
            
            contextConfig.setInclusiveConfigLocations(applicationContextPaths);


            GUIApplication.launch(MainFormMDI.class, args, contextConfig);

        } finally {
            if (splashScreen != null) {
                splashScreen.dispose();
            }
        }
    }
}
