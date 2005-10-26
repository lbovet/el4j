/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch 
 */
package ch.elca.el4j.util.codingsupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The parent class for comparable enums using the typesafe enum pattern.
 * <p>
 * <h2>How to Use</h2>
 * <H3>Usage example</H3>
 * The following code shows how to write an enumeration with the three possible
 * values <BR>
 * <code>{EXPECTED, NORMAL, UNEXPECTED}</code>:<code> <pre>
 * 
 *     /**
 *      * Severity Levels for reporting exceptions with code traces.
 *      * /
 *     static public class SeverityLevel extends AbstractComparableEnum {
 * 
 *         private SeverityLevel (String name, int code)
 *         {
 *             super (name, code);
 *         }
 * 
 *         static public final SeverityLevel EXPECTED =
 *             new SeverityLevel (&quot;EXPECTED&quot;,1);
 * 
 *         static public final SeverityLevel NORMAL =
 *             new SeverityLevel (&quot;NORMAL&quot;,2);
 * 
 *         static public final SeverityLevel UNEXPECTED =
 *             new SeverityLevel (&quot;UNEXPECTED&quot;,3);
 *     }
 *  
 * </pre> </code> Please refer also to the Javadoc of the uncomparable enums in
 * {@link AbstractDefaultEnum}. The duplication of <code>readResolve</code>
 * method in each enumeration is not needed because JDK 1.2.2 is no longer
 * supported by EL4J.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 */
public abstract class AbstractComparableEnum extends AbstractDefaultEnum
    implements Comparable {
    /**
     * The list of all comparable enums ever created.
     */
    protected static final ArrayList ENUM_LIST = new ArrayList();

    /**
     * The ordinal assigned to this comparable enum. This value is directly used
     * for comparison.
     */
    protected final int m_ordinal;

    /**
     * Override this default constructor with your own constructor.
     * 
     * @param name Is the name.
     * @param code Is the code.
     */
    protected AbstractComparableEnum(String name, int code) {
        super(name, code);

        synchronized (AbstractComparableEnum.class) {
            m_ordinal = ENUM_LIST.size();
            ENUM_LIST.add(this);
        }
    }

    /**
     * @param myClass Is the type to count.
     * @return Return the number of enums of an enumeration.
     */
    protected static int size(Class myClass) {
        int result = 0;

        synchronized (AbstractComparableEnum.class) {
            for (int i = 0; i < ENUM_LIST.size(); i++) {
                if (myClass == ENUM_LIST.get(i).getClass()) {
                    result++;
                }
            }
        }

        return result;
    }

    /**
     * @param myClass Is the class to be used.
     * @return Return an iterator for the elements of the specified enum class.
     */
    protected static Iterator iterator(final Class myClass) {
        Iterator it = new Iterator() {
            private int m_currentOrdinal = -1;

            public boolean hasNext() {
                synchronized (AbstractComparableEnum.class) {
                    for (int i = m_currentOrdinal + 1;
                        i < ENUM_LIST.size(); i++) {
                        if (myClass == ENUM_LIST.get(i).getClass()) {
                            m_currentOrdinal = i - 1;

                            return true;
                        }
                    }
                }

                return false;
            }

            public Object next() {
                synchronized (AbstractComparableEnum.class) {
                    for (int i = m_currentOrdinal + 1; 
                        i < ENUM_LIST.size(); i++) {
                        if (myClass == ENUM_LIST.get(i).getClass()) {
                            m_currentOrdinal = i;

                            return ENUM_LIST.get(i);
                        }
                    }
                }

                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        return it;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        return m_ordinal - ((AbstractComparableEnum) o).m_ordinal;
    }
}