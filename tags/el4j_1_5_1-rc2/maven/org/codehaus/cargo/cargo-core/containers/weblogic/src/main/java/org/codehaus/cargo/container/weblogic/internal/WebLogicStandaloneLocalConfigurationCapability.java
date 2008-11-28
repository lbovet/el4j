/* 
 * ========================================================================
 * 
 * Copyright 2004-2006 Vincent Massol.
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
package org.codehaus.cargo.container.weblogic.internal;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability;
import org.codehaus.cargo.container.weblogic.WebLogicPropertySet;

/**
 * Capabilities of the WebLogic's 
 * {@link org.codehaus.cargo.container.weblogic.WebLogicStandaloneLocalConfiguration} configuration.
 *  
 * @version $Id: WebLogicStandaloneLocalConfigurationCapability.java 1188 2006-09-24 16:01:08Z vmassol $
 */
public class WebLogicStandaloneLocalConfigurationCapability
    extends AbstractStandaloneLocalConfigurationCapability
{
    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;

    /**
     * Initialize the configuration-specific supports Map.
     */
    public WebLogicStandaloneLocalConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap();

        this.supportsMap.put(GeneralPropertySet.HOSTNAME, Boolean.FALSE);
        this.supportsMap.put(ServletPropertySet.USERS, Boolean.FALSE);

        this.supportsMap.put(WebLogicPropertySet.ADMIN_USER, Boolean.TRUE);
        this.supportsMap.put(WebLogicPropertySet.ADMIN_PWD, Boolean.TRUE);
        this.supportsMap.put(WebLogicPropertySet.SERVER, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     * @see AbstractStandaloneLocalConfigurationCapability#getPropertySupportMap()
     */
    protected Map getPropertySupportMap()
    {
        return this.supportsMap;
    }
}