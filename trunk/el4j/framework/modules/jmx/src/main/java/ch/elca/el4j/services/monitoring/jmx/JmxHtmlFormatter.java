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
package ch.elca.el4j.services.monitoring.jmx;

/**
 * This class creates a HtmlAdapter for an MBeanServer. <script
 * type="text/javascript">printFileStatus ("$URL:
 * https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/module/jmx/java/ch/elca/el4j/services/monitoring/jmx/HtmlAdapterFactoryBean.java
 * $", "$Revision$", "$Date: 2006-03-13 14:15:43 +0100 (Mo, 13 Mrz 2006)
 * $", "$Author$" );</script>
 * 
 * @author Rashid Waraich (RWA)
 */
public class JmxHtmlFormatter {
    /**
     * Hide default constructor.
     */
    protected JmxHtmlFormatter() { }

    /**
     * This method creates an Html from a two dimentional String array (first
     * dimention=row, second dimention=colomn).
     * 
     * @param cells
     *            The cells of the table.
     * @return The HTML code of the table.
     */
    public static String getHtmlTable(String[][] cells) {
        String result = "";

        // write headings
        result = result.concat("<table border=\"1\"><tr>");
        for (int i = 0; i < cells[0].length; i++) {
            result = result.concat("<th>" + cells[0][i] + "</th>");
        }
        result = result.concat("</tr>");

        // write table content
        for (int i = 1; i < cells.length; i++) {
            result = result.concat("<tr>");
            for (int j = 0; j < cells[i].length; j++) {
                result = result.concat("<td>" + cells[i][j] + "</td>");
            }
            result = result.concat("</tr>");
        }
        result = result.concat("</table>");

        return result;
    }

    /**
     * Create an XML tag for a log4j root element (root Logger) form the level.
     * 
     * @param level
     *            The level of the root element.
     * @return An HTML embeddable representation of the root XML element.
     */
    public static String getXMLLog4jRootTag(String level) {
        String result = "";

        result = openTagPre(result);
        result = concatTab(result);
        result = result.concat("<root>");
        result = concatLineBreak(result);

        result = concatdoubleTab(result);
        result = result.concat("<level value=\"" + level + "\"/>");
        result = concatLineBreak(result);

        result = concatTab(result);
        result = result.concat("</root>");
        result = closeTagPre(result);

        return convertTags(result);
    }

    /**
     * This method creates an XML representation of a Logger level change
     * (suitable for log4j.xml).
     * 
     * @see Comment of method 'convertTags'.
     * @param category
     *            The category of the logger.
     * @param level
     *            The log-level of the logger.
     * @return An HTML embeddable version of the XML tag.
     */
    public static String getXmlLog4jConfigString(
        String category, String level) {
        
        String result = "";

        result = openTagPre(result);
        result = concatTab(result);
        result = result.concat("<logger name=\"" + category + "\">");
        result = concatLineBreak(result);

        result = concatdoubleTab(result);
        result = result.concat("<level value=\"" + level + "\"/>");
        result = concatLineBreak(result);

        result = concatTab(result);
        result = result.concat("</logger>");
        result = closeTagPre(result);

        return convertTags(result);
    }

    /**
     * See comments for method 'convertTags'.
     * 
     * @param input
     *            See comments for method 'convertTags'.
     * @return See comments for method 'convertTags'.
     */
    private static String concatLineBreak(String input) {
        return input.concat("#br");
    }

    /**
     * See comments for method 'convertTags'.
     * 
     * @param input
     *            See comments for method 'convertTags'.
     * @return See comments for method 'convertTags'.
     */
    private static String concatTab(String input) {
        return input.concat("#tab1");
    }

    /**
     * See comments for method 'convertTags'.
     * 
     * @param input
     *            See comments for method 'convertTags'.
     * @return See comments for method 'convertTags'.
     */
    private static String concatdoubleTab(String input) {
        return input.concat("#tab2");
    }

    /**
     * See comments for method 'convertTags'.
     * 
     * @param input
     *            See comments for method 'convertTags'.
     * @return See comments for method 'convertTags'.
     */
    private static String openTagPre(String input) {
        return input.concat("#pre_open");
    }

    /**
     * See comments for method 'convertTags'.
     * 
     * @param input
     *            See comments for method 'convertTags'.
     * @return See comments for method 'convertTags'.
     */
    private static String closeTagPre(String input) {
        return input.concat("#pre_close");
    }

    /**
     * The 'getXmlLog4jConfigString' should return XML, which is embeddable in
     * HTML. For this reason, the XML tag symbols '<' and '>' are converted to
     * '&lt;' resp. '&gt;'. As these two symbols are also used in HTML tags,
     * they are enconded through special methods (concatLineBreak,
     * concatTab,...). In this method they are again converted back to the
     * original tag.
     * 
     * @param input
     *            Input String from getXmlLog4jConfigString.
     * @return HTML embeddable XML.
     */
    private static String convertTags(String input) {
        String output = input;
        output = output.replaceAll("<", "&lt;");
        output = output.replaceAll(">", "&gt;");

        output = output.replaceAll("#br", "<br>");
        output = output.replaceAll("#tab1", "&#09;");
        output = output.replaceAll("#tab2", "&#09;&#09;");
        output = output.replaceAll("#pre_open", "<pre>");
        output = output.replaceAll("#pre_close", "</pre>");
        return output;
    }
}
