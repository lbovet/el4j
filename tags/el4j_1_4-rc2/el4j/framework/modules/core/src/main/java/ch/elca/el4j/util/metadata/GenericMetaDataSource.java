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

package ch.elca.el4j.util.metadata;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.springframework.metadata.Attributes;

/**
 * Interface for the metadata source.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Raphael Boog (RBO)
 * @author Martin Zeltner (MZE)
 */
public interface GenericMetaDataSource {

    /**
     * Setter method to set the metadata implementation.
     * 
     * @param attributes
     *            The attributes to set
     */
    public void setMetaDataDelegator(Attributes attributes);

    /**
     * Getter method to get the metadata implementation.
     * 
     * @return Attributes
     */
    public Attributes getMetaDataDelegator();

    /**
     * Setter method for the list of metadata types where to apply the 
     * interceptor.
     * 
     * @param interceptingAttributes
     *            Is the metadata type list.
     */
    public void setInterceptingMetaData(List<Class> interceptingAttributes);

    /**
     * @return Returns the metadata type list.
     */
    public List<Class> getInterceptingMetaData();

    /**
     * @param method
     *            Is the method for the current invocation. Must not be null.
     * @param targetClass
     *            target class for this invocation. May be null.
     * @return Returns a collection of the matching meta data for the given
     *         method and targetClass.
     */
    public Collection getMetaData(Method method, Class targetClass);

}