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
package ch.elca.el4j.gui.swing.dialog.search;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.elca.el4j.gui.swing.GUIApplication;

import zappini.designgridlayout.DesignGridLayout;

/**
 * This class represents a standard search dialog.
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
public abstract class AbstractSearchDialog extends JPanel {
    /**
     * The label stating something like 'Search for:'.
     */
    protected JLabel m_searchFor;
    
    /**
     * The text field containing the search string.
     */
    protected JTextField m_searchField;
    
    /**
     * The button to start the search.
     */
    protected JButton m_searchButton;
    
    /**
     * Optional components to set search options.
     */
    protected JComponent[] m_options = null;
    
    /**
     * @param app    the appFramework application
     */
    public AbstractSearchDialog(GUIApplication app) {
        createBasicComponents();
        createOptionalComponents();
        createLayout();
    }
    
    /**
     * @return Returns the option components.
     */
    public JComponent[] getOptions() {
        return m_options;
    }

    /**
     * @param options Sets the option components.
     */
    public void setOptions(JComponent[] options) {
        m_options = options;
    }
    
    
    /**
     * Create the basic form components.
     */
    private void createBasicComponents() {
        m_searchFor = new JLabel();
        m_searchFor.setName("searchFor");
        
        m_searchField = new JTextField();
        m_searchField.setName("searchField");
        
        m_searchButton = new JButton();
        m_searchButton.setName("searchButton");
    }
    
    /**
     * Create the optional form components. Hook for subclasses.
     */
    protected void createOptionalComponents() { }
    
    /**
     * Layout the form components.
     */
    private void createLayout() {
        // create the form layout
        DesignGridLayout layout = new DesignGridLayout(this);
        setLayout(layout);

        layout.row().left().add(m_searchFor, 2);
        layout.row().add(m_searchField, 2);
        layout.row().right().add(m_searchButton);
        if (m_options != null) {
            for (JComponent component : m_options) {
                layout.row().left().add(component, 2);
            }
        }
    }
}
