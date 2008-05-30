package ch.elca.el4j.services.xmlmerge;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;

/**
 * Holds thread local context information that would otherwise be difficult to pass into
 *  each part of the framework. Is intentionally designed to be extensible.
 *  
 *  CAVEAT:
 *     * Do not abuse. It could be used as general thread-local global variables
 *     * There is a potential security risk here (do not put confidential info in here)
 *  
 * @author Philipp H. Oser (POS)
 *
 */
public class XmlMergeContext {

    protected static final String ENTITY_RESOLVER_KEY = "entityResolver";
    
    protected static ThreadLocal<Map<String,Object>> m_context = new ThreadLocal<Map<String,Object>>() {
        protected Map<String,Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };
    
    public static EntityResolver getEntityResolver() {
        if (m_context.get().containsKey(ENTITY_RESOLVER_KEY)) {
            return (EntityResolver) m_context.get().get(ENTITY_RESOLVER_KEY);
        }
        return null;
    }
    
    public static void setEntityResolver(EntityResolver er) {
        m_context.get().put(ENTITY_RESOLVER_KEY, er);
    }
    
}
