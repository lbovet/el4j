/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.cli;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.cli.MavenLoggerManager;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.logging.Logger;

import ch.elca.el4j.maven.logging.AbstractFormattingLogger;
import ch.elca.el4j.maven.logging.console.ColorLogger;
import ch.elca.el4j.maven.logging.html.HtmlLogger;

/**
 * This is an absolutely horrible hack, and of course
 * very fragile. It is based on MavenCli from maven-embedder:3.0.1
 * If it suddenly stops working, look at the diff between
 * the current maven and version 3.0.1.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philipp Brüschweiler (PBW)
 */
public final class ColorMavenCli301 extends MavenCli {

	/** Make checkstyle happy. */
	private ColorMavenCli301() { }

	/**
	 * Intercept the main method of MavenCli, possibly
	 * rewrite the logger and then delegate the call.
	 * @param args The arguments.
	 * @param classWorld The classworld.
	 * @return The return value.
	 */
	public static int main(String[] args, ClassWorld classWorld) {

		ColorMavenCli301 cli = new ColorMavenCli301();

		// What we basically want to do is this:
		// cli.doMain(new CliRequest(args, classWorld));

		Class<?> cliRequestClass = null;
		Class<?>[] classes = MavenCli.class.getDeclaredClasses();
		for (Class<?> cl : classes) {
			if (cl.getName().equals(
				"org.apache.maven.cli.MavenCli$CliRequest")) {

				cliRequestClass = cl;
				break;
			}
		}
		if (cliRequestClass == null) {
			throw new IllegalStateException(
				"Getting the CliRequest class failed.");
		}

		try {
			Constructor<?> cliRequestConstructor = cliRequestClass
				.getDeclaredConstructor(new Class<?>[] {
					String[].class, ClassWorld.class});
			cliRequestConstructor.setAccessible(true);
			Object cliRequest = cliRequestConstructor
				.newInstance(args, classWorld);

			Method[] methods = MavenCli.class.getDeclaredMethods();
			Method doMainMethod = null;
			for (Method method : methods) {
				if (method.getName().equals("doMain")
					&& (method.getParameterTypes().length == 1)) {
					doMainMethod = method;
					break;
				}
			}
			if (doMainMethod == null) {
				throw new IllegalStateException(
					"The 'doMain' method could not be found");
			}

			return (Integer) doMainMethod.invoke(cli, cliRequest);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Use this opportunity to overwrite the logger both in the cli
	 * and in the container.
	 * @param container The container.
	 */
	@Override
	protected void customizeContainer(PlexusContainer container) {
		// Decide which logger to use based on properties.
		String type = System.getProperty("plexus.logger.type");
		AbstractFormattingLogger logger;

		if (type == null) {
			logger = null;
		} else if (type.equals("ansi")) {
			logger = new ColorLogger(Logger.LEVEL_INFO, "ignored");
		} else if (type.equals("html")) {
			logger = new HtmlLogger(Logger.LEVEL_INFO, "ignored");
		} else {
			logger = null;
		}

		if (logger == null) {
			return;
		}

		// We want to set the logger

		// ... in the cli,
		boolean fieldSet = false;
		try {
			Field[] fields = MavenCli.class.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals("logger")) {
					field.setAccessible(true);
					field.set(this, logger);
					fieldSet = true;
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (!fieldSet) {
			throw new IllegalStateException(
				"Setting the logger failed.");
		}

		// ... and in the container.
		((DefaultPlexusContainer) container)
			.setLoggerManager(new MavenLoggerManager(logger));
	}
}
