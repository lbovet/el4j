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
package ch.elca.el4j.services.gui.swing.panel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.springframework.richclient.control.ShadowBorder;

import com.jgoodies.looks.LookUtils;

import ch.elca.el4j.services.gui.swing.border.RaisedHeaderBorder;

/**
 * Controlable internal frame. Used to wrap a <code>JComponent</code>.
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
public class ControlableInternalFrame extends JPanel {
    /**
     * Is the title of this frame.
     */
    private String m_title;
    
    /**
     * Is the icon of this frame.
     */
    private Icon m_icon;
    
    /**
     * Is the <code>JLabel</code> for the title.
     */
    private JLabel m_titleLabel;

    /**
     * Is the gradient panel that is used as background for the title label.
     */
    private GradientPanel m_headerGradientPanel;

    /**
     * Is the panel that contains the header gradient panel.
     */
    private JPanel m_headerPanel;

    /**
     * Flag if corrent frame is selected.
     */
    private boolean m_selected = false;
    
    /**
     * Initializes this frame.
     * 
     * @param content Is the content for this frame.
     * @param frameControl Is the control element for this frame.
     */
    public void init(JComponent content, JComponent frameControl) {
        removeAll();
        initHeaderPanel(frameControl);
        
        setLayout(new BorderLayout());
        add(m_headerPanel, BorderLayout.NORTH);
        if (content != null) {
            add(content, BorderLayout.CENTER);
        }
        setBorder(new ShadowBorder());
        setSelected(true);
        updateHeader();
    }

    /**
     * Initializes the header panel.
     * 
     * @param frameControl Is the control element for this frame.
     */
    protected void initHeaderPanel(JComponent frameControl) {
        m_titleLabel
            = new JLabel(getTitle(), getIcon(), SwingConstants.LEADING);
        m_titleLabel.setOpaque(false);

        m_headerGradientPanel 
            = new GradientPanel(new BorderLayout(), getHeaderBackground());
        m_headerGradientPanel.add(m_titleLabel, BorderLayout.WEST);
        if (frameControl != null) {
            m_headerGradientPanel.add(frameControl, BorderLayout.EAST);
        }
        // Checkstyle: MagicNumber off
        m_headerGradientPanel.setBorder(
            BorderFactory.createEmptyBorder(3, 4, 3, 1));
        // Checkstyle: MagicNumber on
        
        m_headerPanel = new JPanel(new BorderLayout());
        m_headerPanel.add(m_headerGradientPanel, BorderLayout.CENTER);
        m_headerPanel.setBorder(new RaisedHeaderBorder());
        m_headerPanel.setOpaque(false);
    }
    
    /**
     * @return Returns the frame's icon.
     */
    public Icon getIcon() {
        return m_icon;
    }

    /**
     * @param newIcon
     *            Is the icon to set.
     */
    public void setIcon(Icon newIcon) {
        Icon oldIcon = getIcon();
        m_icon = newIcon;
        if (m_titleLabel != null) {
            m_titleLabel.setIcon(newIcon);
        }
        firePropertyChange("icon", oldIcon, newIcon);
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Sets a new title text.
     * 
     * @param newText
     *            the title text tp be set
     */
    public void setTitle(String newText) {
        String oldText = getTitle();
        m_title = newText;
        if (m_titleLabel != null) {
            m_titleLabel.setText(newText);
        }
        firePropertyChange("title", oldText, newText);
    }

    /**
     * Answers if the panel is currently selected (or in other words active) or
     * not. In the selected state, the header background will be rendered
     * differently.
     * 
     * @return Returns <code>true</code> if current state is selected.
     */
    public boolean isSelected() {
        return m_selected;
    }

    /**
     * This panel draws its title bar differently if it is selected, which may
     * be used to indicate to the user that this panel has the focus, or should
     * get more attention than other simple internal frames.
     * 
     * @param newValue
     *            Is the new state of this component.
     */
    public void setSelected(boolean newValue) {
        boolean oldValue = isSelected();
        m_selected = newValue;
        updateHeader();
        firePropertyChange("selected", oldValue, newValue);
    }

    /**
     * Updates the header.
     */
    protected void updateHeader() {
        if (m_headerPanel != null) {
            m_headerGradientPanel.setBackground(getHeaderBackground());
            m_headerGradientPanel.setOpaque(isSelected());
            m_titleLabel.setForeground(getHeaderForeground(isSelected()));
            m_headerPanel.repaint();
        }
    }

    /**
     * Updates the UI. In addition to the superclass behavior, we need to update
     * the header component.
     */
    public void updateUI() {
        super.updateUI();
        updateHeader();
    }

    /**
     * Determines and answers the header's text foreground color. Tries to
     * lookup a special color from the L&amp;F. In case it is absent, it uses
     * the standard internal frame forground.
     * 
     * @param selected
     *            <code>true</code> to lookup the active color, 
     *            <code>false</code> for the inactive
     * @return Returns the color of the text foreground.
     */
    protected Color getHeaderForeground(boolean selected) {
        Color c = UIManager.getColor(
            selected ? "SimpleInternalFrame.activeTitleForeground"
                : "SimpleInternalFrame.inactiveTitleForeground");
        if (c != null) {
            return c;
        }
        return UIManager.getColor(selected 
            ? "InternalFrame.activeTitleForeground" : "Label.foreground");
    }

    /**
     * Determines and answers the header's background color. Tries to lookup a
     * special color from the L&amp;F. In case it is absent, it uses the
     * standard internal frame background.
     * 
     * @return the color of the header's background
     */
    protected Color getHeaderBackground() {
        Color c = UIManager.getColor(
            "SimpleInternalFrame.activeTitleBackground");
        if (c != null) {
            return c;
        }
        if (LookUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            c = UIManager.getColor("InternalFrame.activeTitleGradient");
        }
        return c != null ? c 
            : UIManager.getColor("InternalFrame.activeTitleBackground");
    }
}
