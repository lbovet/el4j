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
package ch.elca.el4j.gui.swing.wrapper;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

import ch.elca.el4j.gui.swing.events.OpenCloseEventHandler;

/**
 * A wrapper for panels to make them an internal frame.
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
public class JInteralFrameWrapper extends JInternalFrame
    implements FrameWrapper {
    
    /**
     * The wrapped component.
     */
    private JComponent m_component;
    
    /** {@inheritDoc} */
    public void setContent(JComponent component) {
        m_component = component;

        add(component);
        
        setClosable(true);
        setResizable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        pack();
    }
    
    /** {@inheritDoc} */
    public JComponent getContent() {
        return m_component;
    }
    
    /** {@inheritDoc} */
    @Override
    public void show() {
        if (m_component instanceof OpenCloseEventHandler) {
            OpenCloseEventHandler handler = (OpenCloseEventHandler) m_component;
            handler.onOpen();
        }
        super.show();
    }
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (m_component != null) {
            if (m_component instanceof OpenCloseEventHandler) {
                OpenCloseEventHandler handler
                    = (OpenCloseEventHandler) m_component;
                handler.onClose();
            }
        }
        AbstractWrapperFactory.removeWrapper(m_component);
        super.dispose();
        m_component = null;
    }
}
