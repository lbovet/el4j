/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.generic;

import java.util.Collection;

/** 
 * An object receiving change notifications from the repository.
 * 
 * @param <T> the type of the entities managed by the repository this object
 *            listens to.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public interface RepositoryListener<T> {
    /** the passed entities have been added. */
    public void added(Collection<? extends T> entities);
    
    /** the passed entities have been removed. */
    public void removed(Collection<? extends T> entities);
    
    /** the passed entities have changed. */
    public void changed(Collection<? extends T> entities);
    
    /** anything may have changed. */
    public void invalidate();    
}
