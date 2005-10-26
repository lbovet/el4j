/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.core.io.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

/**
 * This class resolves resources using a list of resource names to preserve
 * a specific order. A request is delegated to another {@link
 * org.springframework.core.io.support.ResourcePatternResolver), if there are no
 * resources found by this class. Hence the list of resource names does not
 * have to be exhaustive.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class ListResourcePatternResolverDecorator
    implements ResourcePatternResolver {

    /** Prefix for single classpath resources. */
    public static final String CLASSPATH_RESOURCE = "classpath:";
    
    /** The static logger. */
    private static Log s_logger = LogFactory.getLog(
            ListResourcePatternResolverDecorator.class);
    
    /** 
     * The resource pattern resolver, where unsatisfied requests are delegated
     * to.
     */
    private ResourcePatternResolver m_patternResolver;
    
    /** The (ordered) list of configuration locations. */
    private String[] m_locations;
    
    /** A path matcher providing a specific wildcard notation. */
    private PathMatcher m_pathMatcher;
    
    /** 
     * Whether to merge resources hold in the location list should be merged
     * with resources looked up in the file system.
     */
    private boolean m_mergeWithOuterResources = false;
    
    /**
     * Creates a new instance using a {@link
     * PathMatchingResourcePatternResolver} to delegate unresolved requests to
     * and an {@link AntPathMatcher} to interprete wildcard notations.
     * 
     * @param locationProvider
     *      The configuration location provider to get the locations from.
     */
    public ListResourcePatternResolverDecorator(
            ConfigLocationProvider locationProvider) {
        
        this(locationProvider,
                new PathMatchingResourcePatternResolver(),
                new AntPathMatcher());
    }
    
    /**
     * Creates a new instance, that is fully configured through constructor
     * arguments.
     * 
     * @param locationProvider
     *      The configuration location provider to use.
     *      
     * @param patternResovler
     *      The pattern resolver to delegate unresolved requests to.
     *      
     * @param pathMatcher
     *      The path matcher that interprets a specific wildcard notation.
     */
    public ListResourcePatternResolverDecorator(
            ConfigLocationProvider locationProvider,
            ResourcePatternResolver patternResovler,
            PathMatcher pathMatcher) {
        
        m_locations = locationProvider.getConfigLocations();
        m_patternResolver = patternResovler;
        m_pathMatcher = pathMatcher;
    }
    
    /**
     * @return Returns the path matcher that is used to interprete a specific
     *      wildcard notation.
     */
    protected PathMatcher getPathMatcher() {
        return m_pathMatcher;
    }

    /**
     * Sets a specific path matcher that is used to interprete a specific
     * wildcard notation.
     * 
     * @param pathMathcher
     *      The path matcher to set.
     */
    public void setPathMatcher(PathMatcher pathMathcher) {
        m_pathMatcher = pathMathcher;
    }

    /**
     * {@inheritDoc}
     */
    public Resource getResource(String location) {
        return m_patternResolver.getResource(location);
    }

    /**
     * @return Returns whether to merge resources hold in this classe's resource
     *      list with resources looked up in the file system.
     */
    public boolean isMergeWithOuterResources() {
        return m_mergeWithOuterResources;
    }

    /**
     * Sets whether resources that are hold in this classe's resource list have
     * to be merged with resources looked up in the file system. Resources
     * that are in the list have precedence.
     * 
     * @param mergeWithOuterResources
     *      <code>true</code> to merge file system resources with the ones hold
     *      in this classe's resource list. <code>false</code> to force
     *      closed-world assumption.
     */
    public void setMergeWithOuterResources(boolean mergeWithOuterResources) {
        m_mergeWithOuterResources = mergeWithOuterResources;
    }

    /**
     * {@inheritDoc}
     */
    public Resource[] getResources(String locationPattern) throws IOException {
        Resource[] resources;
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            String locationSubPattern
                = locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length());
            
            String[] locations;
            
            if (getPathMatcher().isPattern(locationSubPattern)) {
                locations = findAllMatchingResources(locationSubPattern);
            } else {
                locations = findAllClasspathResources(locationSubPattern);
            }

            resources = resolveResourceLocations(locations);
            
            if (m_mergeWithOuterResources || locations.length == 0) {
                // delegate to Spring's resource pattern resolver
                try {
                    resources = mergeResources(resources,
                        delegateResourcesLookup(locationPattern));
                } catch (IOException ioe) {
                    s_logger.error("Couldn't merge configuration locations.",
                            ioe);
                }
            }
            
        } else {
            resources = delegateResourcesLookup(locationPattern);
        }
        return resources;
    }

    /**
     * Resolves the given location into Resource objects, using delegation.
     * 
     * @param locationPattern
     *      The pattern to resolve.
     *      
     * @return Returns the list with resolved resources.
     * 
     * @throws IOException
     *      If an I/O error occurs.
     */
    private Resource[] delegateResourcesLookup(String locationPattern) 
        throws IOException {
        
        s_logger.info("Delegating resource lookup [" + locationPattern + "]");
        return m_patternResolver.getResources(locationPattern);
    }
    
    /**
     * Finds all resources that match the given location pattern that is 
     * interpreted by the path matcher which this class is configured for.
     * 
     * @param locationPattern
     *      The location pattern to match against (contains a particular
     *      wildcard notation).
     *      
     * @return Returns a list of location names that match the given pattern.
     */
    protected String[] findAllMatchingResources(String locationPattern) {
        List result = new ArrayList();
        for (int i = 0; i < m_locations.length; i++) {
            if (getPathMatcher().match(locationPattern, m_locations[i])) {
                result.add(m_locations[i]);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }
    
    /**
     * Finds all resources that do not contain any wildcards except the
     * <code>classpath*</code>.
     * 
     * @param locationPattern
     *      The location pattern to match resources against (without any
     *      particular wildcard notation).
     *      
     * @return Returns a list of location names that match the given pattern.
     */
    protected String[] findAllClasspathResources(String locationPattern) {
        List result = new ArrayList();
        for (int i = 0; i < m_locations.length; i++) {
            if (locationPattern.equals(m_locations[i])) {
                result.add(m_locations[i]);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }
    
    /**
     * Transforms a list of location names into a list of resources.
     * 
     * @param locations
     *      The list of location names.
     *      
     * @return Returns the list of resources referenced by the given location
     *      names.
     */
    private Resource[] resolveResourceLocations(String[] locations) {
        Resource[] resources = new Resource[locations.length];
        for (int i = 0; i < locations.length; i++) {
            resources[i] = getResource(CLASSPATH_RESOURCE + locations[i]);
        }
        return resources;
    }
    
    /**
     * Merges the two given list of resources with the former having higher
     * precedence, i.e. they are added to the resulting list's head. Items of
     * the latter list that are also member of the former are added only once.
     * 
     * @param former
     *      A list of resources that has higher precedence.
     *      
     * @param latter
     *      A list of resources that has lower precedence. Items that are
     *      already added by the former list are filtered.
     *      
     * @return Returns a list with the merged resources, having the former list
     *      at the resulting list's head. Items of the latter list are ignored,
     *      if they are already added by the former list.
     *      
     * @throws IOException
     *      If an I/O exception occurs.
     */
    private Resource[] mergeResources(Resource[] former, Resource[] latter)
        throws IOException {
        
        if (s_logger.isDebugEnabled()) {
            debugArray("former", former);
            debugArray("latter", latter);
        }
        
        ArrayList result = new ArrayList();
        for (int i = 0; i < latter.length; i++) {
            if (!result.contains(latter[i])) {
                result.add(latter[i]);
            }
        }
        return (Resource[]) result.toArray(new Resource[result.size()]);
    }
    
    /**
     * Debugs the given array.
     * 
     * @param msg
     *      An arbitrary message prepended to the array.
     *      
     * @param array
     *      The array to debug.
     */
    private void debugArray(String msg, Object[] array) {
        String s = StringUtils.arrayToCommaDelimitedString(array);
        s_logger.debug(msg + " [" + s + "]");
    }
}
