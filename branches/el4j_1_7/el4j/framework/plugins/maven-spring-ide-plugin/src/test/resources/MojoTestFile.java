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

/**
 * THIS IS NOT A JAVA SOURCE FILE.
 *
 * It is used only for testing the beans mojo.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author David Bernhard (DBD)
 */
public class MojoTestFile {

	public static void main(String[] args) {
		
		// $$ BEANS INCLUDE $$
		String[] included = {
			"classpath:beansInAFile.xml",
			"classpath:beansInAJar.xml"
		};
		
		// $$ BEANS EXCLUDE $$
		String[] excluded = {};
		
		// $$ BEANS END $$
	}
}