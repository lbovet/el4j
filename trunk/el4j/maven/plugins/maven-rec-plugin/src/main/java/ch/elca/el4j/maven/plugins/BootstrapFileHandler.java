package ch.elca.el4j.maven.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class BootstrapFileHandler {
    /**
     * The namespace of the bootstrap-xml-file.
     */
    public static final String BOOTSTRAP_XML_NAMESPACE = "http://el4j.elca.ch/maven/plugins/maven-rec-plugin/1.0";

    /**
     * Create a JDOM-document from the specified bootstrap-map.
     * 
     * @param bootstrapMap
     *            the map to create the dom from.
     * @return the JDOM-document created from the specified map.
     */
    public static Document createDomFromBoostrapMap(
        Map<String, ProjectData> bootstrapMap) {
        Namespace namespace = Namespace.getNamespace(BOOTSTRAP_XML_NAMESPACE);
        Document doc = new Document();
        // <localProjects>
        Element localProjects = new Element("localProjects", namespace);

        for (String projectId : bootstrapMap.keySet()) {
            ProjectData projectData = bootstrapMap.get(projectId);
            // <project>
            Element project = new Element("project", namespace);
            project.setAttribute("groupId", projectData.getGroupId());
            project.setAttribute("artifactId", projectData.getArtifactId());
            project.setAttribute("version", projectData.getVersion());
            // <path>
            Element path = new Element("path", namespace);
            path.addContent(projectData.getPom().getAbsolutePath());
            project.addContent(path);
            // </path>
            // <dependencies>
            Element dependencies = new Element("dependencies", namespace);
            for (String dependencyId : projectData.getDependencies()) {
                // <dependency>
                Element dependency = new Element("dependency", namespace);
                dependency.setAttribute("projectId", dependencyId);
                dependencies.addContent(dependency);
                // </dependency>
            }
            project.addContent(dependencies);
            // </dependencies>
            localProjects.addContent(project);
            // </project>
        }
        doc.addContent(localProjects);
        // </localProjects>

        return doc;
    }

    /**
     * Create the bootstrapMap from the specified JDOM-document.
     * 
     * @param dom
     *            the JDOM-document to create the bootstrapMap from.
     * @return the bootstrapMap created from the specified document.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ProjectData> createBootstrapMapFromDom(
        Document dom) {
        Namespace namespace = Namespace.getNamespace(BOOTSTRAP_XML_NAMESPACE);
        HashMap<String, ProjectData> bootstrapMap = new HashMap<String, ProjectData>();

        Element localProjects = dom.getRootElement();
        for (Element project : (List<Element>) localProjects.getChildren(
            "project", namespace)) {
            ProjectData projectData = new ProjectData(project
                .getAttributeValue("groupId"), project
                .getAttributeValue("artifactId"), project
                .getAttributeValue("version"));
            Element path = project.getChild("path", namespace);
            projectData.setPom(new File(path.getTextTrim()));
            Element dependencies = project.getChild("dependencies", namespace);
            // add non-empty dependency-entries to dependency-list in projectData
            for (Element dependency : (List<Element>) dependencies.getChildren(
                "dependency", namespace)) {
                if (dependency != null) {
                    String dependencyId = dependency
                        .getAttributeValue("projectId");
                    if (dependencyId != null && !dependencyId.trim().equals("")) {
                        projectData.addDependency(dependencyId.trim());
                    }
                }
            }
            bootstrapMap.put(projectData.getProjectId(), projectData);
        }

        return bootstrapMap;
    }

    /**
     * Creates a JDOM-document from the specified bootstrap-xml.
     * 
     * @param bootstrapXml
     *            file to create the JDOM-document from.
     * @return the JDOM-document created from the specified xml.
     * @throws JDOMException
     *             when errors occur in parsing
     * @throws IOException
     *             when an I/O error prevents a document from being fully parsed
     */
    public static Document loadDomFromXml(File bootstrapXml)
        throws JDOMException, IOException {
        Document doc = new SAXBuilder().build(bootstrapXml);
        Namespace namespace = doc.getRootElement().getNamespace();
        if (namespace != null
            && BOOTSTRAP_XML_NAMESPACE.equals(namespace.getURI())) {
            return doc;
        } else {
            throw new JDOMException(
                "Wrong namespace of bootstrap-file! Should be <"
                    + BOOTSTRAP_XML_NAMESPACE + ">, but found <"
                    + namespace.getURI() + ">.");
        }
    }

    /**
     * Writes the specified JDOM-document to the specified xml-file.
     * 
     * @param dom
     *            the JDOM-document to write to the file.
     * @param bootstrapXml
     *            the file to write the JDOM-document to.
     * @throws IOException
     *             if there's any problem writing.
     */
    public static void storeDomToXml(Document dom, File bootstrapXml)
        throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(bootstrapXml));
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(dom, out);
    }
}
