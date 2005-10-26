/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.buildsystem.tools.distribution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.velocity.VelocityContext;

/**
 * This task generates script files for a executable distribution.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class ScriptGeneratorTask extends Task {

    /** The different template files to generate script files. */
    public static final String[] TEMPLATES = {
        "templates/batchfile.vm",
        "templates/shellscript.vm"
    };
    
    /**
     * The file suffixes for the scripts generated using the above templates.
     * */
    public static final String[] FILE_SIFFIXES = {
        "bat",
        "sh"
    };

    /** The delimiters used to pars a given path. */
    public static final String DELIMITERS = ":;";
    
    /** The delimiter used to separate classpath items on UNIX. */
    public static final String DELIMITER_UNIX = ":";
    
    /** The delimiter used to separate classpath items on Windows. */
    public static final String DELIMITER_WINDOWS = ";";
    
    /** The folder where libraries were copied to. */
    public static final String LIB_DIRECTORY = "lib";
    
    /** The classpath items. */
    private String[] m_classpathItems;
    
    /** The class name which main method is invoked. */
    private String m_mainClass;
    
    /** The path where the scripts are written to. */
    private String m_path;
    
    /** The scripts' base name. */
    private String m_scriptName;
    
    /**
     * Sets the classpath used to invoke the distribution's main method.
     * <b>Note</b>: The path is converted into the distribution specific
     * directory structure.
     * 
     * @param classPath
     *      The classpath to set.
     */
    public void setClassPath(String classPath) {
        List list = new ArrayList();
        String path = classPath.replaceAll("\\\\", "/");
        StringTokenizer tokenizer = new StringTokenizer(path, DELIMITERS);
        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            int idx = nextToken.lastIndexOf("/");
            list.add(
                    LIB_DIRECTORY + "/" + nextToken.substring(idx + 1));
        }
        m_classpathItems = (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * @return Returns the class name which main method has to be invoked.
     */
    public String getMainClass() {
        return m_mainClass;
    }

    /**
     * Sets the class name which main method is executed.
     * 
     * @param mainClass
     *      The fully qualified class name.
     */
    public void setMainClass(String mainClass) {
        m_mainClass = mainClass;
    }

    /**
     * @return Returns the path where the scripts are written to.
     */
    public String getPath() {
        return m_path;
    }

    /**
     * Sets the path where the scripts are written to.
     * 
     * @param path
     *      The path where the scripts are written to.
     */
    public void setPath(String path) {
        m_path = path;
    }

    /**
     * @return Returns the script name.
     */
    public String getScriptName() {
        return m_scriptName;
    }

    /**
     * Sets the name of the scripts to be generated without any file extension.
     * 
     * @param scriptName
     *      The script name.
     */
    public void setScriptName(String scriptName) {
        m_scriptName = scriptName;
    }
    
    /**
     * @return Returns a newly created velocity context that contains all the
     *      needed properties to generate the script files.
     */
    private VelocityContext createVelocityContext() {
        VelocityContext context = new VelocityContext();
        context.put("classpathWindows",
                buildPlatformSpecificClasspath(DELIMITER_WINDOWS));
        context.put("classpathUnix",
                buildPlatformSpecificClasspath(DELIMITER_UNIX));
        context.put("mainClass", getMainClass());
        return context;
    }
    
    /**
     * Builds the platform-specific classpath.
     * 
     * @param delimiter
     *      The delimiter to separate different path elements.
     *      
     * @return Returns the platform-specific classpath.
     */
    private String buildPlatformSpecificClasspath(String delimiter) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < m_classpathItems.length; i++) {
            buffer.append(m_classpathItems[i]);
            buffer.append(delimiter);
        }
        buffer.append(".");
        return buffer.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws BuildException {
        if (m_classpathItems == null) {
            throw new BuildException("Classpath not declared.");
        } else if (m_mainClass == null) {
            throw new BuildException("Main class not declared.");
        }
        
        VelocityContext context = createVelocityContext();
        
        VelocityHelper helper = new VelocityHelper();
        helper.setProjectLogger(getProject(), this);
        
        for (int i = 0; i < TEMPLATES.length; i++) {
            String path = m_path + "/" + m_scriptName + "." + FILE_SIFFIXES[i];
            log("Writing scritp file to '" + path + "'", Project.MSG_DEBUG);
            File f = new File(path);
            helper.writeDataToFile(f, TEMPLATES[i], context);
        }
    }
}
