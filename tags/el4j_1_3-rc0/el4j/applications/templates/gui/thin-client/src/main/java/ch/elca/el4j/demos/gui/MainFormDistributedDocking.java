package ch.elca.el4j.demos.gui;


import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.demos.gui.exceptions.ExampleExceptionHandler;
import ch.elca.el4j.gui.swing.GUIApplication;
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
public final class MainFormDistributedDocking {
    
    /**
     * Hide default constructor.
     */
    private MainFormDistributedDocking() { }
    
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
                "classpath:scenarios/remoting/client/*.xml",
                "classpath:scenarios/swing/demo/*.xml"};
            
            ModuleApplicationContextConfiguration contextConfig
                = new ModuleApplicationContextConfiguration();
            
            contextConfig.setInclusiveConfigLocations(applicationContextPaths);
            contextConfig.setExclusiveConfigLocations(new String[] {
                "classpath*:mandatory/refdb/refdb-core-service-config.xml"});
            contextConfig.setAllowBeanDefinitionOverriding(true);


            GUIApplication.launch(MainFormDocking.class, args, contextConfig);

        } finally {
            if (splashScreen != null) {
                splashScreen.dispose();
            }
        }
    }
}
