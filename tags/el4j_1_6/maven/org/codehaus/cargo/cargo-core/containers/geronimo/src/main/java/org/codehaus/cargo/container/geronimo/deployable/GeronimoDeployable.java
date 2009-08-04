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
package org.codehaus.cargo.container.geronimo.deployable;

import org.codehaus.cargo.container.deployable.Deployable;

/**
 * Geronimo-specific deployable which adds supports for passing Geronimo deployment plans.
 *
 * @version $Id: GeronimoDeployable.java 860 2006-02-12 23:33:44Z vmassol $
 */
public interface GeronimoDeployable extends Deployable
{
    /**
     * @param plan path to the Geronimo plan
     */
    void setPlan(String plan);

    /**
     * @return the plan path
     */
    String getPlan();
}