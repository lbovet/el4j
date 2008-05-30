package com.zanthan.sequence;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * This class is a fasade for the Sequence library. 
 * It is located in the pacakge com.zanathan.sequence to access classes that
 * have other than public visability.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class Fasade {

    /**
     * Create a Sequence Diagram of the given graph.
     * 
     * @param graph
     *            The graph to create the diagram for
     * @param filename
     *            The file to save the graph in
     */
    public void createSequenceDiagram(String graph, String filename) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int width = size.width / 2;
        int height = size.height / 2;
        createSequenceDiagram(graph, filename, width, height);
    }
    
    /**
     * Create a Sequence Diagram of the given graph.
     * 
     * @param graph
     *            The graph to create the diagram for
     * @param filename
     *            The file to save the graph in
     * @param width
     *            The width of the diagram
     * @param height
     *            The height of the diagram
     */
    public void createSequenceDiagram(String graph, String filename, 
        int width, int height) {
        Display disp = new Display(null);
        disp.setSize(width, height);
        disp.init(graph);
        
        BufferedImage bi = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        disp.paintComponent(bi.createGraphics());
        File file = new File(filename);
        try {
            file.createNewFile();
            ImageIO.write(bi, "png", file);
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }
}
