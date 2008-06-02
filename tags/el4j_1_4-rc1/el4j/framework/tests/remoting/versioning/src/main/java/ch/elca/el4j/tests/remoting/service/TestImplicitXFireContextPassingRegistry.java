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

package ch.elca.el4j.tests.remoting.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.core.contextpassing.ImplicitContextPasser;
import ch.elca.el4j.core.contextpassing.ImplicitContextPassingRegistry;

/**
 * This class is used to test if the implicit context passing works.
 * Extended to test whether the passing of complex contexts works with XFires
 * internal context passing.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @author Philippe Jacot (PJA)
 */
public class TestImplicitXFireContextPassingRegistry implements
        ImplicitContextPassingRegistry {
    /**
     * 
     */   
    private static final int CALCULATOR_INT = 42;
    
    /**
     * 
     */
    private static final double CALCULATOR_DOUBLE = 1.23456;
    
    /**
     * 
     */
    private static final long CALCULATOR_LONG = Long.MAX_VALUE - CALCULATOR_INT;
    
    /**
     * 
     */
    private static final String CALCULATOR_STRING = "Hey, you're invited to a "
        + "Schadenfreude party";
    
    /**
     * 
     */
    private static final String[] CALCULATOR_STRING_ARRAY = new String[]{
        "Hm, I don't know what 'Schadenfreude' means, but OK'", 
        "Too bad, you're not invited"};
    
    /**
     * 
     */
    private static final int[] CALCULATOR_INT_ARRAY 
        = new int[]{0, -1, 2, -3, 4, -5, 6, -7};
    
    /**
     * 
     */
    private static final  byte[] CALCULATOR_BYTE_ARRAY 
        = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
    
    /**
     * 
     */
    private static final String CALCULATOR_CONTEXT = "calculatorValueObject";
    
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(TestImplicitXFireContextPassingRegistry.class);

    /**
     * {@inheritDoc}
     */
    public void registerImplicitContextPasser(
        ImplicitContextPasser passer) {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void unregisterImplicitContextPasser(
        ImplicitContextPasser passer) {
        // Do nothing.
    }
    
    /**
     * {@inheritDoc}
     */
    public Map getAssembledImplicitContext() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("testMessage", "Hello everybody, I am THE test message.");
        
        TestXFireContextPassingValue valueObject 
            = new TestXFireContextPassingValue();
        valueObject.setMyInt(CALCULATOR_INT);
        valueObject.setMyByteArray(CALCULATOR_BYTE_ARRAY);
        valueObject.setMyDouble(CALCULATOR_DOUBLE);
        valueObject.setMyIntArray(CALCULATOR_INT_ARRAY);
        valueObject.setMyLong(CALCULATOR_LONG);
        valueObject.setMyString(CALCULATOR_STRING);
        valueObject.setMyStringArray(CALCULATOR_STRING_ARRAY);
        map.put(CALCULATOR_CONTEXT, valueObject);
        
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public void pushAssembledImplicitContext(Map contexts) {
        s_logger.info("Test message: " + contexts.get("testMessage"));
        
        // Test if the passed map contains the expected value object
//        TestXFireContextPassingValue o 
//            = (TestXFireContextPassingValue) contexts.get(CALCULATOR_CONTEXT);
//        
//        // For some reason 
//        // junit.framework.Assert.assertEquals
//        // Does not work (Java 5 keyword assert problem?)
//        // Therefore it's handmade here ;)
//        
//        if (o.getMyInt() != CALCULATOR_INT) {
//            throw new IllegalStateException("MyInt changed");
//        }
//        
//        if (o.getMyDouble() != CALCULATOR_DOUBLE) {
//            throw new IllegalStateException("MyDouble changed");
//        }
//        
//        if (o.getMyLong() != CALCULATOR_LONG) {
//            throw new IllegalStateException("MyLong changed");
//        }
//        
//        if (!o.getMyString().equals(CALCULATOR_STRING)) {
//            throw new IllegalStateException("MyString changed");
//        }
//        
//        if (!Arrays.equals(o.getMyByteArray(), CALCULATOR_BYTE_ARRAY)) {
//            throw new IllegalStateException("MyByteArray changed");
//        }
//        
//        if (!Arrays.equals(o.getMyIntArray(), CALCULATOR_INT_ARRAY)) {
//            throw new IllegalStateException("MyIntArray changed");
//        }
//        
//        if (!Arrays.equals(o.getMyStringArray(), CALCULATOR_STRING_ARRAY)) {
//            throw new IllegalStateException("MyStringArray changed");
//        }        
    }
   
}