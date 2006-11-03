package ch.elca.el4j.services.persistence.hibernate.dao;



import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * Convenience Hibernate dao support class to be able to return the convenience
 * Hibernate template without casting it.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class ConvenienceHibernateDaoSupport extends HibernateDaoSupport {
      
    /**
     * @return Returns the Hibernate template casted to the convenience model of
     *         it.
     */
    public ConvenienceHibernateTemplate getConvenienceHibernateTemplate() {
        return (ConvenienceHibernateTemplate) getHibernateTemplate();
    }
    
    /**
     * @param template
     *            Is the convenience Hibernate template to set.
     */
    public void setConvenienceHibernateTemplate(
        ConvenienceHibernateTemplate template) {
        setHibernateTemplate(template);
    }
    
    /**
     * {@inheritDoc}
     */
    protected HibernateTemplate 
    createHibernateTemplate(SessionFactory sessionFactory) {
            return new ConvenienceHibernateTemplate(sessionFactory);
    }
}
