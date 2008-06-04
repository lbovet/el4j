package org.codehaus.plexus.compiler;

/**
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import java.util.List;

/**
 * @author <a href="mailto:jason@plexus.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:matthew.pocock@ncl.ac.uk">Matthew Pocock</a>
 * @version $Id: Compiler.java 2723 2005-10-15 14:58:55Z brett $
 */
public interface Compiler
{
    String ROLE = Compiler.class.getName();

    CompilerOutputStyle getCompilerOutputStyle();

    String getInputFileEnding( CompilerConfiguration configuration )
        throws CompilerException;

    String getOutputFileEnding( CompilerConfiguration configuration )
        throws CompilerException;

    String getOutputFile( CompilerConfiguration configuration )
        throws CompilerException;

    boolean canUpdateTarget( CompilerConfiguration configuration )
        throws CompilerException;

    List compile( CompilerConfiguration configuration )
        throws CompilerException;

    /**
     * Create the command line that would be executed using this configuration.
     * If this particular compiler has no concept of a command line then returns
     * null.
     *
     * @param config     the CompilerConfiguration describing the
     *   compilation
     * @return an array of Strings that make up the command line, or null if
     *   this compiler has no concept of command line
     * @throws CompilerException  if there was an error generating the command
     *   line
     */
    String[] createCommandLine(CompilerConfiguration config )
        throws CompilerException;
}
