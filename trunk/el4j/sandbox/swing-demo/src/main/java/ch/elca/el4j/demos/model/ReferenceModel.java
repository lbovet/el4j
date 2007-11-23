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
package ch.elca.el4j.demos.model;

import java.util.List;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.apps.refdb.dom.Reference;

/**
 * This wraps the "reference aspect" of the ServiceBroker. This simplifies
 * binding using beans binding.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class ReferenceModel {
    
    /**
     * The default constructor.
     */
    public ReferenceModel() {
    }
    
    /**
     * @param context    the spring application context.
     */
    public ReferenceModel(ApplicationContext context) {
        ServiceBroker.setApplicationContext(context);
    }
    
    /**
     * @return    the list of references
     */
    public List<Reference> getReferences() {
        return ServiceBroker.getReferenceService().getAllReferences();
    }
    
    /**
     * @param l    the references to set
     */
    public void setReferences(List<Reference> l) {
        //
    }
    
    /**
     * @see ReferenceService.saveReference
     * @param r    the reference to save
     */
    public void saveReference(Reference r) {
        ServiceBroker.getReferenceService().saveReference(r);
    }
    
    /**
     * @see ReferenceService.getReferenceByKey
     * @param key    the key to search
     * @return       the found reference
     */
    public Reference getRefByKey(int key) {
        return ServiceBroker.getReferenceService().getReferenceByKey(key);
    }
    
    
}
