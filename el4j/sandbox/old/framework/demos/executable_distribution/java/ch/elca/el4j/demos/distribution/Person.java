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

package ch.elca.el4j.demos.distribution;

/**
 * This class represents a person.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Andreas Bur (ABU)
 */
public class Person {

    /** The person's name. */
    private String m_name;
    
    /** The person's age. */
    private int m_age;

    /**
     * @return Returns the person's age.
     */
    public int getAge() {
        return m_age;
    }

    /**
     * Sets the person's age.
     * 
     * @param age
     *      The age to set.
     */
    public void setAge(int age) {
        m_age = age;
    }

    /**
     * @return Returns the person's name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the person's name.
     * 
     * @param name
     *      The name to set.
     */
    public void setName(String name) {
        m_name = name;
    }
}
