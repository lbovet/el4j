/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.jpa.dao;

import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 *
 * Convenience JPA dao support class to be able to return the convenience
 * JPA template without casting it.
 *
 * @svnLink $Revision: 3873 $;$Date: 2009-08-04 13:59:45 +0200 (Di, 04 Aug 2009) $;$Author: swismer $;$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/hibernate/src/main/java/ch/elca/el4j/services/persistence/hibernate/dao/ConvenienceHibernateDaoSupport.java $
 *
 * @author Simon Stelling (SST)
 */
public class ConvenienceJpaDaoSupport extends JpaDaoSupport {
	
	/**
	 * @return Returns the Hibernate template casted to the convenience model of
	 *         it.
	 */
	public ConvenienceJpaTemplate getConvenienceJpaTemplate() {
		return (ConvenienceJpaTemplate) getJpaTemplate();
	}
	
	/**
	 * @param template
	 *            Is the convenience Hibernate template to set.
	 */
	public void setConvenienceJpaTemplate(
		ConvenienceJpaTemplate template) {
		setJpaTemplate(template);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected JpaTemplate createJpaTemplate(EntityManagerFactory emf) {
		return new ConvenienceJpaTemplate(emf);
	}
}
