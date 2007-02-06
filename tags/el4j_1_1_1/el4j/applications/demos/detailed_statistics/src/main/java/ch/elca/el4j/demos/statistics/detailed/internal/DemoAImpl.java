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
package ch.elca.el4j.demos.statistics.detailed.internal;

/**
 * This class is a dummy implementation for presentation purposes for the
 * detailed statistics demo. 
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
public class DemoAImpl implements DemoA {

    /** nonsense demoB object. */
    private DemoB m_demoB;

    /**
     * {@inheritDoc}
     */
    public void computeA(int number) {

        // Checkstyle: MagicNumber off
        try {
            Thread.sleep(3 * number);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m_demoB.computeB(3);
    }

    /**
     * Setter for DemoB.
     * 
     * @param demo
     *            DemoB object
     */
    public void setDemoB(DemoB demo) {
        this.m_demoB = demo;
    }
}
// Checkstyle: MagicNumber on
