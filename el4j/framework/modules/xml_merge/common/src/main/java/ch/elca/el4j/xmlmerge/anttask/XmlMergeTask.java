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

// Checkstyle: MagicNumber off

package ch.elca.el4j.xmlmerge.anttask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import ch.elca.el4j.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.xmlmerge.ConfigurationException;
import ch.elca.el4j.xmlmerge.XmlMerge;
import ch.elca.el4j.xmlmerge.config.ConfigurableXmlMerge;
import ch.elca.el4j.xmlmerge.config.PropertyXPathConfigurer;

/**
 * Task for merging XML files in Ant.
 * 
 * <p>Attributes:
 * <ul>
 * <li><b>dest</b> Output merged file. <i>Required</i>
 * <li><b>conf</b> Configuration file. <i>Required</i>
 * </ul> 
 * 
 * <p>Content:
 * <ul><li><b>FileSet</b> Selects the files to merge.</li></ul>
 * 
 * Usage example:
 * 
 * <pre>
 *   &lt;target name="test-task">
 *       &lt;taskdef name="xmlmerge" classname="ch.elca.el4j.xmlmerge.anttask.XmlMergeTask"
 *           classpath="module-xml_merge.jar;jdom.jar;jaxen.jar;saxpath.jar "/>
 *       
 *       &lt;xmlmerge dest="out.xml" conf="test.properties">
 *           &lt;fileset dir="test">
 *             &lt;include name="source*.xml"/>
 *          &lt;/fileset>
 *       &lt;/xmlmerge>
 *   &lt;/target>
 * </pre>
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
public class XmlMergeTask extends Task {

    /**
     * Configuration file name.
     */
    String m_confFilename = null;

    /**
     * Output merged file name.
     */
    String m_destFilename = null;

    /**
     * List of file sets.
     */
    List m_filesets = new ArrayList();

    /**
     * Sets the configuration file name.
     * @param confFilename The configuration file name to set.
     */
    public void setConf(String confFilename) {
        m_confFilename = confFilename;
    }

    /**
     * Sets the destination file name.
     * @param destFilename The destination file name to set.
     */
    public void setDest(String destFilename) {
        m_destFilename = destFilename;
    }

    /**
     * Adds a file set.
     * @param fileset The file set to add
     */
    public void addFileSet(FileSet fileset) {
        m_filesets.add(fileset);
    }

    /**
     * Validates the configuration and destination files and the file sets.
     */
    public void validate() {
        if (m_confFilename == null) {
            throw new BuildException("conf file not set");
        }
        if (m_destFilename == null) {
            throw new BuildException("dest file not set");
        }
        if (m_filesets.isEmpty()) {
            throw new BuildException("no source fileset specified");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        validate();

        // Get the files to merge
        List streamsToMerge = new ArrayList();

        for (Iterator it = m_filesets.iterator(); it.hasNext();) {
            FileSet fs = (FileSet) it.next();
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] includedFiles = ds.getIncludedFiles();
            for (int i = 0; i < includedFiles.length; i++) {
                try {
                    streamsToMerge.add(new FileInputStream(new File(ds
                        .getBasedir(), includedFiles[i])));
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            }
        }

        // Create the stream to write
        OutputStream out;
        File destFile = new File(m_destFilename);

        if (!destFile.isAbsolute()) {
            destFile = new File(getProject().getBaseDir(), m_destFilename);
        }

        try {
            out = new FileOutputStream(destFile);
        } catch (IOException e) {
            throw new BuildException(e);
        }

        // Create conf properties
        Properties confProps = new Properties();
        File confFile = new File(m_confFilename);

        if (!confFile.isAbsolute()) {
            confFile = new File(getProject().getBaseDir(), m_confFilename);
        }

        try {
            confProps.load(new FileInputStream(confFile));
        } catch (IOException e) {
            throw new BuildException(e);
        }

        // Create the XmlMerge instance and execute the merge

        XmlMerge xmlMerge;
        try {
            xmlMerge = new ConfigurableXmlMerge(new PropertyXPathConfigurer(
                confProps));
        } catch (ConfigurationException e) {
            throw new BuildException(e);
        }

        InputStream in;

        try {
            in = xmlMerge.merge((InputStream[]) streamsToMerge
                .toArray(new InputStream[streamsToMerge.size()]));
        } catch (AbstractXmlMergeException e) {
            throw new BuildException(e);
        }

        writeFromTo(in, out);

        try {
            in.close();
        } catch (IOException e) {
            throw new BuildException(e);
        }

        try {
            out.close();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Writes from an InputStream to an OutputStream.
     * @param in The stream to read from
     * @param out The stream to write to
     * @throws BuildException If an error occurred during the write process
     */
    private void writeFromTo(InputStream in, OutputStream out)
        throws BuildException {
        int len = 0;
        byte[] buffer = new byte[1024];

        try {
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }
    }
}

//Checkstyle: MagicNumber on
