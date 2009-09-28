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
package org.codehaus.cargo.util.log;

import org.codehaus.cargo.util.internal.log.AbstractLogger;

/**
 * Null implementation which does nothing with log messages.
 * 
 * @version $Id: NullLogger.java 1060 2006-06-30 16:02:28Z vmassol $
 */
public class NullLogger extends AbstractLogger
{
    /**
     * {@inheritDoc}
     * @see AbstractLogger#doLog(LogLevel, String, String)
     */
    protected void doLog(LogLevel level, String message, String category)
    {
        // Do nothing
    }
}