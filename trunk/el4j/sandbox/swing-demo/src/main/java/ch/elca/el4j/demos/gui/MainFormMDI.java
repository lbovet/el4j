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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.exceptions.ExampleExceptionHandler;
import ch.elca.el4j.demos.gui.util.JInteralFrameWrapper;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.MDIApplication;
import ch.elca.el4j.gui.swing.dialog.about.AboutDialog;
import ch.elca.el4j.gui.swing.dialog.search.AbstractSearchDialog;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.layout.EqualsLayout;
import ch.elca.el4j.gui.swing.splash.ImageSplashScreen;

/**
 * Sample MDI application that demonstrates how to use the framework.
 * 
 * See also associated MainForm.properties file that contains resources
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
        show(createMainPanel());
    }
    
    
    private JComponent createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createToolBar(), BorderLayout.NORTH);
        
        createDefaultDesktopPane();

        /*
        // create panel on bottom of MDI "desktop"
        JPanel bottomPanel = new JPanel(new EqualsLayout(3,5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Test"));
        JButton addButton2 = new JButton("sendEvent");
        addButton2.setAction(getAction("sendExampleEvent"));
        JButton addButton3 = new JButton("debug");
        addButton3.setAction(getAction("debug"));
        bottomPanel.add(addButton2);
        bottomPanel.add(addButton3);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        */
        
        // popup menu
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
        
        panel.add(m_desktopPane, BorderLayout.CENTER);
        return panel;
    }

    @Action
    public void showDemo1() {
        JInternalFrame jif = new JInteralFrameWrapper(
            "demo1", new ResourceInjectionDemoForm(this));
        showInternalFrame(jif);
    }
    
    @Action
    public void showDemo2() {
        JInternalFrame jif = new JInteralFrameWrapper(
            "demo2", new CancelableDemoForm(this));
        showInternalFrame(jif);
    }

    @Action
    public void showDemo3() {
        JInternalFrame jif = new JInteralFrameWrapper(
            "demo3", new MasterDetailDemoForm(this));
        showInternalFrame(jif);
    }
    
    @Action
    public void showDemo4() {
        JInternalFrame jif = new JInteralFrameWrapper(
            "demo4", new BindingDemoForm(this));
        showInternalFrame(jif);
    }
    
    @Action
    public void showDemo5() {
        JInternalFrame jif = new JInteralFrameWrapper(
            "demo5", new EventBusDemoForm(this));
        showInternalFrame(jif);
    }
    
    @Action
    public void showSearch() {
        JInternalFrame jif = new JInteralFrameWrapper(
            "search", new SearchDialog(this));
        showInternalFrame(jif);
    }
    
    @Action
    public void sendExampleEvent() {
        EventBus.publish(new ExampleEvent("I'm an Example Event!"));
    }
    
    /**
     * Toggle admin flag (for visibility of menu entry)
     */
    @Action
    public void debug() {
        // enable help menuItem
        boolean oldAdmin = m_admin;
        m_admin = true;
        firePropertyChange("admin", oldAdmin, m_admin);
        
        // print event subscribers
        System.out.println(EventBus.getSubscribers(InternalFrameEvent.class));
    }

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
    
    @Action
    public void about() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(this);
        }
        show(aboutDialog);
    }
    
    protected AboutDialog aboutDialog;
    
    /**
     * Indicates whether permission "admin" is set
     *  (used via enabledProperty field of \@Action).
     */
    public boolean isAdmin() {
        return hasRole("ROLE_SUPERVISOR");
    }
    
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
 

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        String[] fileMenuActionNames = {"quit"};
        String[] editMenuActionNames = {"cut", "copy", "paste", "delete"};
        String[] demoMenuActionNames
            = {"showDemo1", "showDemo2", "showDemo3", "showDemo4", "---",
            "showSearch", "---", "showDemo5", "sendExampleEvent"};
        String[] helpMenuActionNames = {"help", "about"};
        menuBar.add(createMenu("fileMenu", fileMenuActionNames));
        menuBar.add(createMenu("editMenu", editMenuActionNames));
        menuBar.add(createMenu("demoMenu", demoMenuActionNames));
        menuBar.add(createMenu("helpMenu", helpMenuActionNames));
        return menuBar;
    }
    

    private JToolBar createToolBar() {
        String[] toolbarActionNames = {"quit", "help"};
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        Border border = new EmptyBorder(2, 9, 2, 9); // top, left, bottom, right
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

            String[] applicationContextPaths = {"classpath*:mandatory/*.xml",
                "classpath*:scenarios/db/raw/*.xml",
                "classpath*:scenarios/dataaccess/hibernate/*.xml",
                "classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
                "classpath:scenarios/ch/elca/el4j/demos/gui/*.xml"};

            ModuleApplicationContext springContext = new ModuleApplicationContext(
                applicationContextPaths, false);

            GUIApplication.launch(MainFormMDI.class, args, springContext);

        } finally {
            if (splashScreen != null) {
                splashScreen.dispose();
            }
        }
    }
}
