/*
 * ========================================================================
 *
 * Copyright 2006 Vincent Massol.
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
package org.codehaus.cargo.container.resin.internal;

import org.codehaus.cargo.container.spi.configuration.AbstractExistingLocalConfigurationCapability;
import org.codehaus.cargo.container.property.GeneralPropertySet;

import java.util.HashMap;
import java.util.Map;

/**
 * Capabilities of the Resin's {@link ResinExistingLocalConfigurationCapability} configuration.
 *
 * @version $Id: ResinExistingLocalConfigurationCapability.java 1001 2006-04-09 15:06:35Z vmassol $
 */
public class ResinExistingLocalConfigurationCapability
    extends AbstractExistingLocalConfigurationCapability
{
    /**
     * Configuration-specific supports Map.
     */
    private Map supportsMap;

    /**
     * Initialize the configuration-specific supports Map.
     */
    public ResinExistingLocalConfigurationCapability()
    {
        super();

        this.supportsMap = new HashMap();

        this.supportsMap.put(GeneralPropertySet.PROTOCOL, Boolean.FALSE);
        this.supportsMap.put(GeneralPropertySet.HOSTNAME, Boolean.FALSE);
    }

    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfigurationCapability#getPropertySupportMap()
     */
    protected Map getPropertySupportMap()
    {
        return this.supportsMap;
    }
}