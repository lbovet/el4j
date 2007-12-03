/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package temp;

public class PerfLogger {
    private String m_task;
    private long m_start;

    public void start(String task) {
        end();
        m_task = task;
        m_start = System.nanoTime();
    }

    public void end() {
        if (m_task != null) {
            long end = System.nanoTime();
            System.err.println(m_task + " took " + (end - m_start) / 1000000
                + " ms");
            m_task = null;
        }
    }
}
