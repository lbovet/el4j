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

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.velocity.VelocityContext;

import ch.elca.el4j.buildsystem.remoting.ejb.util.VelocityHelper;
import ch.elca.el4j.services.remoting.protocol.ejb.generator.EjbBean;
import ch.elca.el4j.services.remoting.protocol.ejb.generator.EjbGeneratorFacade;

/**
 * This task generates EJB business implementations of Spring beans.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Nicolas Schiper (NSC)
 * @author Andreas Bur (ABU)
 */
public class EjbSessionBeanGenerator extends Task {
    
    /** The class name of the EJB generator facade implementation. */
    private static final String EJB_GENERATOR_FACADE_IMPL_CLASS_NAME
        = "ch.elca.el4j.services.remoting.protocol.ejb.generator."
            + "EjbGeneratorFacadeImpl";
    
    /** The stateful EJB template file. */
    private static final String STATEFUL_EJB_TEMPLATE_FILE
        = "templates/statefulSessionBean.vm";
    
    /** The stateless EJB template file. */
    private static final String STATELESS_EJB_TEMPLATE_FILE
        = "templates/statelessSessionBean.vm";
    
    /** The project classpath. */
    private Path m_classPath;
    
    /** The name of the execution unit. */
    private String m_euName;
    
    /** Comma separated list of Spring config locations to exclude. */
    private String m_exclusiveLocations;
    
    /** The directory where the EJBs are generated. */
    private String m_genDir;
    
    /** Comma separated list of Spring config locations to include. */
    private String m_inclusiveLocations;

    /** The name of the module. */
    private String m_moduleName;
    
