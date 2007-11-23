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
package ch.elca.el4j.gui.swing.dialog.about;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * A standard About dialog.
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
public class AboutDialog extends JDialog {
    /**
     * The logger.
     */
    private static Log s_logger = LogFactory.getLog(JDialog.class);
    
    /**
     * The resource map.
     */
    protected ResourceMap m_resourceMap;
    
    
    /**
     * The main panel.
     */
    protected JPanel m_panel;
    
    /**
     * The logo.
     */
    protected JLabel m_logo;
    
    /**
     * The about text.
     */
    protected JLabel m_infoLabel;
    /**
     * The button for closing the about dialog.
     */
    protected JButton m_closeButton; 

    /**
     * The constructor.
     */
    public AboutDialog() {
        GUIApplication app = GUIApplication.getInstance();
        ApplicationContext appContext = app.getContext();
        m_resourceMap = appContext.getResourceMap(AboutDialog.class);

        setTitle(getRes("aboutText"));
        
        createComponents();

        // assign actions
        ApplicationActionMap actionMap = appContext.getActionMap(this);
        m_closeButton.setAction(actionMap.get("close"));

        add(m_panel);

        // inject values from properties file
        m_resourceMap.injectComponents(this);

        // little hack to make button larger
        String space = getRes("close.space");
        if (space != null) {
            Insets s = m_closeButton.getMargin();
            s.right = Integer.parseInt(space);
            s.left = s.right;
            m_closeButton.setMargin(s);
        }

        // prepare to show
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setModal(true);
    }
    
    /**
     * Create the form components.
     */
    private void createComponents() {
        m_panel = new JPanel(new BorderLayout());
        
        // image on the left
        String aboutImage = getRes("aboutImage");
        if (aboutImage != null) {
            ImageIcon icon = createImageIcon(aboutImage);
            m_logo = new JLabel(icon);
            m_panel.add(m_logo, BorderLayout.WEST);
        }
        
        // about-text on the right
        m_infoLabel = new JLabel(getAboutText());
        m_infoLabel.setName("infoLabel");
        // Checkstyle: MagicNumber off
        m_infoLabel.setBorder(new EmptyBorder(3, 6, 3, 3));
        // Checkstyle: MagicNumber on
        
        m_panel.add(m_infoLabel, BorderLayout.CENTER);
        
        // button to close the dialog
        m_closeButton = new JButton();
        m_closeButton.setSelected(true);
        getRootPane().setDefaultButton(m_closeButton);
        
        // make button right aligned
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.TRAILING);
        JPanel closePanel = new JPanel(layout);
        closePanel.add(m_closeButton);

        // compose button and separator
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
        bottom.add(new JSeparator(SwingConstants.HORIZONTAL));
        bottom.add(closePanel);

        m_panel.add(bottom, BorderLayout.SOUTH);
        
    }

    /**
     * Close the dialog.
     */
    @Action
    public void close() {
        dispose();
    }
    
    /**
     * @return   the about text
     */
    protected String getAboutText() {
        final String br = "<br> ";
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(getRes("Application.title")).append(br).append(br);
        sb.append(getRes("Application.description")).append(br).append(
                br);
        sb.append(getRes("versionText")).append(" ");
        sb.append(getRes("Application.version")).append(br);
        sb.append(getRes("buildIdText")).append(" ");
        sb.append(getRes("Application.buildId")).append(br)
                .append(br);
        sb.append(getRes("Application.copyright")).append(br);
        sb.append(getRes("Application.homepage"));
        sb.append("</html>");
        
        return sb.toString();
    }

    /**
     * @param id    the resource ID
     * @return      the String associated with the given resource ID
     */
    protected String getRes(String id) {
        return m_resourceMap.getString(id);
    }

    /**
     * @param path           the path to the image
     * @return               an ImageIcon, or null if the path was invalid.
     */
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "");
        } else {
            s_logger.error("Couldn't find file: " + path);
            return null;
        }
    }
}
