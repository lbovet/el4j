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

import ch.elca.el4j.util.metadata.AbstractGenericMetaDataCollector.collectionTarget;

/**
 * This class is ... TODO
 * 
 * The Default method target can inherit meta data
 * from classes and interface method definitions.
 * 
 * The target can change during the collection process.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Haefeli (ADH)
 */
public class Target {
    
    private Object m_target;
    private ElementType m_targetType;
    private Class m_targetClass;
    private Class m_parentClass;
    private collectionTarget m_collectionTarget 
        = collectionTarget.COLLECT_FROM_TARGET;
    
    /**
     * @param target
     * @param targetType
     * @param targetClass
     */
    public Target(Object target, Class targetClass) {
        m_target = target;
        m_targetClass = targetClass;
    }

    /**
     * @return Returns the target.
     */
    public Object getTarget() {
        return m_target;
    }

    /**
     * @return Returns the targetClass.
     */
    public Class getTargetClass() {
        return m_targetClass;
    }

    /**
     * @return Returns the targetType.
     */
    public ElementType getTargetType() {
        return m_targetType;
    }
    
    public Method getMethodFromParentClass() {
        
        if(m_parentClass == null) {
            //TODO
        }
        
        if(m_targetType == ElementType.TYPE) {
            return null;
        }
        
        Method m = null; //TODO Exception concept
        try {
            String methodName = ((Method) m_target).getName();
            Class[] parameterTypes = ((Method) m_target).getParameterTypes();
            
            m = m_parentClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
        }
        return m;
       
    }
    
    public void setParentClass(Class parentClass) {
        m_parentClass = parentClass;
    }

    /**
     * @return Returns the parentClass.
     */
    public Class getParentClass() {
        return m_parentClass;
    }

    /**
     * @return Returns the collectionTarget.
     */
    public collectionTarget getCollectionTarget() {
        return m_collectionTarget;
    }

    /**
     * @param collectionTarget Is the collectionTarget to set.
     */
    public void setCollectionTarget(collectionTarget collectionTarget) {
        m_collectionTarget = collectionTarget;
    }

    /**
     * @param target Is the target to set.
     */
    public void setTarget(Object target) {
        m_target = target;
    }
    
    


}
