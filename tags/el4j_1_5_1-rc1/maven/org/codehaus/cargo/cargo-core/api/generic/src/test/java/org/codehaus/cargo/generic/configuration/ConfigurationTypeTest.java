/* 
 * ========================================================================
 * 
 * Copyright 2005 Vincent Massol.
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

import org.codehaus.cargo.container.configuration.ConfigurationType;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ConfigurationType}.
 * 
 * @version $Id: ConfigurationTypeTest.java 608 2005-11-05 15:12:15Z vmassol $
 */
public class ConfigurationTypeTest extends TestCase
{
    public void testEquals()
    {
        ConfigurationType type = ConfigurationType.EXISTING;
        assertEquals(ConfigurationType.EXISTING, type);
    }
}