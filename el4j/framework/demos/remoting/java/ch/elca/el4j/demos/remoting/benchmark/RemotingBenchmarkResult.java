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

/**
 * This class contains a result set for a benchmark.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Rashid Waraich (RWA)
 */
public class RemotingBenchmarkResult {
    /**
     * Average time in seconds for method "getArea".
     */
    private double m_averageGetArea;

    /**
     * Average time in seconds for method "throwMeAnException".
     */
    private double m_averageThrowMeAnException;
    
    /**
     * Average time in seconds for method "countNumberOfUppercaseLetters".
     */
    private double m_averageCountNumberOfUppercaseLetters;
    
    /**
     * Average time in seconds for method "addComplexNumbers".
     */
    private double m_averageAddComplexNumbers;    
    
    /**
     * This is the name of the bean.
     */
    private final String m_beanName;

    /**
     * Constructor.
     * 
     * @param beanName
     *            Is the name of the bean where the average times belongs to.
     */
    public RemotingBenchmarkResult(String beanName) {
        m_beanName = beanName;
    }

    /**
     * @return Returns the beanName.
     */
    public String getBeanName() {
        return m_beanName;
    }

    /**
     * @return Returns the averageCountNumberOfUppercaseLetters.
     */
    public double getAverageCountNumberOfUppercaseLetters() {
        return m_averageCountNumberOfUppercaseLetters;
    }

    /**
     * @param averageCountNumberOfUppercaseLetters
     *            The averageCountNumberOfUppercaseLetters to set.
     */
    public void setAverageCountNumberOfUppercaseLetters(
            double averageCountNumberOfUppercaseLetters) {
        this.m_averageCountNumberOfUppercaseLetters 
            = averageCountNumberOfUppercaseLetters;
    }

    /**
     * @return Returns the averageGetArea.
     */
    public double getAverageGetArea() {
        return m_averageGetArea;
    }

    /**
     * @param averageGetArea
     *            The averageGetArea to set.
     */
    public void setAverageGetArea(double averageGetArea) {
        m_averageGetArea = averageGetArea;
    }

    /**
     * @return Returns the averageThrowMeAnException.
     */
    public double getAverageThrowMeAnException() {
        return m_averageThrowMeAnException;
    }

    /**
     * @param averageThrowMeAnException
     *            The averageThrowMeAnException to set.
     */
    public void setAverageThrowMeAnException(double averageThrowMeAnException) {
        m_averageThrowMeAnException = averageThrowMeAnException;
    }

    public double getAverageAddComplexNumbers() {
        return m_averageAddComplexNumbers;
    }

    public void setAverageAddComplexNumbers(double averageAddComplexNumbers) {
        m_averageAddComplexNumbers = averageAddComplexNumbers;
    }
}