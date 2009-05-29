/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.env.xml.handlers;

import java.util.Properties;

import org.springframework.core.io.Resource;
import org.xml.sax.ContentHandler;

/**
 * The interface that a handler has to implement to parse a group of the env.xml file.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public interface EnvGroupHandler extends ContentHandler {
	/**
	 * Notify which resource will be parsed next.
	 * @param resource    the resource that will be parsed next
	 */
	public void startResource(Resource resource);
	
	/**
	 * @param properties    the evaluated variables (${variable} -> value)
	 */
	public void filterData(Properties properties);
	/**
	 * @return    the gathered data
	 */
	public Object getData();
}
