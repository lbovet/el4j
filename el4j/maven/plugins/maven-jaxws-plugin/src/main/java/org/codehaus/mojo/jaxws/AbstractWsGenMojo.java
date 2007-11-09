/*
 * Copyright 2006 Guillaume Nodet.
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

package org.codehaus.mojo.jaxws;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.sun.tools.ws.WsGen;

/**
 * 
 *
 * @author gnodet <gnodet@apache.org>
 * @author dantran <dantran@apache.org>
 * @version $Id$
 */
abstract class AbstractWsGenMojo extends AbstractJaxwsMojo {

    /**
     * Specify that a WSDL file should be generated in ${resourceDestDir}
     * 
     * @parameter default-value="false"
     */
    private boolean genWsdl;

    
    /**
     * Directory containing the generated wsdl files.
     * 
     * @parameter default-value="${project.build.directory}/jaxws/wsgen/wsdl"
     */
    
    private File resourceDestDir;

    /**
     * service endpoint implementation class name.
     * 
     * @parameter 
     * @required
     */
    private String sei;
    
    /**
     * Project test classpath.
     *
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    private List classpathElements;
    
    /**
     * The source directories containing the sources to be compiled.
     *
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List compileSourceRoots;
    
    /**
     * The directory where compiled classes go.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File classDirectory;

    /**
     * Used in conjunction with genWsdl to specify the protocol to use in the 
     * wsdl:binding.  Value values are "soap1.1" or "Xsoap1.2", default is "soap1.1". 
     * "Xsoap1.2" is not standard and can only be used in conjunction with the 
     * -extensions option
     * 
     * @parameter 
     */
    private String protocol;

    
    /**
     * Specify where to place generated source files, keep is turned on with this option. 
     * 
     * @parameter 
     */
    private File sourceDestDir;
    //default-value="${project.build.directory}/jaxws/java"
    
    private URLClassLoader classLoader = null;

    public void execute()
        throws MojoExecutionException, MojoFailureException {
        init();

        // Need to build a URLClassloader since Maven removed it form the chain
        ClassLoader parent = this.getClass().getClassLoader();
        String orginalSystemClasspath = this.initClassLoader(parent);

        try {
            List<String> classesToProcess = new LinkedList<String>();
            if (sei.equals("*")) {
                // init classloader
                ArrayList<URL> urls = new ArrayList<URL>();
                URL url = classDirectory.toURL();
                urls.add(url);
                
                for (Object cp : classpathElements) {
                    url = new File((String) cp).toURL();
                    urls.add(url);
                }
                classLoader = new URLClassLoader(urls.toArray(new URL[0]));
                
                // find class files to process
                addClassFiles(classesToProcess, classDirectory);
            } else {
                classesToProcess.add(sei);
            }
            
            for (String classToPrecess : classesToProcess) {
                sei = classToPrecess;
                ArrayList<String> args = getWsGenArgs();

                if (WsGen.doMain(args.toArray(new String[args.size()])) != 0)
                    throw new MojoExecutionException("Error executing: wsgen " + args);
            }
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Throwable e) {
            throw new MojoExecutionException("Failed to execute wsgen",e);
        } finally {
            // Set back the old classloader
            Thread.currentThread().setContextClassLoader(parent);
            System.setProperty("java.class.path", orginalSystemClasspath);
        }
    }

    private void init() throws MojoExecutionException, MojoFailureException {
        if (!getDestDir().exists())
            getDestDir().mkdirs();
    }

    /**
     * Construct wsgen arguments
     * @return a list of arguments
     * @throws MojoExecutionException
     */
    private ArrayList<String> getWsGenArgs()
        throws MojoExecutionException {
        ArrayList<String> args = new ArrayList<String>();

        if (verbose) {
            args.add("-verbose");
        }

        if (keep || this.sourceDestDir!=null) {
            args.add("-keep");
        }

        if (this.sourceDestDir != null) {
            args.add("-s");
            args.add(this.sourceDestDir.getAbsolutePath());
            this.sourceDestDir.mkdirs();
        }

        args.add("-d");
        args.add(getDestDir().getAbsolutePath());

        args.add("-cp");
        StringBuilder buf = new StringBuilder();
        buf.append(getDestDir().getAbsolutePath());
        for (Artifact a : (Set<Artifact>)project.getArtifacts()) {
            buf.append(File.pathSeparatorChar);
            buf.append(a.getFile().getAbsolutePath());
        }
        args.add(buf.toString());

        if (this.genWsdl) {
            if (this.protocol != null) {
                args.add("-wsdl:" + this.protocol);
            } else {
                args.add("-wsdl");
            }

            args.add("-r");
            args.add(this.resourceDestDir.getAbsolutePath());
            this.resourceDestDir.mkdirs();

        }

        args.add(sei);

        getLog().debug("jaxws:wsgen args: " + args);

        return args;
    }
    
    @SuppressWarnings("unchecked")
    private void addClassFiles(List<String> list, File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addClassFiles(list, file);
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                
                // cut classDirectory part away
                String path = file.toString().substring(
                    classDirectory.toString().length() + 1);
                
                // only take non-generated files (they must have source files)
                boolean found = false;
                for (Object srcDir : compileSourceRoots) {
                    String dirReplaced = ((String) srcDir)
                        + File.separatorChar + path;
                    dirReplaced = dirReplaced.substring(0,
                        dirReplaced.length() - "class".length());
                    if (new File(dirReplaced + "java").exists()) {
                        found = true;
                    }
                }
                if (!found) {
                    continue;
                }
                
                String className = path.replace(File.separatorChar, '.');
                className = className.substring(0,
                    className.length() - ".class".length());

                try {
                    Class c = classLoader.loadClass(className);
                    // second check for generated files
                    if (c.getPackage().getName().endsWith(".gen")) {
                        continue;
                    }
                    Annotation[] annots = c.getAnnotations();
                    // search for @WebService annotations
                    if (annots.length > 0) {
                        for (Annotation annotation : annots) {
                            if (annotation.annotationType().getName()
                                .equals("javax.jws.WebService")) {
                                list.add(className);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore class
                }
            }
        }
    }
}
