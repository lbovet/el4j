/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.xmlmerge.action;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDAny;
import com.wutka.dtd.DTDContainer;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDParser;

import ch.elca.el4j.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.xmlmerge.Action;
import ch.elca.el4j.xmlmerge.DocumentException;
import ch.elca.el4j.xmlmerge.ElementException;

/**
 * Copy the patch element in the output parent with the correct position
 * according to the DTD declared in doctype.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class DtdInsertAction implements Action {

    /**
     * Map containing (ID, DTD) pairs, where ID represents the system ID of a 
     * DTD, and DTD represents the corresponding DTD.
     */
    static Map s_dtdMap = new Hashtable();
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(DtdInsertAction.class);
        
    /**
     * {@inheritDoc}
     */
    public void perform(Element originalElement, Element patchElement,
        Element outputParentElement) throws AbstractXmlMergeException {

        Element element;

        if (originalElement != null) {
            element = (Element) originalElement.clone();
        } else {
            element = (Element) patchElement.clone();
        }

        DTD dtd = getDTD(outputParentElement);

        List dtdElements = dtd.getItemsByType(DTDElement.class);

        // Find the corresponding element
        DTDElement parentDtdElement = null;
        for (Iterator it = dtdElements.iterator(); it.hasNext();) {
            DTDElement dtdElement = (DTDElement) it.next();

            if (dtdElement.getName().equals(outputParentElement.getName())) {
                parentDtdElement = dtdElement;
            }
        }

        if (parentDtdElement == null) {
            throw new ElementException(element, "Element "
                + outputParentElement.getName() + " not defined in DTD");
        } else {

            DTDItem item = parentDtdElement.getContent();

            if (item instanceof DTDAny) {
                // the parent element accepts anything in any order
                outputParentElement.addContent(element);
            } else if (item instanceof DTDContainer) {

                // List existing elements in output parent element
                List existingChildren = outputParentElement.getChildren();

                if (existingChildren.size() == 0) {
                    // This is the first child
                    outputParentElement.addContent(element);
                } else {

                    List orderedDtdElements = getOrderedDtdElements(
                        (DTDContainer) item);

                    int indexOfNewElementInDtd = orderedDtdElements
                        .indexOf(element.getName());
                    s_logger.debug("index of element " + element.getName() 
                        + ": " + indexOfNewElementInDtd);

                    int pos = existingChildren.size();

                    // Calculate the position in the parent where we insert the
                    // element
                    for (int i = 0; i < existingChildren.size(); i++) {
                        String elementName = ((Element) existingChildren.get(i))
                            .getName();
                        s_logger.debug("index of child " + elementName + ": "
                            + orderedDtdElements.indexOf(elementName));
                        if (orderedDtdElements.indexOf(elementName) 
                            > indexOfNewElementInDtd) {
                            pos = i;
                            break;
                        }
                    }

                    s_logger.debug("adding element " + element.getName() 
                        + " add in pos " + pos);
                    outputParentElement.addContent(pos, element);

                }

            }

        }

    }

    /**
     * Gets the DTD declared in the doctype of the element's owning document.
     * 
     * @param element
     *            The element for which the DTD will be retrieved
     * @return The DTD declared in the doctype of the element's owning document
     * @throws DocumentException
     *             If an error occurred during DTD retrieval
     */
    public DTD getDTD(Element element) throws DocumentException {

        if (element.getDocument().getDocType() != null) {

            String systemId = element.getDocument().getDocType().getSystemID();

            DTD dtd = (DTD) s_dtdMap.get(systemId);

            // if not in cache, create the DTD and put it in cache
            if (dtd == null) {
                URL url;

                try {
                    url = new URL(systemId);
                } catch (MalformedURLException e) {
                    throw new DocumentException(element.getDocument(), e);
                }

                try {
                    dtd = new DTDParser(new InputStreamReader(url.openStream()))
                        .parse();
                } catch (IOException ioe) {
                    throw new DocumentException(element.getDocument(), ioe);
                }

                s_dtdMap.put(systemId, dtd);
            }

            return dtd;

        } else {
            throw new DocumentException(element.getDocument(),
                "No DTD specified in document " + element.getDocument());
        }
    }

    /**
     * Retieves a list containing the DTD elements of a given DTD container. 
     * @param container A DTD container.
     * @return A list containing the DTD elements of a given DTD container
     */
    public List getOrderedDtdElements(DTDContainer container) {
        List result = new ArrayList();

        DTDItem[] items = container.getItems();

        for (int i = 0; i < items.length; i++) {
            if (items[i] instanceof DTDContainer) {
                // recursively add container children
                result.addAll(getOrderedDtdElements((DTDContainer) items[i]));
            } else if (items[i] instanceof DTDName) {
                result.add(((DTDName) items[i]).getValue());
            }
        }

        return result;

    }

}
