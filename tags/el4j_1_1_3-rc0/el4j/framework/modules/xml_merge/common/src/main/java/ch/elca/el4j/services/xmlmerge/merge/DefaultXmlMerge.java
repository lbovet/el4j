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
package ch.elca.el4j.services.xmlmerge.merge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.DocumentException;
import ch.elca.el4j.services.xmlmerge.Mapper;
import ch.elca.el4j.services.xmlmerge.Matcher;
import ch.elca.el4j.services.xmlmerge.MergeAction;
import ch.elca.el4j.services.xmlmerge.ParseException;
import ch.elca.el4j.services.xmlmerge.XmlMerge;
import ch.elca.el4j.services.xmlmerge.XmlMergeContext;
import ch.elca.el4j.services.xmlmerge.action.OrderedMergeAction;
import ch.elca.el4j.services.xmlmerge.factory.StaticOperationFactory;
import ch.elca.el4j.services.xmlmerge.mapper.IdentityMapper;
import ch.elca.el4j.services.xmlmerge.matcher.TagMatcher;

// Checkstyle: MagicNumber off 

/**
 * Default implementation of XmlMerge. Create all JDOM documents, then perform
 * the merge into a new JDOM document.
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
public class DefaultXmlMerge implements XmlMerge {

    /**
     * Root mapper.
     */
    private Mapper m_rootMapper = new IdentityMapper();

    /**
     * Root merge action.
     */
    private MergeAction m_rootMergeAction = new OrderedMergeAction();

    /**
     * Root matcher.
     */
    private Matcher m_rootMatcher = new TagMatcher();

    
    /**
     * Creates a new DefaultXmlMerge instance.
     */
    public DefaultXmlMerge() {
        m_rootMergeAction.setActionFactory(new StaticOperationFactory(
            new OrderedMergeAction()));
        m_rootMergeAction.setMapperFactory(new StaticOperationFactory(
            new IdentityMapper()));
        m_rootMergeAction.setMatcherFactory(new StaticOperationFactory(
            new TagMatcher()));
    }

    /**
     * {@inheritDoc}
     */
    public void setRootMapper(Mapper rootMapper) {
        this.m_rootMapper = rootMapper;
    }

    /**
     * {@inheritDoc}
     */
    public void setRootMergeAction(MergeAction rootMergeAction) {
        this.m_rootMergeAction = rootMergeAction;
    }

    /**
     * {@inheritDoc}
     */
    public String merge(String[] sources) throws AbstractXmlMergeException {

        InputStream[] inputStreams = new InputStream[sources.length];

        for (int i = 0; i < sources.length; i++) {
            inputStreams[i] = new ByteArrayInputStream(sources[i].getBytes());
        }

        InputStream merged = merge(inputStreams);

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = merged.read(buffer)) != -1) {
                result.write(buffer, 0, len);
            }
        } catch (IOException ioe) {
            // should never happen
            throw new RuntimeException(ioe);
        }

        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    public org.w3c.dom.Document merge(org.w3c.dom.Document[] sources)
        throws AbstractXmlMergeException {
        DOMBuilder domb = new DOMBuilder();

        // to save all XML files as JDOM objects
        Document[] docs = new Document[sources.length];

        for (int i = 0; i < sources.length; i++) {
            // ask JDOM to parse the given inputStream
            docs[i] = domb.build(sources[i]);
        }

        Document result = doMerge(docs);

        DOMOutputter outputter = new DOMOutputter();

        try {
            return outputter.output(result);
        } catch (JDOMException e) {
            throw new DocumentException(result, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream merge(InputStream[] sources)
        throws AbstractXmlMergeException {
        SAXBuilder sxb = new SAXBuilder();

        EntityResolver entityResolver = XmlMergeContext.getEntityResolver();
        if (entityResolver != null) {
           sxb.setEntityResolver(entityResolver);
        }
        
        // to save all XML files as JDOM objects
        Document[] docs = new Document[sources.length];

        for (int i = 0; i < sources.length; i++) {
            try {
                // ask JDOM to parse the given inputStream
                docs[i] = sxb.build(sources[i]);
            } catch (JDOMException e) {
                throw new ParseException(e);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw new ParseException(ioe);
            }
        }

        Document result = doMerge(docs);

        Format prettyFormatter = Format.getPrettyFormat();
        // Use system line seperator to avoid problems 
        // with carriage return under linux
        prettyFormatter.setLineSeparator(System.getProperty("line.separator"));
        XMLOutputter sortie = new XMLOutputter(prettyFormatter);
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            sortie.output(result, buffer);
        } catch (IOException ex) {
            throw new DocumentException(result, ex);
        }

        return new ByteArrayInputStream(buffer.toByteArray());
    }

    /**
     * Performs the actual merge.
     * 
     * @param docs
     *            The documents to merge
     * @return The merged result document
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */
    private Document doMerge(Document[] docs) throws AbstractXmlMergeException {
        Document temporary = docs[0];

        for (int i = 1; i < docs.length; i++) {

            if (!m_rootMatcher.matches(temporary.getRootElement(), docs[i]
                .getRootElement())) {
                throw new IllegalArgumentException(
                    "Root elements do not match.");
            }

            Document output = new Document();
            if (docs[0].getDocType() != null) {
                output.setDocType((DocType) docs[0].getDocType().clone());
            }
            output.setRootElement(new Element("root"));

            m_rootMergeAction.perform(temporary.getRootElement(), docs[i]
                .getRootElement(), output.getRootElement());

            Element root = (Element) output.getRootElement().getChildren().get(
                0);
            root.detach();

            temporary.setRootElement(root);
        }

        return temporary;
    }

}

// Checkstyle: MagicNumber on