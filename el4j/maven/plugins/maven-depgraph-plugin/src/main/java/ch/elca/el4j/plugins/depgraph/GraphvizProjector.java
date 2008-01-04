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
package ch.elca.el4j.plugins.depgraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * This class projects a dependency graph to the graphviz format. 
 *
 * * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Philippe Jacot (PJA)
 */
public class GraphvizProjector implements DepGraphProjector {
    /**
     * Graphiv supported output formats.
     */
    private static final List<String> SUPPORTED_FORMATS = Arrays
        .asList(new String[] {"canon", "cmap", "cmapx", "dia", "dot", "fig",
            "gd", "gd2", "gif", "hpgl", "imap", "ismap", "jpeg", "jpg", "mif",
            "mp", "pcl", "pic", "plain", "plain-ext", "png", "ps", "ps2",
            "svg", "svgz", "vrml", "vtx", "wbmp", "xdot"});

    /**
     * Graphviz header parameter.
     */
    private static final String GVH_RANKDIR = "TB";

    /**
     * Graphviz header parameter.
     */
    private static final String GVH_LABELJUST = "l";

    /**
     * Graphviz header parameter.
     */
    private static final String GVH_NODESEP = ".05";

    /**
     * Graphviz header parameter.
     */
    private static final String GVH_RANKSEP = "1";

    /**
     * Graphviz header parameter.
     */
    private static final String GVH_CONCENTRATE = "true";

    /**
     * Graphviz node parameter.
     */
    private static final String GVN_SHAPE = "box";

    /**
     * Logger.
     */
    private static Log s_log = LogFactory
        .getLog(GraphvizProjector.class);

    /**
     * Id counter.
     */
    private int m_idCounter = 0;
    
    /**
     * Dot file, not null to keep it.
     */
    private File m_dotFile;

    /**
     * The graph.
     */
    private DependencyGraph m_graph;

    /**
     * A mapping from artifact to id. Non-generic HashMap because of a parser
     * error: http://jira.codehaus.org/browse/QDOX-89
     */
    private Map<String, String> m_ids = new HashMap();

    /**
     * The file to write the image to.
     */
    private File m_imageFile;
    
    /**
     * Whether the edges should be labeled.
     */
    private boolean edgeLabel = false;

    /**
     * Create a new projector with a stream to write to.
     * 
     * @param outputFile
     *            The File to write to
     */
    public GraphvizProjector(File outputFile) {
        if (outputFile == null
            || (!outputFile.canWrite() && outputFile.exists())) {
            throw new IllegalArgumentException("Illegal File submitted");
        }

        String extension = FileUtils.getExtension(outputFile.getName());
        if (!SUPPORTED_FORMATS.contains(extension)) {
            s_log.error("Extension \"" + extension + "\" not supported.");
            s_log.error("Supported formats are: ");
            s_log.error(SUPPORTED_FORMATS.toString());
        }

        m_imageFile = outputFile;
    }
    
    /**
     * Create a new projector with a stream to write to. Define whether the
     * edges should be labeled with the dependency-scope (default: false).
     * 
     * @param outputFile
     *            The File to write to
     * @param edgeLabel
     *            set to true to label the edges with dependency-scope.
     */
    public GraphvizProjector(File outputFile, boolean edgeLabel) {
        this(outputFile);
        this.edgeLabel = edgeLabel;
    }

    /**
     * Get a uniqe id.
     * 
     * @param artifact
     *            The artifact this id belongs to.
     * @return A unique id
     */
    private String getId(DepGraphArtifact artifact) {
        if (m_ids.containsKey(artifact.getQualifiedName())) {
            return m_ids.get(artifact.getQualifiedName());
        } else {
            String newId = "n" + ++m_idCounter;
            m_ids.put(artifact.getQualifiedName(), newId);
            return newId;
        }
    }

    /**
     * Draw the nodes.
     * 
     * @param out
     *            Writer to write to
     * @throws IOException
     */
    private void writeNodes(PrintStream out) throws IOException {
        for (DepGraphArtifact a : m_graph.getArtifacts()) {
            // Write this node
            out.println("\"" + getId(a) + "\" [");

            out.print("label = \"");
            out.print(a.getArtifactId());
            out.print("\\n" + a.getGroupId());
            out.print("\\n" + a.getVersion());
            out.println("\"");

            out.println("shape = " + GVN_SHAPE);

            out.println("];");

        }
    }

