package org.codehaus.plexus.compiler.util.scan;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Test for 
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id: SimpleSourceInclusionScannerTest.java 3089 2006-03-23 05:55:25Z carlos $
 */
public class SimpleSourceInclusionScannerTest
    extends AbstractSourceInclusionScannerTest
{
    
    private Set includes, excludes;

    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        includes = Collections.singleton( "*.java" );
        excludes = new HashSet();
        scanner = new SimpleSourceInclusionScanner( includes, excludes );
    }

}
