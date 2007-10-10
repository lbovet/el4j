package ch.elca.el4j.demos.gui;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * Sample internal frame that shows auto bindings 
 * @author SWI
 *
 */
public class BindingDemoFrame extends JInternalFrame {
    public BindingDemoFrame(GUIApplication app) {
        setTitle("BindingDemoFrame");
        
        // create panel containing form
        JPanel panel = new ExampleForm(app);
        add(panel);
        

        setClosable(true);
        setResizable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
