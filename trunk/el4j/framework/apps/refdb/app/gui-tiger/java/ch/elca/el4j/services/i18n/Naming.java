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

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.services.dom.info.EntityType;
import ch.elca.el4j.services.dom.info.Property;



/**
 * provides localized names and descriptions for DOM elements.
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
public class Naming {
    /** the default Naming. */
    private static Naming s_instance;
    
    /** the backing message source. */
    MessageSourceAccessor m_source;

    /** @param ms the backing message source */
    public Naming(MessageSource ms) {
        m_source = new MessageSourceAccessor(ms);
    }
    
    /** returns {@code t}'s lookup key.
     * @param t .
     * @return .
     */
    protected String keyFor(EntityType t) {
        return t.name;
    }
    
       
    /**
     * 
     * @param p    the model property 
     * @param key  the visible property
     * @param def  the visible property's default message or
     *             <code>null</code> if there is none
     * @return     the resolved message, or <code>null</code> if none could
     *             be found
     */
    protected String get(Property p, String key, String def) {
        try {
            return m_source.getMessage(new DefaultMessageSourceResolvable(
                new String[] {
                    keyFor(p.declaringType) + "." + p.name + "." + key,
                    p.name + "." + key
                },
                new Object[] {
                    p.declaringType.name
                },
                def
            ));
        } catch (NoSuchMessageException e) {
            return null;
        }
    }
    
    /**
     * returns <code>p</code>'s localized name.
     * @param p .
     * @return .
     */
    public String getName(Property p) {
        // AMS: Personally, I find the key "displayName" a bit verbose 
        // ("Name" would suffice imho), but imho it's not worth creating
        // an incompatibility over.
        return get(p, "displayName", StringUtils.capitalize(p.name));
    }
    
    /**
     * returns <code>p</code>'s localized description, or <code>null</code>
     * if no description is available.
     * @param p .
     * @return .
     */    
    public String getDescription(Property p) {
        return get(p, "description", "");
    }
    
    
    /** 
     * Returns a message provider for the constant value {@code value} of
     * a DOM property {@code property}.
     * @param property .
     * @param value .
     * @return .
     */
    public Fetcher forConstantValue(Property property, String value) {
        return new SimpleFetcher(
            "Dom.value." 
            + property.declaringType.name + "." 
            + property.name + "."
            + value            
        );
    }
    
    
    /**
     * Returns a message source for confirmation strings.
     * @param action the id of the action to confirm
     * @param type the type of the entities affected by the action 
     * @param multiplicity the number of entities affected by the action
     * @return see above
     */
    public Fetcher forConfirmation(String action, EntityType type,
                                   int multiplicity) {
        // TODO also look at supertypes' key (or keys)
        return new SimpleFetcher(
            "Confirm." 
                + action + '.' 
                + type.name + '.' 
                + Integer.toString(multiplicity)
        );
    }
    
    /**
     * Returns a message source for views.
     * @param kind the schema of the requesting view
     * @param type the type of entities shown by the requesting view
     * @return .
     */
    public Fetcher forView(String kind, EntityType type) {
        return new SimpleFetcher("View." + kind + '.' + type.name);
    }
    
    /**
     * An object representing the space of messages that may be fetched by key 
     * suffix.
     */
    public interface Fetcher {
        /** 
         * Attempts to resolve the message associated with the supplied key 
         * suffix.
         * @param keySuffix the string identifying a message in the key space
         *                  represented by this fetcher.
         * @return the resolved message, or null if no such message could be 
         *         found.
         */
        String get(String keySuffix);
    }
    

    /**
     * A fetcher whose namespace is defined by a prefix shared by all keys. 
     */
    protected class SimpleFetcher implements Fetcher {
        /***/
        private final String m_keyPrefix;
        /** the replacements for {0}, ... */
        private final Object[] m_arguments;
        
        /**
         * Constructor.
         */
        SimpleFetcher(String keyPrefix, Object... arguments) {
            m_keyPrefix = keyPrefix;
            m_arguments = arguments;
        }

        /** {@inheritDoc} */
        public String get(String keySuffix) {
            try {
                return m_source.getMessage(
                    m_keyPrefix + '.' + keySuffix,
                    m_arguments
                ); 
            } catch (NoSuchMessageException e) {
                return null;
            }
        }
    }
    
    /**
     * Returns a field face property.
     * @param kind the displaying view's schema
     * @param domProperty the backing property in the domain object model
     * @param faceProperty the name of the desired face property
     * @return see above
     */
    public String getFieldFaceProperty(String kind, Property domProperty,
                                       String faceProperty) {
        
        String key = "View." 
            + kind + '.' 
            + domProperty.declaringType.name + '.'
            + domProperty.name + '.'
            + faceProperty;
 
        try {
            return m_source.getMessage(key);
        } catch (NoSuchMessageException e) {
            System.err.println(e);
            return key;
        }
    }
    
    /** 
     * Returns an array with the sequence of prefixes obtained by iteratively
     * chopping away the last qualifier until only one qualifier remains.
     * 
     * <p> Example: {@code prefixes("ch.elca.el4j.services.richclient")} 
     * returns 
     * <pre>[
     *"ch.elca.el4j.services.richclient",
     *"ch.elca.el4j.services",
     *"ch.elca.el4j",
     *"ch.elca",
     *"ch"
     *]</pre> 
     * 
     * @param s the string to find the prefixes for
     * @return the array containing the prefixes
     */
    protected static String[] prefixes(String s) {
        return prefixes(s, 0);
    }

    /**
     * Helper to {@link #prefixes(String)}.
     * @param depth iteration counter
     */
    private static String[] prefixes(String s, int depth) {
        int i = s.lastIndexOf('.');
        String[] r;
        if (i == -1) {
            // no match
            r = new String[depth + 1];
        } else {
            r = prefixes(s.substring(0, i), depth + 1);
        }
        r[depth] = s;
        return r;
    }
    
    /**
     * Fetches messages from the message source using key prefixes and 
     * replacements (for {0}, {1}, ...). For instance, if the prefixes are
     * {@code "string", "object"} and the key is {@code "displayName"}, the keys
     * {@code "string.displayName"} and {@code "object.displayName"} are tried
     * in order. 
     * 
     * <p> You might wish to use a RewritingMessageSource with pattern rules
     * instead.
     */
    protected class PrefixFetcher implements Fetcher {
        /** The key prefixes to try, starting with the most specific one. */
        String[] m_prefixes;
        
        /** The replacements for {0}, {1}, ... */
        Object[] m_replacements;
        
        /**
         * Constructor.
         * @param prefixes
         * @param replacements
         */
        PrefixFetcher(String[] prefixes, Object... replacements) {
            m_prefixes = prefixes;
            m_replacements = replacements;
        }
        
        /** {@inheritDoc} */
        public String get(String key) {
            try {
                String[] keys = new String[m_prefixes.length];
                for (int i = 0; i < m_prefixes.length; i++) {
                    keys[i] = m_prefixes[i] + "." + key;
                }
                
                return m_source.getMessage(new DefaultMessageSourceResolvable(
                    keys, m_replacements, null
                ));
            } catch (NoSuchMessageException e) {
                return null;
            }
        }
    }
    
    /** returns the registered default naming. */
    public static Naming instance() {
        if (s_instance == null) {
            throw new MisconfigurationRTException(
                "no default Naming was registered"
            );
        }
        return s_instance;
    }
    
    /** registers the default naming instance. */
    public static void setInstance(Naming n) {
        s_instance = n;
    }
}