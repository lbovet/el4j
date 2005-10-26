/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */

package ch.elca.el4j.tests.core.io.support;

import ch.elca.el4j.core.io.support.DefaultModuleSorter;
import ch.elca.el4j.core.io.support.Module;
import ch.elca.el4j.core.io.support.ModuleSorter;

/**
 * This class tests a {@link ch.elca.el4j.core.io.support.ModuleSorter}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class ModuleSorterTest extends AbstractOrderTestCase {

    /** The module sorter to use. */
    private ModuleSorter m_sorter;
    
    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        m_sorter = new DefaultModuleSorter();
    }
    
    /**
     * Sorts a single module.
     */
    public void testSingleModule() {
        Module[] modules = new Module[] {new Module("a")};
        Module[] sorted = m_sorter.sortModules(modules);
        assertEquals(1, sorted.length);
        assertEquals(sorted[0], modules[0]);
    }

    /**
     * Sorts a simple hierarchy.
     */
    public void testSimpleHierarchy() {
        Module a = new Module("a");
        Module b = new Module("b");
        b.addDependency("a");
        
        Module[] sorted = m_sorter.sortModules(new Module[] {a, b});
        assertBefore(a, b, sorted);
        
        sorted = m_sorter.sortModules(new Module[] {b, a});
        assertBefore(a, b, sorted);
    }
    
    /**
     * Sorts a hierarchy in which one node has multiple dependencies.
     */
    public void testBranchedHierarchy() {
        Module a = new Module("a");
        Module b = new Module("b");
        Module c = new Module("c");
        c.addAllDependencies("a,b");
        
        Module[] sorted = m_sorter.sortModules(new Module[] {a, b, c});
        assertBefore(a, c, sorted);
        assertBefore(b, c, sorted);
        
        sorted = m_sorter.sortModules(new Module[] {c, b, a});
        assertBefore(a, c, sorted);
        assertBefore(b, c, sorted);
    }

    /**
     * Tests a hierarchy having a node with two children.
     */
    public void testTwoChildren() {
        Module a = new Module("a");
        Module b = new Module("b");
        Module c = new Module("c");
        b.addDependency("a");
        c.addDependency("a");
        
        Module[] sorted = m_sorter.sortModules(new Module[] {b, a, c});
        assertBefore(a, b, sorted);
        assertBefore(a, c, sorted);
    }
    
    /**
     * Sorts a hierarchy where the order in which the sorting happens has a
     * influence of the outcome.
     */
    public void testWiredHierarchy() {
        Module a = new Module("a");
        Module b = new Module("b");
        Module c = new Module("c");
        Module d = new Module("d");
        c.addAllDependencies("a,b");
        d.addAllDependencies("a,b");
        
        Module[] sorted = m_sorter.sortModules(new Module[] {c, a, d, b});
        assertBefore(a, c, sorted);
        assertBefore(a, d, sorted);
        assertBefore(b, c, sorted);
        assertBefore(b, d, sorted);
    }
    
    /**
     * Sorts a hierarchy where a module has an indirect as well a direct
     * dependency on another module.
     */
    public void testUnnomralizedHierarchy() {
        Module a = new Module("a");
        Module b = new Module("b");
        Module c = new Module("c");
        b.addDependency("a");
        c.addAllDependencies("a,b");
        
        Module[] sorted = m_sorter.sortModules(new Module[] {b, c, a});
        assertBefore(a, b, sorted);
        assertBefore(a, c, sorted);
        assertBefore(b, c, sorted);
    }

    /**
     * Sorts a dependency graph that has a cycle.
     */
    public void testCycle() {
        Module a = new Module("a");
        Module b = new Module("b");
        a.addDependency("b");
        b.addDependency("a");
        
        m_sorter.sortModules(new Module[] {b, a});
    }
}
