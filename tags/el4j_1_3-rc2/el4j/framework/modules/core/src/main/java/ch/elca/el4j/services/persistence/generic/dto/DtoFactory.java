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
package ch.elca.el4j.services.persistence.generic.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.notification.CoreNotificationHelper;
import ch.elca.el4j.services.persistence.generic.primarykey.PrimaryKeyGenerator;

/**
 * This class is used to create dto instances. Each dto will get a reference to
 * the modification key generator. This is needed to enable optimistic locking.
 * A dto must be created with this dto factory, when the dto must be written to
 * database. If you only need a dto to get information from it and not to store
 * it in database you can create the dto directly. But recommended is to create
 * each dto with help of this dto factory.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @deprecated This class in no more used if using 
 *             <code>AbstractIntOptimisticLockingDto</code>.
 * @see AbstractIntOptimisticLockingDto
 * @author Martin Zeltner (MZE)
 */
public class DtoFactory implements InitializingBean {
    /**
     * Primary key generator to generate modification keys for dtos.
     */
    private PrimaryKeyGenerator m_modificationKeyGenerator;

    /**
     * @return Returns the modificationKeyGenerator.
     */
    public PrimaryKeyGenerator getModificationKeyGenerator() {
        return m_modificationKeyGenerator;
    }

    /**
     * @param modificationKeyGenerator
     *            Is the modificationKeyGenerator to set.
     */
    public void setModificationKeyGenerator(
            PrimaryKeyGenerator modificationKeyGenerator) {
        m_modificationKeyGenerator = modificationKeyGenerator;
    }

    /**
     * This method creates a new dto of the given class and fills it with a
     * modification key generator.
     * 
     * @param clazz
     *            Is the dto class.
     * @return Returns the created dto.
     */
    public AbstractDto createDto(Class<?> clazz) {
        Object o;
        try {
            o = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return initializeDto(o);
    }

    /**
     * This method sets the modification key generator of a dto.
     * 
     * @param dto
     *            Where the modification key generator has to be set.
     * @return Returns the modificated dto.
     */
    public AbstractDto initializeDto(Object dto) {
        AbstractDto abstractDto = (AbstractDto) dto;
        abstractDto.setModificationKeyGenerator(getModificationKeyGenerator());
        return abstractDto;
    }

    /**
     * This metod sets the modification key generator of given
     * <code>AbstractDto</code>s. If the given list of dtos is
     * <code>null</code> an empty List will be returned.
     * 
     * @param dtos
     *            Where the modification key generator has to be set.
     * @return Returns the dto list.
     */
    public List<Object> initializeDtos(List<Object> dtos) {
        if (dtos == null) {
            return new ArrayList<Object>();
        }

        Iterator<Object> it = dtos.iterator();
        while (it.hasNext()) {
            initializeDto(it.next());
        }
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (getModificationKeyGenerator() == null) {
            CoreNotificationHelper.notifyLackingEssentialProperty(
                    "modificationKeyGenerator", this);
        }
    }
}