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

package ch.elca.el4j.seam.demo;

import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.annotations.Name;

import ch.elca.el4j.seam.demo.entities.Employee;

/**
 * 
 * This is a small class for testing the editableComboBox control.
 * It holds the selected value and a dummy list of Employees for the items 
 * to display.
 * 
 * @author Frank Bitzer (FBI)
 *
 */
@Name("comboTester")
public class ComboTester {

    /**
     * Currently selected value
     */
    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
    /**
     * Creates a dummy list of employees to be displayed in the
     * dropdown list. In a real-world scenario,
     * this method would fetch the needed items from the database.
     * 
     * @return
     */
    public List<Employee> getList() {

        List<Employee> l = new LinkedList<Employee>();
        
        Employee e = new Employee();
        
        e.setFirstName("Frank");
       
        l.add(e);
        
        
        Employee e2 = new Employee();
        
        e2.setFirstName("Max");
       
        l.add(e2);
        
        return l;
            


    }
}
