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
package ch.elca.el4j.services.statistics.detailed.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * 
 * This class is a simple, generic FIFO Cache restricted to a certain size.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <K> Generic type of Key.
 * @param <E> Generic type of Element.
 * 
 * @author David Stefan (DST)
 */
public class LRUCache<K, E> {

    /** 
     * Internal representation of key queue.
     */
    private LinkedList<K> m_keyList;
    
    /**
     * Internal bag for cache elements.
     */
    private Map<K, E> m_items;
    
    /**
     * Maximum size of queue.
     */
    private int m_maxCacheSize;

    /**
     * Creates a new LRU cache.
     * 
     * @param cacheSize
     *            the maximum number of entries that will be kept in this cache.
     */
    public LRUCache(int cacheSize) {
        this.m_maxCacheSize = cacheSize;
        m_keyList = new LinkedList<K>();
        m_items = new HashMap<K, E>();
    }

    /**
     * Put element to cache.
     * 
     * @param key
     *            Key of element
     * @param element
     *            Element to add
     */
    public synchronized void put(K key, E element) {
        // check for size. If cache is full, throw LRU element away
        if (m_keyList.size() >= m_maxCacheSize) {
            m_items.remove(m_keyList.removeFirst());
        }
        updateKeyList(key);
        m_items.put(key, element);
    }

    /**
     * Get element out of cache.
     * 
     * @param key
     *            Key of the element to get
     * @return Element from cache
     */
    public synchronized E get(K key) {
        updateKeyList(key);
        return m_items.get(key);
    }
    
    /**
     * Get all elements of cache.
     * 
     * @return Collection of all cache elements
     */
    public synchronized Collection<E> getAll() {
        return m_items.values();
    }
    
    /**
     * Get all keys of bag.
     * @return key set of bag
     */
    public synchronized Set<K> getKeys() {
        return m_items.keySet();
    }
    

    /**
     * Clear the cache.
     */
    public synchronized void clear() {
        m_keyList.clear();
    }
    
    /**
     * Moves key to last position in keyList when it's written or read, because
     * it is now the most recently used.
     * 
     * @param key Key to update in list
     */
    private void updateKeyList(K key) {
        // remove key in case it was in the list
        m_keyList.remove(key);
        // append it to the list
        m_keyList.add(key);
    }
    
    
   
}

