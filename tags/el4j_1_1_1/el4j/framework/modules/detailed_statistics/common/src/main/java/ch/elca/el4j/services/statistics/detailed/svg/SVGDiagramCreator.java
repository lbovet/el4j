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
package ch.elca.el4j.services.statistics.detailed.svg;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * This class is ...
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
public class SVGDiagramCreator {
    
    /**
     * Standard font size.
     */
    private static final int FONT_SIZE = 8;

    /**
     * X coord where graph starts.
     */
    private static final int X_SCALE_ZERO = 150;
    
    /**
     * Y position where graph starts.
     */
    private static final int Y_SCALE_ZERO = 75;
    /**
     * StringBuffer of diagram.
     */
    private StringBuffer m_diagram;
    
    /**
     * RequestId to build graph for.
     */
    private String m_requestId;
    
    /**
     * System's lineseperator.
     */
    private final String m_lineSeparator;
    
    /**
     * Horizontal screen size.
     */
    private int m_xSize;
    
    /**
     * Vertical screen size.
     */
    private int m_ySize;
    
    /**
     * Gap between two services on graph.
     */
    private int m_serviceGap;
    
    /**
     * Zoom factor for graph.
     */
    private int m_zoomFactor;
    
    
    /**
     * Constructor.
     * 
     * @param requestId Title 
     * @param x Horizontal screen size
     * @param y Vertical screen size
     * @param zoomfactor The zoomfactor
     */
    public SVGDiagramCreator(String requestId, int x, int y, int zoomfactor) {
        m_diagram = new StringBuffer();
        this.m_requestId = requestId;
        m_lineSeparator = System.getProperty("line.separator");
        m_xSize = x;
        m_ySize = y;
        m_zoomFactor = zoomfactor;
    } 
 
    /**
     * Creates a SVG graph from the services and methodCalls given.
     * 
     * @param services List of all ServiceObjects
     * @param calls List of all methodCalls
     * @param ySize The vertical size (equivalent to maximum time)
     * @return The graph created
     */
    public String createGraph(Collection<ServiceData> services, 
        List<MethodCall> calls, int ySize) {
        createHeader();
        createContent(services, calls, ySize);
        createFooter();
        return m_diagram.toString();
    }

