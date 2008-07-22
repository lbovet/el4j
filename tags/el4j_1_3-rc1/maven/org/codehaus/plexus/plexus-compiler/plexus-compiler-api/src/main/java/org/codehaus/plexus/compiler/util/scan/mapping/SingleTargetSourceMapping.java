package org.codehaus.plexus.compiler.util.scan.mapping;

/**
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.compiler.util.scan.InclusionScanException;

import java.util.Set;
import java.util.Collections;
import java.io.File;

/**
 * Maps a set of input files to a single output file.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: SingleTargetSourceMapping.java 2408 2005-08-18 13:39:41Z trygvis $
 */
public class SingleTargetSourceMapping
    implements SourceMapping
{
    private String sourceSuffix;

    private String outputFile;

    public SingleTargetSourceMapping( String sourceSuffix, String outputFile )
    {
        this.sourceSuffix = sourceSuffix;

        this.outputFile = outputFile;
    }

    public Set getTargetFiles( File targetDir, String source )
        throws InclusionScanException
    {
        if ( !source.endsWith( sourceSuffix ) )
        {
            return Collections.EMPTY_SET;
        }

        return Collections.singleton( new File( targetDir, outputFile ) );
    }
}