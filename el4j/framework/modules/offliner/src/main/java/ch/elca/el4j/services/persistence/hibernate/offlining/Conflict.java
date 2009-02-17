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
package ch.elca.el4j.services.persistence.hibernate.offlining;

import java.io.Serializable;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;

/**
 * A conflict occurs on synchronizing the local with the remote database. It represents
 * an object that caused an exception while trying to save it. 
 * Invariant: All conflicts carry the object in question in m_localObject, and it is keyed
 * correctly for the user to recommit it (after dealing with the conflict) to the server.
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
public class Conflict implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8643997737055561217L;

	/**
	 * Some common types of exception that are indicated specially. 
	 */
	public enum Type { 
		/** Anything that we have not handled specially. */
		OTHER,
		
		/** Dependent conflict. */
		DEPENDENT,
		
		/** Versioning/Locking conflict. */
		VERSION, 
		
		/** Constraint violation. */
		CONSTRAINT
	}
	
	/**
	 * The phase during which this conflict was encountered.
	 */
	public enum Phase {
		/** Synchronizing an object. */
		SYNCHRONIZE,
		
		/** Deleting an object during synchronization. */
		DELETE,
		
		/** 
		 * Offlining. This can refer both to a user-initiated offline() or to an offline
		 * during the last phase of synchronization.
		 */
		OFFLINE,
		
		/** Forcing an object. */
		FORCE
	}
	
	/** The exception that cause this conflict. */
	private final Throwable m_cause;
	
	/**
	 * The phase during which the conflict occurred.
	 */
	private final Phase m_phase;
	
	/**
	 * The type of this conflict.
	 */
	private Type m_causeType;
	
	/**
	 * The local object we were trying to commit when the conflict occurred.
	 * This is always non-null. 
	 */
	private final Object m_localObject;
	
	/**
	 * The remote object that caused this conflict, if applicable. For instance,
	 * an optimistic locking failure would contain the database's new version
	 * of this object. Is null if not applicable.
	 */
	private final Object m_remoteObject;

	/**
	 * Create a conflict object.
	 * @param cause The exception that caused the conflict.
	 * @param localObject The object we are trying to commit.
	 * @param remoteObject The remote object, if applicable.
	 */
	public Conflict(Phase phase, Throwable cause, Object localObject, Object remoteObject) {
		m_cause = cause;
		m_phase = phase;
		m_localObject = localObject;
		m_remoteObject = remoteObject;

		// Default value.
		m_causeType = Type.OTHER;
		
		// Handle a few standard cases of exceptions.
		if (m_cause instanceof OptimisticLockingFailureException) {
			m_causeType = Type.VERSION;
		}
		
		if (m_cause instanceof DataIntegrityViolationException) {
			m_causeType = Type.CONSTRAINT;
		}
	}

	/**
	 * Factory method to create a dependent conflict.
	 * @param localObject The object with a dependent conflict.
	 * @return The conflict object.
	 */
	public static Conflict newDependent(Phase phase, Object localObject) {
		Conflict conflict = new Conflict(phase, null, localObject, null);
		conflict.m_causeType = Type.DEPENDENT;
		return conflict;
	}
		
	/**
	 * @return The phase during which this conflict occurred.
	 */
	public Phase getPhase() {
		return m_phase;
	}
	
	/**
	 * @return 
	 * The local object we were trying to commit when the conflict occurred.
	 * This is always non-null. 
	 */
	public Object getLocalObject() {
		return m_localObject;
	}

	/**
	 * @return The remote object that caused this conflict, if applicable.
	 * For instance, an optimistic locking failure would contain the database's
	 * new version of this object. Is <code>null</code> if not applicable.
	 */
	public Object getRemoteObject() {
		return m_remoteObject;
	}
	
	/**
	 * @return The type of conflict.
	 */
	public Type getCauseType() {
		return m_causeType;
	}

	/**
	 * Get the cause.
	 * @return The cause.
	 */
	public Throwable getCause() {
		return m_cause;
	}
}
