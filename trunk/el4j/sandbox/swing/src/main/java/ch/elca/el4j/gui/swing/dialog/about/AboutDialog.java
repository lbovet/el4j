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

import org.apache.log4j.Logger;
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
    private static Logger s_logger = Logger.getLogger(JDialog.class);
    
    /**
     * The resource map.
     */
    private ResourceMap m_resourceMap;

    public AboutDialog(GUIApplication app) {
        ApplicationContext appContext = app.getContext();
        m_resourceMap = appContext.getResourceMap(AboutDialog.class);

        setTitle(getRes("aboutText"));
        JPanel panel = new JPanel(new BorderLayout());

        // image on the left
        ImageIcon icon = createImageIcon(getRes("aboutImage"));
        JLabel logo = new JLabel(icon);
        panel.add(logo, BorderLayout.WEST);

        // about-text on the right
        final String lineSep = "<br> ";
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(getRes("Application.title")).append(lineSep).append(lineSep);
        sb.append(getRes("Application.description")).append(lineSep).append(
                lineSep);
        sb.append(getRes("versionText")).append(" ");
        sb.append(getRes("Application.version")).append(lineSep);
        sb.append(getRes("buildIdText")).append(" ");
        sb.append(getRes("Application.buildId")).append(lineSep)
                .append(lineSep);
        sb.append(getRes("Application.copyright")).append(lineSep);
        sb.append(getRes("Application.homepage"));
        sb.append("</html>");

        JLabel infoLabel = new JLabel(sb.toString());
        infoLabel.setName("infoLabel");
        infoLabel.setBorder(new EmptyBorder(3, 6, 3, 3)); // top, left,
                                                            // bottom, right
        panel.add(infoLabel, BorderLayout.CENTER);

        // button to close the dialog
        JButton closeButton = new JButton(getRes("closeButton.Action.text"));
        closeButton.setSelected(true);
        getRootPane().setDefaultButton(closeButton);

        // make button right aligned
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.TRAILING);
        JPanel closePanel = new JPanel(layout);
        closePanel.add(closeButton);

        // compose button and separator
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
        bottom.add(new JSeparator(SwingConstants.HORIZONTAL));
        bottom.add(closePanel);

        panel.add(bottom, BorderLayout.SOUTH);

        // assign actions
        ApplicationActionMap actionMap = appContext.getActionMap(this);
        closeButton.setAction(actionMap.get("close"));

        add(panel);

        // inject values from properties file
        m_resourceMap.injectComponents(this);

        // little hack to make button larger
        Insets s = closeButton.getMargin();
        s.right = Integer.parseInt(getRes("closeButton.space"));
        s.left = s.right;
        closeButton.setMargin(s);

        // prepare to show
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setModal(true);
    }

    /**
     * Close the dialog
     */
    @Action
    public void close() {
        dispose();
    }

    /**
     * @param id    the resource ID
     * @return      the String associated with the given resource ID
     */
    protected String getRes(String id) {
        return m_resourceMap.getString(id, new Object[0]);
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
