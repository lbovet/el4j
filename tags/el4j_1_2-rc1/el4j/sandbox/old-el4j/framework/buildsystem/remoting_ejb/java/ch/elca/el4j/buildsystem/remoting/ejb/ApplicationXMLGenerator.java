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
package ch.elca.el4j.buildsystem.remoting.ejb;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.velocity.VelocityContext;

import ch.elca.el4ant.velocity.VelocityEngineTask;

/**
 * This task generates the ear deployment descriptor (application.xml).
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Nicolas Schiper (NSC)
 */
public class ApplicationXMLGenerator extends Task {

    /** The application template's location. */
    public static final String APPLICATIONXML_TEMPLATE_FILE
        = "templates/application.vm";
    
    /** The location where the deployment descriptor is written to. */
    private String m_location;
    
    /** The name of the module. */
    private String m_moduleName;
    
    /** The name of the execution unit. */
    private String m_euName;

    /**
     * Sets the module name.
     * 
     * @param moduleName
     *      The module name to set.
     */
    public void setModuleName(String moduleName) {
        this.m_moduleName = moduleName;
    }

    /**
     * Sets the execution unit's name.
     * 
     * @param euName
     *      The execution unit name to set.
     */
    public void setEuName(String euName) {
        this.m_euName = euName;
    }
    
    /**
     * Sets the location of the file, where the deployment descriptor is
     * written to.
     * 
     * @param file
     *      The deployment descriptor's location.
     */
    public void setLocation(String file) {
        m_location = file;
    }

    /**
     * Generate the ear deployment descriptor (application.xml).
     * 
     * @throws BuildException
     *      If something goes wrong.
     */
    public void execute() throws BuildException {
        VelocityContext context = new VelocityContext();
        context.put("moduleName", m_moduleName);
        context.put("euName", m_euName);
        
        File file = new File(m_location);
        
        VelocityEngineTask velocityEngine = new VelocityEngineTask();
        velocityEngine.setProject(getProject());
        velocityEngine.setFile(file);
        velocityEngine.setTemplate(APPLICATIONXML_TEMPLATE_FILE);
        velocityEngine.setContext(context);
        velocityEngine.execute();
    }
}