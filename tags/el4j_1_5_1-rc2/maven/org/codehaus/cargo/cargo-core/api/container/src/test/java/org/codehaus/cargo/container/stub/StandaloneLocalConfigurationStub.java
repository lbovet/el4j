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
package org.codehaus.cargo.container.stub;

import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.StandaloneLocalConfiguration;

/**
 * Mock for {@link org.codehaus.cargo.container.configuration.LocalConfiguration}. We need a static
 * mock rather than a dynamic mock (which we could get using JMock for example) because we're
 * testing factory classes which create an object out of a class name.
 *
 * @version $Id: StandaloneLocalConfigurationStub.java 1158 2006-07-29 10:55:48Z vmassol $
 */
public class StandaloneLocalConfigurationStub
    extends AbstractLocalConfigurationStub implements StandaloneLocalConfiguration
{
    public StandaloneLocalConfigurationStub(String home)
    {
        super(home);
    }

    public ConfigurationType getType()
    {
        return ConfigurationType.STANDALONE;
    }
}