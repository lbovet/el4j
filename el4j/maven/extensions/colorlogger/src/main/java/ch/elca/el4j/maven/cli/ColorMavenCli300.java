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
import org.apache.maven.cli.PrintStreamLogger;
import org.codehaus.plexus.classworlds.ClassWorld;

import ch.elca.el4j.maven.logging.console.ColorLogger;
import ch.elca.el4j.maven.logging.html.HtmlLogger;

import com.jcraft.jsch.Logger;

/**
 * This is an absolutely horrible hack, and of course
 * very fragile. It is based on MavenCli from maven-embedder:3.0
 * If it suddenly stops working, look at the diff between
 * the current maven and version 3.0.0.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philipp Brüschweiler (PBW)
 */
public final class ColorMavenCli300 {

	/** Make checkstyle happy. */
	private ColorMavenCli300() { }

	/**
	 * Intercept the main method of MavenCli, possibly
	 * rewrite the logger and delegate.
	 * @param args The arguments.
	 * @param classWorld The classworld.
	 * @return The return value.
	 * @throws Exception if any of the introspection stuff fails.
	 */
	public static int main(String[] args, ClassWorld classWorld)
		throws Exception {

		// Decide which logger to use based on properties.
		String type = System.getProperty("plexus.logger.type");

		if (type == null) {
			return MavenCli.main(args, classWorld);
		} else if (type.equals("ansi")) {
			return doMainWithLogger(args, classWorld,
				new ColorLogger(Logger.INFO, "ignored"));
		} else if (type.equals("html")) {
			return doMainWithLogger(args, classWorld,
				new HtmlLogger(Logger.INFO, "ignored"));
		} else {
			return MavenCli.main(args, classWorld);
		}
	}

	/**
	 * We want to overwrite the logger, and maven doesn't want to
	 * let us. Use introspection.
	 * @param args The arguments.
	 * @param classWorld The classworld.
	 * @param logger The logger that overrides the maven supplied one.
	 * @return The return value.
	 * @throws Exception if any of the introspection stuff fails.
	 */
	private static int doMainWithLogger(String[] args,
		ClassWorld classWorld, PrintStreamLogger logger)
		throws Exception {

		MavenCli cli = new MavenCli();
		
		// What we basically want to do is this:
		/*
		 * cli.setLogger(logger);
		 * cli.doMain(new CliRequest(args, classWorld));
		 */
		boolean fieldSet = false;
		Field[] fields = cli.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("logger")) {
				field.setAccessible(true);
				field.set(cli, logger);
				fieldSet = true;
				break;
			}
		}
		if (!fieldSet) {
			throw new IllegalStateException(
				"Setting the logger failed.");
		}

		Class<?> cliRequestClass = null;
		Class<?>[] classes = cli.getClass().getDeclaredClasses();
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

		Constructor<?> cliRequestConstructor = cliRequestClass
			.getDeclaredConstructor(new Class<?>[] {
				String[].class, ClassWorld.class});
		cliRequestConstructor.setAccessible(true);
		Object cliRequest = cliRequestConstructor
			.newInstance(args, classWorld);

		Method[] methods = cli.getClass().getDeclaredMethods();
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
	}
}
