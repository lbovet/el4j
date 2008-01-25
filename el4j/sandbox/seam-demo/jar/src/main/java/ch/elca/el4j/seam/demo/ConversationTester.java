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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("conversationTester")
@Scope(ScopeType.CONVERSATION)
public class ConversationTester {
    @Out
    private int counter = 0;
    
    public void refresh() {
        System.out.println("refresh");
        counter++;
    }
    
    @Begin
    public void begin() {
        System.out.println("begin");
        counter++;
    }
    
    @Begin(join = true)
    public void beginJoin() {
        System.out.println("begin(join = true)");
        counter++;
    }
    
    @End
    public void end() {
        System.out.println("end");
        counter++;
    }

}
