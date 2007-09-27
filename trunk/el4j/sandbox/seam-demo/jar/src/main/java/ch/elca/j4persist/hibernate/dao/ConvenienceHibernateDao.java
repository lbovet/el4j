package ch.elca.j4persist.hibernate.dao;

import ch.elca.j4persist.generic.dao.ConvenienceGenericDao;

public interface ConvenienceHibernateDao extends ConvenienceGenericDao {
    public void setPersistentClass(Class c);
    public Class getPersistentClass();
}
