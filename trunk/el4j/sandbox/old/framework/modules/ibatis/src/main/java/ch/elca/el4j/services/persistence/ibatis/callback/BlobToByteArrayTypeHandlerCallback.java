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
package ch.elca.el4j.services.persistence.ibatis.callback;

import java.sql.Blob;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * This class is used to make blobs work with ibatis sqlmaps.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @deprecated Use callback handler of ibatis instead.
 * @see com.ibatis.sqlmap.engine.type.BlobTypeHandlerCallback
 */
public class BlobToByteArrayTypeHandlerCallback implements TypeHandlerCallback {
	/**
	 * {@inheritDoc}
	 */
	public void setParameter(ParameterSetter setter, Object parameter)
		throws SQLException {
		byte[] bytes = (byte[]) parameter;
		setter.setBytes(bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult(ResultGetter getter) throws SQLException {
		Blob blob = getter.getBlob();
		if (blob != null) {
			int size = (int) blob.length();
			return blob.getBytes(1, size);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object valueOf(String s) {
		return s;
	}
}