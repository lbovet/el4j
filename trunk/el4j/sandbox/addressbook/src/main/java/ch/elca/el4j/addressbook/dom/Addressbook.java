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
package ch.elca.el4j.addressbook.dom;

import java.util.List;

import ch.elca.el4j.addressbook.dao.ContactDao;
import ch.elca.el4j.services.persistence.generic.dao.impl.DefaultDaoRegistry;

/**
 * 
 * This class is a dom class.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class Addressbook {

    /**
     * Dao Registry.
     */
    private DefaultDaoRegistry m_daoRegistry;
    
    /**
     * My contacts.
     */
    private List<Contact> m_myContacts;

   /**
    * Constructor.
    *
    */
    public Addressbook() {
    }
    
    /**
     * @return My Contacts
     */
    public List<Contact> getMyContacts() {
        if (m_myContacts == null) {
            ContactDao dao = (ContactDao) m_daoRegistry.getFor(Contact.class);
            m_myContacts = dao.findAll();
        }
        return m_myContacts;
    }

    /**
     * @param myContacts Set My Contacts
     */
    public void setMyContacts(List<Contact> myContacts) {
        this.m_myContacts = myContacts;
    }

    /**
     * Getter.
     * @return .
     */
    public DefaultDaoRegistry getDaoRegistry() {
        return m_daoRegistry;
    }

    /**
     * Setter.
     * @param daoRegistry .
     */
    public void setDaoRegistry(DefaultDaoRegistry daoRegistry) {
        this.m_daoRegistry = daoRegistry;
    }
    
}
