package ch.elca.el4j.gui.swing.widgets;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

// TODO improve this class
public class IntegerField extends JTextField {
    Color normalColor;
    
    public IntegerField() {
        this(new Color(255, 128, 128));
    }
    public IntegerField(Color invalidColor) {
        final Color color = invalidColor;
        normalColor = getBackground();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((Character.isDigit(c) || (c == '-')
                        || (c == KeyEvent.VK_BACK_SPACE)
                        || (c == KeyEvent.VK_DELETE)))) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(getText());
                    setBackground(normalColor);
                } catch (Exception ex) {
                    setBackground(color);
                }
                super.keyReleased(e);
            }
        });
    }
}
