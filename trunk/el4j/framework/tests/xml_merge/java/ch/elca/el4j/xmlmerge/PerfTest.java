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

package ch.elca.el4j.xmlmerge;

import ch.elca.el4j.xmlmerge.tool.XmlMergeTool;

// Checkstyle: MagicNumber off
// Checkstyle: UncommentedMain off

/**
 * 
 * This class can be used for profiling. It merges the input files 25 times.
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
public class PerfTest {

    /**
     * Hide default constructor.
     */
    protected PerfTest() { }
    
    /**
     * Starts the performance test.
     * 
     * @param args The names of the files to merge
     */
    public static void main(String[] args) throws Exception {       

        for (int i = 0; i < 25; i++) {
            XmlMergeTool.main(args);
        }
        
    }

}

//Checkstyle: MagicNumber on
//Checkstyle: UncommentedMain on