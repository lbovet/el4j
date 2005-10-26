/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.services.remoting.protocol.ejb.generator;

/**
 * This interface represents a spring bean's meta info that allow to build
 * an EJB wrapper. 
 *
 * <p/><b>Important</b>: This class is duplicated into the build system plug in
 * that creates the wrapper classes. This minimizes the usage of reflection
 * mechanisms to a minimum.
 * 
 * <p/><b>Copy this interface definition to the build system plugin that crates
 * the EJB classes after each modification.</b>
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
public interface EjbBean {

    /**
     * @return Returns the name of the service's package.
     */
    public String getServicePackage();
    
    /**
     * @return Returns the service interface's package or the the package of its
     *      enriched version.
     */
    public String getInterfacePackage();
    
    /**
     * @return Returns the service's name.
     */
    public String getServiceName();
    
    /**
     * @return Returns whether the underlying bean is exported with context
     *      passing enabled.
     */
    public boolean isContextPassingAvailable();
    
    /**
     * @return Returns whether the wrapper shall be realized as a stateful or
     *      stateless session bean.
     */
    public boolean isStateful();
    
    /**
     * @return Returns a string consisting of all inclusive configuration
     *      locations.
     */
    public String getInclusiveLocations();
    
    /**
     * @return Returns a string consisting of all exclusive configuration
     *      locations.
     */
    public String getExclusiveLocations();
    
    /**
     * @return Returns the exporter bean's name.
     */
    public String getExporterBeanName();
    
    /**
     * The configuration object is used to provide additional properties related
     * with a specific protocol.
     * 
     * <p/><b>Note</b>: The method will always return a
     * {@link
     * ch.elca.el4j.services.remoting.protocol.ejb.EjbConfigurationObject}.
     * But since this class is not directly accessible by the build system
     * plugin, the method is declared to return an object. 
     * 
     * @return Returns the configuration object associated with the underlying
     *      services.
     */
    public Object getConfigurationObject();
    
    /**
     * {@link MethodSignature}s transform {@link java.lang.reflect.Method}s into
     * different string representations needed to put them into java source
     * code.
     * 
     * <p/><b>Note</b>: The method will always return an array of
     * {@link MethodSignature}s. But since this class is not directly
     * accessible by the build system plugin, the method is declared to return
     * an array of objects.
     * 
     * @return Returns the class' methods, prepared to be used in java source
     *      code. 
     */
    public Object[] getMethodSignatures();
    
    /**
     * @return Returns the XDoclet comment associated with this bean class.
     *      Notice: The whole comment is returned.
     */
    public String getXDocletTags();
}
