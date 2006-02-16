/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.generic.primarykey;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This key generator generates UUIDs as described in "EJB Design Patterns", F.
 * Marinescu, pages 112-116. It is a simplified version of the algorithm
 * presented in
 * http://www1.ics.uci.edu/~ejw/authoring/uuid-guid/draft-leach-uuids-guids-01.txt
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class UuidPrimaryKeyGenerator implements PrimaryKeyGenerator {
    /**
     * Private logger of this class.
     */
    private static Log s_logger 
        = LogFactory.getLog(UuidPrimaryKeyGenerator.class);

    /**
     * Middle value.
     */
    private String m_midValue;
    
    /**
     * Seeder.
     */
    private SecureRandom m_seeder;
    
    /**
     * String aaray with zeros.
     */
    private String[] m_zeros;

    /**
     * Default constructor.
     */
    public UuidPrimaryKeyGenerator() {
        init();
    }

    /**
     * {@inheritDoc}
     */
    public String getPrimaryKey() {
        long time = System.currentTimeMillis();
        int timeLow = (int) time & 0xFFFFFFFF;
        int value = m_seeder.nextInt();
        String keyString = toHexString(timeLow) + m_midValue
            + toHexString(value);
        return keyString;
    }

    // Checkstyle: MagicNumber off
    /**
     * Initalize the primary key generator.
     */
    private void init() {
        // Fill the m_zeros table
        final int ZERO_ARRAY_LENGTH = 8;
        m_zeros = new String[ZERO_ARRAY_LENGTH];
        m_zeros[0] = "";
        for (int i = 1; i < ZERO_ARRAY_LENGTH; i++) {
            m_zeros[i] = m_zeros[i - 1] + "0";
        }

        // Get the IP address as an hex string
        byte[] bytes = null;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            bytes = inetAddress.getAddress();
        } catch (UnknownHostException npe) {
            s_logger.debug("Host ip could not be found. Following " 
                + "will be used: 127.0.0.1");
            bytes = new byte[] {1, 0, 0, 127};
        }
        int intAddress = 0;
        for (int i = 0; i < bytes.length; i++) {
            int unsignedByte = bytes[i] & 0xFF;
            intAddress = (intAddress + unsignedByte) << 8;
        }
        String hexInetAddress = toHexString(intAddress);

        String thisHashCode = toHexString(System.identityHashCode(this));

        m_midValue = hexInetAddress + thisHashCode;

        m_seeder = new SecureRandom();
    }

    /**
     * Method to convert from decimal to hexadecimal.
     * 
     * @param i Is the given decimal number.
     * @return Returns the hexadecimal value of given decimal number.
     */
    private String toHexString(int i) {
        String hex = Integer.toHexString(i);
        int hexLength = hex.length();
        return m_zeros[8 - hexLength] + hex;
    }
    // Checkstyle: MagicNumber on
}

