/*
 * Copyright 2006 by ELCA Informatique SA
 * Av. de la Harpe 22-24, 1000 Lausanne 13
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ELCA Informatique SA. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with ELCA.
 */
package ch.elca.el4j.apps.refdb.dao.impl.hibernate;

import ch.elca.el4j.apps.refdb.dao.WorkElementDao;
import ch.elca.el4j.apps.refdb.dom.WorkElement;
import ch.elca.el4j.services.persistence.generic.dao.AutocollectedGenericDao;
import ch.elca.el4j.services.persistence.hibernate.dao.GenericHibernateDao;


/**
 * 
 * This class is a hibernate dao for the WorkElement entity.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author your name (???)
 */
@AutocollectedGenericDao("workElementDao")
public class HibernateWorkElementDao extends GenericHibernateDao<WorkElement, Integer> 
implements WorkElementDao{

	

}
