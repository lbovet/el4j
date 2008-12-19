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
package ch.elca.el4j.services.persistence.hibernate.offlining.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Property dao implementation that uses a file.
 * Used only until the bug is found that prevents them from going to database.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class PropertyDaoFileImpl implements PropertyDaoInterface {

	/** The property file name. */
	private static final String FILE_NAME = "offliner.properties";
	
	/** The file. */
	private File m_file;

	/** The properties object. */
	private Properties m_props;
	
	/**
	 * Set up the file.
	 */
	public PropertyDaoFileImpl() {
		BufferedInputStream bis = null;
		try {
			m_file = new File(FILE_NAME);
			if (!m_file.exists() && !m_file.createNewFile()) {
				throw new IOException("Couldn't create file " + FILE_NAME);
			}
			m_props = new Properties();
			bis = new BufferedInputStream(new FileInputStream(m_file));
			m_props.load(bis);
		} catch (IOException e) {
			throw new RuntimeException("IO exception loading propererties file " + FILE_NAME, e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
	
	/** {@inheritDoc} */
	public int getIntProperty(String name) {
		return Integer.parseInt(getStringProperty(name));
	}

	/** {@inheritDoc} */
	public String getStringProperty(String name) {
		Object value = m_props.get(name);
		if (value == null) {
			throw new IllegalArgumentException("No property " + name);
		}
		return value.toString();
	}

	/** {@inheritDoc} */
	public boolean isPropertyPresent(String name) {
		return m_props.containsKey(name);
	}

	/** {@inheritDoc} */
	public void saveProperty(String name, String value) {
		m_props.setProperty(name, value);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(
				m_file));
			m_props.store(bos, "Offliner Properties");
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException("Error writing property to file", e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	/** {@inheritDoc} */
	public void saveProperty(String name, int value) {
		saveProperty(name, Integer.toString(value));
	}
}
