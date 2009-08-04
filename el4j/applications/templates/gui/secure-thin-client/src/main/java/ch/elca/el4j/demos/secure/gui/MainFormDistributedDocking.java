package ch.elca.el4j.demos.secure.gui;


import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
import ch.elca.el4j.demos.gui.MainFormDocking;
import ch.elca.el4j.demos.gui.exceptions.ExampleExceptionHandler;
import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.services.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.services.gui.swing.splash.ImageSplashScreen;

/**
 * Sample MDI application that demonstrates how to use the framework.
 *
 * See also associated MainFormMDI.properties file that contains resources
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
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
				"classpath*:optional/security-client.xml",
				"classpath*:scenarios/securityscope/distributed-security-scope-client.xml",
				"classpath*:scenarios/remoting/client/*.xml",
				"classpath*:scenarios/gui/swing/*.xml"};
			
			ModuleApplicationContextConfiguration contextConfig
				= new ModuleApplicationContextConfiguration();
			
			contextConfig.setInclusiveConfigLocations(applicationContextPaths);
			contextConfig.setAllowBeanDefinitionOverriding(true);


			GUIApplication.launch(MainFormDocking.class, args, contextConfig);

		} finally {
			if (splashScreen != null) {
				splashScreen.dispose();
			}
		}
	}
}
