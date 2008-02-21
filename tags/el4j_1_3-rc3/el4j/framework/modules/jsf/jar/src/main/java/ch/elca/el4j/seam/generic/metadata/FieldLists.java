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
package ch.elca.el4j.seam.generic.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import ch.elca.el4j.seam.generic.EntityShortNameMapping;
import ch.elca.el4j.seam.generic.ResultCache;
import ch.elca.el4j.seam.generic.humanization.HumanizationComparator;

/**
 * A class to compute field/column lists based on hibernate metadata as well
 * as given inclusion("shown") / exclusion ("hidden") field lists.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author  Baeni Christoph (CBA)
 */
public class FieldLists {
    /**
     * The current Hibernate session factory.
     */
    private SessionFactory m_sessionFactory;
    
    /**
     * The mapping between short and fully qualified class names.
     */
    private EntityShortNameMapping m_shortNameMapping;

    /**
     * A simple cache to store type information.
     */
    private ResultCache m_cache = new ResultCache();

    /**
     * @param sessionFactory    the hibernate session factory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        m_sessionFactory = sessionFactory;
    }

    /**
     * @param entityShortnameMapping    the short <-> full entity name mapping
     */
    public void setShortNameMapping(
        EntityShortNameMapping entityShortnameMapping) {
        
        m_shortNameMapping = entityShortnameMapping;
    }

    /**
     * @param entityClassName    the entity class name
     * @return                   all reasonable fields (without db related)
     */
    private String[] getCompleteFieldListInternal(String entityClassName) {
        if (m_sessionFactory == null) {
            throw new RuntimeException("Session factory not set.");
        }

        if (m_shortNameMapping == null) {
            throw new RuntimeException("Entity shortname mapping not set.");
        }

        ClassMetadata metadata;
        try {
            metadata = m_sessionFactory.getClassMetadata(Class
                .forName(entityClassName));
        } catch (ClassNotFoundException e) {
            return null;
        }

        String[] propertyNames = metadata.getPropertyNames();
        List<String> propertyList
            = new ArrayList<String>(Arrays.asList(propertyNames));
        propertyList.remove("version");
        propertyList.remove("optimisticLockingVersion");
        String entityShortName = m_shortNameMapping
            .getShortName(entityClassName);
        Comparator<String> humanizationComparator = new HumanizationComparator(
            entityShortName);
        Collections.sort(propertyList, humanizationComparator);

        return propertyList.toArray(new String[0]);
    }

    /**
     * @param entityClassName    the entity class name
     * @return                   all reasonable fields (without db related)
     */
    private String[] getCompleteFieldList(String entityClassName) {
        String cacheKey = m_cache.computeKey("getCompleteFieldList",
            entityClassName);

        if (!m_cache.doesExist(cacheKey)) {
            m_cache.store(cacheKey,
                getCompleteFieldListInternal(entityClassName));
        }

        return (String[]) m_cache.lookup(cacheKey);
    }

    /**
     * @param fieldsString    a comma sepatared list
     * @return                the corresponding array
     */
    public String[] parseList(String fieldsString) {
        if (fieldsString.trim().equals("")) {
            return new String[0];
        }
        return fieldsString.trim().split(" *, *");
    }

    /**
     * @param entityClassName    the entity class name
     * @param shown              comma separated list of fields that
     *                           should be rendered
     * @param hidden             comma separated list of fields that
     *                           should not be rendered
     * @return                   an array of all visible fields
     */
    private String[] computeFieldListInternal(String entityClassName,
        String shown, String hidden) {
        String[] fieldArray;

        if ((shown == null) || shown.equals("")) {
            fieldArray = getCompleteFieldList(entityClassName);
        } else {
            fieldArray = parseList(shown);
        }

        LinkedHashSet<String> fields = new LinkedHashSet<String>(Arrays
            .asList(fieldArray));
        if ((hidden != null) && !hidden.equals("")) {
            fields.removeAll(Arrays.asList(parseList(hidden)));
        }

        return fields.toArray(new String[0]);
    }

    /**
     * @param entityClassName    the entity class name
     * @param shown              comma separated list of fields that
     *                           should be rendered
     * @param hidden             comma separated list of fields that
     *                           should not be rendered
     * @return                   an array of all visible fields
     */
    public String[] computeFieldList(String entityClassName, String shown,
        String hidden) {
        String cacheKey = m_cache.computeKey(
            "computeFieldList", entityClassName, shown, hidden);

        if (!m_cache.doesExist(cacheKey)) {
            m_cache.store(cacheKey, computeFieldListInternal(entityClassName,
                shown, hidden));
        }

        return (String[]) m_cache.lookup(cacheKey);
    }
}
