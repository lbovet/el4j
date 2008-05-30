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
package ch.elca.el4j.tests.remoting.jaxws.service;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is an adapter which converts int[][] to String and back.
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
public class IntMatrixAdapter extends XmlAdapter<String, int[][]> {

    /** {@inheritDoc} */
    @Override
    public String marshal(int[][] v) throws Exception {
        StringBuilder b = new StringBuilder();
        for (int[] is : v) {
            for (int i : is) {
                b.append(i).append(",");
            }
            b.setLength(b.length() - 1);
            b.append(";");
        }
        b.setLength(b.length() - 1);
        return b.toString();
    }
    
    /** {@inheritDoc} */
    @Override
    public int[][] unmarshal(String v) throws Exception {
        String[] tmp = v.split(";");
        int[][] list1 = new int[tmp.length][];
        for (int i = 0; i < list1.length; i++) {
            String[] tmp2 = tmp[i].split(",");
            int[] list2 = new int[tmp2.length];
            for (int j = 0; j < list2.length; j++) {
                list2[j] = Integer.parseInt(tmp2[j]);
            }
            list1[i] = list2;
        }
        return list1;
    }
}