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

package ch.elca.el4j.apps.refdb.gui.views;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

/**
 * Keyword view.
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
public class KeywordView extends AbstractRefdbView {
    /**    
     * Fills list with data.
     */
    protected void fillDataList() {
        getDataList().addAll(getReferenceService().getAllKeywords());
    }
    
    /**
     * {@inheritDoc}
     * 
     * Returns the root component for this view.
     */
    protected JComponent createControl() {
        fillDataList();
        
        JPanel p = new JPanel(new BorderLayout());
        
        // Model for the table.
        initializeSortedBeanTable();

        p.add(new JScrollPane(getBeanTable()), BorderLayout.CENTER);
        
        JPanel keywordSearch = new JPanel();
        keywordSearch.add(new JRadioButton("All keywords."));
        p.add(keywordSearch, BorderLayout.WEST);
        keywordSearch.setVisible(false);
        
        
        return p;
    }
}
