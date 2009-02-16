/* 
 * ========================================================================
 * 
 * Copyright 2004-2005 Vincent Massol.
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
package org.codehaus.cargo.generic.configuration;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.ContainerType;

/**
 * Create a {@link Configuration} knowing the container to which it is attached to and the 
 * configuration type. A container is identified by both its id and its type. Indeed, it's possible
 * to register different configuration classes for the same container id but for a different
 * container type.
 * 
 * @version $Id: ConfigurationFactory.java 1233 2006-11-22 09:15:56Z vmassol $
 */
public interface ConfigurationFactory
{
    /**
     * Registers a configuration implementation.
     * 
     * @param containerId the container id attached to this configuration class
     * @param containerType the container type attached to this configuration class
     * @param configurationType the type to differentiate this configuration from others for the
     *        specified container
     * @param configurationClass the configuration implementation class to register
     */
    void registerConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType, Class configurationClass);

    /**
     * @param containerId the container id attached to this configuration class
     * @param containerType the container type attached to this configuration class
     * @param configurationType the configuration's type
     * @return the configuration implementation class
     */
    Class getConfigurationClass(String containerId, ContainerType containerType,
        ConfigurationType configurationType);

    /**
     * @param containerId the container id attached to this configuration class
     * @param containerType the container type attached to this configuration class
     * @param configurationType the type to differentiate this configuration from others for the
     *        specified container
     * @return true if the specified configuration is already registered or false otherwise 
     */
    boolean isConfigurationRegistered(String containerId, ContainerType containerType,
        ConfigurationType configurationType);

    /**
     * Create a configuration instance matching the specified container and type.
     *
     * @param containerId the id of the container for which to create a configuration
     * @param containerType the type of the container for which to create a configuration
     * @param configurationType the type that differentiates the configuration we wish to create
     *        from other configurations for this container
     * @return the configuation instance
     */
    Configuration createConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType);

    /**
     * Create a configuration instance matching the specified container and type.
     * 
     * @param containerId the id of the container for which to create a configuration
     * @param containerType the type of the container for which to create a configuration
     * @param configurationType the type that differentiates the configuration we wish to create
     *        from other configurations for this container
     * @param home the configuration home
     * @return the configuation instance
     */
    Configuration createConfiguration(String containerId, ContainerType containerType,
        ConfigurationType configurationType, String home);
}