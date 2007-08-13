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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * A helper class which handles the loading and storing of Properties to/from
 * files including Spring path resolving.
 * 
 * <p>
 * The files can be indicated absolutely or via classpath, i.e. either by
 * "file:C:/folder/..." or by "classpath:folder/...".
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Raphael Boog (RBO)
 */
public class PropertiesHelper {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(PropertiesHelper.class);

    /**
     * Resolves the given file name to an absolute file name and then loads the
     * properties from this file to a Properties Object.
     * 
     * @param inputFileName
     *            The file which will be loaded
     * @return the Properties Object
     */
    public Properties loadProperties(String inputFileName) {

        Properties props = new Properties();

        PathMatchingResourcePatternResolver pmrpr 
            = new PathMatchingResourcePatternResolver();

        Resource res = pmrpr.getResource(inputFileName);
        InputStream in = null;

        try {
            // Load the properties into the Properties object
            try {
                in = res.getInputStream();
            } catch (IOException e) {
                File file = new File(inputFileName);
                in = new FileInputStream(file);
            }
            props.load(in);
        } catch (IOException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "An IOException was thrown. The responsible file is '"
                    + inputFileName + "'.", e);
        }

        return props;
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

        try {
            try {
                // Resolve the resource into an absolute file path
                fileName = res.getURL().getFile();
            } catch (FileNotFoundException e) {
                // The file is new
                File file = new File(outputFileName);
                fileName = file.getAbsolutePath();
            }
            props.store(new FileOutputStream(fileName), "Title");
        } catch (FileNotFoundException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "The file '" + outputFileName + "' could not be found.", e);
        } catch (IOException e) {
            CoreNotificationHelper.notifyMisconfiguration(
                    "An IOException was thrown. The responsible file is '"
                    + outputFileName + "'.", e);
        }

    }

}