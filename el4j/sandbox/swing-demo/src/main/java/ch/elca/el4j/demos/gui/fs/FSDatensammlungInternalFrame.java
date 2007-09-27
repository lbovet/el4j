package ch.elca.el4j.demos.gui.fs;


import java.awt.Dimension;

import javax.swing.JInternalFrame;

import ch.elca.el4j.demos.gui.fs.panel.DatensammlungPanel;
import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * Sample internal frame 
 * @author SWI
 *
 */
public class FSDatensammlungInternalFrame extends JInternalFrame {

    private DatensammlungPanel m_pnlDatensammlung;

    public FSDatensammlungInternalFrame(GUIApplication app) {
        m_pnlDatensammlung = new DatensammlungPanel(app);
        
        setTitle("Collect Data");
        
        getContentPane().add(m_pnlDatensammlung);
        setResizable(true);
        setMaximizable(true);
        setClosable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        pack();
        setVisible(true);
        setSize(new Dimension(800, 400));
    }
}
