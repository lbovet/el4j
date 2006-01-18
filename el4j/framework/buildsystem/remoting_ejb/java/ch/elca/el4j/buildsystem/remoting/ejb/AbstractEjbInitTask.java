/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.buildsystem.remoting.ejb;

import java.util.Iterator;

import org.apache.tools.ant.Task;

import ch.elca.el4ant.model.Attribute;
import ch.elca.el4ant.model.ConfigurationEvent;
import ch.elca.el4ant.model.ConfigurationListener;
import ch.elca.el4ant.model.ExecutionUnit;
import ch.elca.el4ant.model.Module;
import ch.elca.el4ant.model.ProjectRepository;

/**
 * Abstract class to setup hooks for the EJB integration build system plugin.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Nicolas Schiper (NSC)
 * @author Andreas Bur (ABU)
 */
public abstract class AbstractEjbInitTask
    extends Task implements ConfigurationListener {

    /** The post compile hook. */
    protected static final String POST_COMPILE_HOOK = "post.compile.[module]";
    
    /** EJB module attribute name specifying the container. */
    private static final String REMOTING_EJB = "remoting.ejb";
    
    /**
     * {@inheritDoc}
     */
    public void execute() {
        // subscribe to receive configuration events
        ProjectRepository pr = ProjectRepository.getInstance();
        pr.addConfigurationListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void componentConfiguring(ConfigurationEvent event) {
        if (!(event.getSource() instanceof Module)) {
            return;
        }

        Module source = (Module) event.getSource();
        Iterator euIt = source.getExecutionUnitList().iterator();

        while (euIt.hasNext()) {
            ExecutionUnit eu = (ExecutionUnit) euIt.next();
            
            // Check at the module level
            Iterator iter = eu.getAttributesByName(REMOTING_EJB);
            
            if (iter.hasNext()) {
                Attribute attribute = (Attribute) iter.next();
                if (attribute.isTrue()) {
                    // module marked
                    configureEu(source, eu);
                }
            }
        }
    }

    /**
     * Configures a tagged execution unit.
     * 
     * @param module
     *      The module where the given execution unit belongs to.
     * @param eu
     *      The execution unit to add hooks to.
     */
    protected abstract void configureEu(Module module, ExecutionUnit eu);
    
    /**
     * {@inheritDoc}
     */
    public void componentConfigured(ConfigurationEvent event) {
        // do nothing
    }
}
