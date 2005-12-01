/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.buildsystem.remoting.ejb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.elca.el4ant.model.Dependency;
import ch.elca.el4ant.model.ExecutionUnit;
import ch.elca.el4ant.model.Module;

/**
 * This class adds container specific dependencies to the modules that make use
 * of the remoting EJB support.
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
public class ContainerInitTask extends AbstractEjbInitTask {

    /** Container specific client dependencies. */
    private List m_clientDependencies = new ArrayList();
    
    /**
     * Adds a container specific client dependency which is attached to the
     * client as JAR dependency.
     * 
     * @param dependency
     *      The container specific client dependency.
     */
    public void add(ClientDependency dependency) {
        m_clientDependencies.add(dependency);
    }

    /**
     * {@inheritDoc}
     */
    protected void configureEu(Module module, ExecutionUnit eu) {
        // Add container specific dependencies needed by the client in order to
        // access the container's services.
        Dependency dep;
        for (Iterator iter = m_clientDependencies.iterator(); iter.hasNext();) {
            dep = new Dependency();
            ClientDependency next = (ClientDependency) iter.next();
            dep.setJar(next.getName());
            module.addDependency(dep);
        }
    }
}
