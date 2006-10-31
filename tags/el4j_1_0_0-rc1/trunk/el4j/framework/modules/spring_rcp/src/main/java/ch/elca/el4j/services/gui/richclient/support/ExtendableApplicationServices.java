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
package ch.elca.el4j.services.gui.richclient.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.richclient.application.support.DefaultApplicationServices;

public class ExtendableApplicationServices extends DefaultApplicationServices 
                                        implements InitializingBean {
    
    /** The list of extender bean names. */
    private List<String> m_extenderNames = new ArrayList<String>();
    
    /** 
     * Whether to enforce referential integrity by throwing an exception
     * if registered extenders are not found.
     */
    private boolean m_enforceReferentialIntegrity = false;
    
    /**
     * See {@link #m_enforceReferentialIntegrity}.
     */
    public boolean isEnforceReferentialIntegrity() {
        return m_enforceReferentialIntegrity;
    }

    /**
     * See {@link #m_enforceReferentialIntegrity}.
     */
    public void setEnforceReferentialIntegrity(boolean enforceReferentialIntegrity) {
        m_enforceReferentialIntegrity = enforceReferentialIntegrity;
    }

    /**
     * See {@link #m_extenderNames}.
     */
    public List<String> getExtenderNames() {
        return m_extenderNames;
    }

    /**
     * See {@link #m_extenderNames}.
     */
    public void setExtenderNames(List<String> extenderNames) {
        m_extenderNames = extenderNames;
    }

    public void afterPropertiesSet() throws Exception {
        for (String bn : m_extenderNames) {
            try {
                ApplicationContext ac 
                    = (ApplicationContext) getService(ApplicationContext.class);
                Extender ex = (Extender) ac.getBean(bn);
                setRegistryEntries(ex.m_services);
            } catch (NoSuchBeanDefinitionException e) {
                if (m_enforceReferentialIntegrity) {
                    throw e; 
                }
            }
        }
    }
    
    public static class Extender {
        private Map<?, ?> m_services;
        
        public void setServices(Map<?, ?> services) {
            m_services = services;
        }
    }
}