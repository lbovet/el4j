package ch.elca.el4j.services.gui.swing.dialog.about;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

public class AboutDialog extends JDialog {
    private ResourceMap map;

    public AboutDialog(ApplicationContext appContext) {
        map = appContext.getResourceMap(AboutDialog.class);

        setTitle(getRes("aboutText"));
        JPanel panel = new JPanel(new BorderLayout());

        // image on the left
        ImageIcon icon = createImageIcon(getRes("aboutImage"), "");
        JLabel logo = new JLabel(icon);
        panel.add(logo, BorderLayout.WEST);

        // about-text on the right
        final String lineSep = "<br> "; // System.getProperty( "line.separator"
                                        // );
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
        closeButton.setAction(actionMap.get("closeButton"));

        add(panel);

        // inject values from properties file
        map.injectComponents(this);

        // little hack to make button larger
        Insets s = closeButton.getMargin();
        s.left = s.right = Integer.parseInt(getRes("closeButton.space"));
        closeButton.setMargin(s);

        // prepare to show
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setModal(true);
    }

    @Action
    public void closeButton() {
        dispose();
    }

    protected String getRes(String id) {
        return map.getString(id, new Object[0]);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
