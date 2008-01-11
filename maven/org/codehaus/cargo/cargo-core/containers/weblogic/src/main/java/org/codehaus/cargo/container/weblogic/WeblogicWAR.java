/**
 * 
 */
package org.codehaus.cargo.container.weblogic;


import java.util.Iterator;
import java.util.List;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.module.DescriptorElement;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicWARArchive;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicXml;
import org.codehaus.cargo.module.webapp.weblogic.WeblogicXmlTag;
import org.jdom.Element;

/**
 * 
 * Extension of WAR deployable to support Weblogic weblogic.xml deployment
 * descriptor.
 * 
 * @author Frank Bitzer (FBI)
 * 
 * @see TomcatWAR
 *
 */
public class WeblogicWAR extends WAR {
	
	/**
     * The parsed Weblogic descriptors in the WAR.
     */
    private WeblogicWARArchive warArchive;
    
    /**
     * @param war the location of the WAR being wrapped. This must point to either a WAR file or an
     *        expanded WAR directory.
     */
    public WeblogicWAR(String war)
    {
        super(war);
        
        try
        {
            this.warArchive = new WeblogicWARArchive(getFile());
        }
        catch (Exception e)
        {
            throw new ContainerException("Failed to parse Weblogic WAR file "
                + "in [" + getFile() + "]", e);
        }
    }

    /**
     * root context for deployment to Weblogic.
     * 
     * @return if no context is specified yet, get the context defined 
     * 			in <code>weblogic.xml</code> if any.
     *         If there is no <code>weblogic.xml</code> or if it doesn't
     *         define any root context, then return {@link WAR#getContext()}
     *         (that is, derive it from filename).
     */
    public synchronized String getContext()
    {
    	String result = this.context;
    	
    	if (this.context == null){
	        result = parseWeblogicContextXml();
	        if (result == null)
	        {
	            result = super.getContext();
	        }
    	}
    	
    	return result;
    }
    
    
    /**
     * Sets the <context-root> element in weblogic.xml to given value.
     * If no <context-root> element exists, it is created.
     * 
     * @param value
     */
    public void setWeblogicContext(String value){
    	
    	 if (this.warArchive.getWeblogicXml() != null) {
    		 
    		 WeblogicXml xml = this.warArchive.getWeblogicXml();
    		 
    		
    		 //edit current context-root, if existing
    		 Iterator elements = xml.getElements(xml.getDescriptorType().getTagByName(
    		            WeblogicXmlTag.CONTEXT_ROOT));
    		 
    		 //helper variable
    		 //becomes true, if a context root is already specified
    		 boolean ctxExists = false;
    		 //becomes true, if there were changes necessary in weblogic.xml
    		 boolean wasModified = false;
    		 
    		 while (elements.hasNext())
    		 {
    		     Element e = (Element) elements.next();
    		     
    		     //only modify value if context root 
    		     //is not already set correctly
    		     if (!e.getText().equals(value)) {
    		    	 e.setText(value);
    		    	 wasModified = true;
    		     }
    		     
    		     ctxExists = true;
    		    
    		 }
    		 
    		 //add context-root element if it does not already exist
    		 if (!ctxExists) {
    			 this.addContextRoot(xml,value);
    			 wasModified = true;
    		 }
    		 
    		 
    		 try {
    		
    			 if (wasModified){
    				 this.warArchive.updateWeblogicXML(xml);
    			 }
    			 
    			
    			 
    		 } catch (Exception ex){
    			 throw new ContainerException("Error modifying weblogic.xml: "
    					 + ex.getLocalizedMessage());
    		 }
    		 
    	 } else {
    		 
    		 throw new ContainerException("Deployment without " +
    		 		"weblogic.xml is currently not supported!");
    		 
    	 }
    	 
    }
    
    /**
     * Helper function, adds a brand new <context-root> element with given value
     * to weblogic.xml
     * 
     * @param xml the weblogic.xml to edit
     * @param value String value to set context-root to
     */
    private void addContextRoot(WeblogicXml xml, String value){
    	Element contextroot;
    	
    	contextroot =
            new Element(WeblogicXmlTag.CONTEXT_ROOT);
    	
    	contextroot.addContent(value);
    	
    	
    	xml.addElement(xml.getDescriptorType().getTagByName(
            WeblogicXmlTag.CONTEXT_ROOT), contextroot, xml.getRootElement());
    

	}
    

//    /**
//     * @return true if the WAR contains a <code>weblogic.xml</code>
//     *              */
//    private boolean containsWeblogicXml()
//    {
//        return (this.warArchive.getWeblogicXml() != null);
//    
//    }
    
    
    /**
     * @return the context-root from Weblogic's <code>weblogic.xml</code> if
     *         it is defined or <code>null</code> otherwise.
     */
    private String parseWeblogicContextXml()
    {
        String context = null;
        
        if (this.warArchive.getWeblogicXml() != null)
        {
            List l = this.warArchive.getWeblogicXml().getTags(WeblogicXmlTag.CONTEXT_ROOT);
            
            if (l != null && l.size() == 1){
            	context = ((DescriptorElement)l.get(0)).getTextNormalize();
            	
            }
            
        }
        
        return context;
    }   

}
