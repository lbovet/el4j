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
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import ch.elca.el4j.services.persistence.hibernate.offlining.Offliner;
import ch.elca.el4j.services.persistence.hibernate.offlining.OfflinerInternalRTException;


/**
 * Proxy for local daos that intercepts delete methods and marks the objects for deletion in the database
 * on resynchronisation.
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
public final class LocalDaoProxy implements InvocationHandler {

	/**
	 * This class and its instantiation HANDLERS is an extensible system for handling 
	 * all delete calls on the daos.
	 * <p>
	 * If the proxy thinks a call is a delete (the current
	 * strategy is <code>name.contains("delete")</code> ), it will iterate over all handlers
	 * calling canHandle() until one returns true. If none does, an error is raised.
	 * <p>
	 * If one accepts, the proxy will call the following sequence of operations.
	 * <ol><li>begin()</li>
	 * <li>handleBefore()</li>
	 * <li><i>call the method</i></li>
	 * <li>handleAfter(result) if the method returns, else handleException()</li></ol>
	 * <p>
	 * To extend the system for a new delete method in the dao interface, just add a new 
	 * handler instantiation to HANDLERS. The handlers there are prototypes, so it is safe
	 * to store the method invocation and arguments without leaking memory - the instance
	 * used is out of scope once invoke() returns.
	 */
	abstract static class AbstractHandler implements Cloneable {
		
		/** The method concerned. */
		protected Method m_method;
		
		/** The arguments to the method. */
		protected Object[] m_argv;
		
		/** The offliner. */
		protected Offliner m_offliner;
		
		/** The target object (the dao). */
		protected Object m_target;
		
		/** Required for prototype pattern. 
		 * {@inheritDoc} 
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		/** 
		 * Is this my method? 
		 * <p>
		 * The handler must check method name, arguments, annotations etc. here.
		 * 
		 * @param m The method the user called.
		 * @return <code>true</code> if this is the handler for this method.
		 */
		protected abstract boolean canHandle(Method m);
		
		/**
		 * Handle a method - "before advice". Any actions that require the object to be present
		 * like looking up by id nust be performed here as the object no longer exists after 
		 * the delete is invoked.
		 */
		protected void handleBefore() { }
		
		/**
		 * Handle a method - "after advice". If possible all marking for deletion should be done
		 * here as a recoverable exception in the deletion process will lead to this being skipped.
		 * @param result The invocation result.
		 */
		protected void handleAfter(Object result) { }
		
		/**
		 * Handle a method that throws an exception. It can be assumed the deletion process failed.
		 */
		protected void handleException() { }
				
		/**
		 * Called once as a pseudo-constructor for each invocation to save the relevant data. 
		 * @param target The method target.
		 * @param m The method. This is guaranteed to have been accepted by canHandle. 
		 * @param argv The arguments.
		 * @param offliner The offliner.
		 */
		public void begin(Object target, Method m, Object[] argv, Offliner offliner) {
			m_method = m;
			m_argv = argv;
			m_offliner = offliner;
			m_target = target;
		}
		
		/**
		 * Call a dao method reflectively over the proxy.
		 * @param name The Method name.
		 * @param args The arguments.
		 * @return The invocation result.
		 */
		protected final Object callDaoMethod(String name, Class<?>[] argClasses, Object[] args) {
			try {
				Method method = m_target.getClass()
					.getMethod(name, argClasses);
				return method.invoke(m_target, args);
			} catch (InvocationTargetException e) {
				throw error();
			} catch (NoSuchMethodException e) {
				throw error();
			}  catch (IllegalAccessException e) {
				throw error();
			}
		}
		
		/**
		 * @return An error.
		 */
		private OfflinerInternalRTException error() {
			return new OfflinerInternalRTException("Reflection exception calling dao method.");
		}
	}
	
	/** A logger. */
	private static final Logger s_log = Logger.getLogger(LocalDaoProxy.class);
	
	/*
	 * Add new handlers here if any new delete methods are ever made.
	 */
	
	/** The handlers. There must be one for each delete method. */
	private static final AbstractHandler[] HANDLERS = {
		
		/** delete(Collection). */
		new AbstractHandler() {
			private Collection<?> m_collection;
			public boolean canHandle(Method m) {
				return m.getName().equals("delete")
					&& m.getParameterTypes().length == 1
					&& Collection.class.isAssignableFrom(m.getParameterTypes()[0]);
			}
			public void handleBefore() {
				m_collection = (Collection<?>) m_argv[0];
			}
			public void handleAfter(Object result) {
				m_offliner.markForDeletion(m_collection.toArray());
			}
		},
		
		
		/** delete(Object obj). Must not be deprecated to distinguish from delete(ID). */
		new AbstractHandler() {
			public boolean canHandle(Method m) {
				return m.getName().equals("delete")
					&& m.getAnnotation(Deprecated.class) == null
					&& m.getParameterTypes().length == 1
					&& !Collection.class.isAssignableFrom(m.getParameterTypes()[0]);
			}
			public void handleAfter(Object result) {
				m_offliner.markForDeletion(m_argv[0]);
			}
		},
		
		/** deleteById(id). */
		new AbstractHandler() {
			private Object m_entity;
			
			public boolean canHandle(Method m) {
				return m.getName().equals("deleteById")
					&& m.getParameterTypes().length == 1;
			}
			public void handleAfter(Object result) {
				m_offliner.markForDeletion(m_entity);
			}
			public void handleBefore() {
				m_entity = callDaoMethod("findById", new Class[] {Serializable.class}, new Object[] {m_argv[0]});
			}
		},
		
		/** deleteAll(). */
		new AbstractHandler() {
			private List<?> m_all;
			
			public boolean canHandle(Method m) {
				return m.getName().equals("deleteAll")
					&& m.getParameterTypes().length == 0;
			}
			public void handleAfter(Object result) {
				m_offliner.markForDeletion(m_all.toArray());
			}
			public void handleBefore() {
				m_all = (List<?>) callDaoMethod("getAll", new Class[0], new Object[0]);
			}
		},
		
		/** @deprecated delete(ID) */
		new AbstractHandler() {
			private Object m_entity;
			
			public boolean canHandle(Method m) {
				return m.getName().equals("delete")
					&& m.getParameterTypes().length == 1
					&& m.getAnnotation(Deprecated.class) != null;
			}
			public void handleAfter(Object result) {
				m_offliner.markForDeletion(m_entity);
			}
			public void handleBefore() {
				s_log.warn("Using deprecated method delete(ID) of dao. "
					+ "Use deleteById(ID) instead.");
				m_entity = callDaoMethod("findById", new Class[] {Serializable.class}, new Object[] {m_argv[0]});
				
			}
		}
	};
	
	/** The offliner to notify of deletes. */
	private final Offliner m_offliner;

	/** The dao we proxy. */
	private final Object m_targetDao;
	
	/**
	 * @param dao The dao we proxy.
	 * @param offliner The offliner.
	 */
	public LocalDaoProxy(Object dao, Offliner offliner) {
		m_offliner = offliner;
		m_targetDao = dao;
		
	}

	/** 
	 * {@inheritDoc}
	 * Methods to intercept: delete (all versions), deleteAll.
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
		String name = method.getName();
		boolean isDelete = name.contains("delete");
		
		if (isDelete) {
			AbstractHandler handler = null;
			for (AbstractHandler candidate : HANDLERS) {
				if (candidate.canHandle(method)) {
					handler = (AbstractHandler) candidate.clone();
					break;
				}
			}
			if (handler == null) {
				throw new OfflinerInternalRTException("Delete method with no handler in dao.");
			}
			
			handler.begin(m_targetDao, method, args, m_offliner);
			handler.handleBefore();
			// This is a bit of a hack to avoid touching any exceptions.
			// Success == false in the finally block means invoke threw something.
			boolean success = false;
			try {
				Object result = method.invoke(m_targetDao, args); 
				success = true;
				handler.handleAfter(result);
				return result;
			} finally {
				if (!success) {
					handler.handleException();
				}
			}
		} else {
			return method.invoke(m_targetDao, args); 
		}
	}
}
