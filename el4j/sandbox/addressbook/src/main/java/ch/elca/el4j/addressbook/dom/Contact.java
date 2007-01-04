/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.elca.el4j.addressbook.dom;

import javax.persistence.Entity;

import ch.elca.el4j.util.dom.annotations.MemberOrder;

/**
 * This class provides a trivial domain object for the sample application. It
 * represents a simple Contact entry in a personal address book. It is not very
 * useful in that it only allows a single address for an individual and it
 * doesn't support arbitrary contact data, just predefined fields. However,
 * since we're not going into the Address Book business, this will suffice for
 * demonstration purposes in this sample application.
 * <p>
 * This class makes use of one subordinate (or nested) object in order to show
 * how nested property paths can be used in forms. It doesn't really serve any
 * other great design need.
 * <p>
 * The validation rules for this class are provided externally, by
 * {@link ValidationRulesSource}. This configuration is often required
 * when you don't have any mechanism to extend the domain object directly, or
 * for other design reasons, don't want to include the validation rules directly
 * in the domain object implementation.
 * 
 * <script type="text/javascript">printFileStatus
*   ("$URL$",
    *    "$Revision$",
    *    "$Date$",
    *    "$Author$"
    * );</script>
    * 
    * @author David Stefan (DST)
 * @see ValidationRulesSource
 */
@MemberOrder({
    "firstName",
    "lastName"
})
@Entity
public class Contact {

    /**
     * Id.
     */
    private int m_id;

    /**
     * Contact type.
     */
    private ContactType m_contactType;

    /**
     * First name.
     */
    private String m_firstName;

    /**
     * Last name.
     */
    private String m_lastName;

    /**
     * Address.
     */
    private Address m_address;
    
    /**
     * Default constructor.
     */
    public Contact() {
        setAddress(new Address(""));
    }

    /**
     * @return the id
     */
    public int getId() {
        return m_id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.m_id = id;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return m_firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.m_firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return m_lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.m_lastName = lastName;
    }

    /**
     * @return the contactType
     */
    public ContactType getContactType() {
        return m_contactType;
    }

    /**
     * @param contactType
     *            the contactType to set
     */
    public void setContactType(ContactType contactType) {
        this.m_contactType = contactType;
    }

    /**
     * Compare two objects for equality. Just test their ids.
     * @param o object to compare
     * @return boolean
     */
    public boolean equals(Object o) {
        if (o instanceof Contact) {
            return m_id == ((Contact) o).m_id;
        }
        return false;
    }

    /**
     * Hashcode.
     * @return Hashcode
     */
    public int hashCode() {
        return m_id;
    }
    
    /**
     * @return the address
     */
    public Address getAddress() {
        return m_address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(Address address) {
        this.m_address = address;
    }
}
