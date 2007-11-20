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
package ch.elca.el4j.services.search.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationEvent;

import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.util.codingsupport.CollectionUtils;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Event for query objects.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class QueryObjectEvent extends ApplicationEvent {
    /**
     * Are the query objecs of this event.
     */
    private final List m_queryObjects;

    /**
     * Constructor for one query object.
     * 
     * @param source Is the place this event has been created.
     * @param queryObject Is the query object of this event.
     */
    public QueryObjectEvent(Object source, QueryObject queryObject) {
        super(source);
        Reject.ifNull(queryObject);
        List queryObjects = new ArrayList();
        queryObjects.add(queryObject);
        m_queryObjects = Collections.unmodifiableList(queryObjects);
    }
    
    /**
     * Constructor for multiple query objects.
     * 
     * @param source Is the place this event has been created.
     * @param queryObjects Are the query objects of this event.
     */
    public QueryObjectEvent(Object source, Collection queryObjects) {
        super(source);
        Reject.ifEmpty(queryObjects, "Minimum one query object required.");
        CollectionUtils.containsOnlyObjectsOfType(
            queryObjects, QueryObject.class);
        m_queryObjects 
            = Collections.unmodifiableList(new ArrayList(queryObjects));
    }

    
    /**
     * @return Returns the first unspecific query object of this event. This has
     *         the same effect as method invocation
     *         {@link #getQueryObject(Class)} with <code>null</code>.
     */
    public QueryObject getQueryObject() {
        return getQueryObject(null);
    }
    
    /**
     * @param beanClass
     *            Is the bean class the requested query object must be made for.
     * @return Returns the query object that is made for the given bean class.
     */
    public QueryObject getQueryObject(Class beanClass) {
        QueryObject queryObject = null;
        Iterator it = m_queryObjects.iterator();
        while (queryObject == null && it.hasNext()) {
            QueryObject element = (QueryObject) it.next();
            if (element.getBeanClass() == beanClass) {
                queryObject = element;
            }
        }
        return queryObject;
    }
    
    /**
     * @return Returns the list of query objects of this event.
     */
    public List getQueryObjects() {
        return m_queryObjects;
    }
}
