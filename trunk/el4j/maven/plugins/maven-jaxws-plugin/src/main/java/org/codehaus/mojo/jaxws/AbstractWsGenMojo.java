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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
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
 * @author Stefan Wismer (SWI)
 * 
 * @version $Id$
 */
abstract class AbstractWsGenMojo extends AbstractJaxwsMojo {

    /**
     * Specify that a WSDL file should be generated in ${resourceDestDir}
     * 
     * @parameter default-value="true"
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
     * @parameter  default-value="*"
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
     * The host part of the URL (like http://localhost:8080).
     * @parameter 
     */
    private String hostURL;
    
    /**
     * The context part of the URL (this becomes the directory).
     * @parameter 
     */
    private String contextURL;
    
    /**
     * The service part of the URL. The star charcter (*) gets replaced by
     * the service interface name.
     * @parameter 
     */
    private String serviceURL;

    
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
                
                
                if (hostURL != null && contextURL != null
                    && serviceURL != null) {
                    
                    if (!hostURL.endsWith("/")) {
                        hostURL = hostURL + "/";
                    }
                    if (!contextURL.endsWith("/")) {
                        contextURL = contextURL + "/";
                    }
                    
                    String serviceName = getServiceName(classToPrecess);
                    serviceURL = serviceURL.replaceAll("\\*", serviceName);
                    
                    replaceURLinWSDL(serviceName,
                        hostURL + contextURL + serviceURL);
                }
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
    
    /**
     * Adds all the class files located in the folder to the list.
     * @param list      the list to add the class names
     * @param folder    the folder to examine
     */
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
                    for (Annotation annotation : annots) {
                        if (annotation.annotationType().getName()
                            .equals("javax.jws.WebService")) {
                            
                            list.add(className);
                            break;
                        }
                    }
                } catch (Exception e) {
                    // ignore class
                }
            }
        }
    }
    
    /**
     * Replaces REPLACE_WITH_ACTUAL_URL by actual url in WSDL file.
     * @param serviceName    the service name
     * @param url            the url which replaces REPLACE_WITH_ACTUAL_URL
     * @throws MojoExecutionException
     */
    private void replaceURLinWSDL(String serviceName, String url)
        throws MojoExecutionException {
        
        File file = new File(resourceDestDir.getAbsolutePath()
            + File.separatorChar + serviceName + "WSService.wsdl");
        
        if (file.exists()) {
            String line;
            StringBuffer sb = new StringBuffer();
            try {
                // read file and replace
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis));
                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll("REPLACE_WITH_ACTUAL_URL", url);
                    sb.append(line + "\n");
                }
                reader.close();
                
                // write file
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(sb.toString());
                out.close();
            } catch (Throwable e) {
                throw new MojoExecutionException(
                    "Could not modify WSDL file for service " + serviceName);
            }
        }
    }
    
    /**
     * @param classToPrecess   the web service class to process
     * @return                 the service name derived from the annotated class
     * @throws MojoExecutionException
     */
    @SuppressWarnings("unchecked")
    private String getServiceName(String classToPrecess)
        throws MojoExecutionException {
        
        String serviceName = null;
        try {
            Class c = classLoader.loadClass(classToPrecess);
            
            Annotation[] annots = c.getAnnotations();
            // search for @WebService annotations
            for (Annotation annotation : annots) {
                if (annotation.annotationType().getName()
                    .equals("javax.jws.WebService")) {
                    
                    serviceName = (String) annotation
                        .annotationType().getMethod("serviceName")
                        .invoke(annotation);
                    
                    break;
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException(
                "Could not get serviceName from " + classToPrecess);
        }
        if (serviceName == null) {
            throw new MojoExecutionException(
                "Could not get serviceName from " + classToPrecess);
        }
        
        // cut "WSService" suffix away
        if (serviceName.endsWith("WSService")) {
            serviceName = serviceName.substring(0,
                serviceName.length() - "WSService".length());
        } else {
            throw new MojoExecutionException(
                classToPrecess + " does not follow the convention that "
                + "serviceName must end with WSService");
        }
        
        return serviceName;
    }
}
