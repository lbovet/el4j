/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.core.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;

/**
 * This class allows excluding some items out of a file list.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class ModuleApplicationContextUtils {

    /**
     * String to find all spring configuration files in folder 
     * <code>mandatory</code>.
     */
    private static final String MANDATORY = "mandatory/*.xml";
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            ModuleApplicationContextUtils.class);
    
    /** The application context that uses this instance. */
    private ApplicationContext m_appContext;
    
    /**
     * @see #setReverseConfigLocationResourceArray(boolean)
     */
    private boolean m_reverseConfigLocationResourceArray = false;
    
    /**
     * Creates a new instance that is connected to the given application
     * contet.
     * 
     * @param context
     *          The application context to connect to.
     */
    public ModuleApplicationContextUtils(ApplicationContext context) {
        m_appContext = context;
    }
    
    /**
     * Calculate the array of xml configuration files which are loaded into the
     * ApplicationContext, i.e. exclude the xml files in inclusiveFileNames
     * which are in exclusiveFileNames.
     * 
     * @param inclusiveConfigLocations
     *            array of file paths
     * @param exclusiveConfigLocations
     *            array of file paths which are excluded
     * @param allowBeanDefinitionOverriding
     *            a boolean which defines if overriding of bean definitions is
     *            allowed
     * @return Returns the adapted list of configuration locations.
     */
    public String[] calculateInputFiles(String[] inclusiveConfigLocations,
            String[] exclusiveConfigLocations,
            boolean allowBeanDefinitionOverriding) {
        
        if (ArrayUtils.isEmpty(inclusiveConfigLocations)) {
            s_logger.warn("No inclusive configuration locations given!");
            return null;
        }

        checkConfigLocations(inclusiveConfigLocations[0]);
        
        List<String> inclusiveFileNames 
            = getResolvedFileNames(inclusiveConfigLocations);

        List<String> exclusiveFileNames 
            = getResolvedFileNames(exclusiveConfigLocations);

        //remove the xml files in inclusiveFileNames which are in
        // exclusiveFileNames
        for (int i = 0; i < inclusiveFileNames.size(); i++) {
            Object obj = inclusiveFileNames.get(i);
            if (exclusiveFileNames.contains(obj)) {
                inclusiveFileNames.remove(i);
                i--;
            }
        }

        String[] conLoc = new String[inclusiveFileNames.size()];

        for (int i = 0; i < inclusiveFileNames.size(); i++) {
            conLoc[i] = (String) inclusiveFileNames.get(i);
        }

        return conLoc;
    }
    
    /**
     * Check whether the 'classpath*:mandatory/*.xml' config location is loaded.
     * 
     * @param configLocation
     *            The config location
     */
    protected void checkConfigLocations(String configLocation) {
        if (!(configLocation.equals("classpath*:" + MANDATORY)
                || (configLocation.equals("classpath*:/" + MANDATORY)))) {

            s_logger.warn("The config location 'classpath*:" + MANDATORY 
                    + "' is not loaded or is not the first config location"
                    + " which is loaded.");
        }
    }

    /**
     * Changes the syntax of the pathnames, i.e. filepaths beginning with
     * "file:$Drive" and not with "file:/$Drive" are changed and "\" characters
     * are changed to "/". This is necessary for the
     * PathMatchingResourcePatternResolver to resolve ant-style filepaths.
     * 
     * @param unresolvedFileNames Are the names of unresolved file names.
     * @return Returns a list of resolved file names.
     */
    protected List<String> getResolvedFileNames(String[] unresolvedFileNames) {

        List<String> result = new ArrayList<String>();

        if (unresolvedFileNames == null) {
            return result;
        }

        for (int i = 0; i < unresolvedFileNames.length; i++) {
            String[] resolvedFileNames = resolveAttribute(unresolvedFileNames[i]
                    .replace('\\', '/'));
            for (int j = 0; j < resolvedFileNames.length; j++) {
                if ((resolvedFileNames[j].startsWith("file:"))
                        && (!resolvedFileNames[j].startsWith("file:/"))) {
                    resolvedFileNames[j] = resolvedFileNames[j].replaceFirst(
                            "file:", "file:/");
                }
                result.add(resolvedFileNames[j]);
            }
        }
        return result;
    }

    /**
     * Resolves a path (i.e. file- or classpath) by applying Ant-style path
     * matching. Returns all resolved xml files. A warning will be displayed if
     * a resource does not exist.
     * 
     * @param path
     *            a path of an xml file, either absolute, relative or Ant-style
     * @return all resolved xml files
     */
    protected String[] resolveAttribute(String path) {
        List<String> resolvedAttributes = new ArrayList<String>();

        try {
            Resource[] resLocal = m_appContext.getResources(path);
            if (isReverseConfigLocationResourceArray()) {
                ArrayUtils.reverse(resLocal);
            }

            for (int i = 0; i < resLocal.length; i++) {
                if (resLocal[i].exists()) {
                    resolvedAttributes.add(resLocal[i].getURL().toString());
                } else {
                    s_logger.warn("The file '" + resLocal[i].toString()
                            + "' does not exist.");
                }
            }
        } catch (IOException e) {
            String message = "An IOException has occurred.";
            CoreNotificationHelper.notifyMisconfiguration(message, e);
        }

        String[] result = new String[resolvedAttributes.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = (String) resolvedAttributes.get(i);
        }
        return result;
    }

    /**
     * @return Returns the reverseConfigLocationResourceArray.
     */
    public boolean isReverseConfigLocationResourceArray() {
        return m_reverseConfigLocationResourceArray;
    }

    /**
     * Flag to indicate if the resource array of a config location should be
     * reversed. The default is set to <code>false</code>.
     * 
     * @param reverseConfigLocationResourceArray
     *            Is the reverseConfigLocationResourceArray to set.
     */
    public void setReverseConfigLocationResourceArray(
        boolean reverseConfigLocationResourceArray) {
        m_reverseConfigLocationResourceArray 
            = reverseConfigLocationResourceArray;
    }
}
