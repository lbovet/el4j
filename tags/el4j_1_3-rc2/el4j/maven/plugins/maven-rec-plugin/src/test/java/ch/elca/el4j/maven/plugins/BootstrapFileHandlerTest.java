package ch.elca.el4j.maven.plugins;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import junit.framework.TestCase;

public class BootstrapFileHandlerTest extends TestCase {

    private File testXml = new File(PlexusTestCase.getBasedir()
        + "/src/test/resources", "test_bootstrap.xml");;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateBootstrapMapFromDom() throws JDOMException,
        IOException {
        Map<String, ProjectData> map = null;
        Document dom = new Document();
        Element localProjects = new Element("localProjects");
        dom.addContent(localProjects);

        map = BootstrapFileHandler.createBootstrapMapFromDom(dom);
        assertTrue("Map should be empty", map.isEmpty());

        dom = BootstrapFileHandler.loadDomFromXml(testXml);
        map = BootstrapFileHandler.createBootstrapMapFromDom(dom);

        assertEquals(3, map.keySet().size());

        assertTrue(map.containsKey("group1:artifact1:1.0"));
        assertTrue(map.containsKey("group2:artifact2:2.0"));
        assertTrue(map.containsKey("group3:artifact3:3.0"));

        ProjectData projectData = map.get("group3:artifact3:3.0");

        assertEquals("group3", projectData.getGroupId());
        assertEquals("artifact3", projectData.getArtifactId());
        assertEquals("3.0", projectData.getVersion());
        assertEquals(2, projectData.getDependencies().size());
        assertTrue(projectData.getDependencies().contains(
            "group1:artifact1:1.0"));
        assertTrue(projectData.getDependencies().contains(
            "group2:artifact2:2.0"));
    }

    @SuppressWarnings("unchecked")
    public void testLoadDomFromXml() throws JDOMException, IOException {
        Document dom = BootstrapFileHandler.loadDomFromXml(testXml);
        Namespace ns = Namespace
            .getNamespace(BootstrapFileHandler.BOOTSTRAP_XML_NAMESPACE);

        Element localProjects = dom.getRootElement();
        assertEquals("Require root-element <localProject>", "localProjects",
            localProjects.getName());

        List<Element> projectList = (List<Element>) localProjects.getChildren(
            "project", ns);
        assertEquals("Require three <project> elements", 3, projectList.size());

        for (Element project : projectList) {
            assertTrue(project.getAttributeValue("groupId").startsWith("group"));
            assertTrue(project.getAttributeValue("artifactId").startsWith(
                "artifact"));
            assertEquals(3, project.getAttributeValue("version").length());

            Element path = project.getChild("path", ns);
            assertNotNull("Require <path> element", path);
            assertTrue(path.getTextTrim().startsWith("D:\\path"));

            Element dependencies = project.getChild("dependencies", ns);
            List<Element> dependencyList = (List<Element>) dependencies
                .getChildren("dependency", ns);

            String groupId = project.getAttributeValue("groupId");
            if (groupId.endsWith("1")) {
                assertEquals(0, dependencyList.size());
            } else if (groupId.endsWith("2")) {
                assertEquals(1, dependencyList.size());
                assertEquals("group1:artifact1:1.0", dependencyList.get(0)
                    .getAttributeValue("projectId"));
            } else if (groupId.endsWith("3")) {
                assertEquals(2, dependencyList.size());
                assertEquals("group2:artifact2:2.0", dependencyList.get(0)
                    .getAttributeValue("projectId"));
                assertEquals("group1:artifact1:1.0", dependencyList.get(1)
                    .getAttributeValue("projectId"));
            } else {
                fail("Invalid groupId.");
            }
        }

    }
}
