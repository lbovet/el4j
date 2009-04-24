// EMMA plugin for Maven 2
// Copyright (c) 2007 Alexandre ROMAN and contributors
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

// $Id$

package org.codehaus.mojo.emma.task;

import java.io.File;

import org.codehaus.plexus.util.cli.Commandline;

/**
 * Create a report from EMMA coverage data.
 * 
 * @author <a href="mailto:alexandre.roman@gmail.com">Alexandre ROMAN</a>
 */
public class ReportTask extends AbstractTask
{
    private File[] sourcePaths = new File[0];

    private String encoding;

    private String depth;

    private String columns;

    private String sort;

    private String metrics;

    private File metadataFile;

    private File[] dataFiles = new File[0];

    protected void prepare( Commandline cmd ) throws Exception
    {
        // tell EMMA to create a report
        cmd.createArg().setValue( "report" );

        // set metadata file
        if ( metadataFile != null )
        {
            cmd.createArg().setValue( "-input" );
            cmd.createArg().setValue( metadataFile.getCanonicalPath() );
        }

        // set additionnal coverage data files
        if ( dataFiles != null )
        {
            for ( int i = 0; i < dataFiles.length; ++i )
            {
                if (dataFiles[i] != null)
                {
                    cmd.createArg().setValue( "-input" );
                    cmd.createArg().setValue( dataFiles[i].getCanonicalPath() );
                }
            }
        }

        // set paths to Java source code
        if ( sourcePaths != null )
        {
            for ( int i = 0; i < sourcePaths.length; ++i )
            {
                cmd.createArg().setValue( "-sourcepath" );
                cmd.createArg().setValue( sourcePaths[i].getCanonicalPath() );
            }
        }

        // set report options
        cmd.createArg().setValue( "-Dreport.colums=" + columns );
        cmd.createArg().setValue( "-Dreport.sort=" + sort );
        cmd.createArg().setValue( "-Dreport.metrics=" + metrics );
        cmd.createArg().setValue( "-Dreport.depth=" + depth );
        cmd.createArg().setValue( "-Dreport.out.encoding=" + encoding );
        cmd.createArg().setValue( "-report" );
        cmd.createArg().setValue( "html" );

        // set report output file
        final File outputFile = new File( getOutputDirectory(), "index.html" );
        cmd.createArg().setValue( "-Dreport.html.out.file=" + outputFile.getCanonicalPath() );
    }

    public String getColumns()
    {
        return columns;
    }

    public void setColumns( String columns )
    {
        this.columns = columns;
    }

    public String getDepth()
    {
        return depth;
    }

    public void setDepth( String depth )
    {
        this.depth = depth;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public String getMetrics()
    {
        return metrics;
    }

    public void setMetrics( String metrics )
    {
        this.metrics = metrics;
    }

    public String getSort()
    {
        return sort;
    }

    public void setSort( String sort )
    {
        this.sort = sort;
    }

    public File[] getSourcePaths()
    {
        return sourcePaths;
    }

    public void setSourcePaths( File[] sourcePaths )
    {
        this.sourcePaths = sourcePaths;
    }

    public File[] getDataFiles()
    {
        return dataFiles;
    }

    public void setDataFiles( File[] dataFiles )
    {
        this.dataFiles = dataFiles;
    }

    public File getMetadataFile()
    {
        return metadataFile;
    }

    public void setMetadataFile( File metadataFile )
    {
        this.metadataFile = metadataFile;
    }
}
