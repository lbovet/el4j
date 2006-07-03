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
package ch.elca.el4j.services.richclient.naming;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.lang.Character.isWhitespace;

/**
 * A rewriting rule providing a set of utility functions. The keys accepted
 * by this rule are of the form
 * 
 * <pre><i>namespace</i> <i>function</i> . <i>argument</i> </pre>
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
public class Utilities implements MessageRewriter.Rule {
    /** A utility function. */
    protected abstract static class Function {
        /** The function's name. */
        private String m_name;
        
        /** 
         * Constructor.
         * @param name the function's name
         */
        protected Function(String name) {
            m_name = name;
        }
        
        /** 
         * Applies this utility function to {@code argument}, appending the 
         * result to {@code target}.
         */
        abstract void apply(String argument, StringBuffer target);
    }
    

    private static void changeCapitalization(String argument, boolean toUpper, 
                                             StringBuffer target) {
        
        int i = 0;
        do {
            target.append(
                toUpper ? toUpperCase(argument.charAt(i))
                        : toLowerCase(argument.charAt(i))
            );
            i++;
            
            int j;
            for (j = i; j < argument.length(); j++) {
                if (isWhitespace(argument.charAt(j))) {
                    break;
                }
            }
            target.append(argument.substring(i, j));
            i = j;                   
        } while (i < argument.length());
    }
    
    /** Default functions. */
    private static Function[] s_defaultFunctions = {
        new Function("decapitalize") {
            @Override void apply(String argument, StringBuffer target) {
                changeCapitalization(argument, false, target);
            }
        },
        new Function("capitalize") {
            @Override void apply(String argument, StringBuffer target) {
                changeCapitalization(argument, true, target);
            }
        }
    };
    
    
    /** The package-like prefix to function names. Includes delimiter. */
    protected String m_requiredPrefix;

    /** the functions provided by this instance, keyed by function.m_name. */
    protected Map<String, Function> m_functions 
        = new HashMap<String, Function>();
    
    /**
     * Creates a utility function package called {@code Utils} and 
     * adds default utility functions.
     */
    public Utilities() {
        this("Utils.");
    }
    
    /** 
     * Constructor. Adds default utility functions.
     * @param packageName the package name as used to form keys.
     */
    public Utilities(String packageName) {
        m_requiredPrefix = packageName;
        for (Function f : s_defaultFunctions) {
            add(f);
        }
    }
    
    /** Adds a function, replacing any previous function with the same name. */
    public void add(Function f) {
        m_functions.put(f.m_name, f);
    }
    
    /** 
     * removes a function. No effect if no matching function is present.
     * @param functionName the name of the function to be removed
     */
    public void remove(String functionName) {
        m_functions.remove(functionName);
    }
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer rewrite(String key, Object[] arguments, 
                                StringBuffer target) {

        if (!key.startsWith(m_requiredPrefix)) { return null; }

        int i = key.indexOf('.', m_requiredPrefix.length());
        if (i == -1) { return null; }

        String functionName = key.substring(m_requiredPrefix.length(), i);
        String argument = key.substring(i + 1);

        Function function = m_functions.get(functionName);
        if (function == null) { return null; }
        
        function.apply(argument, target);
        return target;
    }
}
