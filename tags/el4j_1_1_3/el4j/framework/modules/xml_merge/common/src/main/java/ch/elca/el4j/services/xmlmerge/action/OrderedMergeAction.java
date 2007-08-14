/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.xmlmerge.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.Action;
import ch.elca.el4j.services.xmlmerge.DocumentException;
import ch.elca.el4j.services.xmlmerge.Mapper;
import ch.elca.el4j.services.xmlmerge.Matcher;
import ch.elca.el4j.services.xmlmerge.MergeAction;

/**
 * Merge implementation traversing parallelly both element contents. Works when
 * contents are in the same order in both elements.
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
public class OrderedMergeAction extends AbstractMergeAction {

    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(OrderedMergeAction.class);
    
    /**
     * {@inheritDoc}
     */
    public void perform(Element originalElement, Element patchElement,
        Element outputParentElement) throws AbstractXmlMergeException {

        s_logger.debug("Merging: " + originalElement + "(List 1) and "
            + patchElement + "(List 2)");

        Mapper mapper = (Mapper) m_mapperFactory.getOperation(originalElement,
            patchElement);

        if (originalElement == null) {
            outputParentElement.addContent(mapper.map(patchElement));
        } else if (patchElement == null) {
            outputParentElement.addContent((Content) originalElement.clone());
        } else {

            Element workingElement = new Element(originalElement.getName(),
                originalElement.getNamespacePrefix(), originalElement
                    .getNamespaceURI());
            addAttributes(workingElement, originalElement);

            s_logger.debug("Adding " + workingElement);
            outputParentElement.addContent(workingElement);

            doIt(workingElement, originalElement, patchElement);
        }

    }

    /**
     * Performs the actual merge between two source elements.
     * 
     * @param parentOut
     *            The merged element
     * @param parentIn1
     *            The first source element
     * @param parentIn2
     *            The second source element
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */
    private void doIt(Element parentOut, Element parentIn1, Element parentIn2)
        throws AbstractXmlMergeException {

        addAttributes(parentOut, parentIn2);

        Content[] list1 = (Content[]) parentIn1.getContent().toArray(
            new Content[] {});
        Content[] list2 = (Content[]) parentIn2.getContent().toArray(
            new Content[] {});

        int offsetTreated1 = 0;
        int offsetTreated2 = 0;

        for (int i = 0; i < list1.length; i++) {

            s_logger.debug("List 1: " + list1[i]);

            if (list1[i] instanceof Comment || list1[i] instanceof Text) {
                parentOut.addContent((Content) list1[i].clone());
                offsetTreated1++;
            } else if (!(list1[i] instanceof Element)) {
                throw new DocumentException(list1[i].getDocument(),
                    "Contents of type " + list1[i].getClass().getName()
                        + " not supported");
            } else {
                Element e1 = (Element) list1[i];

                // does e1 exist on list2 and has not yet been treated
                int posInList2 = -1;
                for (int j = offsetTreated2; j < list2.length; j++) {

                    s_logger.debug("List 2: " + list2[j]);

                    if (list2[j] instanceof Element) {

                        if (((Matcher) m_matcherFactory.getOperation(e1,
                                (Element) list2[j]))
                            .matches(e1, (Element) list2[j])) {
                            s_logger.debug("Match found: " + e1 + " and "
                                + list2[j]);
                            posInList2 = j;
                            break;
                        }
                    } else if (list2[j] instanceof Comment
                        || list2[j] instanceof Text) {
                        // skip
                    } else {
                        throw new DocumentException(list2[j].getDocument(),
                            "Contents of type " + list2[j].getClass().getName()
                                + " not supported");
                    }
                }

                // element found in second list, but there is some elements to
                // be
                // treated before in second list
                while (posInList2 != -1 && offsetTreated2 < posInList2) {
                    Content contentToAdd;
                    if (list2[offsetTreated2] instanceof Element) {
                        applyAction(parentOut, null,
                            (Element) list2[offsetTreated2]);
                    } else {
                        contentToAdd = (Content) list2[offsetTreated2].clone();
                        parentOut.addContent(contentToAdd);
                    }

                    offsetTreated2++;
                }

                // element found in all lists
                if (posInList2 != -1) {

                    applyAction(parentOut, (Element) list1[offsetTreated1],
                        (Element) list2[offsetTreated2]);

                    offsetTreated1++;
                    offsetTreated2++;
                } else {
                // element not found in second list
                    applyAction(parentOut, (Element) list1[offsetTreated1],
                        null);
                    offsetTreated1++;
                }
            }
        }

        // at end of list1, is there some elements on list2 which must be still
        // treated?
        while (offsetTreated2 < list2.length) {
            Content contentToAdd;
            if (list2[offsetTreated2] instanceof Element) {
                applyAction(parentOut, null, (Element) list2[offsetTreated2]);
            } else {
                contentToAdd = (Content) list2[offsetTreated2].clone();
                parentOut.addContent(contentToAdd);
            }

            offsetTreated2++;
        }

    }

    /**
     * Applies the action which performs the merge between two source elements.
     * 
     * @param workingParent
     *            Output parent element
     * @param originalElement
     *            Original element
     * @param patchElement
     *            Patch element
     * @throws AbstractXmlMergeException
     *             if an error occurred during the merge
     */
    private void applyAction(Element workingParent, Element originalElement,
        Element patchElement) throws AbstractXmlMergeException {
        Action action = (Action) m_actionFactory.getOperation(originalElement,
            patchElement);
        Mapper mapper = (Mapper) m_mapperFactory.getOperation(originalElement,
            patchElement);

        // Propagate the factories to deeper merge actions
        // TODO: find a way to make it cleaner
        if (action instanceof MergeAction) {
            MergeAction mergeAction = (MergeAction) action;
            mergeAction.setActionFactory(m_actionFactory);
            mergeAction.setMapperFactory(m_mapperFactory);
            mergeAction.setMatcherFactory(m_matcherFactory);
        }

        action
            .perform(originalElement, mapper.map(patchElement), workingParent);
    }

    /**
     * Adds attributes from in element to out element.
     * @param out out element
     * @param in in element
     */
    private void addAttributes(Element out, Element in) {

        LinkedHashMap allAttributes = new LinkedHashMap();

        List outAttributes = new ArrayList(out.getAttributes());
        List inAttributes = new ArrayList(in.getAttributes());

        for (int i = 0; i < outAttributes.size(); i++) {
            Attribute attr = (Attribute) outAttributes.get(i);
            attr.detach();
            allAttributes.put(attr.getQualifiedName(), attr);
            s_logger.debug("adding attr from out:" + attr);
        }

        for (int i = 0; i < inAttributes.size(); i++) {
            Attribute attr = (Attribute) inAttributes.get(i);
            attr.detach();
            allAttributes.put(attr.getQualifiedName(), attr);
            s_logger.debug("adding attr from in:" + attr);
        }

        out.setAttributes(new ArrayList(allAttributes.values()));
    }

}
