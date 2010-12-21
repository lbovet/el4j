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

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import ch.elca.el4j.core.context.ModuleApplicationContextConfiguration;
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
public final class MainFormDistributedMDI {
	
	/**
	 * Hide default constructor.
	 */
	private MainFormDistributedMDI() { }
	
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
			
			// $$ BEANS INCLUDE $$
			String[] inclusiveConfigLocations = {
				"classpath*:mandatory/*.xml",
				"classpath*:scenarios/gui/swing/*.xml",
				"classpath*:scenarios/remoting/client/httpInvoker-client-config.xml",
				"classpath:scenarios/caching/*.xml"
			};
			
			// $$ BEANS EXCLUDE $$
			String[] exclusiveConfigLocations = {
				"classpath*:mandatory/refdb-core-config.xml"
			};
			
			ModuleApplicationContextConfiguration contextConfig
				= new ModuleApplicationContextConfiguration();
			
			contextConfig.setInclusiveConfigLocations(inclusiveConfigLocations);
			contextConfig.setExclusiveConfigLocations(exclusiveConfigLocations);
			contextConfig.setAllowBeanDefinitionOverriding(true);


			GUIApplication.launch(MainFormMDIXML.class, args, contextConfig);

		} finally {
			if (splashScreen != null) {
				splashScreen.dispose();
			}
		}
	}
}
