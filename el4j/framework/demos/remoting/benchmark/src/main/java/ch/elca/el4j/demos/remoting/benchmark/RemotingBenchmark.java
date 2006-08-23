/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.demos.remoting.benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.core.exceptions.BaseRTException;
import ch.elca.el4j.demos.remoting.Calculator;
import ch.elca.el4j.demos.remoting.CalculatorException;

//Checkstyle: UncommentedMain off

/**
 * This benchmark compares following protocols.
 * <ul>
 * <li>Rmi direct from the Springframework</li>
 * <li>Hessian direct from the Springframework</li>
 * <li>Burlap direct from the Springframework</li>
 * <li>Rmi with implicit context passing</li>
 * <li>Hessian with implicit context passing</li>
 * <li>Burlap with implicit context passing</li>
 * <li>Soap with implicit context passing</li>
 * </ul>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class RemotingBenchmark {
    /**
     * Path where the big text file can be found.
     */
    public static final String FILE_PATH_OF_BIG_TEXT_FILE 
        = "files/gulliverstravels.txt";

    /**
     * Paths where the config files can be found.
     */
    public static final String[] CONFIG_LOCATIONS 
        = {"classpath*:mandatory/*.xml", "client/benchmark-config.xml"};

    /**
     * This counter declares how many times on warm up a test must be repeated.
     */
    public static final int WARMUP_REPETITION_COUNT = 100;

    /**
     * This counter declares how many times a test must be repeated the receive
     * a possible average time.
     */
    public static final int TEST_REPETITION_COUNT = 100;

    /**
     * This is the first site parameter for method 'getArea'.
     */
    public static final double AREA_SIDE_A = 3.4;

    /**
     * This is the second site parameter for method 'getArea'.
     */
    public static final double AREA_SIDE_B = 4.1;

    /**
     * Is the result of method "getArea".
     */
    public static final double RESULT_METHOD_GET_AREA 
        = AREA_SIDE_A * AREA_SIDE_B;

    /**
     * Is the fault tolerance for result of method "getArea".
     */
    public static final double RESULT_METHOD_GET_AREA_FAULT_TOLERANCE 
        = 0.0000000001;

    /**
     * Is the result of method "CountNumberOfUppercaseLetters".
     */
    public static final int RESULT_METHOD_COUNT_NUMBER_OF_UPPERCASE_LETTERS 
        = 59;

    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(RemotingBenchmark.class);

    /**
     * These are the tests, which has to be run.
     */
    private static String[] s_tests = {"rmiWithoutContextCalculator",
        "rmiWithContextCalculator", "hessianWithoutContextCalculator",
        "hessianWithContextCalculator", "burlapWithoutContextCalculator",
        "burlapWithContextCalculator", "httpInvokerWithoutContextCalculator",
        "httpInvokerWithContextCalculator", "soapWithContextCalculator"};

    /**
     * This member contains the large text. It will be used for testing.
     */
    private String m_largeText;

    /**
     * This is the application context to work with.
     */
    private ConfigurableApplicationContext m_appContext;

    /**
     * This list contains the test results.
     */
    private List m_benchmarkResults = new LinkedList();

    /**
     * In this constructor a big text file will be loaded and an application
     * context will be created.
     */
    public RemotingBenchmark() {
        Resource resource = new ClassPathResource(FILE_PATH_OF_BIG_TEXT_FILE);
        InputStream in = null;
        StringBuffer sb = new StringBuffer();
        try {
            in = resource.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            m_largeText = sb.toString();
        } catch (IOException e) {
            s_logger.error("Unable to read file '" + FILE_PATH_OF_BIG_TEXT_FILE
                    + "'", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    s_logger.error("Unable to close file '"
                            + FILE_PATH_OF_BIG_TEXT_FILE + "'", e);
                }
            }
        }

        m_appContext = new ModuleApplicationContext(CONFIG_LOCATIONS, true);
    }

    /**
     * This is the main method.
     * 
     * @param args
     *            Are the arguments from console.
     */
    public static void main(String[] args) {
        RemotingBenchmark b = new RemotingBenchmark();
        System.out.println("Please wait, benchmarks are running...");
        for (int i = 0; i < s_tests.length; i++) {
            String testName = s_tests[i];
            System.out.print("Benchmark " + (i + 1) + " of " + s_tests.length
                    + " with name '" + testName + "' is running... ");
            b.executeTest(testName);
            System.out.println("done.");
        }
        b.printTestResults();
        b.close();
    }

    /**
     * Closes all related parts.
     */
    protected void close() {
        if (m_appContext != null) {
            m_appContext.close();
        }
    }

    /**
     * This method prints the test results on console.
     */
    private void printTestResults() {
        final int COLUMN_SIZE_TEST_NAME = 35;
        final int COLUMN_SIZE_TIME = 15;
        final char HORIZONTAL_CHARACTER = '-';
        final char VERTICAL_CHARACTER = '|';
        final int MAXIMUM_FRACTION_DIGITS = 3;
        final int MINIMUM_FRACTION_DIGITS = 1;
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

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        df.setMinimumFractionDigits(MINIMUM_FRACTION_DIGITS);

        Iterator it = m_benchmarkResults.iterator();
        while (it.hasNext()) {
            RemotingBenchmarkResult r = (RemotingBenchmarkResult) it.next();
            String line = createTestResultLine(COLUMN_SIZE_TEST_NAME, 
                COLUMN_SIZE_TIME, VERTICAL_CHARACTER, df, r);
            System.out.println(line);
            System.out.println(horizontalRow);
        }
        System.out.println();
        System.out.print(appendSpacesOnString("Legend:", LEGEND_INTENTION));
        System.out.println("Method 1: double getArea(double a, double b)");
        System.out.print(appendSpacesOnString("", LEGEND_INTENTION));
        System.out.println("Method 2: void throwMeAnException() "
            + "throws CalculatorException");
        System.out.print(appendSpacesOnString("", LEGEND_INTENTION));
        System.out.println("Method 3: int "
            + "countNumberOfUppercaseLetters(String textOfSize60kB)");
    }

    /**
     * @param columnSizeTestName Is the size for the test name column.
     * @param columnSizeTime Is the size for the time column.
     * @param verticalCharacter Is the vertical character to use.
     * @return Returns the created benchmark header.
     */
    private String createTestResultHeader(final int columnSizeTestName, 
        final int columnSizeTime, final char verticalCharacter) {
        StringBuffer header = new StringBuffer();
        header.append(verticalCharacter);
        header.append(' ');
        header.append(
            appendSpacesOnString("*Name of test*", columnSizeTestName));
        header.append(' ');
        header.append(verticalCharacter);
        header.append(' ');
        header.append(
            appendSpacesOnString("*Method 1 [ms]*", columnSizeTime));
        header.append(' ');
        header.append(verticalCharacter);
        header.append(' ');
        header.append(
            appendSpacesOnString("*Method 2 [ms]*", columnSizeTime));
        header.append(' ');
        header.append(verticalCharacter);
        header.append(' ');
        header.append(
            appendSpacesOnString("*Method 3 [ms]*", columnSizeTime));
        header.append(' ');
        header.append(verticalCharacter);
        return header.toString();
    }

    /**
     * @param columnSizeTestName Is the size for the test name column.
     * @param columnSizeTime Is the size for the time column.
     * @param verticalCharacter Is the vertical character to use.
     * @param df Is the decimal format for the test time.
     * @param r Is the benchmark result.
     * @return Returns the create benchmark result line.
     */
    private String createTestResultLine(final int columnSizeTestName, 
        final int columnSizeTime, final char verticalCharacter, 
        DecimalFormat df, RemotingBenchmarkResult r) {
        StringBuffer line = new StringBuffer();
        line.append(verticalCharacter);
        line.append(' ');
        line.append(appendSpacesOnString(
            r.getBeanName(), columnSizeTestName));
        line.append(' ');
        line.append(verticalCharacter);
        line.append(' ');
        line.append(appendSpacesOnString(
            df.format(r.getAverageGetArea()), columnSizeTime));
        line.append(' ');
        line.append(verticalCharacter);
        line.append(' ');
        line.append(appendSpacesOnString(
            df.format(r.getAverageThrowMeAnException()), 
            columnSizeTime));
        line.append(' ');
        line.append(verticalCharacter);
        line.append(' ');
        line.append(appendSpacesOnString(
            df.format(r.getAverageCountNumberOfUppercaseLetters()), 
            columnSizeTime));
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
     * This method executes tests for the given bean.
     * 
     * @param beanName
     *            Is the bean which has to be used for testing.
     */
    private void executeTest(String beanName) {
        Calculator calc = (Calculator) m_appContext.getBean(beanName);
        warmupTest(calc);
        double averageGetArea 
            = executeTestMethodGetArea(calc);
        double averageThrowMeAnException 
            = executeTestMethodThrowMeAnException(calc);
        double averageCountNumberOfUppercaseLetters 
            = executeTestMethodCountNumberOfUppercaseLetters(calc);
        addTestResults(beanName, averageGetArea, averageThrowMeAnException,
                averageCountNumberOfUppercaseLetters);
    }

    /**
     * This method adds the test results in a map.
     * 
     * @param beanName
     *            Is the bean where the results are comming from.
     * @param averageGetArea
     *            Is the mesured time for method 'getArea'.
     * @param averageThrowMeAnException
     *            Is the mesured time for method 'throwMeAnException'.
     * @param averageCountNumberOfUppercaseLetters
     *            Is the mesured time for method
     *            'countNumberOfUppercaseLetters'.
     */
    private void addTestResults(String beanName, double averageGetArea,
            double averageThrowMeAnException,
            double averageCountNumberOfUppercaseLetters) {
        RemotingBenchmarkResult result = new RemotingBenchmarkResult(beanName);
        result.setAverageGetArea(averageGetArea);
        result.setAverageThrowMeAnException(averageThrowMeAnException);
        result.setAverageCountNumberOfUppercaseLetters(
            averageCountNumberOfUppercaseLetters);
        m_benchmarkResults.add(result);
    }

    /**
     * Execute one first call for each method to minimize adulterations.
     * 
     * @param calc
     *            Is the object to do operations on it.
     */
    private void warmupTest(Calculator calc) {
        for (int i = 0; i < WARMUP_REPETITION_COUNT; i++) {
            calc.getArea(AREA_SIDE_A, AREA_SIDE_B);
            // Checkstyle: EmptyBlock off
            try {
                calc.throwMeAnException();
            } catch (CalculatorException e) {
                // Okay.
            }
            // Checkstyle: EmptyBlock on
            calc.countNumberOfUppercaseLetters(m_largeText);
        }
    }

    /**
     * This method executes the test for method "getArea" TEST_REPETITION_COUNT
     * times and returns the average execution time.
     * 
     * @param calc
     *            Is the object to do operations on it.
     * @return Returns the average execution time.
     */
    private double executeTestMethodGetArea(Calculator calc) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < TEST_REPETITION_COUNT; i++) {
            double result = calc.getArea(AREA_SIDE_A, AREA_SIDE_B);
            if (result - RESULT_METHOD_GET_AREA 
                > RESULT_METHOD_GET_AREA_FAULT_TOLERANCE) {
                throw new BaseRTException("Benchmark is corrupted!");
            }
        }
        long stop = System.currentTimeMillis();
        return (stop - start) / (double) TEST_REPETITION_COUNT;
    }

    /**
     * This method executes the test for method "throwMeAnException"
     * TEST_REPETITION_COUNT times and returns the average execution time.
     * 
     * @param calc
     *            Is the object to do operations on it.
     * @return Returns the average execution time.
     */
    private double executeTestMethodThrowMeAnException(Calculator calc) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < TEST_REPETITION_COUNT; i++) {
            // Checkstyle: EmptyBlock off
            try {
                calc.throwMeAnException();
                throw new BaseRTException("Benchmark is corrupted!");
            } catch (CalculatorException e) {
                // Okay.
            }
            // Checkstyle: EmptyBlock on
        }
        long stop = System.currentTimeMillis();
        return (stop - start) / (double) TEST_REPETITION_COUNT;
    }

    /**
     * This method executes the test for method "countNumberOfUppercaseLetters"
     * TEST_REPETITION_COUNT times and returns the average execution time.
     * 
     * @param calc
     *            Is the object to do operations on it.
     * @return Returns the average execution time.
     */
    private double executeTestMethodCountNumberOfUppercaseLetters(
            Calculator calc) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < TEST_REPETITION_COUNT; i++) {
            int result = calc.countNumberOfUppercaseLetters(m_largeText);
            if (result != RESULT_METHOD_COUNT_NUMBER_OF_UPPERCASE_LETTERS) {
                throw new BaseRTException("Benchmark is corrupted!");
            }
        }
        long stop = System.currentTimeMillis();
        return (stop - start) / (double) TEST_REPETITION_COUNT;
    }
}
//Checkstyle: UncommentedMain on
