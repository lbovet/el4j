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
package ch.elca.el4j.services.i18n;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;

import ch.elca.el4j.util.codingsupport.Reject;

/**
 * A rewriting ressource bundle provides localized strings, which are defined 
 * using rewrite rules. The bundle is backed by a list of such rules. When
 * asked to look up a key, the bundle uses the last <i>matching</i> rule.
 * 
 * <h4>Rule File Format </h4>
 *
 * We extend the syntax of MessageFormat and will therefore refer to its
 * productions in this syntax definition. Also, please note that rule files are
 * white space sensitive, i.e. we explicitly mention where spaces are permitted.
 * 
 * <p> Every non-black, non-comment line in a rule file defines a rewrite rule:
 *  
 *<pre>
 *<i>rule:</i>
 *    <i>pattern</i> = <i>MessageFormatPattern</i> 
 *    <i>pattern</i> <i>space</i> = <i>space</i> <i>MessageFormatPattern</i>
 *</pre>
 *
 * When asked to look up a key, the bundle finds the last rule whose pattern
 * matches the key, and uses the rule's MessageFormatPattern to format the 
 * result.
 * 
 *<pre>
 *<i>pattern:</i>
 *    <i>qualifier</i>
 *    <i>pattern</i> . <i>qualifier</i>
 *     
 *<i>qualifier:</i>
 *    <i>literal</i>
 *    { <i>variableName</i> }
 *
 *<i>variableName:</i>
 *    <i>String</i>
 *</pre>
 *
 * A key is said to match a rule if it matches the rule's pattern. It matches
 * the pattern if, after it is split up in its different qualifiers, every
 * qualifier of the key matches the qualifier of the pattern. If the pattern's
 * qualifier is a literal, the key's qualifier matches only if it is identical.
 * If the pattern's qualifier is a variable name, this defines a variable
 * and assigns the key's qualifier to it.
 * 
 * <p>For instance, the key "ch.elca.el4j" matches the patterns "ch.elca.el4j"
 * and "ch.{company}.el4j". In the latter case, the variable "company" contains
 * "elca" after the matching. The key does not match "tv.elca.el4j"
 * as the first qualifier does not match.
 *   
 *<pre>
 *<i>FormatElement:</i> (in addition to inherited members)
 *    { <i>variableName</i> }
 *    { <i>MessageFormatPattern</i> }
 *</pre>
 *
 * where the MessageFormatPattern must not start with a digit. The former
 * addition asks the message format to insert the value of the variable, the
 * latter asks to insert the value associated with the key obtained by 
 * evaluating the MessageFormatPattern.
 * 
 * <p>For instance, if we look up "earth.poetic" in the rule source
 *<pre>
 *{planet}.name = {planet}
 *earth.name = home
 *{planet}.poetic = A piece of rock called {{planet}.name}. *</pre>
 *we get "A piece of rock called home.". If we look up "Mars.poetic" instead,
 *we get "A piece of rock called Mars."
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
public class MessageRewriter {
    /** Hm ... now what could this be ...? ;) */
    protected static Log s_logger
        = LogFactory.getLog(MessageRewriter.class); 

    /** An empty Object[]. */
    static final Object[] EMPTY = new Object[0];
    
    
    /** the list of loaded rules, ordered such that earlier rules superseed
     * later ones. */
    List<Rule> m_rules = new ArrayList<Rule>();
    
    /** 
     * Constructor.
     * @param readers readers providing the rule lists to be used.
     *                The lists are locically 
     *                concatenated in the order they were provided.
     */
    public MessageRewriter(List<Reader> readers) {
        for (Reader r : readers) {
            BufferedReader reader = new BufferedReader(r);

            String l;
            int lineNumber = 1;
            try {
                while ((l = reader.readLine()) != null) {
                    if (StringUtils.hasText(l) && !l.startsWith("#")) {
                        int i = l.indexOf('=');
                        int j = 0;
                        if (isWhitespace(l.charAt(i - 1))) {
                            if (isWhitespace(l.charAt(i + 1))) {
                                j = 1;
                            } else {
                                throw new IllegalRuleFormatException(
                                    "either use no whitespace around '=' or put"
                                    + " whitespace on both sides"
                                );
                            }
                        }
                        m_rules.add(new TextRule(
                            this,
                            l.substring(0, i - j),
                            l.substring(i + j + 1))
                        );
                    }
                    
                    lineNumber++;
                }
            } catch (IOException e) {
                throw new RuntimeException("reading from ressource failed", e);
            } catch (IllegalRuleFormatException e) {
                e.m_lineNumber = lineNumber;
                throw e;
            }
        }
        Collections.reverse(m_rules);
    }
    
    /** Utility class representing a pointer into a string. */
    static class Cursor {
        /** 
         * The character that should be returned by reads beyond the end of the
         * string.
         */
        public char eosChar;
        
        /** The backing string. */
        public String string;
        
        /** The location in the backing string. */
        public int loc;
        
        /** Reads a character and advances the location.
         * @return the character read, or {@link #eosChar} if this cursor
         *         points beyond the end of the stream.
         */
        public char read() { 
            try {
                return string.charAt(loc++);                
            } catch (StringIndexOutOfBoundsException e) {
                return eosChar;
            }
        }
        
        /** Returns the character this cursor points to. */
        public char peek() { return string.charAt(loc); }
        
        /** Returns the substring between the string locations pointed to by
         * {@code this} and {@code c}.
         * @param c .
         * @return .
         */
        public String textTo(Cursor c) { return string.substring(loc, c.loc); }
        
        /** Returns {@code true} iff the end of the string has been reached. */
        public boolean eos() { return loc == string.length(); }
        
        /** {@inheritDoc} */
        public Cursor clone() {
            Cursor copy = new Cursor();
            copy.string = string;
            copy.loc = loc;
            copy.eosChar = eosChar;
            return copy;
        }
        
        /** {@inheritDoc} */
        public String toString() {
            return "\"" + string + "\" @ " + loc; 
        }
    }

    /** Represents a rewriting rule. */
    public static interface Rule {
        /**
         * Attempts to rewrite the key with this rule and the provided
         * user arguments. If successful, the resolved message is appended to
         * {@code target}, which is then returned. If unsuccessful, this method
         * does not incur observable side effects and returns {@code null}.
         * 
         * @param key the key to rewrite 
         * @param arguments the user arguments (used to replace {0}, {1}, ...)
         * @param target the StringBuffer to append to
         * @return {@code target} if successful, {@code null} otherwise
         */
        StringBuffer rewrite(String key, Object[] arguments, 
                            StringBuffer target);
    }
    
    /** Represents a rewriting rule having a textual representation. */
    static final class TextRule implements Rule {
        /** The message format performing the rewrite.
         * 
         * <p>The argument array
         * expected consists of three parts. First, there are the standard 
         * MessageFormat arguments referenced by the replacement string. Then,
         * there are the values for the named variables defined in the rule,
         * and finally, there is one lookup helper variable keeping a reference
         * to the argument array itself.
         * 
         * <p>The formats used by standard arguments and named variables are
         * not tampered with. Every use of the special lookup helper variable
         * is given a LookupFormatter that uses the argument array to
         * format its key, and then looks up the associated value in the lookup
         * context. 
         * */
        private MessageFormat m_format;
        
        /** The context to be used for resolving key look ups. */
        private MessageRewriter m_lookupContext;
        
        /** Contains the parts of this rule's pattern after slicing at '.'. */
        private String[] m_patternParts;
        
        /**
         * Translation array mapping variable numbers to the part id of the
         * declaring pattern part.
         */
        private int[] m_variableLocations;

        /** Number of user variables used by the rule. User variables are {0},
         * {1}, ... that are replaced by called-supplied paramters. */
        private int m_userVariables;
        
        /**
         * Constructor.
         * @param lookupContext the object to use for key lookups
         * @param pattern the pattern string
         * @param replacement the replacement format string
         */
        private TextRule(MessageRewriter lookupContext,
                     String pattern, String replacement) {
            
            m_lookupContext = lookupContext;
            m_patternParts = qualifiers(pattern);
            
            int[] varloc = new int[m_patternParts.length];
            int varnum = 0;
            for (int i = 0; i < m_patternParts.length; i++) {
                if (isVariableName(m_patternParts[i])) {
                    varloc[varnum++] = i;
                }
            }
            m_variableLocations = new int[varnum];
            System.arraycopy(varloc, 0, m_variableLocations, 0, varnum);
            
            m_userVariables = 0;
            int i = 0;
            while ((i = replacement.indexOf('{', i)) != -1) {
                i++;
                int j = i;
                while (j < replacement.length() 
                    && Character.isDigit(replacement.charAt(j))) {
                    
                    j++;
                }
                if (j > i) {
                    int id = Integer.parseInt(replacement.substring(i, j));
                    if (id + 1 > m_userVariables) {
                        m_userVariables = id + 1;
                    }
                }
            }

            Cursor cursor = new Cursor();
            cursor.string = replacement;
            cursor.loc = 0;
            cursor.eosChar = '}';
            m_format = getMessageFormat(cursor.clone(), cursor);
            if (!cursor.eos()) {
                throw new IllegalArgumentException(
                    "closing brace before opening one"
                );
            }   
        }

        
        /** parses the contents of a non-messageformat, curly brace, appending
         * the translation to MessageFormat's syntax to sb and returning the
         * lookup format used for format the parameter or null, if there is
         * none. 
         * @param sb the format string for MessageFormat to be appended to
         * @param cursor points to the first character after the opening brace.
         *               will point to the corrsponding closing brace afterwards
         * @return see above
         */
        LookupFormat handleBrace(StringBuilder sb, Cursor cursor) {
            Cursor begin = cursor.clone();
        loop:
            while (true) {
                switch (cursor.read()) {
                case '{':
                case '}':
                    break loop;
                }
            }
            cursor.loc--;
            
            int varid = variableId(begin.textTo(cursor));
            if (varid == -1) {
                // key lookup
                sb.append(m_userVariables + m_variableLocations.length);

                LookupFormat sf = new LookupFormat();
                sf.m_backing = m_lookupContext;
                sf.m_keyFormatter = getMessageFormat(begin, cursor);
                return sf;
            } else {
                // variable lookup
                sb.append(m_userVariables + varid);
                return null;
            }
        }
        
        /**
         * Parses replacement text and constructs a MessageFormat that
         * can format the replacement text.
         * @param begin points to the start of the text to be processed
         * @param cursor points to some location such that begin.textTo(cursor)
         *               is a literal
         * @return the MessageFormat constructed. See {@link #m_format} 
         */
        // assembles a MessageFormat string by mapping variable lookups to
        // MessageFormat variables and key lookups to one special variable
        // for which custom formatters (see LookupFormatter) are registered.
        MessageFormat getMessageFormat(Cursor begin, Cursor cursor) {
            Map<Integer, LookupFormat> subFormats
                = new HashMap<Integer, LookupFormat>();
            int currentFormatElementId = 0;
            StringBuilder sb = new StringBuilder();
            
        loop:       
            while (true) {
                switch (cursor.read()) {
                    case '{':
                        if (isDigit(cursor.peek())) {
                            // MessageFormat can handle it
                            while (cursor.read() != '}') { }
                        } else {
                            sb.append(begin.textTo(cursor));
                            LookupFormat sf = handleBrace(sb, cursor);
                            if (cursor.eos()) {
                                throw new IllegalRuleFormatException(
                                    "missing '}'"
                                );
                            }
                            begin = cursor.clone();
                            cursor.loc++;
                            if (sf != null) {
                                subFormats.put(currentFormatElementId, sf);
                            }
                        }
                        currentFormatElementId++;
                        break;
                    case '}':
                        break loop;
                }
            }
            cursor.loc--;
            sb.append(begin.textTo(cursor));
            
            MessageFormat mf = new MessageFormat(sb.toString());
            for (Map.Entry<Integer, LookupFormat> entry 
                : subFormats.entrySet()) {
                
                mf.setFormat(
                    entry.getKey(),
                    entry.getValue()
                );
            }
            return mf;
        }
        
        /** {@inheritDoc} */
        public StringBuffer rewrite(String key, Object[] arguments, 
                             StringBuffer target) {
            String[] keyparts = qualifiers(key);
            if (keyparts.length == m_patternParts.length) {
                // check matching
                for (int i = 0; i < keyparts.length; i++) {
                    String pp = m_patternParts[i];
                    if (!(isVariableName(pp) || pp.equals(keyparts[i]))) {
                        return null;
                    }
                }
                
                // it matches --> prepare args
                Object[] args = new Object[
                    m_userVariables 
                    + m_variableLocations.length
                    + 1
                ];
                System.arraycopy(arguments, 0, args, 0, m_userVariables);
                for (int i = 0; i < m_variableLocations.length; i++) {
                    args[m_userVariables + i] = keyparts[m_variableLocations[i]];
                }
                args[m_userVariables + m_variableLocations.length] = args;
                
                // let the message format work
                try {
                    return m_format.format(args, target, new FieldPosition(0));
                } catch (NoSuchMessageException e) {
                    throw new RewritingFailedException(key, this, e);
                }
            } else {
                return null;
            }
        }
        
        /** {@inheritDoc} */
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String pp : m_patternParts) {
                sb.append(pp).append('.');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" = ...");
            return sb.toString();
        }
        
        /** returns the id of the variable named {@code varname}, or -1 if no
         * such variable exists.
         * @param varname .
         * @return see above
         */
        private int variableId(String varname) {
            for (int i = 0; i < m_variableLocations.length; i++) {
                String pp = m_patternParts[m_variableLocations[i]];
                String vn = pp.substring(1, pp.length() - 1);
                if (vn.equals(varname)) {
                    return i;
                }
            }
            return -1;
        }
        
        /** Parser utility function returning true iff {@code n} is a variable
         * declaration.
         * @param n .
         * @return .
         */
        private static boolean isVariableName(String n) {
            return n.charAt(0) == '{' && n.charAt(n.length() - 1) == '}';
        }        
    }
    
    /** 
     * A Format that looks up a value in the backing RewritingRessourceBundle,
     * using a key obtained by formatting the supplied object.
     */
    static class LookupFormat extends Format {
        /** The key formatter. */
        MessageFormat m_keyFormatter;
        
        /** The RewritingRessourceBundle used for look ups. */ 
        MessageRewriter m_backing;
        
        /** {@inheritDoc} */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, 
                                   FieldPosition pos) {
            return m_backing.resolveAndAppend(
                m_keyFormatter.format(obj),
                EMPTY,
                toAppendTo
            );
        }

        /** not implemented. */
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            Reject.ifFalse(false, "not implemented");
            return null;
        }
    }
    
    /** returns the qualifiers in s, i.e. the list of maximal substrings not
     * containing '.'.
     * @param s the fully qualified name
     * @return .
     */
    public static String[] qualifiers(String s) {
        return qualifiers(s, 0);
    }
    
    /** Helper to {@link #qualifiers(String)}. */
    private static String[] qualifiers(String s, int depth) {
        int i = s.indexOf(".");
        String[] r;
        if (i == -1) {
            i = s.length();
            r = new String[depth + 1];
        } else {
            r = qualifiers(s.substring(i + 1), depth + 1);
        }
        r[depth] = s.substring(0, i);
        return r;
    }

    
    /** 
     * Adds the provided rules to the list of rewriting rules used.
     */
    public void add(Rule...rules) {
        m_rules.addAll(Arrays.asList(rules));
    }
    
    /** 
     * Adds the provided rules to the list of rewriting rules used.
     */    
    public void add(List<? extends Rule> rules) {
        m_rules.addAll(rules);        
    }

    
    /** resolves the value of the provided key with the provided user
     * arguments and appends the result to target, or throws 
     * a {@link NoDefinitionException} if no rule applies to the
     * key.
     * @param key .
     * @param arguments the user arguments. must not be null, but may be empty.
     * @param target .
     * @return {@code target}
     */
    public StringBuffer resolveAndAppend(String key, Object[] arguments,
                                         StringBuffer target) {
        Object[] args = arguments == null ? EMPTY : arguments;
        for (Rule r : m_rules) {
            StringBuffer sb = r.rewrite(key, args, target);
            if (sb != null) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug(key + " -> " + sb);
                }
                return sb;
            }
        }
        throw new NoDefinitionException(key);
    }
    
    
    /** returns the value associated with the given key, using the supplied
     * arguments, or throws a NoDefinitionException, if the key could not be
     * resolved.
     */
    public String resolve(String key, Object[] arguments) {
        return resolveAndAppend(key, arguments, new StringBuffer()).toString();
    }
    
    /** Like {@link #resolve(String, Object[])} with empty argument list. */ 
    public String resolve(String key) {
        return resolve(key, EMPTY);
    }
    
    /**
     * Thrown to indicate that the matching rule failed to rewrite the key.
     */
    static class RewritingFailedException extends NoSuchMessageException {
        String m_key;
        NoSuchMessageException m_cause;
        Rule m_at;        

        /**
         * Constructor.
         * @param key the key that should have been rewritten
         * @param at  the rule that attempted to rewrite
         * @param cause the cause the rewrite failed
         */
        public RewritingFailedException(String key, Rule at, 
                                        NoSuchMessageException cause) {
            super(key);
            m_key = key;
            m_at = at;
            m_cause = cause;
        }
        
        /** Prints an evaluation stack trace. */
        public String toString() {
            return m_cause.toString()
                + "\n    while evaluating \'" + m_key + '\''
                + "\n          using rule \'" + m_at + '\'';
        }
    }
    
    /** 
     * thrown to indicate that there is no matching rule for the requested key.
     **/
    public static class NoDefinitionException extends NoSuchMessageException {
        /***/
        String m_key;
        
        /***/
        public NoDefinitionException(String key) {
            super(key);
            m_key = key;
        }

        /** {@inheritDoc} */
        public String toString() {
            return "No definition found for key \'" + m_key + "\'.";
        }
    }
    
    /** Thrown if a rule definition's format was not understood. */
    static class IllegalRuleFormatException extends IllegalArgumentException {
        /** The line this exception occured at. */
        int m_lineNumber;
        
        /**
         * Constructor.
         * @param msg the message
         */
        IllegalRuleFormatException(String msg) {
            super(msg);
        }
        
        /** {@inheritDoc} */
        public String toString() {
            return super.toString() + " on line " + m_lineNumber;
        }
    }
}
