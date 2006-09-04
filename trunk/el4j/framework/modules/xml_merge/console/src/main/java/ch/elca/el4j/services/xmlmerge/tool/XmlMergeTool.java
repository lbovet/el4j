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

//Checkstyle: UncommentedMain off
//Checkstyle: MagicNumber off

package ch.elca.el4j.services.xmlmerge.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.elca.el4j.services.xmlmerge.XmlMerge;
import ch.elca.el4j.services.xmlmerge.config.ConfigurableXmlMerge;
import ch.elca.el4j.services.xmlmerge.config.PropertyXPathConfigurer;
import ch.elca.el4j.services.xmlmerge.merge.DefaultXmlMerge;

/**
 * XmlMerge as a Command-line tool.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$$URL$$",
 *    "$$Revision$$",
 *    "$$Date$$",
 *    "$$Author$$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class XmlMergeTool {
    
    /**
     * Hide default constructor.
     */
    protected XmlMergeTool() { }
    
    /**
     * Starts the XmlMerge Command-line tool.
     * @param args The names of the files to merge
     * @throws Exception If an error occurs during the execution of the tool
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            usage();
        }

        int argCursor = 0;

        String configFilename = null;

        if ("-config".equals(args[0])) {
            if (args[1] != null) {
                configFilename = args[1];
            }

            argCursor = 2;

            if (args.length < 4) {
                usage();
            }
        }

        XmlMerge xmlMerge;

        if (configFilename != null) {
            Properties props = new Properties();

            try {
                props.load(new FileInputStream(configFilename));
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + configFilename);
                System.exit(1);
            } catch (IOException ioe) {
                System.err.println("Cannot read file: " + configFilename);
                System.exit(1);
            }

            xmlMerge = new ConfigurableXmlMerge(new DefaultXmlMerge(),
                new PropertyXPathConfigurer(props));
        } else {
            xmlMerge = new DefaultXmlMerge();
        }

        InputStream[] sources = new InputStream[args.length - argCursor];

        for (int i = argCursor; i < args.length; i++) {

            String filename = args[i];

            try {
                sources[i - argCursor] = new FileInputStream(filename);
            } catch (FileNotFoundException ex) {
                System.err.println("File not found: " + filename);
                System.exit(1);
            }
        }

        InputStream stream = xmlMerge.merge(sources);

        byte[] buffer = new byte[2048];
        int len;

        try {
            while ((len = stream.read(buffer)) != -1) {
                System.out.write(buffer, 0, len);
            }
        } catch (IOException ioex) {
            // Should not happen...
            ioex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Desribes the usage of this tool. 
     */
    public static void usage() {
        System.err
           .println("xmlmerge [-config <config-file>] file1 file2 [file3 ...]");
        System.exit(1);
    }
}

//Checkstyle: UncommentedMain on
//Checkstyle: MagicNumber on
