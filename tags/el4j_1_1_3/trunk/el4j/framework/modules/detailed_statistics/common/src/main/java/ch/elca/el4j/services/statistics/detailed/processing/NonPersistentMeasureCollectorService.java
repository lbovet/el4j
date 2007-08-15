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
package ch.elca.el4j.services.statistics.detailed.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.elca.el4j.services.statistics.detailed.MeasureItem;
import ch.elca.el4j.services.statistics.detailed.cache.LRUCache;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class implements a non-persistant MeasureCollectionSerice. This means
 * that this measure collector service is not backed by any database or file
 * system. All the collected measurements are only stored in the memory. The
 * cacheSize determines the maximum number of MeasureIDs, which are stored. If
 * the no of MeasureIDs, exceeds the cacheSize all MeasureItems belonging to one
 * MeasureId will be removed. This MeasureId for removal is chosen by a
 * least-recently-used strategy. 
 * 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Rashid Waraich (RWA)
 * @author David Stefan (DST)
 */

public class NonPersistentMeasureCollectorService implements
    MeasureCollectorService {

    /**
     * Cache for Measurements.
     */
    private LRUCache<String, List<MeasureItem>> m_cache;

    /**
     * Construtor.
     * 
     * @param maxCacheSize
     *            The maximum number of Measurements in the cache.
     */
    public NonPersistentMeasureCollectorService(int maxCacheSize) {
        Reject.ifFalse(maxCacheSize >= 0);
        m_cache = new LRUCache<String, List<MeasureItem>>(maxCacheSize);
    }

    /**
     * Won't do anything, as this is a non-persistant MeasureCollectorService.
     */
    public void writeMeasures() {

    }

    /**
     * {@inheritDoc}
     */
    public void add(MeasureItem item) {
        List<MeasureItem> list = m_cache.get(item.getID().getFormattedString());

        if (list == null) {
            list = new LinkedList<MeasureItem>();
        }
        synchronized (this) {
            list.add(item);
            m_cache.put(item.getID().getFormattedString(), list);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int amount) {
        m_cache.clear();
    }

    /**
     * {@inheritDoc}
     */
    public List<MeasureItem> getAllMeasureItems() {
        List<MeasureItem> result = new ArrayList<MeasureItem>();
        synchronized (this) {
            Iterator<List<MeasureItem>> iter = m_cache.getAll().iterator();
            while (iter.hasNext()) {
                result = mergeLists(result, iter.next());
            }
        }
        return result;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public List<MeasureItem> getFirstMeasureItems() {
        List<MeasureItem> list = new ArrayList<MeasureItem>();

        synchronized (this) {
            Iterator<String> iter = m_cache.getKeys().iterator();
            while (iter.hasNext()) {
                list.add((m_cache.get(iter.next())).get(0));
            }
        }
        return list;
    }

    
    /**
     * Merge two lists.
     * 
     * @param list1
     *            First list.
     * @param list2
     *            Second list.
     * @return The resulting list.
     */
    private List<MeasureItem> mergeLists(List<MeasureItem> list1,
        List<MeasureItem> list2) {

        List<MeasureItem> result = new ArrayList<MeasureItem>();
        Iterator iter = list1.iterator();
        while (iter.hasNext()) {
            result.add((MeasureItem) iter.next());
        }
        iter = list2.iterator();
        while (iter.hasNext()) {
            result.add((MeasureItem) iter.next());
        }
        return result;
    }
}