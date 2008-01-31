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
package ch.elca.el4j.seam.generic;

import org.jboss.seam.Component;

import ch.elca.el4j.seam.generic.metadata.EntityInfoBase;
import ch.elca.el4j.seam.generic.metadata.EnumFieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldInfo;
import ch.elca.el4j.seam.generic.metadata.FieldLists;
import ch.elca.el4j.seam.generic.metadata.RelationFieldInfo;

/**
 * The heart of the generic master/detail views.
 * 
 * A facade, consisting of functions intended to be exposed to and used from
 * JSF view templates as a spring bean via Seam's DelegatingVariableResolver.
 * 
 * The functionality provided includes (among others): automatic computation of
 * entity type, entity field list, field types etc.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Baeni Christoph (CBA)
 */
public class ElUtils {
    /**
     * The mapping between short and fully qualified class names.
     */
    private EntityShortNameMapping m_shortNameMapping;
    
    /**
     * The type information provider class.
     */
    private EntityInfoBase m_entityInfoBase;
    
    /**
     * The field lists.
     */
    private FieldLists m_fieldLists;

    /**
     * @param entityShortnameMapping    the entity short name mapping
     */
    public void setShortNameMapping(
        EntityShortNameMapping entityShortnameMapping) {
        
        m_shortNameMapping = entityShortnameMapping;
    }

    /**
     * @param entityInfoBase    the type information provider
     */
    public void setEntityInfoBase(EntityInfoBase entityInfoBase) {
        m_entityInfoBase = entityInfoBase;
    }

    /**
     * @param fieldLists    the field lists
     */
    public void setFieldLists(FieldLists fieldLists) {
        m_fieldLists = fieldLists;
    }

    /* Boolean parsing support */

    /**
     * @param booleanString    a String representing a boolean
     * @return                 <code>true</code> if string is one of the
     *                         following: "y", "yes", "t", "true", "1"
     */
    public boolean getBooleanValue(String booleanString) {
        boolean result = false;
        
        if (booleanString != null) {
            if (booleanString.equalsIgnoreCase("yes")
                || booleanString.equalsIgnoreCase("y")) {
                result = true;
            } else if (booleanString.equalsIgnoreCase("true")
                || booleanString.equalsIgnoreCase("t")) {
                result = true;
            } else if (booleanString.equals("1")) {
                result = true;
            }
        }
        
        return result;
    }

    /* Shortname mapping based support */

    /**
     * @param entityShortName    the entity short name
     * @return                   the corresponding full class name
     */
    public String getEntityClassName(String entityShortName) {
        return m_shortNameMapping.getClassName(entityShortName);
    }

    /**
     * @param entityClassName    the full entity class name
     * @return                   the corresponding short name
     */
    public String getEntityShortName(String entityClassName) {
        return m_shortNameMapping.getShortName(entityClassName);
    }

    /* FieldLists based support */

    /**
     * @param entityClassName    the full entity class name
     * @param shown              comma separated list of fields that
     *                           should be rendered
     * @param hidden             comma separated list of fields that
     *                           should not be rendered
     * @return                   an array of all visible fields
     */
    public String[] computeFieldList(String entityClassName, String shown,
        String hidden) {
        return m_fieldLists.computeFieldList(entityClassName, shown, hidden);
    }

    /* FieldInfo based support */

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the FieldInfo for this field
     */
    private FieldInfo getFieldInfo(String entityClassName, String fieldName) {
        return m_entityInfoBase.getEntityInfo(entityClassName).getFieldInfo(
            fieldName);
    }

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the type of this field
     */
    public String getFieldType(String entityClassName, String fieldName) {
        return getFieldInfo(entityClassName, fieldName).getTypeString();
    }

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   <code>true</code> if field is required
     */
    public boolean isRequired(String entityClassName, String fieldName) {

        // modified 2008-1-30, FBI
        // for boolean fields, always return false because a boolean field
        // can not be required (letting it out means setting it to false)
        FieldInfo fi = getFieldInfo(entityClassName, fieldName);

        if (fi.getTypeString().toLowerCase().equals("boolean")) {
            return false;
        } else {
            return fi.isRequired();
        }

    }

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the enum constants of this field as array
     */
    public Object[] getEnumList(String entityClassName, String fieldName) {
        EnumFieldInfo enumFieldInfo = (EnumFieldInfo) (getFieldInfo(
            entityClassName, fieldName));

        return enumFieldInfo.getEnumList();
    }

    /* Default master/detail page view id support */

    /**
     * @param entityShortName    the short name of the entity
     * @return                   the default detail page of this entity
     */
    public String getDefaultDetailPage(String entityShortName) {
        return PageViewIDHelper.getDefaultDetailPage(entityShortName);
    }

    /**
     * @param entityShortName    the short name of the entity
     * @return                   the default master page of this entity
     */
    public String getDefaultMasterPage(String entityShortName) {
        return PageViewIDHelper.getDefaultMasterPage(entityShortName);
    }

    /**
     * Guess entity shortname from the current JSF view id.
     * 
     * @return Returns the guessed entity shortname.
     */
    public String deriveEntityShortName() {
        return PageViewIDHelper.deriveEntityShortName();
    }

    /* Relation support */

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the related full entity class name
     */
    public String getRelatedEntityClassName(String entityClassName,
        String fieldName) {
        
        FieldInfo fieldInfo = getFieldInfo(entityClassName, fieldName);
        Class<?> relatedClass
            = ((RelationFieldInfo) fieldInfo).getRelatedClass();

        return relatedClass.getName();
    }

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the related short entity class name
     */
    public String getRelatedEntityShortName(String entityClassName,
        String fieldName) {
        
        return getEntityShortName(getRelatedEntityClassName(entityClassName,
            fieldName));
    }

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the related entites
     */
    public Object[] getRelatedEntities(String entityClassName,
        String fieldName) {
        
        String relatedEntityClassName = getRelatedEntityClassName(
            entityClassName, fieldName);
        EntityManager entityManager = (EntityManager) Component
            .getInstance("entityManager");

        return entityManager.getAllEntities(relatedEntityClassName);
    }

    /**
     * @param entityClassName    the full entity class name
     * @param fieldName          the field name
     * @return                   the default detail page of this entity
     */
    public String getDefaultRelatedDetailPage(String entityClassName,
        String fieldName) {
        String relatedEntityShortName = getRelatedEntityShortName(
            entityClassName, fieldName);

        return getDefaultDetailPage(relatedEntityShortName);
    }

    /* Filter support */

    /**
     * @param filterOn    a comma sepatared list
     * @return            the corresponding array
     */
    public String[] parseFilterList(String filterOn) {
        return m_fieldLists.parseList(filterOn);
    }

    /**
     * @param entityClassName    the full entity class name
     * @param filterName         the filter name
     * @return                   the type of this filter
     */
    public String getFilterType(String entityClassName, String filterName) {
        return getFieldType(entityClassName, filterName);
    }

    /**
     * @param entityClassName    the full entity class name
     * @param filterName         the filter name
     * @return                   the enum constants of this filter as array
     */
    public Object[] getFilterEnumList(String entityClassName,
        String filterName) {
        
        return getEnumList(entityClassName, filterName);
    }
}