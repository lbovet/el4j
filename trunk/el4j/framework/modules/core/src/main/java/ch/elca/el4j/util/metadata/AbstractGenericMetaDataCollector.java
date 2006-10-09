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

import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;

/**
 * <p>
 * The <code>AbstractGenericMetaDataCollector</code> implements the collection
 * of meta data. The class is used to define collectors for specific meta data 
 * types, for example annoations.</p>
 * 
 * The collection considers inheritance, like its configuration
 * {@link InheritanceConfiguration}, 
 * {@link #setInheritenceConfiguration(InheritanceConfiguration)}. If no
 * configuration is set, following default is used. From a method will got
 * the meta data of the method, its class and all interfaces (also the 
 * interfaces on superclasses). The following example illustrates that:
 * 
 * <pre>
 * &#47;**
 *  * &#64;&#64;AttributeClassX()
 *  *&#47;
 * public interface X {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodX()
 *      *&#47;
 *     public int getValue();
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassY()
 *  *&#47;
 * public interface Y {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodY()
 *      *&#47;
 *     public int getValue();
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassA()
 *  *&#47;
 * public class A implements X {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodA()
 *      *&#47;
 *     public int getValue() {...}
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassB()
 *  *&#47;
 * public class B extends A implements Y {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodB()
 *      *&#47;
 *     public int getValue() {...}
 * }
 * 
 * &#47;**
 *  * &#64;&#64;AttributeClassC()
 *  *&#47;
 * public class C extends B {
 *     &#47;**
 *      * &#64;&#64;AttributeMethodC()
 *      *&#47;
 *     public int getValue() {...}
 * }
 * </pre>
 * 
 * If you would like to get attributes from class <code>C</code> you will get
 * the following:
 * <ul>
 * <li>AttributeClassC</li>
 * <li>AttributeClassY</li>
 * <li>AttributeClassX</li>
 * </ul>
 * 
 * Analog, if you would like to get attributes from method
 * <code>getValue()</code> of class <code>C</code> you will get the
 * following:
 * <ul>
 * <li>AttributeClassC</li>
 * <li>AttributeMethodC</li>
 * <li>AttributeMethodY</li>
 * <li>AttributeMethodX</li>
 * </ul>
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Adrian Haefeli(ADH)
 */
