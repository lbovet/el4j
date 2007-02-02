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
package ch.elca.el4j.demos.statistics.detailed;

import java.text.DecimalFormat;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.demos.statistics.detailed.internal.DemoA;

// Checkstyle: UncommentedMain off
// Checkstyle: UseLogger off
/**
 * This class is the base class for the detailed statistics demo.
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
public class DetailedStatisticsDemo {

    /**
     * Locations of configuration files.
     */
    private static final String[] CONFIG_LOCATIONS = {
        "scenarios/demo-rmi-server-config.xml",
        "scenarios/demo-client-config.xml",
        "scenarios/client-detailedStatistics.xml",
        "scenarios/common-detailedStatistics.xml",
        "scenarios/server-detailedStatistics.xml",
        "classpath*:mandatory/*.xml"};

    /**
     * Name of interface, resp. proxy bean.
     */
    private final String m_serviceName = "printer";

    /**
     * This is the application context to work with.
     */
    private ApplicationContext m_appContext;

    /**
     * Constructor. 
     */
    public DetailedStatisticsDemo() {
        m_appContext = new ModuleApplicationContext(CONFIG_LOCATIONS, true);
    }

    /**
     * This is the main method.
     * 
     * @param args
     *            Are the arguments from console (ignored).
     */
    public static void main(String[] args) {
        DetailedStatisticsDemo demo = new DetailedStatisticsDemo();
        System.out.println("Starting statistic demo...");
        double time = demo.executeTest();
        System.out.println("done.");
        demo.printTestResults(time);

        /**
         * Wait forever, in order to give the user chance to see what
         * measurements are available in the JMX console
         */

        System.out.println("Waiting forever...");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // exit system with parameter 0 to stop RMI Registry too
            System.exit(0);
        }

    }

    /**
     * This method prints the test results on console.
     * 
     * @param result Result of test executed.
     * 
     */
    private void printTestResults(double result) {
        final int COLUMN_SIZE_TEST_NAME = 35;
        final int COLUMN_SIZE_TIME = 15;
        final char HORIZONTAL_CHARACTER = '-';
        final char VERTICAL_CHARACTER = '|';
        final int LEGEND_INTENTION = 15;

        String header = createTestResultHeader(COLUMN_SIZE_TEST_NAME,
            COLUMN_SIZE_TIME, VERTICAL_CHARACTER);

        StringBuffer horizontalRow = new StringBuffer();
        for (int i = 0; i < header.length(); i++) {
            horizontalRow.append(HORIZONTAL_CHARACTER);
        }

        System.out.println(horizontalRow);
        System.out.println(header);
        System.out.println(horizontalRow);

        String line = createTestResultLine(COLUMN_SIZE_TEST_NAME,
            COLUMN_SIZE_TIME, VERTICAL_CHARACTER, result);
        System.out.println(line);
        System.out.println(horizontalRow);

        System.out.println();
        System.out.println("JMX console can be started in browser with "
            + "URL http://localhost:9092");
        System.out.println();
        System.out.print(appendSpacesOnString("", LEGEND_INTENTION));
        System.out.println();
    }

    /**
     * @param columnSizeTestName
     *            Is the size for the test name column.
     * @param columnSizeTime
     *            Is the size for the time column.
     * @param verticalCharacter
     *            Is the vertical character to use.
     * @return Returns the created benchmark header.
     */
    private String createTestResultHeader(final int columnSizeTestName,
        final int columnSizeTime, final char verticalCharacter) {
        StringBuffer header = new StringBuffer();
        header.append(verticalCharacter);
        header.append(' ');
        header
            .append(appendSpacesOnString("*Name of test*", columnSizeTestName));
        header.append(' ');
        header.append(verticalCharacter);
        header.append(' ');
        header.append(appendSpacesOnString("*Method [ms]*", columnSizeTime));
        header.append(' ');
        header.append(verticalCharacter);
        return header.toString();
    }

    /**
     * @param columnSizeTestName
     *            Is the size for the test name column.
     * @param columnSizeTime
     *            Is the size for the time column.
     * @param verticalCharacter
     *            Is the vertical character to use.
     * @param value
     *            Result value of performance test.
     * @return Returns the create benchmark result line.
     */
    private String createTestResultLine(final int columnSizeTestName,
        final int columnSizeTime, final char verticalCharacter, double value) {

        final int MAXIMUM_FRACTION_DIGITS = 3;
        final int MINIMUM_FRACTION_DIGITS = 1;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        df.setMinimumFractionDigits(MINIMUM_FRACTION_DIGITS);

        StringBuffer line = new StringBuffer();
        line.append(verticalCharacter);
        line.append(' ');
        line.append(appendSpacesOnString(m_serviceName, columnSizeTestName));
        line.append(' ');
        line.append(verticalCharacter);
        line.append(' ');
        line.append(appendSpacesOnString(df.format(value), columnSizeTime));
        line.append(' ');
        line.append(verticalCharacter);
        return line.toString();
    }

    /**
     * This method adds spaces to a string, but until the string has reached the
     * maximal length.
     * 
     * @param s
     *            Is the string which must be appended with spaces.
     * @param maxLength
     *            Is the given maximal length for the returned string.
     * @return Returns the prepared string.
     */
    private String appendSpacesOnString(String s, int maxLength) {
        StringBuffer sb = new StringBuffer(s);
        while (sb.length() < maxLength) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * This method executes the test for method "compute" of class "DemoA".
     * 
     * @return Time for method execution
     */
    private double executeTest() {
        DemoA printer = (DemoA) m_appContext
            .getBean(m_serviceName);

        long start = System.currentTimeMillis();

        try {
            // Checkstyle: MagicNumber off
            printer.computeA(10);
            // Checkstyle: MagicNumber on
        } catch (Exception e) {
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();

        return (stop - start);
    }
}
// Checkstyle: UseLogger on
// Checkstyle: UncommentedMain on
