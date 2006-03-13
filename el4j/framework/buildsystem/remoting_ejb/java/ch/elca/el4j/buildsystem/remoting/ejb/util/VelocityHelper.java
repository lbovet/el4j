/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.buildsystem.remoting.ejb.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;


/**
 * Helper class for dealing with Velocity templates.
 *
 * <p/><b>copied from the Eclipse plugin.</b>
 * TBD: replace it with the velocity EL4Ant plugin as soon as it's available.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *   "$Revision$", "$Date$", "$Author$"
 * );</script>
 *
 * @author Jacques-Olivier Haenni (JOH)
 */
public class VelocityHelper {

    /** Velocity engine. */
    private static VelocityEngine s_engine;
    
    /** Project reference to enable logging. */
    private Project m_project;

    /** Task reference to improve logging. */
    private Task m_task;

    /**
     * Setter called before generation to enable logging.
     *
     * @param project the Ant <code>Project</code> instance
     * @param task the Ant <code>Task</code> instance
     */
    public void setProjectLogger(Project project, Task task) {
        m_project = project;
        m_task = task;
    }

    /**
     * Log a message with Ant logging facilities.
     *
     * @param message the message to log
     * @param msgLevel the Ant logging level
     */
    protected void log(String message, int msgLevel) {
        m_project.log(m_task, message, msgLevel);
    }


    /**
     * Write data to a disk file.
     */
    public void writeDataToFile(File file, String templateName,
                                Context context) {
        // File creation, if necessary
        try {
            if (!file.exists()) {
                m_project.log("Creating file " + file + ".", Project.MSG_DEBUG);
                file.createNewFile();
            }
        } catch (IOException ioe) {
            throw new BuildException("Unable to create the file " + file, ioe);
        }

        if (!file.canWrite()) {
            throw new BuildException("Unable to modify the file " 
                + file + " because it is set read only.");
        }

        // Write the content to disk
        VelocityEngine engine = getConfiguredVelocityEngine();
        Writer writer = null;
        try {
            m_project.log("Writing file " + file + ".", Project.MSG_VERBOSE);
            writer = new FileWriter(file);
            engine.init();
            engine.mergeTemplate(templateName, "UTF-8", context, writer);
        } catch (IOException ioe) {
            throw new BuildException("Cannot write the file " + file, ioe);
        } catch (ResourceNotFoundException rnfe) {
            throw new BuildException("Cannot found the template "
                                     + templateName);
        } catch (Exception e) {
            throw new BuildException("An unknown error has occurred with "
                                     + "the Velocity engine.", e);
        } finally {
            try {
                writer.close();
            } catch (IOException ioe) {
                // ignore the exception
            }
        }
    }


    /**
     * @return Returns a properly configured (but not initialized) Velocity
     *      engine.
     */
    private VelocityEngine getConfiguredVelocityEngine() {
        if (s_engine == null) {
            s_engine = new VelocityEngine();
            s_engine.setProperty("runtime.log.logsystem", new VelocityLogger());
            s_engine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader."
                + "ClasspathResourceLoader");
            s_engine.setProperty("resource.loader", "class");
        }
        return s_engine;
    }


    /**
     * This class is used to redirect the Velocity logs into the Ant logs.
     */
    public class VelocityLogger implements LogSystem {

        /**
         * {@inheritDoc}
         */
        public void init(RuntimeServices rs) throws Exception {
        }

        /**
         * {@inheritDoc}
         */
        public void logVelocityMessage(int level, String message) {
            if (level == LogSystem.DEBUG_ID) {
                m_project.log("<D> " + message, Project.MSG_DEBUG);
            } else if (level == LogSystem.ERROR_ID) {
                m_project.log("<E> " + message, Project.MSG_VERBOSE);
            } else if (level == LogSystem.INFO_ID) {
                m_project.log("<I> " + message, Project.MSG_DEBUG);
            } else if (level == LogSystem.WARN_ID) {
                m_project.log("<W> " + message, Project.MSG_VERBOSE);
            } else {
                m_project.log("<?> " + message, Project.MSG_DEBUG);
            }
        }
    }
}
