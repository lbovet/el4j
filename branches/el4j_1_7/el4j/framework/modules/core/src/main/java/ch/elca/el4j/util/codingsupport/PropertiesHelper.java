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

package ch.elca.el4j.util.codingsupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import ch.elca.el4j.core.io.support.ListResourcePatternResolverDecorator;
import ch.elca.el4j.core.io.support.ManifestOrderedConfigLocationProvider;
import ch.elca.el4j.core.io.support.OrderedPathMatchingResourcePatternResolver;
import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * A helper class which handles the loading and storing of Properties to/from
 * files including Spring path resolving.
 *
 * <p>
 * The files can be indicated absolutely or via classpath, i.e. either by
 * "file:C:/folder/..." or by "classpath:folder/...".
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Raphael Boog (RBO)
 */
public class PropertiesHelper {
	
	/**
	 * Loads properties from given resources. Properties declared in late resources overwrite properties.
	 * declared in earlier resources. Invalid resources just get skipped.
	 * @param resources    the properties files
	 * @return             the merged properties
	 */
	public static Properties loadPropertiesFromResources(Resource[] resources) {
		Properties properties = new Properties();
		InputStream in = null;
		
		for (Resource resource : resources) {
			try {
				in = resource.getInputStream();
				properties.load(in);
			} catch (IOException e) {
				// ignore
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						CoreNotificationHelper.notifyMisconfiguration(
							"An IOException was thrown while loading properties from '"
							+ resource + "'.", e);
					}
				}
			}
		}
		
		return properties;
	}
	/**
	 * Resolves the given file name to an absolute file name and then loads the
	 * properties from this file to a Properties Object.
	 *
	 * @param inputFileName
	 *            The file(s) which will be loaded
	 * @return the Properties Object
	 */
	public Properties loadProperties(String inputFileName) {
		// downwards compatibility: append "file:" if it is missing and filename exists
		String resourceName = inputFileName;
		if (new File(inputFileName).exists() && !inputFileName.startsWith("file:")) {
			resourceName = "file:" + resourceName; 
		}

		ListResourcePatternResolverDecorator resolver = new ListResourcePatternResolverDecorator(
			new ManifestOrderedConfigLocationProvider(),
			new OrderedPathMatchingResourcePatternResolver());
		// most specific resource has to be last, because later properties will overwrite previously set ones.
		resolver.setMostSpecificResourceLast(true);
		resolver.setMergeWithOuterResources(true);
		
		try {
			return loadPropertiesFromResources(resolver.getResources(resourceName));
		} catch (IOException e) {
			CoreNotificationHelper.notifyMisconfiguration(
				"An IOException was thrown. The responsible file is '"
				+ inputFileName + "'.", e);
			return null;
		}
	}

	/**
	 * Resolves the given file name to an absolute file name and then stores the
	 * properties from the Properties Object to this file.
	 *
	 * @param props
	 *            The Properties Object
	 * @param outputFileName
	 *            The file where the data is stored
	 */
	public void storeProperties(Properties props, String outputFileName) {

		PathMatchingResourcePatternResolver pmrpr
			= new PathMatchingResourcePatternResolver();

		String fileName = null;

		Resource res = pmrpr.getResource(outputFileName);
		OutputStream out = null;
		try {
			try {
				// Resolve the resource into an absolute file path
				fileName = res.getURL().getFile();
			} catch (FileNotFoundException e) {
				// The file is new
				File file = new File(outputFileName);
				fileName = file.getAbsolutePath();
			}
			out = new FileOutputStream(fileName);
			props.store(out, "Title");
		} catch (FileNotFoundException e) {
			CoreNotificationHelper.notifyMisconfiguration(
					"The file '" + outputFileName + "' could not be found.", e);
		} catch (IOException e) {
			CoreNotificationHelper.notifyMisconfiguration(
					"An IOException was thrown. The responsible file is '"
					+ outputFileName + "'.", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					CoreNotificationHelper.notifyMisconfiguration(
						"The file '" + outputFileName
						+ "' could not be close.", e);
				}
			}
		}

	}

}