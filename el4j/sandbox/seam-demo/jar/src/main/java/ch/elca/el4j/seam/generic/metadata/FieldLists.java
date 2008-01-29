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
 * CAUTION: This code is (partially) hibernate specific, thus not portable!
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

    private ResultCache cache = new ResultCache();

    public void setSessionFactory(SessionFactory sessionFactory) {
        m_sessionFactory = sessionFactory;
    }

    public void setShortNameMapping(
        EntityShortNameMapping entityShortnameMapping) {
        
        m_shortNameMapping = entityShortnameMapping;
    }

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
        List<String> propertyList = new ArrayList<String>(Arrays.asList(propertyNames));
        propertyList.remove("version");
        propertyList.remove("optimisticLockingVersion");
        String entityShortName = m_shortNameMapping
            .getShortName(entityClassName);
        Comparator<String> humanizationComparator = new HumanizationComparator(
            entityShortName);
        Collections.sort(propertyList, humanizationComparator);

        return propertyList.toArray(new String[0]);
    }

    private String[] getCompleteFieldList(String entityClassName) {
        String cacheKey = cache.computeKey("getCompleteFieldList",
            entityClassName);

        if (!cache.doesExist(cacheKey)) {
            cache.store(cacheKey,
                getCompleteFieldListInternal(entityClassName));
        }

        return (String[]) cache.lookup(cacheKey);
    }

    public String[] parseList(String fieldsString) {
        if (fieldsString.trim().equals("")) {
            return new String[0];
        }
        return fieldsString.trim().split(" *, *");
    }

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

    public String[] computeFieldList(String entityClassName, String shown,
        String hidden) {
        String cacheKey = cache.computeKey("computeFieldList", entityClassName,
            shown, hidden);

        if (!cache.doesExist(cacheKey)) {
            cache.store(cacheKey, computeFieldListInternal(entityClassName,
                shown, hidden));
        }

        return (String[]) cache.lookup(cacheKey);
    }

    private String[] makeTableColumnArray(String[] fieldArray) {
        String[] columnArray = new String[fieldArray.length];

        for (int i = 0; i < fieldArray.length; i++) {
            columnArray[i] = new String(fieldArray[i]);
        }

        return columnArray;
    }

    private String[] computeColumnListInternal(String entityClassName,
        String shown, String hidden) {
        String[] columnArray;

        if ((shown == null) || shown.equals("")) {
            String[] fieldArray = getCompleteFieldList(entityClassName);
            columnArray = makeTableColumnArray(fieldArray);
        } else {
            String[] fieldArray = parseList(shown);
            columnArray = makeTableColumnArray(fieldArray);
        }

        LinkedHashSet<String> columns = new LinkedHashSet<String>(
            Arrays.asList(columnArray));
        if ((hidden != null) && !hidden.equals("")) {
            columns.removeAll(Arrays.asList(makeTableColumnArray(
                parseList(hidden))));
        }

        return columns.toArray(new String[0]);
    }

    public String[] computeColumnList(String entityClassName,
        String shown, String hidden) {
        String cacheKey = cache.computeKey("computeColumnList",
            entityClassName, shown, hidden);

        if (!cache.doesExist(cacheKey)) {
            cache.store(cacheKey, computeColumnListInternal(entityClassName,
                shown, hidden));
        }

        return (String[]) cache.lookup(cacheKey);
    }
}
