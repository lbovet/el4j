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

import java.util.Properties;

import org.apache.tools.ant.Project;

import ch.elca.el4ant.model.Attribute;
import ch.elca.el4ant.model.ExecutionUnit;
import ch.elca.el4ant.model.HookTask;
import ch.elca.el4ant.model.Module;
import ch.elca.el4ant.model.ProjectRepository;

/**
 * This class adds a hook to the execution unit to create, XDoclet and compile
 * EJB session beans.
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
public class GeneratorInitTask extends AbstractEjbInitTask {

    /** The environment's key. */
    public static final String ENVIRONMENT = "environment";
    
    /** The name of the application server attribute. */
    public static final String APPLICATION_SERVER = "j2ee-ejb.container";
    
    /** The name of the deployment descriptor property. */
    public static final String EAR_DD = "j2ee.ear.dd";
    
    /** The ant target to generate the EJB session beans. */
    public static final String GENERATE_EJB_TARGET
        = "remoting_ejb.generate.module.eu";
    
    /** Path where the generated session beans are stored. */
    private String m_genPath;
    
    /**
     * Sets the path where generated session beans and necessary files are
     * stored.
     *  
     * @param genPath
     *      The path.
     */
    public void setGenPath(String genPath) {
        m_genPath = genPath;
    }

    /**
     * {@inheritDoc}
     */
    public void execute() {
        super.execute();
        initEnvironment();
    }

    /**
     * Loads remoting_ejb specific environment variables, if environment support
     * is enabled.
     */
    private void initEnvironment() {
        ProjectRepository pr = ProjectRepository.getInstance();
        Properties environment = (Properties) pr.getUserData(ENVIRONMENT);
        if (environment != null) {
            String appServer = environment.getProperty(APPLICATION_SERVER);

            log("loading remoting_env support for container [" 
                    + appServer + "]", Project.MSG_VERBOSE);
            
            getProject().setProperty(APPLICATION_SERVER, appServer);
        } else {
            log("Environment support deactivated", Project.MSG_VERBOSE);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void configureEu(Module module, ExecutionUnit eu) {
        addPostCompileHook(eu);
        addEarDeploymentDescriptorAttribute(module, eu);
    }

    /**
     * Adds the post compile hook that generates and compiles the EJB session
     * beans.
     * 
     * @param eu
     *      The execution unit to add the hook to.
     */
    private void addPostCompileHook(ExecutionUnit eu) {
        HookTask hook = new HookTask();
        hook.setProject(this.getProject());
        hook.setTaskName(this.getTaskName());
        hook.setAction(HookTask.APPEND_LAST);
        hook.setName(POST_COMPILE_HOOK);
        HookTask genHook = hook;
        genHook.setTarget(GENERATE_EJB_TARGET);
        eu.addHook(genHook);
    }

    /**
     * Adds the deployment descriptor attribute to the given execution unit.
     * 
     * @param module
     *      The module that contains the execution unit.
     *      
     * @param eu
     *      The execution unit to add the attribute to.
     */
    private void addEarDeploymentDescriptorAttribute(
            Module module, ExecutionUnit eu) {
        Attribute attr = new Attribute();
        attr.setProject(getProject());
        attr.setName(EAR_DD);
        attr.setValue(m_genPath + "/" + module.getModuleName()
                + "-" + eu.getName() + "/application.xml");
        eu.addAttribute(attr);
    }
}
