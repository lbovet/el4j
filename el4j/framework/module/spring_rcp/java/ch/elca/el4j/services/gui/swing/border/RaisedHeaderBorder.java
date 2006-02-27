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
package ch.elca.el4j.services.gui.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

/**
 * A custom border for the raised header pseudo 3D effect.
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
public class RaisedHeaderBorder extends AbstractBorder {
    /**
     * Are the fix insets for this border.
     */
    public static final Insets INSETS = new Insets(1, 1, 1, 0);

    /**
     * {@inheritDoc}
     */
    public Insets getBorderInsets(Component c) {
        return INSETS;
    }

    /**
     * {@inheritDoc}
     */
    public void paintBorder(
        Component c, Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(UIManager.getColor("controlLtHighlight"));
        g.fillRect(0, 0, w, 1);
        g.fillRect(0, 1, 1, h - 1);
        g.setColor(UIManager.getColor("controlShadow"));
        g.fillRect(0, h - 1, w, 1);
        g.translate(-x, -y);
    }
}