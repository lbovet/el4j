/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.AbstractPostInsertGenerator;
import org.hibernate.id.IdentifierGeneratorFactory;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.SequenceIdentityGenerator.NoCommentsInsert;
import org.hibernate.id.insert.AbstractReturningDelegate;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;

/**
 * A generator with immediate retrieval through JDBC3
 * {@link java.sql.Connection#prepareStatement(String, String[]) getGeneratedKeys}.
 * The value of the identity column must be set from a "before insert trigger"
 * 
 * This generator is tested with Oracle and only known to work with newer Oracle drivers compiled for
 * JDK 1.4 (JDBC3). The minimum version is 10.2.0.1
 * 
 * Note: Due to a bug in Oracle drivers, sql comments on these insert statements
 * are completely disabled.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Jean-Pol Landrain - extern (XYZ)
 * @author Martin Zeltner (MZE)
 */
public class TriggerAssignedIdentityGenerator extends AbstractPostInsertGenerator {
	/**
	 * {@inheritDoc}
	 */
	public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(
		PostInsertIdentityPersister persister, Dialect dialect, boolean isGetGeneratedKeysEnabled)
		throws HibernateException {
		return new Delegate(persister, dialect);
	}

    /**
     * Delegate for the returned generated value.
     */
	public static class Delegate extends AbstractReturningDelegate {
		/**
		 * Use database dialect.
		 */
		private final Dialect m_dialect;

		/**
		 * Are the key columns.
		 */
		private final String[] m_keyColumns;

		/**
		 * @param persister Is the post insert identity persister 
		 * @param dialect Is the dialect of the database.
		 */
		public Delegate(PostInsertIdentityPersister persister, Dialect dialect) {
			super(persister);
			m_dialect = dialect;
			m_keyColumns = getPersister().getRootTableKeyColumnNames();
			if (m_keyColumns.length > 1) {
				throw new HibernateException(
					"trigger assigned identity generator cannot be used with multi-column keys");
			}
		}

		/**
		 * Removes the comments for insertion.
		 * 
		 * {@inheritDoc}
		 */
		public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert() {
			NoCommentsInsert insert = new NoCommentsInsert(m_dialect);
			return insert;
		}

		/**
		 * {@inheritDoc}
		 */
		protected PreparedStatement prepare(String insertSQL, SessionImplementor session) throws SQLException {
			return session.getBatcher().prepareStatement(insertSQL, m_keyColumns);
		}

		/**
		 * {@inheritDoc}
		 */
		protected Serializable executeAndExtract(PreparedStatement insert) throws SQLException {
			insert.executeUpdate();
			return IdentifierGeneratorFactory.getGeneratedIdentity(
				insert.getGeneratedKeys(),
				getPersister().getIdentifierType());
		}
	}
}
