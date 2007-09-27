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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.exceptions.ExampleExceptionHandler;
import ch.elca.el4j.demos.gui.fs.FSDatensammlungInternalFrame;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.MDIApplication;
import ch.elca.el4j.gui.swing.dialog.about.AboutDialog;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.layout.EqualsLayout;
import ch.elca.el4j.gui.swing.splash.ImageSplashScreen;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
/**
 * Sample MDI application that demonstrates how to use the framework.
 * 
 * See also associated MainForm.properties file that contains resources
 * 
 * @author SWI
 *
 */
public class MainForm extends MDIApplication {
    private ApplicationContext guiApplicationContext = null;
    
    /**
     * Main definition of the GUI
     *  This method is called back by the GUI framework
     */
    @Override
    protected void startup() {
        guiApplicationContext = Application.getInstance().getContext();
        getMainFrame().setJMenuBar(createMenuBar());
        show(createMainPanel());
    }
    
    
    private JComponent createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createToolBar(), BorderLayout.NORTH);
        
        createDefaultDesktopPane();

        // create a sample internal frame
        JInternalFrame jif1 = new SomeInternalFrame();
        jif1.setName("Frame1");
        showInternalFrame(jif1);        
        
        // create panel on bottom of MDI "desktop"
        JPanel bottomPanel = new JPanel(new EqualsLayout(3,5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Test"));
        JButton addButton = new JButton("Add");
        addButton.setAction(getAction("addFrame"));
        JButton addButton2 = new JButton("sendEvent");
        addButton2.setAction(getAction("sendEvent"));
        JButton addButton3 = new JButton("debug");
        addButton3.setAction(getAction("debug"));
        bottomPanel.add(addButton);
        bottomPanel.add(addButton2);
        bottomPanel.add(addButton3);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        panel.add(desktopPane, BorderLayout.CENTER);
        return panel;
    }


	@Action
    public void addFrame() {
        JInternalFrame jif1 = new FSDatensammlungInternalFrame(this);
        //JInternalFrame jif1 = new SomeInternalFrame();
        jif1.setName("Frame1");
        showInternalFrame(jif1);
    }

	@Action
    public void addFrame2() {
        JInternalFrame jif1 = new BindingDemoFrame(this);
        jif1.setName("BindingDemoFrame");
        showInternalFrame(jif1);
    }
	
	
    @Action
    public void sendEvent() {
        EventBus.publish(new ExampleEvent("test"));
    }
    
    /**
     * Toggle admin flag (for visibility of menu entry)
     */
    @Action
    public void debug() {
        // enable help menuItem
        boolean oldAdmin = admin;
        admin = true;
        firePropertyChange("admin", oldAdmin, admin);
        
        // print event subscribers
        System.out.println(EventBus.getSubscribers(InternalFrameEvent.class));
    }

    @Action(enabledProperty = "admin")
    public void help() {
        // TODO integrate JavaHelp
    }
    
    @Action
    public void about() {
    	if (aboutDialog == null) {
    		aboutDialog = new AboutDialog(guiApplicationContext); 
    	}
        show(aboutDialog);
    }
    
    protected AboutDialog aboutDialog;
    
    /**
     * Indicates whether permission "admin" is set
     *  (used via enabledProperty field of \@Action) 
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
        return admin;
    }  
 

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        String[] fileMenuActionNames = {  "addFrame2", "---", "quit" };
        String[] helpMenuActionNames = { "help", "about" };
        menuBar.add(createMenu("fileMenu", fileMenuActionNames));
        menuBar.add(createMenu("helpMenu", helpMenuActionNames));
        return menuBar;
    }
    

    private JToolBar createToolBar() {
        String[] toolbarActionNames = { "quit", "help"};
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
     * Sample main application for a GUI
     * @param args command line arguments
     */
    public static void main(String[] args) {
    	
    	ImageSplashScreen splashScreen = null;
    	try {
    		splashScreen = new ImageSplashScreen(); // uses default splash screen   	
    		
    		// add special exception handler
    		Exceptions.getInstance().addHandler(new ExampleExceptionHandler());
        
    	    PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());

            String[] applicationContextPaths = {
                    "classpath*:mandatory/*.xml",
                    "classpath*:scenarios/db/raw/*.xml",
                    "classpath*:scenarios/dataaccess/hibernate/*.xml",
                    "classpath*:scenarios/dataaccess/hibernate/refdb/*.xml",
                    "classpath:scenarios/ch/elca/el4j/demos/gui/*.xml"};    	    
    	    
    	    ModuleApplicationContext springContext = 
    	    	new ModuleApplicationContext (applicationContextPaths, false);    	    
        
    	    GUIApplication.launch(MainForm.class, args, springContext);
        
    	} finally {
    		if (splashScreen != null) {
    			splashScreen.dispose();
    		}
    	}
    }
    
    // for activation demo
    private boolean admin = false;
}
