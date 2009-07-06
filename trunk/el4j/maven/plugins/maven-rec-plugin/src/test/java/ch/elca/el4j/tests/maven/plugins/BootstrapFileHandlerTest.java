/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.maven.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusTestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elca.el4j.maven.plugins.BootstrapFileHandler;
import ch.elca.el4j.maven.plugins.ProjectData;

/**
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 */
public class BootstrapFileHandlerTest {

	private File testXml = new File(PlexusTestCase.getBasedir()
		+ "/src/test/resources", "test_bootstrap.xml");;

	@Before
	public void setUp() {
		
	}

	@After
	public void tearDown() {
		
	}

	@Test
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
	@Test
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