    /**
     * Create the header of the SVG file and the definitions for the arrows.
     */
    private void createHeader() {  
        app("<svg xmlns=\"http://www.w3.org/2000/svg\" "
            + "xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
        app("xml:space=\"preserve\" width=\"" + m_xSize + "px\" height=\""
            + m_ySize + "px\">");
        app("");
        app(buildComment("Start of defs"));
        app("<defs>");
        app("<marker id=\"TriangleMarkerRight\" viewBox=\"0 0 10 10\" "
              + "refX=\"9\" refY=\"5\" markerUnits=\"strokeWidth\" " 
              + "markerWidth=\"4\" markerHeight=\"3\" orient=\"auto\">");
        app("<path d=\"M 0 0 L 10 5 L 0 10 z\" />");
        app("</marker>");
        app("<marker id=\"TiangleMarkerLeft\" viewBox=\"0 0 10 10\" "
               + "refX=\"-5\" refY=\"5\" markerUnits=\"strokeWidth\" "
               + "markerWidth=\"4\" markerHeight=\"3\" orient=\"auto\">");
        app("<path d=\"M 0 5 L 10 0 L 10 10 z\" />");
        app("</marker>");
        app("</defs>");
        app(buildComment("End of defs"));
        app("");
        app(buildComment("Start of content"));
        app("<g id=\"all\" font-family=\"Verdana\" font-size=\"8\" "
                + "transform=\"scale(1 1)\">");
    }
    
    /**
     * Create the content, i.e. the method calls, for the graph.
     * 
     * @param serviceObjects List of all service Objects
     * @param calls List of all method calls
     * @param maxTime Maximum time of call graph
     */
    private void createContent(Collection<ServiceData> serviceObjects, 
        List<MethodCall> calls, int maxTime) {
        
        // Checkstyle: MagicNumber off  
        m_serviceGap = (m_xSize / serviceObjects.size());
        
        app(buildText(
            "Generated SVG Diagram: Time dependent sequence diagram for "
                + "request : " + m_requestId, 25, 25));
        
        app(buildScale("Time [ms]", 35, Y_SCALE_ZERO, maxTime, 50));
        
        ServiceData serviceData = null;
        Iterator<ServiceData> iter = serviceObjects.iterator();
        while (iter.hasNext()) {
            serviceData = iter.next();
            app(buildObjectBox(serviceData.getName(), X_SCALE_ZERO 
                + m_serviceGap * serviceData.getId(), Y_SCALE_ZERO - 20, 
                m_serviceGap - m_serviceGap / 10, maxTime));
        }

        for (int i = 0; i < calls.size(); i++) {
            app(buildMethodCall(calls.get(i)));
        }
    }
    // Checkstyle: MagicNumber on
    
    /**
     * Create footer for SVG diagram.
     */
    private void createFooter() {
        app("</g>");
        app(buildComment("End of content"));
        app("");
        app("</svg>");
        app(buildComment("END OF FILE"));
    }
    
    // Checkstyle: MagicNumber off
    /**
     * Create string for a box in the SVG file.
     * 
     * @param text The text to include in the box
     * @param x X coord
     * @param y Y coord
     * @param dx Width
     * @param dy Height
     * @return Stringbuffer of box
     */
    private StringBuffer buildObjectBox(String text, int x, int y, int dx, 
        int dy) {
        StringBuffer box = new StringBuffer();
        int height = 16;
        int newDx = dx;
        String txt1 = text;
        String txt2 = "";
        if (dx > m_xSize / 7) {
            newDx = m_xSize / 7;
        }
            
        if ((8 * text.length() * 3) / 4 > newDx) {
            txt1 = text.substring(0, text.length() / 2);
            txt2 = text.substring(text.length() / 2 + 1);
        }   
        box.append(m_lineSeparator);
        box.append(buildComment("Start objectbox " + text));
        box.append(m_lineSeparator);
        box.append("<rect x=\"").append(x - newDx / 2).append("\" y=\"")
                .append(y - height);
        box.append("\" width=\"").append(newDx)
                .append("\" height=\"").append(height);
        box.append("\" fill=\"none\" stroke=\"black\" stroke-width=\"2\"/>");
        box.append(m_lineSeparator);
        box.append("<text x=\"").append(x).append("\" y=\"")
                .append(y - height / 2);
        box.append("\"  font-size=\"").append(FONT_SIZE)
                .append("px\" text-anchor=\"middle\" ");
        box.append("fill=\"black\">").append(txt1).append("</text>");
        box.append("<text x=\"").append(x).append("\" y=\"")
                .append((y - height / 2) + 7);
        box.append("\"  font-size=\"").append(FONT_SIZE)
                .append("px\" text-anchor=\"middle\" ");
        box.append("fill=\"black\">").append(txt2).append("</text>");
        box.append(m_lineSeparator);
        box.append("<line x1=\"").append(x).append("\" y1=\"").append(y);
        box.append("\" x2=\"").append(x).append("\" y2=\"")
                .append(dy / m_zoomFactor + y);
        box.append("\" stroke=\"black\" stroke-width=\"2\"/>");
        box.append(m_lineSeparator);
        box.append(buildComment("End objectbox " + text).toString());
        box.append(m_lineSeparator);
        return box;
    }
    // Checkstyle: MagicNumber on
    
    
    /**
     * Add text to SVG graph.
     * @param text to add
     * @param x X coord
     * @param y Y coord
     * @return StringBuffer of text at coords
     */
    private StringBuffer buildText(String text, int x, int y) {
        return buildText(text, x, y, FONT_SIZE);
    }

    /**
     * Add text to SVG graph.
     * @param text to add
     * @param x X coord
     * @param y Y coord
     * @param fontSize Fontsize
     * @return StringBuffer of text at coords
     */
    private StringBuffer buildText(String text, int x, int y, int fontSize) {
        return buildText(text, x, y, fontSize, "", "black");
    }

    /**
     * Add text to SVG graph.
     * @param text to add
     * @param x X coord
     * @param y Y coord
     * @param size Fontsize
     * @param fontStretch Fontstretch
     * @param fontColor Fontcolor
     * @return StringBuffer of text at coords
     */
    private StringBuffer buildText(String text, int x, int y, int size, 
        String fontStretch, String fontColor) {
        StringBuffer tag = new StringBuffer("<text x=\"");
        tag.append(x).append("px\" y=\"").append(y);
        tag.append("px\" font-size=\"").append(size);
        if (!fontStretch.equals("")) {
            tag.append("px\" font-stretch=\"").append(fontStretch).append("\"");
        } else {
            tag.append("px\"");
        }
        tag.append(" fill=\"").append(fontColor).append("\"");
        tag.append(">");
        tag.append(text).append("</text>");
        return tag;
    }

    
    /**
     * Build a dashed arrow for the SVG graph.
     * 
     * @param x X (start) coord
     * @param y Y (start) coord
     * @param dx X difference to target
     * @param dy Y difference to target
     * @return StringBuffer of a dashed arrow
     */
    private StringBuffer buildDashedArrow(int x, int y, int dx, int dy) {
        return buildTextArrow(null, x, y, dx, dy, true);
    }

    /**
     * Build a dashed arrow with text for the SVG graph.
     * 
     * @param text Text of arrow
     * @param x X (start) coord
     * @param y Y (start) coord
     * @param dx X difference to target
     * @param dy Y difference to target
     * @return StringBuffer of a dashed arrow with text
     */
    private StringBuffer buildDashedTextArrow(String text, int x,  int y, 
        int dx, int dy) {
        return buildTextArrow(text, x, y, dx, dy, true);
    }

  
    /**
     * Build an arrow with text for the SVG graph.
     * 
     * @param text Text of arrow
     * @param x X (start) coord
     * @param y Y (start) coord
     * @param dx X difference to target
     * @param dy Y difference to target
     * @return StringBuffer of an arrow with text
     */
    private StringBuffer buildTextArrow(String text, int x, int y, 
        int dx, int dy) {
        return buildTextArrow(text, x, y, dx, dy, false);
    }

    /**
     * Build an dashed/solid arrow with text for the SVG graph.
     * 
     * @param text Text of arrow
     * @param x X (start) coord
     * @param y Y (start) coord
     * @param dx X difference to target
     * @param dy Y difference to target
     * @param dashed Should Arrow be dashed?
     * @return StringBuffer of an arrow with text
     */
    private StringBuffer buildTextArrow(String text, int x, int y, 
        int dx, int dy, boolean dashed) {
        boolean inverted = false;
        int fromX = x;
        int fromY = y;
        int deltaX = dx;
        int deltaY = dy;
        StringBuffer arrow = new StringBuffer();
        
        if (deltaX < 0 && !dashed) {
            inverted = true;
            fromX += deltaX;
            fromX += deltaY;
            deltaX = -deltaX;
            deltaY = -deltaY;
        }
        String id = text + " " + fromX + " " + fromY;
        
        arrow.append(m_lineSeparator);
        arrow.append(buildComment("Start arrow " + id).toString());
        arrow.append(m_lineSeparator);
        arrow.append("<path id=\"").append(id);
        arrow.append("\" d=\"M ").append(fromX).append(" ").append(fromY);
        arrow.append(" l ").append(deltaX).append(" ").append(deltaY);
        if (dashed) {
            arrow.append("\" style=\"stroke-dasharray:5 5");
        }
        if (inverted) {
            arrow.append("\" fill=\"none\" stroke=\"black\" " 
                   + "stroke-width=\"2\" " 
                   + "marker-start=\"url(#TiangleMarkerLeft)\" />");
        } else {
            arrow.append("\" fill=\"none\" stroke=\"black\" " 
                   + "stroke-width=\"2\" " 
                   + "marker-end=\"url(#TriangleMarkerRight)\" />");
        }
        arrow.append(m_lineSeparator);
        if (text != null) {
            addTextToArrow(text, deltaX, arrow, id);
        }
        arrow.append(m_lineSeparator);
        arrow.append(buildComment("End arrow " + id).toString());
        arrow.append(m_lineSeparator);
        return arrow;
    }

    /**
     * Add text to arrow.
     * 
     * @param text Text to add
     * @param deltaX Horizontal size of arrow
     * @param arrow StringBuffer of arrow
     * @param id Xlink reference of arrow
     */
    private void addTextToArrow(String text, int deltaX, StringBuffer arrow, 
        String id) {
        arrow.append("<text font-size=\"").append(FONT_SIZE);
        // Checkstyle: MagicNumber off
        if (Math.abs(deltaX) < 150) {
        // Checkstyle: MagicNumber on
            arrow.append("\" font-stretch=\"ultra-condensed");
        }
        arrow.append("\" fill=\"blue\" text-anchor=\"middle\">");
        arrow.append(m_lineSeparator);
        arrow.append("<textPath xlink:href=\"#").append(id)
                .append("\" startOffset=\"50%\"><tspan dy=\"-2\">");
        arrow.append(text).append("</tspan></textPath>");
        arrow.append(m_lineSeparator);
        arrow.append("</text>");
    }

    // Checkstyle: MagicNumber off    
    /**
     * Build time scale for SVG graph.
     * 
     * @param name Name of scale
     * @param x X coord
     * @param y Y coord
     * @param maxValue Maximum value
     * @param scaleFactor Scale factor
     * @return StringBuffer of scale
     */
    private StringBuffer buildScale(String name, int x, int y, int maxValue, 
        int scaleFactor) {
        StringBuffer scale = new StringBuffer();
        scale.append(m_lineSeparator);
        scale.append(buildComment("Start of scale " + name).toString());
        scale.append(m_lineSeparator);
        scale.append("<text x=\"").append(x).append("\" y=\"").append(y - 15);
        scale.append("\"  font-size=\"").append(FONT_SIZE)
                .append("px\" text-anchor=\"middle\" ");
        scale.append("fill=\"black\">").append(name).append("</text>");
        scale.append(m_lineSeparator);
        scale.append("<line x1=\"").append(x).append("\" y1=\"").append(y);
        scale.append("\" x2=\"").append(x).append("\" y2=\"")
                .append(y + maxValue / m_zoomFactor);
        scale.append("\" stroke=\"black\" stroke-width=\"2\"/>");
        int count = maxValue / scaleFactor;
        for (int i = 0; i <= count; i++) {
            scale.append(m_lineSeparator);
            scale.append("<line x1=\"").append(x - 5).append("\" y1=\"")
                    .append(y + (i * scaleFactor) / m_zoomFactor);
            scale.append("\" x2=\"").append(x + 5).append("\" y2=\"")
                    .append(y + (i * scaleFactor) / m_zoomFactor);
            scale.append("\" stroke=\"black\" stroke-width=\"2\"/>");
            scale.append(m_lineSeparator);
            scale.append("<text x=\"").append(x - 7).append("\" y=\"")
                    .append(y + (i * scaleFactor) / m_zoomFactor + 2);
            scale.append("\"  font-size=\"").append(FONT_SIZE)
                    .append("px\" text-anchor=\"end\" ");
            scale.append("fill=\"black\">").append("" + i * scaleFactor)
                    .append("</text>");
        }

        scale.append(m_lineSeparator);
        scale.append(buildComment("End of scale " + name).toString());
        scale.append(m_lineSeparator);
        return scale;
    }
    // Checkstyle: MagicNumber on 
    
    /**
     * Build method call in SVG graph.
     * 
     * @param mc Methodcall to build
     * @return StringBuffer of method call
     */
    private StringBuffer buildMethodCall(MethodCall mc) {
        StringBuffer method = new StringBuffer();
        
        ServiceData fromService = mc.getFromService();
        ServiceData toService = mc.getToService();
        int toServiceNo = toService.getId();
        int fromServiceNo = fromService.getId();
        boolean selfCalling = false;
        
        if (fromService == toService) {
            selfCalling = true;
        }
            
        int arrowLength = (toServiceNo - fromServiceNo) * m_serviceGap;
        int startTime = 0;
        if (fromService.getRefTimeAbs() != -1L) {
            startTime = (int) (mc.getFromTimestamp() - (long) mc.getFromTime() 
                - fromService.getRefTimeAbs()) + fromService.getRefTimeRel();
        }
            
        method.append(m_lineSeparator);
        method.append(buildComment("Start method " + mc.getName()));
        method.append(m_lineSeparator);
        if (selfCalling) {
            method.append(buildTextArrow("", fromServiceNo * m_serviceGap
                + X_SCALE_ZERO, Y_SCALE_ZERO + startTime / m_zoomFactor + 0,
                m_serviceGap / 2, mc.getTimeDiff1() / m_zoomFactor));

            // Checkstyle: MagicNumber off
            method.append(buildText(mc.getFromTime() + "ms / " 
                + mc.getName() + "(" + mc.getCallType() + ")" + " / " 
                + mc.getToTime() + "ms", fromServiceNo * m_serviceGap 
                + X_SCALE_ZERO + 1, (Y_SCALE_ZERO + startTime / m_zoomFactor 
                + 0) - 3, FONT_SIZE, "ultra-condensed", "blue"));
            // Checkstyle: MagicNumber on
            method.append(buildDashedArrow(toServiceNo * m_serviceGap 
                + m_serviceGap / 2 + X_SCALE_ZERO, Y_SCALE_ZERO 
                + (startTime + mc.getTimeDiff1() + mc.getToTime()) 
                / m_zoomFactor + 0, -m_serviceGap / 2, 
                mc.getTimeDiff2() / m_zoomFactor));
        } else {
            method.append(buildTextArrow(
                mc.getFromTime() + "ms / " + mc.getName() + "("
                    + mc.getCallType() + ")" + " / " + mc.getToTime() + "ms",
                fromServiceNo * m_serviceGap + X_SCALE_ZERO,
                Y_SCALE_ZERO + startTime / m_zoomFactor + 0, arrowLength,
                mc.getTimeDiff1() / m_zoomFactor));

            method.append(buildDashedTextArrow(
            // "ret:" + mc.getName() + "(" + mc.getCallType() + ")",
                "", toServiceNo * m_serviceGap + X_SCALE_ZERO,
                Y_SCALE_ZERO + (startTime + mc.getTimeDiff1() + mc.getToTime())
                    / m_zoomFactor + 0, -arrowLength,
                mc.getTimeDiff2() / m_zoomFactor));

        }
        method.append(m_lineSeparator);
        method.append(buildComment("End method " + mc.getName()));
        method.append(m_lineSeparator);
        
        /* The follwing lines seem not to do what they're supposed.
         * 
         * They influence the vertical position of a method arrow on the graph
         * and if left there, the return arrows are not correct and if taken 
         * away the method arrows themselves.
         */
        
        if (selfCalling) {
            fromService.setRefTime(mc.getFromTimestamp() 
                - (long) mc.getFromTime(), startTime);
        } else {
            toService.setRefTime(mc.getToTimestamp() 
                - (long) mc.getToTime(), startTime + mc.getTimeDiff1());
            fromService.setRefTime(mc.getFromTimestamp() 
                - (long) mc.getFromTime(), startTime);
        }
        return method;
    }
    
    /**
     * Append line to stringbuffer.
     * 
     * @param s Line to append
     */
    private void app(String s) {
        m_diagram.append(s).append(m_lineSeparator);
    }

    /**
     * Append line to stringbuffer.
     * 
     * @param s Line to add
     */
    private void app(StringBuffer s) {
        app(s.toString());
    }

    /**
     * Create a comment in the SVG graph.
     * @param text Text of comment
     * @return Comment as string buffer
     */
    private StringBuffer buildComment(String text) {
        return (new StringBuffer("<!-- ")).append(text).append(" -->");
    }
    
}
