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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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
     * My contacts.
     */
    private List<Contact> m_myContacts;
    
    /**
     * Id for new contacts.
     */
    private int m_nextId = 0;
    
    /**
     * Data Source.
     */
    private DataSource m_source;

   /**
    * Constructor.
    *
    */
    public Addressbook() {
        fillContacts();
    }
    
    /**
     * @return My Contacts
     */
    public List<Contact> getMyContacts() {
        return m_myContacts;
    }

    /**
     * @param myContacts Set My Contacts
     */
    public void setMyContacts(List<Contact> myContacts) {
        this.m_myContacts = myContacts;
    }
    
    /**
     * Fill my contacts with data.
     *
     */
    private void fillContacts() {
        m_myContacts = new ArrayList<Contact>();
        m_myContacts.add(makeContact("Larry", "Streepy", "123 Some St.",
            "Apt. #26C", "New York", "NY", "10010", ContactType.BUSINESS));
        m_myContacts.add(makeContact("Keith", "Donald", "456 WebFlow Rd.", "2",
            "Cooltown", "NY", "10001", ContactType.BUSINESS));
        m_myContacts.add(makeContact("Steve", "Brothers",
            "10921 The Other Street", "", "Denver", "CO", "81234-2121",
            ContactType.PERSONAL));
        m_myContacts.add(makeContact("Carlos", "Mencia", "4321 Comedy Central",
            "", "Hollywood", "CA", "91020", ContactType.PERSONAL));
        m_myContacts.add(makeContact("Jim", "Jones", "1001 Another Place", "",
            "Dallas", "TX", "71212", ContactType.PERSONAL));
        m_myContacts.add(makeContact("Jenny", "Jones", "1001 Another Place", "",
            "Dallas", "TX", "75201", ContactType.PERSONAL));
        m_myContacts.add(makeContact("Greg", "Jones", "9 Some Other Place",
            "Apt. 12D", "Chicago", "IL", "60601", ContactType.PERSONAL));
    }
    

    /**
     * Create a new contact.
     * @param first .
     * @param last .
     * @param address1 .
     * @param address2 .
     * @param city .
     * @param state .
     * @param zip .
     * @param contactType .
     * @return .
     */
    private Contact makeContact(String first, String last, String address1,
        String address2, String city, String state, String zip,
        ContactType contactType) {

        Contact contact = new Contact();
        contact.setId(m_nextId++);
        contact.setContactType(contactType);
        contact.setFirstName(first);
        contact.setLastName(last);

        Address address = contact.getAddress();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setCity(city);
        address.setState(state);
        address.setZip(zip);

        return contact;
    }

    public DataSource getSource() {
        return m_source;
    }

    public void setSource(DataSource source) {
        this.m_source = source;
    }

}
