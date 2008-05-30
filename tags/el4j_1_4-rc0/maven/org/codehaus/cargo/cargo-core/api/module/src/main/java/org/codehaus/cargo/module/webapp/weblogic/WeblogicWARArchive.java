/**
 * 
 */
package org.codehaus.cargo.module.webapp.weblogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import org.codehaus.cargo.module.JarArchive;
import org.codehaus.cargo.module.JarArchiveIo;
import org.codehaus.cargo.module.webapp.DefaultWarArchive;
import org.codehaus.cargo.util.DefaultFileHandler;
import org.codehaus.cargo.util.FileHandler;

import org.jdom.JDOMException;

import sun.security.action.GetLongAction;

/**
 * 
 * WAR file of weblogic application
 * 
 * @author Frank Bitzer (FBI)
 *
 */
public class WeblogicWARArchive extends DefaultWarArchive {

//	 /**
//     * The file representing either the WAR file or the expanded WAR directory.
//     */
//    private String file;
    
    /**
     * The parsed deployment descriptor.
     */
    private WeblogicXml weblogicXml;

    /**
     * Constructor.
     * 
     * @param warFile The web application archive
     * @throws IOException If there was a problem reading the  deployment
     *         descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not
     *         be parsed
     */
    public WeblogicWARArchive(String warFile)
        throws IOException, JDOMException
    {
    	
    	
    	super(warFile);
    	
        //this.warFile = warFile;
        this.weblogicXml = parseWeblogicXml();
    }
    
    
    
    /**
     * Returns the <code>weblogic.xml</code> deployment descriptor of the web application.
     * 
     * @return The parsed deployment descriptor, or <code>null</code> if no such file exists.
     */
    public final WeblogicXml getWeblogicXml()
    {
        return this.weblogicXml;
    }

    
    /**
     * Edits the deployable and writes the given WeblogicXml deployment 
     * descriptor to WEB-INF/weblogic.xml.
     * If it already exists, it is replaced
     * 
     * @param weblogicXML
     * @throws IOException
     */
    public void updateWeblogicXML(WeblogicXml weblogicXML)
    		throws IOException
    	{
    	try {
    		
    		//exploded war, so we can easily replace weblogic.xml
	    	if (new File(this.file).isDirectory())
	        {
	            File contextXmlFile = new File(this.file, "WEB-INF/weblogic.xml");
	            
	            //delete old file at first
	            try {
	            	contextXmlFile.delete();
	            } catch (Exception ex) {
	            }
	            
	            FileOutputStream out = new FileOutputStream(contextXmlFile);
	                
	            WeblogicXmlIo.writeDescriptor(weblogicXml, out, null, true);
	                
	            out.close();
	           
	        }
	        else
	        {
	        	//the deployable is a packed war file, so we
	        	//have to modify this war
	        	FileHandler fileHandler = new DefaultFileHandler();
	       
	        	JarInputStream in = getContentAsStream();
	        	
	        	
	        	File tempWAR = File.createTempFile("war.tmp", null);
	        	tempWAR.deleteOnExit();
	        	
	        	//System.out.println("Created temp file at " + tempWAR.getAbsolutePath());
	        	
	        	
	        	JarOutputStream out = new JarOutputStream(new FileOutputStream(tempWAR));
	        	
	        	
	        	// Copy all entries from the original WAR file except
	        	//weblogic.xml
	            JarEntry entry;
	           
	            
	            while ((entry = in.getNextJarEntry()) != null)
	            {
	                if (!entry.getName().toLowerCase().endsWith("weblogic.xml"))
	                {
	                    out.putNextEntry(entry);
	                    fileHandler.copy(in, out);
	                }
	            }
	            in.close();
	        	
	        	//create new entry in war for weblogic.xml
	        	JarEntry descriptorEntry = new JarEntry("WEB-INF/weblogic.xml");
	            out.putNextEntry(descriptorEntry);
	            
	            WeblogicXmlIo.writeDescriptor(weblogicXml, out, null, true);
	            
	            out.close();
	            
	            //rename temp file
	            File origFile = new File(this.file);
	            
	            
	            
	            if (!origFile.delete()) {
	            	throw new IOException("Failed to delete original war file.");
	            }
	            //if (!tempWAR.renameTo(origFile)){
	            	
	            	//throw new IOException("Renaming of WAR file failed: ");
	            	
	            	//renaming not successful, so try to copy manually
	            //System.out.println("Copying temp file back to original...");
	            this.getFileHandler().copyFile(tempWAR.getAbsolutePath(), this.file);
	            	
	            //}
	            
	            
	            //delete temp file
	            tempWAR.delete();
	             
	        }
    	
	    	
	    } catch (Exception ex){
	    	throw new IOException("Writing deployment descriptor failed: " + ex.getLocalizedMessage());
	    }
    	
        
    	
    }
    
//    /** Copies src file to dst file.
//     * 	If the dst file does not exist, it is created
//     */
//    private void copy(File src, File dst) throws IOException {
//        InputStream in = new FileInputStream(src);
//        OutputStream out = new FileOutputStream(dst);
//    
//        // Transfer bytes from in to out
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = in.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
//        in.close();
//        out.close();
//    }
    

    /**
     * @return the parsed <code>weblogic.xml</code> descriptor or null
     *         if none exists 
     * @throws IOException If there was a problem reading the  deployment
     *         descriptor in the WAR
     * @throws JDOMException If the deployment descriptor of the WAR could not
     *         be parsed
     */
    private WeblogicXml parseWeblogicXml()
        throws IOException, JDOMException
    {
    	WeblogicXml context = null;

    	
        InputStream in = null;
        try
        {
            // Are we manipulating a WAR file or an expanded WAR directory?
            if (new File(this.file).isDirectory())
            {
                File contextXmlFile = new File(this.file, "WEB-INF/weblogic.xml");
                if (contextXmlFile.exists())
                {
                    in = new FileInputStream(contextXmlFile);
                }
            }
            else
            {
                JarArchive jarArchive = JarArchiveIo.open(new File(this.file));
                in = jarArchive.getResource("WEB-INF/weblogic.xml");
            }


            if (in != null)
            {
                context = WeblogicXmlIo.parseWeblogicXml(in);
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }

        return context;
    }
    
    
}

