/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.keyword.dao;

import static ch.elca.el4j.services.search.criterias.CriteriaHelper.and;
import static ch.elca.el4j.services.search.criterias.CriteriaHelper.like;
import static ch.elca.el4j.services.search.criterias.CriteriaHelper.not;
import static ch.elca.el4j.services.search.criterias.CriteriaHelper.or;

import java.util.List;

import ch.elca.el4j.apps.keyword.dao.KeywordDao;
import ch.elca.el4j.apps.keyword.dom.Keyword;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.AndCriteria;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.services.search.criterias.NotCriteria;
import ch.elca.el4j.services.search.criterias.OrCriteria;
import ch.elca.el4j.services.search.criterias.Order;

/**
 * 
 * Test class for <code>HibernateKeywordDao</code>.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 * @author Philipp Oser (POS)
 */
public class HibernateKeywordDaoTest
    extends AbstractKeywordDaoTest {
    
    /**
     * {@inheritDoc}
     */
    protected String[] getIncludeConfigLocations() {
        return new String[] {
            "classpath*:mandatory/*.xml",
            "classpath*:mandatory/keyword/*.xml",
            "classpath*:scenarios/db/raw/*.xml",
            "classpath*:scenarios/dataaccess/hibernate/*.xml",
            "classpath*:scenarios/dataaccess/hibernate/keyword/*.xml",
            "classpath*:optional/interception/transactionJava5Annotations.xml"};
    }
   
    
    /**
     * This test inserts five keywords and performs different searchs on it.
     *  TODO For now, the full criteria converter only works with hibernate
     *   therefore we only put the test here.
     *   
     *   This method is a bit long (it shares the created keywords).
     */
    public void testSearchKeywordsHibernateSpecificForNow() {
        KeywordDao dao = getKeywordDao();
        Keyword keyword = new Keyword();
        keyword.setName("Java");
        keyword.setDescription("Java related documentation");
        Keyword keyword2 = new Keyword();
        keyword2.setName("XML");
        keyword2.setDescription("Xml related documentation");
        Keyword keyword3 = new Keyword();
        keyword3.setName("Ghost");
        keyword3.setDescription("");
        Keyword keyword4 = new Keyword();
        keyword4.setName("Chainsaw");
        keyword4.setDescription("Tool of Log4J to filter logfiles");
        Keyword keyword5 = new Keyword();
        keyword5.setName("Zombie");
        keyword5.setDescription("");
        
        keyword = dao.saveOrUpdate(keyword);
        keyword2 = dao.saveOrUpdate(keyword2);
        keyword3 = dao.saveOrUpdate(keyword3);
        keyword4 = dao.saveOrUpdate(keyword4);
        keyword5 = dao.saveOrUpdate(keyword5);
        
        // ---
        
        // return XML or Ghost objects
        QueryObject query = new QueryObject();
        query.addCriteria(new OrCriteria(ComparisonCriteria.equals("name","XML"),
                                         ComparisonCriteria.equals("name","Ghost")));
        
        List<Keyword> list = dao.findByQuery(query);
        assertEquals(
            "Search for XML or Ghost does not result in two"
            + " keywords.", 2, list.size());
        
        for (Keyword k : list) {
            if (!(k.equals(keyword2) || k.equals(keyword3))) {
                fail("Not expected keyword on search for XML or Ghost ");
            }
        }
        
        // --- testing not equals and standard AND-combination of queries
        
        query = new QueryObject();
        query.addCriteria(new ComparisonCriteria("name","Chainsaw","=","String"),
            new ComparisonCriteria("name","lkasdjflöasdjfa","!=","String"));
        list = dao.findByQuery(query);
        assertEquals(
            "Search for description like 'log4j' results not in one keyword.",
            1, list.size());
        
        for (Keyword k : list) {
            if (!k.equals(keyword4)) {
                fail("Not expected keyword on search for description "
                    + "like 'log4j'.");
            }
        }
        
        // --- this query just adds some "noise" around an existing query (see
        //      test in parent class)
        
        //!! take care: the 2 nested NotCriteria (in parenthesis below)
        //    do not work with derby (other databases were not tested)
        
        query = new QueryObject();
//        query.addCriteria(/*new NotCriteria(
//                             new NotCriteria( */
//                                 new AndCriteria(
//                                     new OrCriteria(new ComparisonCriteria("name","titi","=","String"), 
//                                                    new AndCriteria(LikeCriteria.caseInsensitive("name", "%host%"),
//                                                                    LikeCriteria.caseInsensitive("name", "%host%")))))/*))*/;
        
        query.addCriteria(
              new OrCriteria(
                  new AndCriteria(new NotCriteria(new ComparisonCriteria("name","Ghost","!=","String")), 
                                 (new OrCriteria(new NotCriteria(LikeCriteria.caseInsensitive("name", "%host%")),
                                                 LikeCriteria.caseInsensitive("name", "%host%"))))));
        
//        Criteria c = query.getAndCriterias();
//        System.out.println("");
//        System.out.println("Query is :"+c.getSqlWhereCondition());
//        DetachedCriteria hibernateCriteria = CriteriaTransformer.transform(query,Keyword.class);
//        
//        System.out.println("after transformation:");
//        DataDumper.addObjectDumper("org.hibernate.criterion.DetachedCriteria",new ReflectionObjectDumper());
//        DataDumper.addObjectDumper("org.hibernate.Criteria",new ReflectionObjectDumper());
//        DataDumper.addObjectDumper("extended byorg.hibernate.criterion.Restrictions",new ReflectionObjectDumper());
//        System.out.println("Query is :"+DataDumper.dump(hibernateCriteria));
         
        
        
        list = dao.findByQuery(query);
        assertEquals("Search for name like 'host' results not in one keyword.",
            1, list.size());
        
        for (Keyword k : list) {
            if (!k.equals(keyword3)) {
                fail("Not expected keyword on search for name like 'host'.");
            }
        }
        
        list = dao.findByQuery(new QueryObject());
        assertEquals("Search for empty name and description does not result in "
            + "five keywords.", 5, list.size());
        
        // ---
        
        // test paging with an empty query
        query = new QueryObject();
        query.addOrder(Order.desc("name"));
        
        // load them all together:
        List<Keyword> allEntriesInOrder = dao.findByQuery(query);
        
        
        // and load them one by one:
        query.setMaxResults(1);
        for (int i = 0; i < allEntriesInOrder.size(); i++) {
        	query.setFirstResult(i);
        	
        	List<Keyword> oneElementList = dao.findByQuery(query);
        	
        	assertEquals(" We expected only one element ", 1, oneElementList.size());
        	System.out.println(i+" element: "+oneElementList.get(0).getName());
        	System.out.println(i+" element: "+allEntriesInOrder.get(i).getName());
        	assertEquals(" wrong Element returned "+i,allEntriesInOrder.get(i), oneElementList.get(0));
        }
        
                
        
        // test paging 1:

        // should return 5 results (= all results)
        query = new QueryObject();

        int count = dao.findCountByQuery(query);
        
        assertEquals("Search count was wrong.", 5, count);
        
         
        
        // now we constraint them to 2
        query.setMaxResults(2);        
        
        list = dao.findByQuery(query);
        assertEquals("Search for name like '%' results not in one keyword.",
            2, list.size());
        
        // test paging 2:

        // now we select the last 2 (name field ordered alphabetically) 
        query.addOrder(Order.desc("name"));
        query.setFirstResult(3);
        
        list = dao.findByQuery(query);
        assertEquals("Search for name like '%' results not in one keyword.",
            2, list.size());
        
        for (Keyword k : list) {
            if (!(k.equals(keyword3) || k.equals(keyword4))) {
                System.out.println ("k.name="+k.getName());
                fail("Not expected keyword with paging test");
            }
        }
        

        // test convenience methods:
        
        query = new QueryObject();
      
        query.addCriteria(
            or(and(not(new ComparisonCriteria("name","Ghost","!=","String")), 
                   (or(not(like("name", "%host%")),
                       like("name", "%host%"))))));
            
      
      
      list = dao.findByQuery(query);
      assertEquals("Search for name like 'host' results not in one keyword.",
          1, list.size());
      
      for (Keyword k : list) {
          if (!k.equals(keyword3)) {
              fail("Not expected keyword on search for name like 'host'.");
          }
      }
      
      list = dao.findByQuery(new QueryObject());
      assertEquals("Search for empty name and description does not result in "
          + "five keywords.", 5, list.size());
        
    }
    
    /**
     * {@inheritDoc}
     */
    protected String[] getExcludeConfigLocations() {
        return null;
    }
    
}
