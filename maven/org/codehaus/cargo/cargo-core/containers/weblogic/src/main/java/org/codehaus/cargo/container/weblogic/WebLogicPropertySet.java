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
package org.codehaus.cargo.container.weblogic;

/**
 * Gathers all WebLogic properties.
 * 
 * @version $Id: WebLogicPropertySet.java 1188 2006-09-24 16:01:08Z vmassol $
 */
public interface WebLogicPropertySet
{
    /**
     * User with administrator rights.
     */
    String ADMIN_USER = "cargo.weblogic.administrator.user";
    
    /**
     * Password for user with administrator rights.
     */
    String ADMIN_PWD = "cargo.weblogic.administrator.password";
    
    /**
     * WebLogic server name.
     */
    String SERVER = "cargo.weblogic.server";
    
    /**
     * Domain name
     * 
     * @author Frank Bitzer, added 26.11.07
     */
    String DOMAIN = "cargo.weblogic.domain";
    
    
    /**
     * JVM to use. JRockit is strongly recommended.
     */
    String JVM = "cargo.weblogic.jvm";
    
}