    /**
     * Draw the edges.
     * 
     * @param out
     *            Writer to write to
     * @throws IOException
     */
    private void writeEdges(PrintStream out) throws IOException {
        for (DepGraphArtifact art : m_graph.getArtifacts()) {
            for (DepGraphArtifact dep : art.getDependencies()) {
                // Write the dependency from a to dep
                out.print("\"" + getId(art) + "\"");
                out.print(" -> \"");
                out.print(getId(dep) + "\"");
                out.print(" [");
                if (edgeLabel) {
                    out.print("label=\"" + dep.getScope() + "\"");
                }
                out.println(" ];");
            }
        }
    }

    /**
     * Write the header to the out.
     * 
     * @param out
     *            The stream to write to
     */
    private void writeHeader(PrintStream out) {
        out.println("digraph g {");
        out.println("graph [");
        // out.println("rank = sink");
        out.println("rankdir = " + GVH_RANKDIR);
        if (StringUtils.isNotEmpty(m_graph.getName())) {
            out.println("label = \"" + m_graph.getName() + "\" labeljust = "
                + GVH_LABELJUST);
        }
        out.println("nodesep = " + GVH_NODESEP);
        out.println("concentrate = " + GVH_CONCENTRATE);
        out.println("ranksep = " + GVH_RANKSEP);
        out.println("];");
        // out.println("node [ fontsize = \"16\" shape = \"ellipse\" ];");
    }

    /**
     * Write the footer to the out.
     * 
     * @param out
     *            The Writer to write to
     * @throws IOException
     */
    private void writeFooter(PrintStream out) throws IOException {
        out.println("}");
    }

    /**
     * {@inheritDoc}
     */
    public void project(DependencyGraph graph) throws MojoExecutionException {
        if (graph == null) {
            throw new NullPointerException("dependency is null");
        }

        m_graph = graph;

        File dotFile = null;
        try {
            if (m_dotFile == null) {
                dotFile = File.createTempFile("GraphizProjector", ".dot");
            } else {
                dotFile = m_dotFile;
            }

            PrintStream outStream = new PrintStream(dotFile);

            writeHeader(outStream);
            writeNodes(outStream);
            writeEdges(outStream);
            writeFooter(outStream);

            outStream.close();

            Commandline cmd = new Commandline();
            cmd.setExecutable("dot");

            try {
                cmd.addSystemEnvironment();
            } catch (Exception e) {
                s_log.error(
                    "Error initializing the Environment to execute dot", e);
            }

            // Checkstyle: MagicNumber off
            String[] arguments = new String[3];
            // Checkstyle: MagicNumber on
            arguments[0] = "-T" + FileUtils.getExtension(m_imageFile.getName());
            arguments[1] = "-o" + m_imageFile.getAbsolutePath();
            arguments[2] = dotFile.getAbsolutePath();
            cmd.addArguments(arguments);

            StreamConsumer consumer = new DefaultConsumer();

            int returnValue = CommandLineUtils.executeCommandLine(cmd,
                consumer, consumer);
            if (returnValue != 0) {
                // Error running dot
                s_log.error("Dot returned non-zero value");
                throw new MojoExecutionException("Dot returned non-zero value. Maybe graphviz (http://www.graphviz.org/) is not installed or dot is not in the path?");
            }

            // Try to delete the temp file
            if (m_dotFile == null) {
                dotFile.deleteOnExit();
            }
        } catch (IOException e) {
            s_log.error("Error writing to file", e);
        } catch (CommandLineException e) {
            s_log.error("Error executing dot", e);
        }

    }
    
    /**
     * Set the dot file to use. Set to null to use a temporary file.
     * @param dotFile The dot file to use
     */
    public void setDotFile(File dotFile) {
        m_dotFile = dotFile;
    }
    
    /**
     * Get the dot file.
     * @return The dot file
     */
    public File getDotFile() {
        return m_dotFile;
    }
}