    /**
     * Creates a new EJB generator facade.
     * 
     * @return Returns a new EJB generator facade.
     * 
     * @throws ClassNotFoundException
     *      If the EJB generator class cannot be found.
     *      
     * @throws IllegalAccessException
     *       If the class or its nullary constructor is not accessible.

     * @throws InstantiationException
     *      if this Class represents an abstract class, an interface, an array
     *      class, a primitive type, or void; or if the class has no nullary
     *      constructor; or if the instantiation fails for some other reason.

     */
    private EjbGeneratorFacade createEjbGeneratorFacade() 
        throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        
        Class clazz = Class.forName(EJB_GENERATOR_FACADE_IMPL_CLASS_NAME);
        return (EjbGeneratorFacade) clazz.newInstance();
    }
    
    /**
     * Creates a session bean.
     * 
     * @param bean
     *      The meta infos to create a session bean with.
     *      
     * @throws Exception when something went wrong.
     */
    private void createWrapper(EjbBean bean) throws Exception {
        String template = bean.isStateful() ? STATEFUL_EJB_TEMPLATE_FILE
                : STATELESS_EJB_TEMPLATE_FILE;

        VelocityContext context = buildVelocityContext(bean);
        File file = getFile(bean);
        
        VelocityHelper helper = new VelocityHelper();
        helper.setProjectLogger(getProject(), this);
        helper.writeDataToFile(file, template, context);
    }

    /**
     * Creates a file for the given EJB bean description, where the generated
     * class is written to (by Velocity). The directory is created if it does
     * not already exist.
     * 
     * @param bean
     *      The EJB bean definition to create the files for.
     *      
     * @return Returns the file where the EJB session bean is written to (by
     *      Velocity).
     */
    private File getFile(EjbBean bean) {
        String fileName = bean.getServiceName();
        String ejbPkgPath = bean.getInterfacePackage().replaceAll("\\.", "/");
        ejbPkgPath = ejbPkgPath + "/ejb/";

        // Create the EJB subdirectory
        String dir = this.m_genDir + "/" + ejbPkgPath;
        File file = new File(dir + fileName + ".java");
        file.getParentFile().mkdirs();
        return file;
    }

    /**
     * Generates the velocity context used to fill the templates.
     * 
     * @param bean
     *      The bean which the context is built for.
     *      
     * @return Returns a velocity context for the given bean.
     */
    private VelocityContext buildVelocityContext(EjbBean bean) {
        VelocityContext context = new VelocityContext();
        context.put("inclusiveLocations", bean.getInclusiveLocations());
        context.put("exclusiveLocations", bean.getExclusiveLocations());
        context.put("EJBServiceName", bean.getServiceName());
        context.put("exporterBeanName", bean.getExporterBeanName());
        context.put("interfacePckg", bean.getInterfacePackage());
        context.put("methods", bean.getMethodSignatures());
        context.put("configurationObject", bean.getConfigurationObject());
        context.put("XDoclet", bean.getXDocletTags());
        context.put("contextPassing", 
                Boolean.valueOf(bean.isContextPassingAvailable()));
        return context;
    }
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws BuildException {

        if (m_inclusiveLocations == null) {
            throw new BuildException(getTaskName()
                    + " requires 'inclusiveLocations' attribute.");
        }

        AntClassLoader acl = (AntClassLoader) this.getClass().getClassLoader();
        
        Path p = new Path(getProject(), acl.getClasspath());
        p.append(this.m_classPath);
        acl.setClassPath(p);
        acl.setThreadContextLoader();
        
        try {
            EjbGeneratorFacade ejbGeneratorFacade = createEjbGeneratorFacade();
            EjbBean[] beans = ejbGeneratorFacade.getEjbBeans(
                    m_inclusiveLocations, m_exclusiveLocations);
            for (int i = 0; i < beans.length; i++) {
                createWrapper(beans[i]);
            }
        } catch (Exception e) {
            throw new BuildException("Error while generating the EJB(s) of "
                    + "module: " + m_moduleName + " eu: " + m_euName, e);
        }
        
        acl.resetThreadContextLoader();
    }
    
    /**
     * @return Returns the current execution unit's name.
     */
    public String getEuName() {
        return this.m_euName;
    }


    /**
     * @return Returns a comma separated list of configuration locations to
     *      exclude.
     */
    public String getExclusiveLocations() {
        return m_exclusiveLocations;
    }

    /**
     * @return Returns the directory name where generated files are stored in.
     */
    public String getGenDir() {
        return this.m_genDir;
    }

    /**
     * @return Returns a comma separated list of configuration locations to
     *      include.
     */
    public String getInclusiveLocations() {
        return m_inclusiveLocations;
    }

    /**
     * @return Returns the current module's name
     */
    public String getModuleName() {
        return this.m_moduleName;
    }

    /**
     * Sets the m_classPath value. Optional.
     * @param newClasspath The new CompileClasspath value.
     */
    public void setClasspath(Path newClasspath) {
        if (this.m_classPath == null) {
            this.m_classPath = newClasspath;
        } else {
            this.m_classPath.append(newClasspath);
        }
    }
    
    /**
     * Sets the current execution unit's name.
     * 
     * @param euName
     *      The execution unit name to set.
     */
    public void setEuName(String euName) {
        this.m_euName = euName;
    }


    /**
     * Sets the configuration locations to exclude.
     * 
     * @param exclusiveLocations
     *      Comma separated list of config locations to exclude.
     */
    public void setExclusiveLocations(String exclusiveLocations) {
        this.m_exclusiveLocations = exclusiveLocations;
    }

    /**
     * Sets the direcotry's name where generated files are stored in.
     * 
     * @param genDir
     *  The direcotry's name to store generated files in.
     */
    public void setGenDir(String genDir) {
        this.m_genDir = genDir;
    }

    /**
     * Sets the configuration locations to include.
     * 
     * @param inclusiveLocations
     *      Comma separated list of config locations to include.
     */
    public void setInclusiveLocations(String inclusiveLocations) {
        this.m_inclusiveLocations = inclusiveLocations;
    }
    
    /**
     * Sets the current module's name.
     * 
     * @param moduleName
     *      The module name to set.
     */
    public void setModuleName(String moduleName) {
        this.m_moduleName = moduleName;
    }
}
