/*
 * JInternalFrameComparator.java
 *
 * Copyright (c) 2004-2006 Gregory Kotsaftis
 * gregkotsaftis@yahoo.com
 * http://zeus-jscl.sourceforge.net/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package ch.elca.el4j.gui.swing.mdi;

import java.util.Comparator;
import javax.swing.JInternalFrame;

/**
 * A simple comparator for <code>JInternalFrames</code>, based on their title.
 * <p>
 * @author Gregory Kotsaftis
 * @since 1.04
 */
public final class JInternalFrameComparator
    implements Comparator<JInternalFrame> {
    
    /**
     * Compares internal frames based on their title.
     * <p>
     * @param o1    First frame.
     * @param o2    Second frame.
     * <p>
     * @return      The comparison.
     */
    public int compare(JInternalFrame o1, JInternalFrame o2)
    {
        int ret = 0;
        
        if( o1!=null && o2!=null )
        {
            String t1 = o1.getTitle();
            String t2 = o2.getTitle();
            
            if( t1 != null && t2 != null )
            {
                ret = t1.compareTo(t2);
            }
            else if( t1 == null && t2 != null )
            {
                ret = -1;
            }
            else if( t1 != null && t2 == null )
            {
                ret = 1;
            }
            else
            {
                ret = 0;
            }
        }
        
        return( ret );
    }
    
}
