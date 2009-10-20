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
package ch.elca.el4j.tests.services.persistence.hibernate.offlining.graphwalker.file;

import java.util.Collection;

import ch.elca.el4j.services.persistence.hibernate.offlining.impl.UniqueKey;
import ch.elca.el4j.services.persistence.hibernate.offlining.objectwrapper.UniqueKeyed;
import ch.elca.el4j.util.objectwrapper.ObjectWrapper;
import ch.elca.el4j.util.objectwrapper.ObjectWrapperRTException;
import ch.elca.el4j.util.objectwrapper.impl.AbstractWrapper;
import ch.elca.el4j.util.objectwrapper.interfaces.Linked;


/**
 * Utilities for use with TestFile.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public final class FileUtilities {

	/** Unused. */
	private FileUtilities() { }
	
	/** 
	 * The wrapper for file tests using child links.
	 * @return The wrapper object for tests.
	 */
	public static ObjectWrapper fileWrapperChild() {
		ObjectWrapper wrapper = new ObjectWrapper();
		wrapper.addWrappable(UniqueKeyed.class, new FileUniqueKeyImplementation());
		wrapper.addWrappable(Linked.class, new FileChildLinkImplementation());
		return wrapper;
	}
	
	/** 
	 * The wrapper for file tests using parent links.
	 * @return The wrapper object for tests.
	 */
	public static ObjectWrapper fileWrapperParent() {
		ObjectWrapper wrapper = new ObjectWrapper();
		wrapper.addWrappable(UniqueKeyed.class, new FileUniqueKeyImplementation());
		wrapper.addWrappable(Linked.class, new FileParentLinkImplementation());
		return wrapper;
	}
	
	/**
	 * UniqueKeyed test implementation.
	 */
	static class FileUniqueKeyImplementation extends AbstractWrapper implements UniqueKeyed {

		/** {@inheritDoc} */
		@Override
		public void create() throws ObjectWrapperRTException {	
			if (!(m_target.getClass() == TestFile.class)) {
				throw new ObjectWrapperRTException("This implementation is only valid for TestFile.");
			}
		}

		/** {@inheritDoc} */
		public UniqueKey getUniqueKey() {
			return new UniqueKey(((TestFile) m_target).getPath(), TestFile.class);
		}
		
		/** {@inheritDoc} */
		public UniqueKey getLocalUniqueKey() {
			return getUniqueKey();
		}

		/** {@inheritDoc} */
		public void setUniqueKey(UniqueKey key) throws IllegalArgumentException {
			throw new ObjectWrapperRTException("Cannot set keys on TestFile.");
		}
	}
	
	/**
	 * Linked test implementation that finds children.
	 */
	static class FileChildLinkImplementation extends AbstractWrapper implements Linked {

		/** {@inheritDoc} */
		@Override
		public void create() throws ObjectWrapperRTException {
			if (!(m_target.getClass() == TestFile.class)) {
				throw new ObjectWrapperRTException("This implementation is only valid for TestFile.");
			}
		}

		/** {@inheritDoc} */
		public Object[] getAllLinked() {
			return ((TestFile) m_target).getFiles().toArray();
		}

		/** {@inheritDoc} */
		public Collection<?> getCollectionLinkByName(String name) {
			if (name.equals("children")) {
				return ((TestFile) m_target).getFiles();
			}
			throw new ObjectWrapperRTException("Illegal collection link " + name);
		}

		/** {@inheritDoc} */
		public String[] getCollectionLinkNames() {
			return new String[] {"children"};
		}

		/** {@inheritDoc} */
		public String[] getLinkNames() {
			return new String[0];
		}

		/** {@inheritDoc} */
		public Object getlinkByName(String linkName) {
			throw new ObjectWrapperRTException("No links present.");
		}
	}
	
	/**
	 * Linked test implementation that finds the parent.
	 */
	static class FileParentLinkImplementation extends AbstractWrapper implements Linked {

		/** {@inheritDoc} */
		@Override
		public void create() throws ObjectWrapperRTException {
			if (!(m_target.getClass() == TestFile.class)) {
				throw new ObjectWrapperRTException("This implementation is only valid for TestFile.");
			}
		}

		/** {@inheritDoc} */
		public Object[] getAllLinked() {
			return ((TestFile) m_target).getFiles().toArray();
		}

		/** {@inheritDoc} */
		public Collection<?> getCollectionLinkByName(String name) {
			throw new ObjectWrapperRTException("No collections.");
		}

		/** {@inheritDoc} */
		public String[] getCollectionLinkNames() {
			return new String[] {};
		}

		/** {@inheritDoc} */
		public String[] getLinkNames() {
			return new String[] {"parent"};
		}

		/** {@inheritDoc} */
		public Object getlinkByName(String linkName) {
			if (linkName.equals("parent")) {
				return ((TestFile) m_target).getParent();
			}
			throw new ObjectWrapperRTException("Illegal link name " + linkName);
		}
	}
}
