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
package org.codehaus.cargo.container.internal;

import java.util.Map;

/**
 * All methods that a container that is spawned must implement.
 *
 * @version $Id: SpawnedContainer.java 882 2006-02-27 15:31:16Z vmassol $
 */
public interface SpawnedContainer
{
    /**
     * @param classpath the extra classpath that is added to the container's classpath when it is
     *        started.
     */
    void setExtraClasspath(String[] classpath);

    /**
     * @return the extra classpath that is added to the container's classpath when it is started.
     */
    String[] getExtraClasspath();

    /**
     * @param properties the System properties to set in the container executing VM.
     */
    void setSystemProperties(Map properties);

    /**
     * @return the System properties to set in the container executing VM.
     */
    Map getSystemProperties();
}
