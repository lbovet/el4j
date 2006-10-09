/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.metadata;

/**
 * <p>
 * This class describes the configuration of the meta data inheritance. With
 * a configuratin object it is possible to define how deep meta data will
 * be inherited to its childs. For example can be defined, if superclasses 
 * inherit its meta data to its childs or not. The possible configurations
 * are described in chapter '3 Documentation for module core' of the 
 * <code><a href="http://el4j.sourceforge.net/docs/pdf/ReferenceDoc.pdf">
 * el4j reference documentation</a></code></p>
 * 
 * <p>
 * The child meta data overwrides parent meta data.</p>
 * 
 * <p><b>Example</b><br />
 * A class uses the annotation <code>@ExampleAnnotationOne("Class")</code> and 
 * its method uses <code>@ExampleAnnotationOne("Method")</code>. In this case 
 * a method interceptor got the value <code>Method</code> to proceed.</p>
 * 
 * <p><b>Default configuration</b><br />
 * The public fields containig the default configuration. The default
 * configuration is also described in the el4j reference documentation 
 * (cf. section 1 of class description).</p>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Häfeli (ADH)
 */
public class DefaultInheritanceConfiguration implements InheritanceConfiguration {
    
    /**
     * @see #setIncludePackages(boolean)
     */
    public boolean includePackages = false;
        
    /**
     * @see #setIncludeSuperclasses(boolean)
     */
    public boolean includeSuperclasses = false;
    
    /**
     * @see #setIncludeInterfaces(boolean, boolean)
     */
    public boolean includeInterfaces = true;
        
    /**
     * @see #setIncludeClass(boolean)
     */
    public boolean includeClass = true;
    

    /**
     * <p>
     * Defines the meta data inheritance from classes.<p>
     * 
     * <p><b>Note</b><br />
     * At the moment is just the inheritance to methods implemented. Later
     * on it is possible that this configuration inherit the class meta
     * data also to fields and constructors.</p>
     * 
     * @param includeClass
     *                  <code>true</code> if classes has to 
     *                  inherit its meta data to its methods,
     *                  <code>false</code> otherwise.
     */
    public void setIncludeClass(boolean includeClass) {
        this.includeClass = includeClass;
    }

    /**
     * <p>
     * Defines the meta data inheritance from interfaces.</p>
     * 
     * <p>
     * If the inheritance of interface meta data is set <code>true</code>, 
     * meta data will also inherited to subclasses of classes which 
     * implements the interface.
     * 
     * @param includeInterfaces
     *                        <code>true</code> if interfaces has to 
     *                        inherit its meta data to the classes 
     *                        which implements it, <code>false</code> otherwise.          
     */
    public void setIncludeInterfaces(boolean includeInterfaces) {
        this.includeInterfaces = includeInterfaces;
    }

    /**
     * Defines the meta datainheritance from superclasses.
     * 
     * @param includeSuperclasses
     *                        <code>true</code> if superclasses has to 
     *                        inherit its meta data to the classes 
     *                        which extends from it, <code>false</code> 
     *                        otherwise.
     */
    public void setIncludeSuperclasses(boolean includeSuperclasses) {
        this.includeSuperclasses = includeSuperclasses;
    }  
    
    /**
     * Defines the meta datainheritance from packages.
     * 
     * @param includePackages
     *                        <code>true</code> if packages has to 
     *                        inherit its meta data to the classes 
     *                        and interfaces in the package and its subpackes, 
     *                        <code>false</code> otherwise.
     * @deprecated Not yet implemented
     */
    @Deprecated
    public void setIncludePackages(boolean includePackages) {
        this.includePackages = includePackages;
    }
}
