/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.maven.plugins.duplicatefinder;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Class inspector - launch duplicate finder and show the class inspector.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 *
 * @goal inspect
 */
public class ClassInspectorMojo extends AbstractDuplicateFinderMojo {

	/** {@inheritDoc} */
	public void execute() throws MojoExecutionException, MojoFailureException {
		setUp();
		m_finder.search();

		ClassInspector inspector = new ClassInspector(m_finder);
		
		JPanel inspectorPanel = inspector.getInspector();
		JFrame frame = new JFrame("Class inspector");
		frame.getContentPane().add(inspectorPanel);

		// Checkstyle: MagicNumber off
		
		frame.setSize(600, 400);
		frame.setVisible(true);

		while (frame.isVisible()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e = null;
			}
		}
		
		// Checkstyle: MagicNumber on
		
		if (m_finder.duplicatesFound() && duplicateIsFail) {
			throw new MojoFailureException("Duplicate classes found.");
		}
	}

}
