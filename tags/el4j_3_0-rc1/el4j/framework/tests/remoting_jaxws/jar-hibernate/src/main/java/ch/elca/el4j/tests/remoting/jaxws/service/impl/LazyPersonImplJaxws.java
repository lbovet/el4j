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
package ch.elca.el4j.tests.remoting.jaxws.service.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.sun.xml.ws.developer.UsesJAXBContext;

import ch.elca.el4j.services.persistence.generic.dao.DaoRegistry;
import ch.elca.el4j.services.persistence.hibernate.dao.ConvenienceGenericHibernateDao;
import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.services.remoting.jaxb.hibernate.JAXBContextFactoryImpl;
import ch.elca.el4j.tests.person.dao.PersonDao;
import ch.elca.el4j.tests.person.dao.impl.hibernate.GenericHibernatePersonDaoInterface;
import ch.elca.el4j.tests.person.dom.Brain;
import ch.elca.el4j.tests.person.dom.Person;
import ch.elca.el4j.tests.remoting.jaxws.service.LazyPerson;

/**
 *
 * A service that returns {@link Person} objects that contain lazily loaded properties.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 *
 *
 * Recommended naming convention:
 * name = name of implemented core interface
 * serviceName = name of implemented core interface + "Service"
 */
@WebService(name = "LazyPerson",
	serviceName = "LazyPersonService",
	targetNamespace = "http://webservice.jaxws.remoting.tests.el4j.elca.ch/")
@UsesJAXBContext(JAXBContextFactoryImpl.class)
public class LazyPersonImplJaxws implements LazyPerson {
	
	/**
	 * The Person Dao.
	 */
	private GenericHibernatePersonDaoInterface personDao;
	
	/**
	 * The id (key) of the person.
	 */
	private int personId = -1;

	@Override
	@SuppressWarnings("unchecked")
	public Person getPerson(boolean loadBrain) {
		ConvenienceGenericHibernateDao<Person, Integer> dao
			= (ConvenienceGenericHibernateDao<Person, Integer>) personDao;
		if (personId == -1) {
			Person person = new Person("Peter Muster");
			Brain b = new Brain();
			b.setIq(99);
			person.setBrain(b);
			dao.saveOrUpdate(person);
			
			personId = person.getKey();
		}
		
		if (loadBrain) {
			try {
				DataExtent ex = new DataExtent(Person.class).with("brain");
				Person person = dao.findById(personId, ex);
				// avoid infinite cycle during marshaling
				person.getBrain().setOwner(null);
				return person;
			} catch (Exception e) {
				return null;
			}
		} else {
			return dao.findById(personId);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setPerson(Person person) {
		ConvenienceGenericHibernateDao<Person, Integer> dao
			= (ConvenienceGenericHibernateDao<Person, Integer>) personDao;
		dao.saveOrUpdate(person);
		personId = person.getKey();
	}

	/**
	 * @return    the Person Dao
	 */
	@WebMethod(exclude = true)
	public GenericHibernatePersonDaoInterface getPersonDao() {
		return personDao;
	}

	/**
	 * @param personDao    the Person Dao
	 */
	@WebMethod(exclude = true)
	public void setPersonDao(GenericHibernatePersonDaoInterface personDao) {
		this.personDao = personDao;
	}	
	
}
