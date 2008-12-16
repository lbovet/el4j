/* 
 * ========================================================================
 * 
 * Copyright 2005-2006 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.cargo.module.ejb;

import org.codehaus.cargo.module.VendorDescriptor;

/**
 * Base interface for vendor specific EJB descriptors.
 * 
 * @version $Id: VendorEjbDescriptor.java 1118 2006-07-22 12:54:21Z vmassol $
 */
public interface VendorEjbDescriptor extends VendorDescriptor
{
    /**
     * Returns the jndi name for an EJB.
     * 
     * @param ejb The EjbDef to get the jndi for
     * @return the jndi name of the ejb
     */
    String getJndiName(EjbDef ejb);
}