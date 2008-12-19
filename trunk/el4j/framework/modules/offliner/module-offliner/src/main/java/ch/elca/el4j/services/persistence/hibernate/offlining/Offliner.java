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

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.generic.dao.GenericDao;

/**
 * An offliner implementation adds offlining functionality to a database accessed via
 * DAOs. 
 * <p>
 * To use offlining, the client must get his DAOs from the offliner by using it as
 * a DaoRegistry. These DAOs must not be offlined by the client between state 
 * changes in the offliner (which the client initiates).
 * <p>
 * An offliner has two states: online and offline. In online mode, it forwards all
 * calls to DaoRegistry.getFor(Class) to the database's DaoRegistry. In offline
 * mode, the offliner returns DAOs for a local database. At all times,
 * the offliner's DaoRegistry implementation is guaranteed to return a valid DAO
 * for the active database (if one exists).
 * <p>
 * The offliner offers two offlining methods which both can only be executed in 
 * online mode: offline and synchronize. offline copies a selection of objects to 
 * the local database. synchronize copies all objects in the local database to the
 * database, omitting them if they exist unchanged there already. Any problems
 * that occur during synchronization are wrapped in a Conflict object which
 * is returned by synchronize.
 * <p>
 * After performing a set of offline operations, the client may go offline and
 * work with the local database. He can then synchronize the local and remote databases.
 * Edits in the  local db, the server and synchronizations can be performed and
 * repeated in any order.
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
public interface Offliner extends DaoRegistry {
	
	/**
	 * Checks whether the offliner is in online mode. While online, it returns
	 * DAOs from the original registry. Offline, it returns DAOs from the local db.
	 * @return Whether the offliner is online.
	 */
	boolean isOnline();
	
	/**
	 * Take the offliner online or offline. Is a NOP if the new state is the one
	 * we are in already. May perform operations during going online or offline.
	 * @param online The new state to set.
	 */
	void setOnline(boolean online);
	
	/**
	 * Offline some objects. (Save them into the local database.)
	 * These should be instances of domain classes.
	 * This method can be called online or offline, but the objects must come
	 * from the remote database originally in the sense that the id and version 
	 * properties are set correctly for recomitting them (as they are passed to
	 * this method) to the server later on.
	 * <p>
	 * For example, a client could do this:<br>
	 * <code>
	 * List&lt;Bean> beans = serverDao.getAll();<br>
	 * offliner.offline(beans.toArray());<br>
	 * </code>
	 * @param objects The objects to offline. 
	 * @return All conflicts during offlining. These occur when an object is newer in
	 * the local db than on the server. To recover from these, run synchronize. 
	 */
	Conflict[] offline(Object... objects);

	/**
	 * Attempt to synchronize the local db with the database. This throws an 
	 * exception only if there is some problem accessing the databse (like we
	 * are still offline). Exceptions occurring when elements are being saved 
	 * are caught and wrapped in conflict objects.
	 * @return A Conflict[0] if the everything was successful. Otherwise,
	 * all conflicts that occurred.
	 */
	Conflict[] synchronize();
	
	/**
	 * Clear the local db and all related tables. After this method executes,
	 * the offliner performs as if it had just been initialized.
	 */
	void clearLocal();
	
	/**
	 * Returns the currently active generic DAO for entities of type entityType.
	 * <p>
	 * While online, this will be a DAO for the database (or a proxy thereof),
	 * when offline it is a DAO for the offliner's local database.
	 * <p>
	 * It is important that the client does not cache DAOs got via this method
	 * between state changes he performs on the offliner.
	 * 
	 * @param <T> The tpye parameter for the entity class. 
	 * @param entityType
	 *            The domain class for which a generic DAO should be returned.
	 * @return A fully generic or partially specific DAO for the given type,
	 *            or null if none was found.
	 */
	<T> GenericDao<T> getFor(Class<T> entityType);
	
	/**
	 * Mark a list of objects as "delete this" in the local db. They will be
	 * deleted from the server on synchronization.
	 * This method requires the offliner to be offline. It does not actually
	 * delete the objects itself (it is called by the local DAOs).
	 * @param objects The objects to delete.
	 */
	void markForDeletion(Object... objects);
	
	/**
	 * Evict objects from the local db. This method requires the offliner to be
	 * offline. The objects are deleted in the local db along with their metadata.
	 * On synchronize, these objects are neither modified nor deleted.
	 * @param objects The objects to evict.
	 */
	void evict(Object... objects);
	
	/**
	 * Conflict resolution method. Force the offliner to overwrite the database
	 * version of this object with the local one. This will ignore the version
	 * of the actual object, but still fail if there are any dependent objects
	 * in conflict.
	 * @param object The object to forcibly update.
	 * @return Conflict[0] if the operation was successful, otherwise the conflicts
	 * preventing it.
	 */
	Conflict[] forceLocal(Object object);
	
	/**
	 * Conflict resolution method. Force the offliner to overwrite a offlined object
	 * with the server version, losing changes in the local db.
	 * @param object The object to force. Dependent objects are offlined but not forced.
	 * @return Conflict[0] on succcess, otherwise all conflicts that prevented the operation.
	 */
	Conflict[] forceRemote(Object object);
	
	/**
	 * Conflict resolution method.
	 * Erase all pending delete operations, resulting in the objects not being deleted
	 * on the server.
	 * <p>
	 * Use this when there are delete conflicts, you have handled all you want to and
	 * decide not to do the rest of the deletes.
	 */
	void eraseDeletes();
}
