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
package org.codehaus.cargo.container.jetty.internal;

/**
 * Configuration capability for a Jetty 5.x Embedded container.
 *
 * @version $Id: Jetty5xEmbeddedStandaloneLocalConfigurationCapability.java 1490 2007-07-01 18:35:32Z vmassol $
 */
public class Jetty5xEmbeddedStandaloneLocalConfigurationCapability extends
        AbstractJettyEmbeddedStandaloneLocalConfigurationCapability {
    /**
     * {@inheritDoc}
     * @see org.codehaus.cargo.container.jetty.internal.AbstractJettyEmbeddedStandaloneLocalConfigurationCapability#AbstractJettyEmbeddedStandaloneLocalConfigurationCapability()
     */
    public Jetty5xEmbeddedStandaloneLocalConfigurationCapability()
    {
        super();
    }

}