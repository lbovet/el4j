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
package ch.elca.el4j.apps.refdb.dom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.validator.NotNull;

import ch.elca.el4j.services.persistence.hibernate.dao.extent.DataExtent;
import ch.elca.el4j.util.codingsupport.ObjectUtils;

/**
 * File domain object.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
@Entity
public class File extends FileDescriptorView {
	
	/**
	 * Private logger.
	 */
	private static Log s_logger = LogFactory.getLog(File.class);
	
	/** See corresponding setter method for more details. */
	private byte[] m_content;
	
	/** See corresponding setter method for more details. */
	private Blob m_data;

	/**
	 * Content of the file (usually binary).
	 * @return Returns the content.
	 */
	@Transient
	public byte[] getContent() {
		if (m_content == null) {
			InputStream in = null;
			ByteArrayOutputStream out = null;
			byte[] primitiveData = null;
			Blob blob = getData();
			if (blob != null) {
				try {
					in = blob.getBinaryStream();
					out = new ByteArrayOutputStream();
					byte[] buffer = new byte[4096];
					int readBytes;
					while ((readBytes = in.read(buffer)) > 0) {
						out.write(buffer, 0, readBytes);
					}
					primitiveData = out.toByteArray();
				} catch (IOException e) {
					s_logger.error("Error while reading content stream.", e);
				} catch (SQLException e) {
					s_logger.error("Error while retrieving content.", e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {
							s_logger.error("Error while closing input stream.");
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (Exception e) {
							s_logger.error("Error while closing output stream.");
						}
					}
				}
			}
			if (primitiveData != null) {
				m_content = primitiveData.length > 0 ? primitiveData : null;
			}
		}
		return m_content;
	}
 
	/**
	 * Set the content of the file.
	 * @param content
	 *            The content to set.
	 */
	public void setContent(byte[] content) {
		if (content != null && content.length > 0) {
			setData(Hibernate.createBlob(content));
			m_content = content;
		} else {
			setData(null);
			m_content = null;
		}
	}
	
	/**
	 * Content of the file converted to Blob. Used by hibernate only!
	 * @return Returns the data.
	 */
	@NotNull
	@Lob
	@Column(name = "content")
	private Blob getData() {
		return m_data;
	}
	
	/**
	 * Set the content as Blob. Used by hibernate only!
	 * @param data	the data to set.
	 */
	private void setData(Blob data) {
		m_data = data;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return super.hashCode();
	}
	

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object object) {
		if (super.equals(object)
			&& object instanceof File) {
			File other = (File) object;

			return getKeyToReference() == other.getKeyToReference()
				&& ObjectUtils.nullSaveEquals(getName(), other.getName())
				&& ObjectUtils.nullSaveEquals(getMimeType(),
					other.getMimeType())
				&& Arrays.equals(getContent(), other.getContent());
		} else {
			return false;
		}
	}
	
	/***  Predefined Fetching extent ***/
	
	/**
	 * Light fetching variant: only fetch header data, no content.
	 * @return the light fetch extent.
	 */
	public static final DataExtent HEADER
		= new DataExtent(File.class).all().without("data", "content");
	
	/**
	 * Heavy fetching variant: fetch the whole file, including content.
	 * @return the heavy fetch extent.
	 */
	public static final DataExtent ALL
		= new DataExtent(File.class).all();
}