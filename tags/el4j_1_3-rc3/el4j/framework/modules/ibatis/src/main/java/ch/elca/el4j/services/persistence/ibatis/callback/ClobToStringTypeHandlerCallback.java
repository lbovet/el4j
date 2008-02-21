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

import java.io.StringReader;
import java.sql.Clob;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * This class is used to make clobs work with ibatis sqlmaps.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 * @deprecated Use callback handler of ibatis instead.
 * @see com.ibatis.sqlmap.engine.type.ClobTypeHandlerCallback
 */
public class ClobToStringTypeHandlerCallback implements TypeHandlerCallback {
    /**
     * {@inheritDoc}
     */
    public void setParameter(ParameterSetter setter, Object parameter)
        throws SQLException {
        String s = (String) parameter;
        if (s != null) {
            StringReader reader = new StringReader(s);
            setter.setCharacterStream(reader, s.length());
        } else {
            setter.setString(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getResult(ResultGetter getter) throws SQLException {
        String value = "";
        Clob clob = getter.getClob();
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1, size);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public Object valueOf(String s) {
        return s;
    }
}