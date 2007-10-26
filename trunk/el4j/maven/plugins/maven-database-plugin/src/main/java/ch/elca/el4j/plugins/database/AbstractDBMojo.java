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
package ch.elca.el4j.plugins.database;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

import ch.elca.el4j.plugins.database.holder.DatabaseNameHolder;

/**
 * This class holds all fields and methods commmon to all database mojos, namely
 * the properties from the pom file, the Maven project and repository as well as
 * the DataHolder, which encapsulates all Database specific properties.
 * 
 * It provides methods to its sublcasses to start the derby NetworkServer and to
 * execute an action, i.e. extract and execute SQL statements from a given 
 * source path.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 *
 * @requiresProject true
 * @requiresDependencyResolution test
 */
public abstract class AbstractDBMojo extends AbstractMojo {
    
    // Checkstyle: MemberName off
    
    /**
     * Decides whether to wait after the container is started or to return 
     * the execution flow to the user.
     * 
     * @parameter expression="${db.wait}" default-value = "true"
     */
    private boolean wait;
    
    /**
     * Name of database to use (either db2 or oracle).
     * 
     * 
     */
    private String dbName;

    /**
     * Directory of tools (such as application servers or local 
     * dbs) in the project.
     * 
     * @parameter expression="${el4j.project.tools}"
     * @required
     */
    private String toolsPath;

    /**
     * The maven project from where this plugin is called.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
   
    /**
     * Local maven repository.
     * 
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository repository;
    
    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;
    
    /** 
     * @component
     */
    private ArtifactCollector collector;
    
    /**
     * @component
     */
    private ArtifactFactory factory;
    
    /**
     * @component
     */
    private ArtifactResolver artifactResolver;
    
    /**
     * The Dependency Graph Walker.
     */
    private DepGraphWalker graphWalker;

    /**
     * The Data Holder.
     */
    private DatabaseNameHolder m_holder;
    
    // Checkstyle: MemberName on
   
    /**
     * Returns true if database needs to be started.
     * 
     * @return Return whether database need to be started.
     */
    protected boolean needStartup() {
        /*
         * HACK: Because we only support derby and oracle, we can find out if we
         * have to start database by checking the head of the URL.
         * 
         * In case of an exception, start NetworkServer just to be on the
         * safe side.
         */
        try {
            String db = getHolder().getDbName();
            return (db == null || db.equalsIgnoreCase("db2"));
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Returns directory, where NetworkServer should be started. 
     * This will be the location, where the NetworkServer looks for databases.
     * 
     * @return Home Directory of NetworkServer
     */
    protected String getDerbyLocation() {
        return toolsPath + "/derby/derby-databases";
    }

    /**
     * @return If NetwerkServer is blocking or not
     */
    protected boolean hasToWait() {
        return wait;
    }

    /**
     * @return The Maven artifact repository
     */
    protected ArtifactRepository getRepository() {
        return repository;
    }

    /**
     * @return The maven project
     */
    protected MavenProject getProject() {
        return project;
    }

    /**
     * @return The Database name
     */
    protected String getDbName() {
        return dbName;
    }
    
    /**
     * 
     * @param value the database name
     */
    public void setDbName(String value) {
        this.dbName = value;
    }

    /**
     * @return The Dependency GraphWalker
     */
    protected DepGraphWalker getGraphWalker() {
        if (graphWalker == null) {
            graphWalker = new DepGraphWalker(repository, project, 
                artifactResolver, collector, artifactMetadataSource, factory);
        }
        return graphWalker;
    } 
   
    /**
     * @return The holder
     */
    private DatabaseNameHolder getHolder() {
        if (m_holder == null) {
            m_holder = new DatabaseNameHolder(
                repository, 
                project, 
                getGraphWalker(), 
                dbName);
        }
        return m_holder;
    }
}