public abstract class AbstractGenericMetaDataCollector 
    implements GenericMetaDataCollector {

    /**
     * Private logger of this class.
     */
    protected static Log s_logger = LogFactory
            .getLog(AbstractGenericMetaDataCollector.class);
    /**
     * Canonical value held in m_cache to indicate no attribute was found for
     * this method, and we don't need to look again.
     */
    protected static final Object NULL_ATTRIBUTE = new Object();
       
    /**
     * Cache of Attributes, keyed by Method and target class.
     */
    protected Map m_cache = new HashMap <String, Collection>();
    
    /**
     * List of attribute types, which has to be returnde by
     * all getAttributes methods (for example
     * {@link #getMethodOperatingMetaData(MethodInvocation)}.
     * 
     * @see #setInterceptingMetaData(List)
     */
    protected List <Class> m_interceptingMetaData = null;
    
    /**
     * Configuration of the inheritence. The default configuration
     * is {@link DefaultInheritanceConfiguration}.
     * 
     * @see #setInheritenceConfiguration(InheritanceConfiguration)
     */
    protected DefaultInheritanceConfiguration m_inheritanceConfiguration 
            = new DefaultInheritanceConfiguration();
    
    public enum collectionTarget {COLLECT_FROM_TARGET, //from target itself: e.g. from method
        COLLECT_FROM_CLASS, //if the target is a class
        COLLECT_FROM_PARENT_CLASS,
        COLLECT_FROM_PARENT_TARGET } //e.g. The same method in the interface

    /**
     * <p>
     * Returns the class name of the specified object.</p>
     * 
     * <p>The method is needed because following code returns not
     * the class name if the object is in a <code>ArrayList</code>:</p>
     * 
     * <pre>object.getClass().getName();</pre>
     * 
     * <p>The problem occured by the metaData type Annotation. The method
     * <code>object.getAnnotations()</code> returns an Array containing proxy
     * objects. To get the type name of these proxies it needs annother way as 
     * to get the type name of other metaData, like common attributes.</p>
     * 
     * <p>The implementation is based on the metaData type.</p>
     * 
     * @param object Meta data object, from which the class name has to be got.
     * 
     * @return Returns the (fully qualified) className of the specified 
     * meta data object.
     */
    protected abstract String getClassName(Object object);

    /**
     * <p>
     * MetaData Types can be specified for specific targets (e. g. Classes).
     * The method checks this specification on the specifiec 
     * <code>meta data</code> for the specified <code>elementType</code></p>.
     * 
     * <p><b>Example</b><br />
     * <code>@ExampleAnnotationOne</code> is defined to use on classes. The found 
     * metaData of a method containing this annotation. With the method clean 
     * can be checked if each meta data in the specified collection is allowed 
     * to use one the specified target. In this example the clean method
     * will remove the annotation <code>@ExampleAnnotationOne</code> from the 
     * specified meta data Collection, because the annotation is not allowed 
     * to us on methods.</p>
     * 
     * @param elementType 
     *              Specific target type (e. g. method, class, package) 
     *              of the specified metaData.
     * @param metaData 
     *              Found meta data for a specific target type (e. g. method).
     * 
     * @return Returns the meta data which are allowed to use on the 
     * specified target type.
     */
    protected abstract Collection clean(Collection metaData, 
        ElementType elementType);

    /**
     * {@inheritDoc}
     */
    public Collection getMetaData(Object target, ElementType elementType, 
        Class targetClass) {
    
        Object cacheKey = getCacheKey(target, targetClass);
        Collection metaData = (Collection) m_cache.get(cacheKey);
    
        // Check if the Attributes was already chached before
        if (metaData != null) {
            // Value will either be canonical value indicating there is no
            // attribute or an actual attribute
            if (metaData == NULL_ATTRIBUTE) {
                metaData = null;
            }
        } else {
            // Collect the meta data and cache it
            metaData = new ArrayList();
            metaData = computeMetaDataBasedOnType(metaData, target, 
                elementType, targetClass);
            cacheAttributes(metaData, cacheKey);
        }
        return metaData;
    }

    /**
     * {@inheritDoc}
     */
    public Collection getMetaData(Object target, 
        MethodInvocation methodInvocation, ElementType elementType) {
        
        Class targetClass = methodInvocation.getThis().getClass();
        return getMetaData(target, elementType, targetClass);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getMethodOperatingMetaData(Method targetMethod, 
        Class targetClass) {
        return getMetaData(targetMethod, ElementType.METHOD, targetClass);  
    }

    /**
     * {@inheritDoc}
     */
    public Collection getMethodOperatingMetaData(MethodInvocation methodInvocation) {
        Method targetMethod = methodInvocation.getMethod();
        Class targetClass = methodInvocation.getThis().getClass();
        return getMethodOperatingMetaData(targetMethod, targetClass);
    }

    /**
     * Caches the specified metaData under the specified cacheKey.
     * 
     * @param metaData the metaData to cache.
     * @param cacheKey the cache key.
     */
    protected void cacheAttributes(Collection metaData, Object cacheKey) {
        if (metaData == null) {
            m_cache.put(cacheKey, NULL_ATTRIBUTE);
        } else {
            m_cache.put(cacheKey, metaData);
        }
    }

    /**
     * Calculates the cache key of a certain target.
     * 
     * @param target
     *            target for the current invocation.
     * @param targetClass
     *            The invocated class.
     * @return Returns the cache key,
     */
    protected String getCacheKey(Object target, Class targetClass) {
        // Must not produce same key for overloaded methods
        // Must produce same key for different instances of the same method

        // TODO this works fine, but could consider making it faster in future:
        // Method.toString() is relatively (although not disastrously) slow
        return targetClass + "" + target;
    }

    /**
     * <p>
     * Adds new meta data found on the specified target and add it to the
     * specified Collection.
     * </p>
     * <p>
     * The meta data will searched based on the configured hierarchie
     * {@link #setInheritenceConfiguration(InheritanceConfiguration)}.
     * </p>
     * 
     * @param metaData
     *            <code>Collection</code> to which the found metaData will be
     *            added.
     * @param target
     *            Method on which metaData will be searched.
     * @param elementType
     *            ElementType of the target (e. g. Type, Method)
     * @param targetClass
     *            The class of the specified target.
     * @return Returns the specified metaData collection with the metaData found
     *         on the specified target.
     * @throws ClassCastException
     *             If the target does not corresponding to the specified
     *             elementType.
     * @throws UnsupportedOperationException
     *             If a elementType is specified, which is not yet supported.
     *             Refer to the section 'Pay Attention' for more details.
     */
    protected Collection computeMetaDataBasedOnType(Collection metaData,
        Object target, ElementType elementType, Class targetClass) {

        Target targetObject;

        switch (elementType) {

            case METHOD:
                targetObject = new Target(target, targetClass);
                metaData = computeMethodMetaData(metaData, targetObject);
                /*
                * Remove all metaData, which it's Target definition allows not 
                * the use of it on Methods.
                */
                metaData = clean(metaData, ElementType.METHOD);
                break;

            case TYPE:
                targetObject = new Target(target, null);
                metaData = computeClassMetaData(metaData, targetObject);
                /*
                * Remove all metaData, which it's Target definition allows not 
                * the use of it on Classes.
                */
                metaData = clean(metaData, ElementType.TYPE);
                break;

            default:
                String errorMsg = "The specified elementType '" + elementType
                        + "' does not exists";
                s_logger.error(errorMsg);
                throw new UnsupportedOperationException(errorMsg);
            }

        return metaData;
    }

    /**
     * @see #computeMetaDataBasedOnType(Collection, Object, ElementType, Class)
     * @param metaData
     *            <code>Collection</code> to which the found metaData will be
     *            added.
     * @param targetMethod
     *            Method on which metaData will be searched.
     * @param targetClass
     *            The class of the specified target.
     * @return Returns the specified metaData collection with the metaData found
     *         on the specified method.
     */
    protected Collection computeMethodMetaData(Collection metaData, 
        Target target) {
        
        Collection newMetaData = new ArrayList();
        Method targetMethod = (Method) target.getTarget();
        Class targetClass = target.getTargetClass();
        
        /* Define that the meta data will collected from the target,
         * in this case the methods.
         */
        target.setCollectionTarget(collectionTarget.COLLECT_FROM_TARGET);
        
         
        /* Get attributes from the most specif method
         * 
         * The method may be on an interface, but we need attributes from the
         * target class
         * The AopUtils class provides a convenience method for this. If the
         * target class is null, the AOPUtils returns the method given as 
         * argument.
         */
        Method specificMethod = AopUtils.getMostSpecificMethod(targetMethod,
            targetClass);
        newMetaData = getAttributes(specificMethod);
        /* Call is made because it can later be possible that the parameter 
           metaData contains already data */
        addNewMetaDataBasedOnHierarchie(metaData, newMetaData); 
        
        /* Get attributes from the target method */
        if(!specificMethod.equals(targetMethod)) {
            newMetaData = getAttributes(targetMethod);
            addNewMetaDataBasedOnHierarchie(metaData, newMetaData);
        }
        
        /* The collection target ar from now the corresponding method 
         * in the parent classes which is set by the following methods 
         * which search parent classes, interfaces and packages.
         */
        target.setCollectionTarget(collectionTarget.COLLECT_FROM_PARENT_TARGET);
        
        /* Get metaData from the methods on all superclasses */
        metaData = getMetaDataFromAllSuperclasses(metaData, 
            targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled 
           by getMetaDataFromAllSuperclasses */
    
        /* Get metaData from all interfaces */
        metaData = getMetaDataFromAllDirectInterfaces(metaData, 
            targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled 
           by getMetaDataFromAllInterfaces */
    
        /* Get metaData from all superclass interfaces */
        metaData = getMetaDataFromAllSuperclassInterfaces(metaData, 
            targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled 
           by getMetaDataFromAllSuperclassInterfaces */
        

        
        
        /* Get metaData from all superclasses, inferfacaces, superclass 
         * interfaces and packages */
        target.setTarget(targetClass);
        metaData = computeClassMetaData(metaData, target);
    
        return metaData;
    }

    /**
     * @see #computeMetaDataBasedOnType(Collection, Object, ElementType, Class)
     * 
     * @param metaData 
     *                  <code>Collection</code> to which the found metaData 
     *                  will be added.
     * @param targetClass 
     *                  Target class on which metaData will be searched.
     * @return Returns the specified metaData collection with the metaData 
     * found on the specified class.
     */
    private Collection computeClassMetaData(Collection metaData, 
        Target target) {
        
        Class targetClass = (Class) target.getTarget();
        
        /* Define that the meta data will collected from the class itself */
        target.setCollectionTarget(collectionTarget.COLLECT_FROM_CLASS);
        
        /*
         * Refactoring Note:
         * If later will implemented to search metaData on other elements 
         * (e.g. constructors), make further compute Methods. These call each 
         * other in a hierachical structure. Like method call class, 
         * class call package.
         */
        
        Collection newMetaData = new ArrayList();
        /* Get attributes from the class */
        if (m_inheritanceConfiguration.includeClass) {
            newMetaData = getAttributes(targetClass);
            addNewMetaDataBasedOnHierarchie(metaData, newMetaData);
        }
    
        /* The collection target are from now the parent classes
         * which is set by the following methods which search parent classes,
         * interfaces and packages.
         */
        target.setCollectionTarget(collectionTarget.COLLECT_FROM_PARENT_CLASS);
        
        /* Get metaData from all superclasses */
        metaData = getMetaDataFromAllSuperclasses(metaData, targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled 
           by getMetaDataFromAllSuperclasses */
    
        /* Get metaData from all interfaces */
        metaData = getMetaDataFromAllDirectInterfaces(metaData, targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled 
           by getMetaDataFromAllInterfaces */
    
        /* Get metaData from all superclass interfaces */
        metaData = getMetaDataFromAllSuperclassInterfaces(metaData, 
            targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled 
           by getMetaDataFromAllSuperclassInterfaces */
    
        /* Get metaData from all packages */
        // Method is not yet implemented; The return value is the parameter metaData
        metaData = getMetaDataFromAllPackages(metaData, targetClass, target);
        /* addNewMetaDataBasedOnHierarchie is areadyCalled
           by getMetaDataFromAllPackages */
    
        return metaData;
    }

    /**
     * <p>
     * Adds the new metaData (e. g. Annotations) to the existing set of 
     * metaData. The method respects the inheritance hierarchie.</p>
     * 
     * <p><b>Working of the Hiearchie checking</b><br />
     * <code>exampleMethod()</code> overwrides the <code>@AnnotationOne</code>
     * from the class. The parameter <code>metaDataBottomLevel</code> 
     * containing always meta data of the lower level, in this case the meta 
     * data of the <code>exampleMethod()</code>. The parameter 
     * <code>newMetaDataHigherLevel</code> containig always the meta data of a 
     * higher level, in this case the metaData of the <code>exampleClass</code>.
     * The return collection containig then all meta data from 
     * <code>metaDataBottomLevel</code> and the meta data from 
     * <code>newMetaDataHigherLevel</code>, which are not overwridden by the 
     * lower level. In this case, the return collection containing the following 
     * meta data: </p>
     * <ui>
     * <li>@AnnotationOne(2)   : Because method <code>exampleMethod</code> 
     * does overwride the corresponding annotation of the 
     * <code>exampleClass</code>. Therefore <code>@AnnotationOne(1)</code> 
     * will not be added to the return collection.</li>
     * <li>@AnnotationTwo(3)   : Already exists in collection 
     * <code>metaDataBottomLevel</code>.</li>
     * <li>@AnnotationThree(4) : Inherited from <code>exampleClass</code> 
     * (<code>exampleMethod</code> does not overwride the annotation).
     * Therefore <code>@AnnotationOne(4)</code> (which is in the parameter 
     * <code>newMetaDataHigherLevel</code>) will be added to the return 
     * collection.</li>
     * </ui><br />
     * 
     * <pre>
     * <code>@AnnotationOne(1)</code>
     * <code>@AnnotationThree(4)</code>
     * class exampleClass {
     * 
     * <code>@AnnotationOne(2)</code>
     * <code>@AnnotationTwo(3)</code>
     * public void exampleMethod() { //doSomething }
     * }
     * </pre>
     * 
     * @param metaDataBottomLevel 
     *                          Meta data (e. g. common attributes) of the 
     *                          lower level (because they can overwride a 
     *                          metaData of a higer level).
     * @param newMetaDataHigherLevel 
     *                          Meta data of the higher level.
     * 
     * @return Meta data of the lower Level completed with the inherited meta 
     * data of the specified higher level annotations.
     */
    protected Collection addNewMetaDataBasedOnHierarchie(Collection metaDataBottomLevel, 
        Collection newMetaDataHigherLevel) {
        
        
        String nameClassBLevel, nameClassHLevel;
        ArrayList overwroteMetaData = new ArrayList();
        for (Iterator iter = metaDataBottomLevel.iterator(); iter.hasNext();) {
            nameClassBLevel = getClassName(iter.next());          
            
            for (Iterator iterator = newMetaDataHigherLevel.iterator(); 
                iterator.hasNext();) {
                Object o = iterator.next();
                nameClassHLevel = getClassName(o);
    
                if (nameClassBLevel.equals(nameClassHLevel)) {
                    /* If the metaData (e.g annotation) does exist in a lower level, 
                     * it overwrides the definition on the higer level. Therefore
                     * this metaData in the higer level is not longer needed.
                     */
                    if (!overwroteMetaData.contains(o)) {
                        overwroteMetaData.add(o);
                    }
                }
            }
            
        }
        
        /* Remove all overwrote MetaData from newMetaDataHigherLevel, 
         * so that they will no longer mentioned.
         */
        newMetaDataHigherLevel.removeAll(overwroteMetaData);
        metaDataBottomLevel.addAll(newMetaDataHigherLevel);
        return metaDataBottomLevel;
    
    }

    /**
     * Gets the meta data on all superclasses from the specified 
     * <code>targetClass</code>. All superclasses means that all classes are 
     * mentioned which the <code>targetClass</code> is extended from directly 
     * or indirectly.
     * 
     * @param metaData 
     *                  Collection to which the found meta data 
     *                  (e. g. annotations) will be added.
     * @param targetClass 
     *                  The class to be analyzed.
     * 
     * @return Returns the specified meta data collection supplemented with 
     * the found metaData on the superclasses.
     */
    private Collection getMetaDataFromAllSuperclasses(Collection metaData,
        Class targetClass, Target target) {

        if (targetClass != null
            && m_inheritanceConfiguration.includeSuperclasses) {
            Class superClass = targetClass.getSuperclass();
            if (superClass != null) {
                target.setParentClass(superClass);
                Collection newMetaData = getAttributes(target);
                metaData = addNewMetaDataBasedOnHierarchie(metaData,
                    newMetaData);
                metaData = getMetaDataFromAllSuperclasses(metaData, 
                    superClass, target);
            }
        }
        return metaData;
    }

    /**
     * TODO
     * Gets the meta data on all interfaces, which are implemented 
     * by the specified <code>targetClass</code>.
     * 
     * @param metaData 
     *                  Collection to which the found meta data 
     *                  (e. g. annotations) will be added.
     * @param targetClass 
     *                  The class to be analyzed.
     * 
     * @return Returns the specified meta data collection supplemented 
     * with the found metaData on the interfaces.
     */
    private Collection getMetaDataFromAllDirectInterfaces(Collection metaData,
        Class targetClass, Target target) {

        if (targetClass != null
            && m_inheritanceConfiguration.includeInterfaces) {
            Class[] interfaces = targetClass.getInterfaces();

            for (int i = 0; i < interfaces.length; i++) {
                target.setParentClass(interfaces[i]);
                Collection newMetaData = getAttributes(target);
                metaData = addNewMetaDataBasedOnHierarchie(metaData,
                    newMetaData);
                metaData = getMetaDataFromAllDirectInterfaces(metaData, 
                    interfaces[i], target);
            }
        }

        return metaData;
    }

    /**
     * TODO
     * 
     * Gets the meta data on all interfaces which are implemented
     * by the superclasses of the specified <code>targetClass</code>. All 
     * superclasses means that all classes are mentioned which the 
     * <code>targetClass</code> is extended from directly or indirectly.
     * 
     * @param metaData 
     *                  Collection to which the found meta data 
     *                  (e. g. annotations) will be added.
     * @param targetClass
     *                  The class to be analyzed.
     * 
     * @return Returns the specified meta data collection supplemented 
     * with the found metaData on the superclass interfaces.
     */
    private Collection getMetaDataFromAllSuperclassInterfaces(
        Collection metaData, Class targetClass, Target target) {

        if (targetClass != null
            && m_inheritanceConfiguration.includeInterfaces) {
            Class superClass = targetClass.getSuperclass();
            metaData = getMetaDataFromAllDirectInterfaces(metaData, 
                superClass, target);
            metaData = getMetaDataFromAllSuperclassInterfaces(metaData,
                superClass, target);
        }

        return metaData;
    }

    /**
     * Gets the meta data on all packages of the specified 
     * <code>targetClass</code>. All packages means that also the parent 
     * packages of the <code>targetClass</code> will be mentioned.
     * 
     * @param metaData 
     *                  Collection to which the found meta data 
     *                  (e. g. annotations) will be added.
     * @param targetClass 
     *                  The class to be analyzed.
     * 
     * @return Returns the specified metaData Collection supplemented with the 
     * found meta data on the packages.
     * 
     * @deprecated The method is not implemented. It returns just the specified 
     * <code>Collection metaData</code>.
     */
    @Deprecated
    private Collection getMetaDataFromAllPackages(Collection metaData, 
        Class targetClass, Target target) {
        //This Condition must be placed first: 
        //'if (m_inheritanceConfiguration.includePackages)', if the method
        //will be implemented
        return metaData;
    
    }
    
    /**
     * TODO
     * Gets the meta data from the specified method which are specified in the
     * <code>List</code> of filters.
     * 
     * @param targetMethod
     *              Method from which its annotations has to be collected.
     * @param filters
     *              The meta data types to collect.
     * @return Returns the meta data on the specified method if they are 
     * corresponding to the specified filters.
     */
    private Collection getAttributes(Target target) {
        Collection attributes = new ArrayList();

        if (m_interceptingMetaData == null) {
            attributes = getAttributes(target, (Class) null);
        } else {
            Collection a;
            for (Iterator iter = m_interceptingMetaData.iterator(); iter.hasNext();) {
                Class t = (Class) iter.next();
                a = getAttributes(target, t);
                attributes.addAll(a);
            }
        }

        return attributes;

    }

    /**
     * 
     * @param metaDataFrom
     *              Defines from which target the meta data has to be collected.
     * 
     * 
     * TODO
     * Gets the meta data from the specified class which are specified in the
     * <code>List</code> of filters.
     * 
     * @param targetClass
     *              Class from which its annotations has to be collected.
     * @param filters
     *              The meta data types to collect or <code>null</code> if
     *              the filter option is not needed.
     * @return Returns the meta data on the specified method if they are 
     * corresponding to the specified filters.
     * @throws UsupportedOperationException
     *              If the specified <code>target</code> fits not the speciefied
     *              <code>elementType</code> or if the specified 
     *              <code>elementType</code> does not exists.
     */
    private Collection getAttributes(Target target, Class filter) {

        Collection attributes = null;
        collectionTarget collectionTarget = target.getCollectionTarget();

        try {
            switch (collectionTarget) {

                case COLLECT_FROM_TARGET:
                    attributes 
                        = getAttributes((Method) target.getTarget(), filter);
                    break;
                    
                case COLLECT_FROM_CLASS:
                    attributes 
                        = getAttributes((Class) target.getTarget(), filter);
                    break;

                case COLLECT_FROM_PARENT_CLASS:
                    attributes 
                        = getAttributes((Class) target.getParentClass(), filter);
                    break;
                    
                case COLLECT_FROM_PARENT_TARGET:
                    Method m = (Method) target.getMethodFromParentClass();
                    if (m != null) {
                        //attributes = getAttributes(m, filter);
                        attributes = getAttributes(m);
                    } else {
                        attributes = new ArrayList();
                    }
                    break;
                    

                default:
                    String errorMsg = "The specified collectionTarget '" 
                        + collectionTarget + "' does not exists";
                    s_logger.error(errorMsg);
                    throw new UnsupportedOperationException(errorMsg);
            }
            
        } catch (ClassCastException e) {
            String errorMsg = "The type in target.getTarget() fits not to the" 
                        + " definition in target.getCollectionTarget(). \n"
                        + "Value getTarget(): " + target.getTarget() + "\n"
                        + "Value getCollectionTarget(): " 
                        + target.getCollectionTarget();
            throw new UnsupportedOperationException(errorMsg, e);
        }

        return attributes;
    }

    /**
     * @return List containing the meta data, which the collector considers.
     */
    public List <Class> getInterceptingMetaData() {
        return m_interceptingMetaData;
    }

    /**
     * {@inheritDoc}
     */
    public void setInterceptingMetaData(List <Class> interceptingMetaData) {
        m_interceptingMetaData = interceptingMetaData;
    }

    /**
     * {@inheritDoc}
     * 
     * The default configuration is described in 
     * {@link DefaultInheritanceConfiguration}, and chapter '3 Documentation 
     * for module core' in the 
     * <code><a href="http://el4j.sourceforge.net/docs/pdf/ReferenceDoc.pdf">
     * el4j reference documentation</a></code>).
     * 
     * @param inheritanceConfiguration
     *                              The inheritence confifiguration object. If
     *                              <code>null</code> is set, the default
     *                              configuration is used.
     */
    public void setInheritenceConfiguration(InheritanceConfiguration 
        inheritanceConfiguration) {
        if (inheritanceConfiguration instanceof DefaultInheritanceConfiguration) {
            m_inheritanceConfiguration
                = (DefaultInheritanceConfiguration) inheritanceConfiguration;
            /* If configuration will be changed during runtime, is is not
             * guaranteed that the cached values fits to the new configuration.
             * Thererfore the cache will be cleaned.
             */
            m_cache.clear();
        } else if (inheritanceConfiguration == null) {
            /* Default configuration is already set */ 
        } else {
            String errorMsg = "The configuration object has to be of type '"
                + DefaultInheritanceConfiguration.class + "'. But the actual " 
                + "argument is of type '" + inheritanceConfiguration + "'.";
            throw new IllegalArgumentException(errorMsg);
        }        
    }

}