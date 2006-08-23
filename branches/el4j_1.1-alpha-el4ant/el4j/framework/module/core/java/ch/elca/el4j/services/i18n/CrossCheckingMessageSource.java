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
package ch.elca.el4j.services.i18n;

import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * Test class loading the same properties files in two or more message sources
 * and comparing their behavior.
 * 
 * <h4> Usage </h4>
 * Replace the message source in the spring configuration with an instance of 
 * this class (leaving the basenames-assignment intact) and configure the
 * types to be compared using {@link #m_types}. This class will then resolve 
 * every
 * message it is asked to resolve from both sources, and {@code assert} they
 * agree about the message's value. (You should therefore enable assertions ...)
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class CrossCheckingMessageSource implements MessageSource, 
                                                   InitializingBean {
    /** The message sources to be compared. */
    MessageSource[] m_sources;
    
    /** The types of the message sources to be compared. */
    String[] m_types;
    
    /** 
     * Like {@link ResourceBundleMessageSource#setBasenames(String[])}. 
     */
    String[] m_basenames;
    
    /**
     * Sets the types of the message sources to be compared.
     */
    public void setTypes(String[] types) {
        m_types = types;
    }
    
    /** 
     * Like {@link ResourceBundleMessageSource#setBasenames(String[])}. 
     */    
    public void setBasenames(String[] basenames) {
        m_basenames = basenames;
    }
    
    /** Represents a lookup's result. */
    static interface Result {
        /** Simulates the return of this result. */
        String ret();
    }
    
    /** The lookup found a message. */
    static class Found implements Result {
        /** The resolved message. */
        String m_message;
        
        /**
         * Constructor.
         * @param message The resolved message
         */
        Found(String message) { m_message = message; }
        
        /** {@inheritDoc} */
        public String ret() { return m_message; }
        
        /** {@inheritDoc} */
        public int hashCode() {
            return m_message.hashCode();
        }
        
        /** {@inheritDoc} */
        public boolean equals(Object o) {
            return o instanceof Found 
                && m_message.equals(((Found) o).m_message);
        }
    }
    
    /** The lookup did not find a message. */
    static class NotFound implements Result {
        /** The exception that signaled the failure. */
        NoSuchMessageException m_e;
        
        /**
         * Constructor.
         * @param e The exception signaling the failure.
         */
        NotFound(NoSuchMessageException e) { m_e = e; }
        
        /** {@inheritDoc} */
        public String ret() { throw m_e; }
        
        /** {@inheritDoc} */
        public int hashCode() {
            // Checkstyle: MagicNumber off
            return 0xdead;
            // Checkstyle: MagicNumber on
        }
        
        /** {@inheritDoc} */
        public boolean equals(Object o) {
            return o instanceof NotFound;
        }
    }
    
    /** Asserts that all lookup results are equal. */
    static class Checker {
        /** The result of the first lookup. */
        Result m_first;
        
        /** Checks a result. */
        void process(Result r) {
            if (m_first == null) {
                m_first = r;
            } else {
                assert m_first.equals(r);
            }            
        }
        
        /** Simulates a lookup's result. */
        String get() {
            return m_first.ret();
        }
    }
    
    /** {@inheritDoc} */
    public String getMessage(MessageSourceResolvable resolvable, Locale locale)
        throws NoSuchMessageException {
        Checker c = new Checker();
        for (MessageSource ms : m_sources) {
            try {
                c.process(new Found(ms.getMessage(resolvable, locale)));
            } catch (NoSuchMessageException e) {
                c.process(new NotFound(e));
            }
            
        }
        return c.get();
    }

    /** {@inheritDoc} */
    public String getMessage(String code, Object[] args, Locale locale) 
        throws NoSuchMessageException {
        Checker c = new Checker();
        for (MessageSource ms : m_sources) {
            try {
                c.process(new Found(ms.getMessage(code, args, locale)));
            } catch (NoSuchMessageException e) {
                c.process(new NotFound(e));
            }
            
        }
        return c.get();
    }

    /** {@inheritDoc} */
    public String getMessage(String code, Object[] args, String defaultMessage, 
                             Locale locale) {
        Checker c = new Checker();
        for (MessageSource ms : m_sources) {
            try {
                c.process(new Found(ms.getMessage(code, args, locale)));
            } catch (NoSuchMessageException e) {
                c.process(new NotFound(e));
            }
            
        }
        return c.get();
    }

    /** Creates and initializes the requested message sources. */
    public void afterPropertiesSet() throws Exception {
        m_sources = new MessageSource[m_types.length];
        for (int i = 0; i < m_types.length; i++) {
            MessageSource ms;
            try {
                ms = (MessageSource) Class.forName(m_types[i]).newInstance();
            } catch (Exception e) { 
                throw new RuntimeException(e);
            }
            
            if (ms instanceof RewritingMessageSource) {
                ((RewritingMessageSource) ms).setBasenames(m_basenames);
            } else if (ms instanceof ResourceBundleMessageSource) {
                ((ResourceBundleMessageSource) ms).setBasenames(m_basenames);
            } else {
                Reject.ifFalse(false, "unsupported type");
            }            
            m_sources[i] = ms;
        }
    }

}
