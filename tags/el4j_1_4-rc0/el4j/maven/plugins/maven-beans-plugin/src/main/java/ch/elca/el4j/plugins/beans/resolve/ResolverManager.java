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
package ch.elca.el4j.plugins.beans.resolve;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages resolving of files to classpath entries.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class ResolverManager implements Resolver {

    /** The logger. */
    private static final Log s_log = LogFactory.getLog(ResolverManager.class);
    
    /** Stores active resolvers. */
    private List<Resolver> m_resolvers;
    
    /**
     * Set up a ResolverManager.
     * @param classpath The classpath.
     */
    public ResolverManager(URL[] classpath) {
        m_resolvers = new LinkedList<Resolver>();
        
        /*
         * Put all resolvers here.
         */
        
        m_resolvers.add(new FileResolver(classpath));
        m_resolvers.add(new JarResolver(classpath));
    }
    
    /**
     * Check a single entry. 
     * @param file The file to check.
     * @return Whether this file should be included.
     */
    public boolean accept(String file) {
        Resolver r = getResolver(file);
        if (r != null) {
            return r.accept(file);
        } else {
            // No match.
            s_log.error("Couldn't resolve file " + file);
            return false;
        }
    }

    /**
     * Find the resolver that can handle this file.
     * @param file The file.
     * @return A resolver that can handle this file, or <code>null</code> if 
     * none exists.
     */
    private Resolver getResolver(String file) {
        int pos = file.indexOf(":");
        if (pos == -1) {
            return null;
        }
        String protocol = file.substring(0, pos);
        Resolver resolver = null;
        for (Resolver r : m_resolvers) {
            // Chain of command.
            if (protocol.equals(r.getProtocol())) {
                resolver = r;
                break;
            }
        }
        return resolver;
    }

    /** {@inheritDoc} */
    public String getProtocol() {
        return "";
    }

    /** {@inheritDoc} */
    public void copy(String file, File target) throws IOException {
        Resolver r = getResolver(file);
        if (r == null) {
            s_log.error("Couldn't resolve file " + file);
            return;
        }
        r.copy(file, target);
    }
}
