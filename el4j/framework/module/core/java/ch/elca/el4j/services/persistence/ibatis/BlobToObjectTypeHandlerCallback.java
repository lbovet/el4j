/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.ibatis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Type handler callback for iBatis SqlMaps 2.0 to store an object as a byte
 * array an vice versa.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class BlobToObjectTypeHandlerCallback implements TypeHandlerCallback {
    /**
     * Private logger of this class.
     */
    private static Log s_logger = LogFactory
            .getLog(BlobToObjectTypeHandlerCallback.class);

    /**
     * {@inheritDoc}
     */
    public Object getResult(ResultGetter getter) throws SQLException {
        Object object = null;
        try {
            Blob blob = getter.getBlob();
            if (blob != null) {
                InputStream is = blob.getBinaryStream();
                ObjectInputStream ois = new ObjectInputStream(is);
                object = ois.readObject();
                s_logger.debug("Object deserialized");
            }
        } catch (SQLException sqle) {
            s_logger.debug("rethrow SQLException", sqle);
            throw sqle;
        } catch (IOException ioe) {
            s_logger.debug("caught unexpected IOException", ioe);
        } catch (ClassNotFoundException cnfe) {
            s_logger.debug("caught unexpected ClassNotFoundException", cnfe);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    public void setParameter(ParameterSetter setter, Object object)
        throws SQLException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            baos.flush();

            byte[] bytes = baos.toByteArray();
            s_logger.debug("Object serialized");

            oos.close();
            baos.close();

            setter.setBytes(bytes);
        } catch (IOException ioe) {
            s_logger.info("caught unexpected IOException", ioe);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object valueOf(String s) {
        return s;
    }
}