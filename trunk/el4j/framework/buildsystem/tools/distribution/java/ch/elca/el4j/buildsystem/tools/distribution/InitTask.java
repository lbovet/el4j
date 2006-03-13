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

package ch.elca.el4j.buildsystem.tools.distribution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import ch.elca.el4ant.model.Attribute;
import ch.elca.el4ant.model.ConfigurationEvent;
import ch.elca.el4ant.model.ConfigurationListener;
import ch.elca.el4ant.model.ExecutionUnit;
import ch.elca.el4ant.model.Module;
import ch.elca.el4ant.model.ProjectRepository;
import ch.elca.el4ant.propgen.RuntimePropertyGenerator;

/**
 * This Task adds to all module and execution units an attribute that enables
 * generation of a shortcut used to create a executable distribution.
 * 
 * <b>Note</b>: only execution units with a basic runtime command creator are
 * flagged (i.e.
 * <code>runtime.command.creator=runtime.command.creator.basic</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class InitTask extends Task implements ConfigurationListener {

    /** The basic runtime command creator's name. */
    public static final String RUNTIME_COMMAND_CREATOR_BASIC
        = "runtime.command.creator.basic";
    
    /** The name of the flag-attriubte to generate a shortcut for a module. */
    public static final String DISTRIBUTION_ATTRIBUTE_MODULE
        = "distribution.create.shortcut.module";
    
    /** The name of the flag-attribute used to generate a shortcut for an EU. */
    public static final String DISTRIBUTION_ATTRIBUTE_MODULE_EU
        = "distribution.create.shortcut.module.eu";
    
    /** The delimiter used to separate module sets in the module set list. */
    public static final String DELIMITER = ",";
    
    /** The name of the attribute that specifies a module's set. */
    public static final String SET = "set";
    
    /** The list of module sets to generate shortcuts for. */
    private String[] m_setList;
    
    /**
     * Sets the list of sets that enables shortcut generation for modules that
     * are in one of them.
     * 
     * @param distributionSetList
     *      The list of module sets to generate shortcuts for.
     */
    public void setDistributionSetList(String distributionSetList) {
        List result = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(
                distributionSetList, DELIMITER);
        
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            result.add(token);
        }
        
        m_setList = (String[]) result.toArray(new String[result.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public void componentConfigured(ConfigurationEvent event) {
        if (!(event.getSource() instanceof Module)) {
            return;
        }
        
        boolean addedAttributeToModule = false;
        
        Module source = (Module) event.getSource();

        // check whether the given module is in one of the enumerated sets
        Iterator attr = source.getAttributesByName(SET);
        if (attr.hasNext()) {
            Attribute anAttribute = (Attribute) attr.next();
            if (!isInEnumeratedSet(anAttribute.getValue())) {
                return;
            }
        }
        
        attr = source.getAttributesByName(
                RuntimePropertyGenerator.RUNTIME_COMMAND_CREATOR);
        
        if (attr.hasNext()) {
            Attribute anAttribute = (Attribute) attr.next();
            if (RUNTIME_COMMAND_CREATOR_BASIC.equals(anAttribute.getValue())) {
                source.addAttribute(createDistributionAttribute(
                        DISTRIBUTION_ATTRIBUTE_MODULE));
                addedAttributeToModule = true;
            }
        }
        
        int numberOfEuAdds = 0;
        Iterator euIt = source.getExecutionUnitList().iterator();
        while (euIt.hasNext()) {
            ExecutionUnit eu = (ExecutionUnit) euIt.next();

            Iterator attEu = eu.getAttributesByName(
                    RuntimePropertyGenerator.RUNTIME_COMMAND_CREATOR);
            if (!attEu.hasNext()) {
                continue;
            } else {
                Attribute anAttribute = (Attribute) attEu.next();
                if (!RUNTIME_COMMAND_CREATOR_BASIC.equals(
                        anAttribute.getValue())) {
                    // No marker - give up this eu
                    continue;
                }
            }
            // That eu is marked
            eu.addAttribute(createDistributionAttribute(
                    DISTRIBUTION_ATTRIBUTE_MODULE_EU));
            numberOfEuAdds++;
        }
        
        // if there was at least one execution unit, that got the shortcut
        // attribute, generate also one for the whole module.
        if (!addedAttributeToModule && numberOfEuAdds > 0) {
            source.addAttribute(createDistributionAttribute(
                    DISTRIBUTION_ATTRIBUTE_MODULE));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void componentConfiguring(ConfigurationEvent event) { }
    
    /**
     * Creates an attribute that enables shortcut generation if attached
     * to a module or execution unit.
     * 
     * @param name
     *      The attribute's name.
     *      
     * @return Returns a new attribute instance with the given name and
     *      <code>true</code> as value.
     */
    private Attribute createDistributionAttribute(String name) {
        Attribute attr = new Attribute();
        attr.setName(name);
        attr.setValue("true");
        return attr;
    }

    /**
     * Checks whether the given set is contained in the list of selected
     * sets.
     * 
     * @param set
     *      The set to test.
     *      
     * @return Returns whether the given set is enumerated in the list of
     *      selected sets.
     */
    private boolean isInEnumeratedSet(String set) {
        boolean result = false;
        if (set == null
                || set.trim().length() == 0
                || m_setList == null
                || m_setList.length == 0) {
            result = true;
            
        } else {
            for (int i = 0; i < m_setList.length; i++) {
                if (m_setList[i].equals(set)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public void execute() throws BuildException {
        ProjectRepository.getInstance().addConfigurationListener(this);
    }
}
