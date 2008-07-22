/*
 * ========================================================================
 *
 * Copyright 2005-2006 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * ========================================================================
 */
package org.codehaus.cargo.container.spi.configuration;

import org.codehaus.cargo.container.property.RemotePropertySet;

/**
 * Base implementation of {@link org.codehaus.cargo.container.configuration.ConfigurationCapability}
 * that needs to be extended by each runtime configuration's implementation.
 *
 * @version $Id: AbstractRuntimeConfigurationCapability.java 969 2006-03-27 11:34:18Z vmassol $
 */
public abstract class AbstractRuntimeConfigurationCapability
    extends AbstractConfigurationCapability
{
    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.spi.configuration.AbstractConfigurationCapability#AbstractConfigurationCapability()
     */
    protected AbstractRuntimeConfigurationCapability()
    {
        super();

        this.defaultSupportsMap.put(RemotePropertySet.USERNAME, Boolean.TRUE);
        this.defaultSupportsMap.put(RemotePropertySet.PASSWORD, Boolean.TRUE);
    }
}