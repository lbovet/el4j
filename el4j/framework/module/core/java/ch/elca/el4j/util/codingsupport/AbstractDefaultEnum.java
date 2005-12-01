/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Hashtable;


/**
 * The parent class for enums using the typesafe enum pattern (these enums are
 * not comparable). Use the AbstractComparableEnum class as a parent if you need
 * comparable enums. The pattern ensures that enums are correctly serialized and
 * that there are some common accessor methods.
 * <p>
 *
 * <h2>Concepts</h2>
 * <ul>
 * <li><b>Enumerations</b> are programming language types. Elements of an
 * enumeration are one of a fixed (small) number of enumerable elements. For
 * example, a simple enumeration for a program for playing cards could be
 * {Spade, Heart, Diamonds, Club}. An element of this enumeration would then be
 * any one of the four types of cards.
 * </ul>
 *
 *  <h2>How to Use</h2>
 *
 *  <H3> Usage example</H3>
 *
 *  The following code shows how to write an enumeration with the three possible
 *  values <BR>
 *  <code>{ASSERT, REQUIRE, ENSURE}</code>:
 * <code> <pre>
 * package ch.elca.el4j.codingsupport;
 *
 * import java.io.ObjectStreamException; 
 * // only needed due to a IBM JDK 1.2.2 bug
 *
 * /**
 *  * Represents the type of an assertion.
 *  * It uses the typesafe enum pattern for <BR>
 *  * <code> enum AssertType { ASSERT, REQUIRE, ENSURE }; </code>
 *  * <p>
 *  *
 *  * The code for class is potentially generated.
 *  *
 *  * /
 *  public class AssertType extends AbstractDefaultEnum {
 * 
 *      //  define a private constructor with the two arguments 
 *      //  and delegate construction
 *      //  to the parent class's constructor:
 *      private AssertType(String name, int code) {
 *        super(name, code);
 *      }
 * 
 *      // now define the elements:
 * 
 *      public static final AssertType ASSERT =
 *        new AssertType(&quot;ASSERT&quot;,1);
 * 
 *      public static final AssertType REQUIRE =
 *        new AssertType(&quot;REQUIRE&quot;,2);
 * 
 *      public static final AssertType ENSURE =
 *        new AssertType(&quot;ENSURE&quot;,3);
 *      }
 *  
 *      // Customize the get(String) method
 *      public static AssertType get(String name) {
 *        return (AssertType) AbstractDefaultEnum.get(AssertType.class, name);
 *      }
 *  }
 * </pre> </code>
 *
 * <H2>Deployment Environment</H2>
 * The duplication of <code>readResolve</code> method in each enumeration is
 * not needed because JDK 1.2.2 is no longer supported by EL4J.
 *
 * See also AbstractComparableEnum for a variant of this class that allow 
 * comparisons (i.e. it implements Comparable). <br/>
 * The pattern for the implementation of this class was inspired by the book
 * "Effective Java" by Joshua Bloch.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @see <a href="http://www.javaworld.com/javaworld/javatips/jw-javatip122.html">
 *          Typesafe Enum Pattern Pitfalls</a>
 * 
 * @author Raphael Boog (RBO)
 */
public abstract class AbstractDefaultEnum implements Serializable {
    /**
     * A hastable containing all instances of this class.
     */
    protected static Hashtable s_singletons = new Hashtable();

    /**
     * The name of this enum element.
     */
    protected String m_name;

    /**
    * The integer code for this enum element.
    */
    protected int m_code;

    /**
     * Constructor that initializes the name and and adds this instance into
     * the signletons list along with its key. The key is comprised of the class
     * name and the name of this enum
     * 
     * @param name Is the name of this instance.
     * @param code Is the code.
     */
    protected AbstractDefaultEnum(String name, int code) {
        m_name = name;
        m_code = code;
        // First parameter is a unique key.
        // Second parameter is a unique value.
        s_singletons.put((this.getClass()).getName() + "." + m_name, this);
    }

    /**
     * Return the named enum (names are case sensitive!).
     * 
     * @param myClass Is the type to get.
     * @param name Is the name of the instantiated class.
     * @return the named enum or null in case it is not found.
     */
    protected static Object get(Class myClass, String name) {
        return s_singletons.get(myClass.getName() + "." + name);
    }

    /**
     * Return the enum's name.
     * @return the enum's name.
     */
    public String toString() {
        return m_name;
    }

    /**
     * @return Return the enum's code.
     */
    public int toInt() {
        return m_code;
    }

    /**
     * Return true if and only if that object is equivalent to the object this
     * operation is invoked upon. Remark: due to the fact that there is only one
     * Object for each element of an enumeration, the == operator has the same
     * semantics (but more efficient).
     * 
     * {@inheritDoc}
     */
    public final boolean equals(Object that) {
        return super.equals(that);
    }

    /**
     * Retruns the hash code of this instance.
     * 
     * {@inheritDoc}
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Performs an unique return of this instance after deserialzation.<br>
     *
     * This method has no effect with the JDK 1.2.2 of IBM. If you use the JDK
     * version, this <code>readResolve</code> method must be duplicated in each
     * enum subclass. EL4J does not support it any longer.
     * 
     * @return readResolve
     * @throws ObjectStreamException On reading error.
     */
    public Object readResolve() throws ObjectStreamException {
        return s_singletons.get((this.getClass()).getName() + "." + m_name);
    }
}
