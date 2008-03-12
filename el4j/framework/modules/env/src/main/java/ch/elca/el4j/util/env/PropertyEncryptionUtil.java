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
package ch.elca.el4j.util.env;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import ch.elca.el4j.core.context.ModuleApplicationContext;
import ch.elca.el4j.util.encryption.AbstractPropertyEncryptor;
import ch.elca.el4j.util.encryption.PasswordSource;

/**
 * Utility to load a custom key from file (cryptor.properties). This is in a
 * separate class to make it accessible from Override- and
 * Placeholder-configurer and avoid code duplication. <script
 * type="text/javascript">printFileStatus ("$URL:
 * https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml
 * $", "$Revision: 2754 $", "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008)
 * $", "$Author: swismer $" );</script>
 * 
 * @author David Bernhard (DBD)
 */
public class PropertyEncryptionUtil {

    /**
     * The logger.
     */
    private static Log s_logger = LogFactory
        .getLog(ModuleApplicationContext.EL4J_DEBUGGING_LOGGER);
    
    /**
     * The default cryptor configuration file.  
     */
    protected String m_defaultConfigFile = "classpath:cryptor.properties";
 
    /**
     * This is used to see if the internal cryptor class is available.
     */
    private ClassLoader m_cl;

    /**
     * The classloader returns type Class which we save and cast later on.
     */
    @SuppressWarnings("unchecked")
    private Class m_c;

        /**
     * Status of the encryption module. We need this because
     * we cannot initialize in a constructor
     * but must wait for spring to set a property in the caller. 
     * Therefore an init() method is provided.
     */
    private enum CryptStatus {
        
        /** We have not called init() yet to check if we are in internal. */
        UNINITED, 
        
        /** init() found no cryptor. */
        EXTERNAL, 
        
        /** init found cryptor and all is ready. */
        ACTIVE, 
        
        /** Turned off by deactivate(). */
        DEACTIVATED
    };
    
    /**
     * Stores the current status.
     */
    private CryptStatus m_status;

    /**
     * The cryptor object which provides encryption.
     */
    private AbstractPropertyEncryptor m_cryptor;

    /**
     * An optional source for a true custom password.
     */
    private PasswordSource m_source;

    /**
     * Flag to indicate whether m_source is used.
     */
    private boolean m_useSource = false;

    /**
     * Constructor. We cannot do init() here due to the caller not having all 
     * properties at construction time.
     */
    public PropertyEncryptionUtil() {
        m_status = CryptStatus.UNINITED;
    }

    /**
     * @return Whether init() has been called. 
     */
    public boolean isInited() {
        return !m_status.equals(CryptStatus.UNINITED);
    }

    /**
     * @return Whether we can do encryption.
     */
    public boolean isActive() {
        return m_status.equals(CryptStatus.ACTIVE);
    }

    /**
     * Deactivate encryption features.
     */
    public void deactivate() {
        m_status = CryptStatus.DEACTIVATED;
    }

    /**
     * Set up encryption. We need to pass ctx to access resources.
     * @param ctx ModuleApplicationContext
     */
    public void init(ApplicationContext ctx) {
        init(ctx, m_defaultConfigFile);
    }

    /**
     * @param ctx ModuleApplicationContext
     * @param configFile Custom config file location.
     */
    public void init(ApplicationContext ctx, String configFile) {

        if (m_status != CryptStatus.UNINITED) {
            throw new RuntimeException(
                "You cannot call init() more than once.");
        }

        /* Check whether we are in internal. */
        m_cl = Thread.currentThread().getContextClassLoader();
        try {
            m_c = m_cl
                .loadClass("ch.elca.el4j.modules.encryption.PropertyEncryptor");
            m_cryptor = (AbstractPropertyEncryptor) m_c.newInstance();
        } catch (ClassNotFoundException e) {
            // An exception landing us here means we are in external.
            m_status = CryptStatus.EXTERNAL;
            return;
        } catch (Exception e) {
            throw new RuntimeException("Error initializing cryptor.");
        }

        /*
         * We have a cryptor - set it up. Read password to use from
         * configuration file.
         */

        m_status = CryptStatus.ACTIVE;

        s_logger.info("Trying to read cryptor config file: " + configFile);

        Properties p = new Properties();
        Resource res = ctx.getResource(configFile);
        if (!res.exists()) {
            s_logger
                .error("The config file " + configFile + " does not exist.");
        }

        try {
            //File file = res.getFile();
            p.load(res.getInputStream());
        } catch (Exception e) {
            s_logger.error("Config file " + configFile + " is not accessible.");
            e.printStackTrace();
        }

        if (p.containsKey("cryptor.passwordSource")
            && p.containsKey("cryptor.customPassword")) {
            String source = p.getProperty("cryptor.passwordSource");
            String custom = p.getProperty("cryptor.customPassword");

            try {
                if (source.equals("mixed")) {
                    String key = m_cryptor.decrypt(custom);
                    m_cryptor.deriveKey(key);
                } else if (source.equals("custom")) {
                    if (m_useSource) {
                        m_cryptor.deriveKey(this.m_source.getPassword());
                    } else {
                        // If no source defined but using a custom password,
                        // error.
                        s_logger.error("Internal password mode set to custom"
                            + "but no passwordSource defined.");
                    }
                }
                s_logger.info("Success reading file.");
            } catch (Exception e) {
                s_logger.error("Error reading config file.");
            }
        } else {
            s_logger.error("Config file must contain entries "
                + "cryptor.passwordSource and " + "cryptor.customPassword.");
        }
    }

    /**
     * Called to get the cryptor object managed by this class.
     * @return An initialized cryptor.
     */
    public AbstractPropertyEncryptor getCryptor() {
        if (!isActive()) {
            throw new RuntimeException("You can only get a cryptor in the "
                + "ACTIVE state.");
        }
        return m_cryptor;
    }

    /**
     * @param source A custom PasswordSource
     */
    public void setSource(PasswordSource source) {
        // As we might not be init()ed yet, just save the source -
        // and set a flag.
        this.m_source = source;
        m_useSource = true;
    }

}