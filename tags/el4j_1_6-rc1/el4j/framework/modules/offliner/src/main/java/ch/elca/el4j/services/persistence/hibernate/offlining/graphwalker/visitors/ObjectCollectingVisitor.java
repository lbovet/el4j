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
package ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.visitors;

import java.util.LinkedHashSet;
import java.util.Set;

import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.GraphWalker.NodeState;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeException;
import ch.elca.el4j.services.persistence.hibernate.offlining.graphwalker.NodeVisitor;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.MappingEntry;
import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.Mapped;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;


/**
 * The ObjectCollectingVisitor collects all objects in an object graph and their metadata. It is run locally
 * on object graphs to synchronize; results of one or more such runs form a Chunk for sending to the server.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class ObjectCollectingVisitor implements NodeVisitor {

	/** The collected objects. */
	private Set<Object> m_objects;
	
	/** The collected mapping entries. */
	private Set<MappingEntry> m_entries; 
	
	/** The object wrapper. */
	private final ObjectWrapper m_wrapper;
	
	/**
	 * Constructor.
	 * @param wrapper The object wrapper.
	 */
	public ObjectCollectingVisitor(ObjectWrapper wrapper) {
		m_wrapper = wrapper;
		m_objects = new LinkedHashSet<Object>();
		m_entries = new LinkedHashSet<MappingEntry>();
	}

	/** {@inheritDoc} */
	public Object markError(Object node, Object cause) {
		throw new OfflinerInternalRTException("markRed called for collcetor.");
	}

	/** {@inheritDoc}
	 * An object is visited if it's been added before. 
	 */
	public NodeState preVisit(Object node) {
		if (m_objects.contains(node)) {
			return NodeState.PROCESSED;
		}
		return NodeState.UNSEEN;
	}

	/** {@inheritDoc} 
	 * PRE: All children have been visited, this object has not been visited.
	 * Add the object to the list. 
	 */
	public void visit(Object node) throws NodeException {
		add(node);
	}

	/**
	 * Add an object and its mapping entry to the list. (It is a linked hash set
	 * to provide both O(1) contains() and iteration in fixed order.)
	 * @param obj The object.
	 */
	private void add(Object obj) {
		if (m_objects.contains(obj)) {
			throw new OfflinerInternalRTException("Added object to collector twice.");
		}
		m_objects.add(obj);
		Mapped mapped = m_wrapper.wrap(Mapped.class, obj); 
		MappingEntry entry = mapped.getEntry();
		if (entry == null) {
			// A new object in the local database.
			UniqueKey localKey = m_wrapper.wrap(UniqueKeyed.class, obj).getUniqueKey();
			entry = new MappingEntry(localKey, null, 0L, 0L);
			mapped.setEntry(entry);
		}
		m_entries.add(entry);
	}
	
	/**
	 * @return The objects.
	 */
	public Object[] getObjects() {
		return m_objects.toArray();
	}
	
	/**
	 * @return The mapping entries, in the same order as the objects.
	 */
	public MappingEntry[] getEntries() {
		return m_entries.toArray(new MappingEntry[m_entries.size()]);
	}
	
	/**
	 * Clear all entries.
	 */
	public void clear() {
		m_objects.clear();
		m_entries.clear();
	}
}
