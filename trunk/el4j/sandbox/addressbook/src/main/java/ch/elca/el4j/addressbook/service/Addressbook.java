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
package ch.elca.el4j.addressbook.service;

import java.util.List;

import ch.elca.el4j.addressbook.dao.ContactDao;
import ch.elca.el4j.addressbook.dom.Contact;
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
     * The Contact Dao.
     */
    private ContactDao m_contactDao;
    
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
            m_myContacts = m_contactDao.findAll();
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
     * Delete a contact in database.
     * @param contact Contact to delete
     */
    public void deleteContact(Contact contact) {
        
    }
    
    /**
     * Add this contact to addressbook.
     * @param contact Contact to add
     */
    public void addContact(Contact contact) {
        
    }
    
    /**
     * @param daoRegistry Set Dao Registry to use
     */
    public void setDaoRegistry(DefaultDaoRegistry daoRegistry) {
        m_contactDao = (ContactDao) daoRegistry.getFor(Contact.class);
    }
    
    /**
     * @return ContactDao
     */
    public ContactDao getContactDao() {
        return m_contactDao;
    }
    
}
