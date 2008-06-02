/*
 * WindowManager.java
 *
 * Copyright (c) 2004-2006 Gregory Kotsaftis
 * gregkotsaftis@yahoo.com
 * http://zeus-jscl.sourceforge.net/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package ch.elca.el4j.gui.swing.mdi;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import ch.elca.el4j.gui.swing.mdi.JInternalFrameComparator;

/**
 * A JDesktop window manager for MDI support.
 * <p>
 * Major functions implemented:
 * <ul>
 * <li>close()</li>
 * <li>closeAll()</li>
 * <li>minimize()</li>
 * <li>minimizeAll()</li>
 * <li>maximize()</li>
 * <li>maximizeAll()</li>
 * <li>restore()</li>
 * <li>restoreAll()</li>
 * <li>hide()</li>
 * <li>hideAll()</li>
 * <li>selectNext()</li>
 * <li>selectPrevious()</li>
 * <li>reset()</li>
 * <li>resetAll()</li>
 * <li>tileHorizontally()</li>
 * <li>tileVertically()</li>
 * <li>tile()</li>
 * <li>cascade()</li>
 * </ul>
 * <p>
 * 
 * Adapted from project http://zeus-jscl.sourceforge.net/ 
 * 
 * @author Gregory Kotsaftis
 * @since 1.04
 */
public final class WindowManager {

    /**
     * Number of menu items.
     */
    private int m_lastMenuItemsCount = 0;

    /**
     * Stores 'radio button menu items' and 'internal frames' pairs.
     */
    private final Hashtable<JRadioButtonMenuItem, JInternalFrame> m_radioMenuItemsAndFrames = new Hashtable<JRadioButtonMenuItem, JInternalFrame>();

    /**
     * Stores 'internal frames' and 'radio button menu items' pairs.
     */
    private final Hashtable<JInternalFrame, JRadioButtonMenuItem> m_framesAndRadioMenuItems = new Hashtable<JInternalFrame, JRadioButtonMenuItem>();

    /**
     * Compares frames by title.
     */
    private final JInternalFrameComparator m_frameComparator = new JInternalFrameComparator();

    /**
     * Container listener.
     */
    private final FrameListener m_frameListener = new FrameListener();

    /**
     * Internal frame listener.
     */
    private final SelectFrameListener m_selectFrameListener = new SelectFrameListener();

    /**
     * Radio button menu item listener.
     */
    private final MenuItemActionListener m_radioMenuItemListener = new MenuItemActionListener();

    /**
     * JDesktopPane instance.
     */
    private JDesktopPane m_desktop = null;

    /**
     * JMenu instance.
     */
    private JMenu m_windowsMenu = null;

    /**
     * Check to see which drag mode should be used.
     */
    private boolean m_outlineDragMode = false;

    /**
     * Used to set the de-iconifiable policy.
     */
    private boolean m_deiconifiablePolicy = false;

    /**
     * Used to set the close policy.
     */
    private boolean m_closePolicy = false;

    /**
     * Used to set the auto-position policy.
     */
    private boolean m_autoPositionPolicy = true;

    /**
     * Position for next placed JInternalFrame within the desktop.
     * Used in conjuction with <code>m_autoPositionPolicy</code>.
     */
    private Point m_nextFramePos = new Point(0, 0);

    /**
     * Closes an internal frame if it is not already closed and is closable.
     * <p>
     * @param f     The <code>JInternalFrame</code> to close.
     */
    private void close_frame(JInternalFrame f) {
        if (f != null && !f.isClosed() && f.isClosable()) {
            try {
                if (m_closePolicy) {
                    f.setClosed(true);
                } else {
                    f.doDefaultCloseAction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Iconifies a frame if it is iconifiable but not yet iconified.
     * <p>
     * @param f     The <code>JInternalFrame</code> to iconify.
     */
    private void iconify_frame(JInternalFrame f) {
        if (f != null && !f.isIcon() && f.isIconifiable()) {
            try {
                f.setIcon(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * De-iconifies a frame if it is iconified.
     * <p>
     * @param f     The <code>JInternalFrame</code> to de-iconify.
     */
    private void deiconify_frame(JInternalFrame f) {
        if (f != null && f.isIcon() && f.isIconifiable()) {
            try {
                f.setIcon(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Restores a frame.
     * <p>
     * @param f     The <code>JInternalFrame</code> to restore.
     */
    private void restore_frame(JInternalFrame f) {
        if (f != null) {
            try {
                f.setMaximum(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Maximizes a frame if it is can be maximized and is not already maximized.
     * <p>
     * @param f     The <code>JInternalFrame</code> to maximize.
     */
    private void maximize_frame(JInternalFrame f) {
        if (f != null && !f.isMaximum() && f.isMaximizable()) {
            try {
                f.setMaximum(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resets a frame to it's original size.
     * <p>
     * @param f     The <code>JInternalFrame</code> to reset to original size.
     */
    private void reset_frame(JInternalFrame f) {
        if (f != null) {
            f.pack();
        }
    }

    /**
     * Selects a frame and brings it to front of it's layer.
     * <p>
     * @param f     The selected <code>JInternalFrame</code>.
     */
    private void select_frame(JInternalFrame f) {
        if (f != null && !f.isSelected()) {
            try {
                f.setSelected(true);
                f.toFront();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hides a frame if it is visible. Also rebuilds the windows menu.
     * <p>
     * @param f     The <code>JInternalFrame</code> to hide.
     */
    private void hide_frame(JInternalFrame f) {
        if (f != null && f.isVisible()) {
            f.setVisible(false);

            if (f.isSelected()) {
                try {
                    f.setSelected(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // this event will not be fired otherwise!
            m_frameListener.manualFireEvent();
        }
    }

    /**
     * Finds if an internal frame has our frame listener attached. In this case
     * i should skip attaching it again. This method is needed because
     * when i debuged the code around <code>addInternalFrameListener()</code>
     * i saw, to my surprise, that the <b>SAME</b> listener was added in the
     * array with the internal frame listeners over and over again! Perhaps
     * this is a bug of swings, perhaps not. Until this issue is solved i use
     * this approach.
     * <p>
     * @todo Check java forums about this!
     */
    private boolean is_internal_frame_listener_attached(JInternalFrame jif) {
        boolean found = false;

        InternalFrameListener[] all = jif.getInternalFrameListeners();
        for (int i = 0; i < all.length; i++) {
            if (all[i] == m_selectFrameListener) {
                found = true;
                break;
            }
        }

        return (found);
    }

    /**
     * Constructor. Creates a new <code>WindowManager</code> and
     * attaches it to a desktop pane.
     * <p>
     * @param d             The <code>JDesktopPane</code> instance.
     * @param windowsMenu   The <code>JMenu</code> instance.
     */
    public WindowManager(JDesktopPane d, JMenu windowsMenu) {
        m_desktop = d;
        m_windowsMenu = windowsMenu;

        if (m_desktop == null) {
            throw new NullPointerException(
                    "JDesktopPane instance provided to WindowManager is NULL!");
        }

        if (m_windowsMenu == null) {
            throw new NullPointerException(
                    "JMenu instance provided to WindowManager is NULL!");
        }

        // prepare the windows menu to add our radio buttons
        
        m_lastMenuItemsCount = m_windowsMenu.getMenuComponentCount();
        
        // listener for new frames that will be added in the desktop
        m_desktop.addContainerListener(m_frameListener);

        // find if there are any frames in the desktop and start monitoring them
        JInternalFrame[] frames = m_desktop.getAllFrames();
        for (int i = 0; frames != null && i < frames.length; i++) {
            frames[i].addInternalFrameListener(m_selectFrameListener);
        }

        // rebuild the windows menu if there were any frames opened before we
        // attached our ContainerListener.
        m_frameListener.manualFireEvent();
    }

    /**
     * Selects frames' drawing strategy.
     * <p>
     * @param outline   <code>true</code> to enable
     *                  <code>JDesktopPane.OUTLINE_DRAG_MODE</code>, or
     *                  <code>false</code> to enable
     *                  <code>JDesktopPane.LIVE_DRAG_MODE</code>.
     */
    public void setOutlineDragMode(boolean outline) {
        m_outlineDragMode = outline;

        if (m_desktop != null) {
            if (m_outlineDragMode) {
                m_desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
            } else {
                m_desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
            }
        }
    }

    /**
     * Gets frames' drawing strategy.
     * <p>
     * @return  <code>true</code> if desktop's <code>DragMode</code> is
     *          <code>JDesktopPane.OUTLINE_DRAG_MODE</code> or
     *          <code>false</code> if desktop's <code>DragMode</code> is
     *          <code>JDesktopPane.LIVE_DRAG_MODE</code>.
     */
    public boolean getOutlineDragMode() {
        return (m_outlineDragMode);
    }

    /**
     * Sets de-iconifiable policy. Should we force a frame to de-iconify
     * if it is iconified, during cascade operations?
     * <p>
     * @param p     <code>true</code> if force, <code>false</code> otherwise.
     */
    public void setDeiconifiablePolicy(boolean p) {
        m_deiconifiablePolicy = p;
    }

    /**
     * Gets the de-iconifiable policy.
     * <p>
     * @return  The de-iconifiable policy.
     */
    public boolean getDeiconifiablePolicy() {
        return (m_deiconifiablePolicy);
    }

    /**
     * Sets the close policy. Should we do default close operation per frame
     * or force close it?
     * <p>
     * @param p     <code>true</code> to force close,
     *              <code>false</code> do default close operation.
     */
    public void setClosePolicy(boolean p) {
        m_closePolicy = p;
    }

    /**
     * Gets the close policy.
     * <p>
     * @return  <code>true</code> for force close,
     *          <code>false</code> for default close operation.
     */
    public boolean getClosePolicy() {
        return (m_closePolicy);
    }

    /**
     * Sets the auto position frames policy. Should we auto position the
     * new frames in the desktop or not?
     * <p>
     * @param p     <code>true</code> for auto-position,
     *              <code>false</code> for none.
     */
    public void setAutoPositionPolicy(boolean p) {
        m_autoPositionPolicy = p;
    }

    /**
     * Gets the auto position frames policy.
     * <p>
     * @return  The auto-position policy.
     */
    public boolean getAutoPositionPolicy() {
        return (m_autoPositionPolicy);
    }

    /**
     * Counts all frames, even those that are closed with
     * <code>DefaultCloseOperation = HIDE_ON_CLOSE</code>
     * <p>
     * <b>NOTE</b>Use this method in order to understand how many
     * "ghost" frames remain within the desktop.
     * <p>
     * @return  The number of frames.
     */
    public int countFrames() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return (0);
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        return (frames.length );
    }

    /**
     * Counts only visible frames.
     * <p>
     * @return  The number of visible frames.
     */
    public int countVisibleFrames() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return (0);
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        int count = 0;
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].isVisible()) {
                count++;
            }
        }

        return (count);
    }

    /**
     * Closes the selected frame if it is closable.
     */
    public void close() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        close_frame(activeframe);
    }

    /**
     * Closes all frames that are closable.
     */
    public void closeAll() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            close_frame(frames[i]);
        }
    }

    /**
     * Minimizes the selected frame if it is iconifiable.
     */
    public void minimize() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        iconify_frame(activeframe);
    }

    /**
     * Minimizes all frames that are iconifiable.
     */
    public void minimizeAll() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not minimize it!
            if (!frames[i].isVisible())
                continue;

            iconify_frame(frames[i]);
        }
    }

    /**
     * Restores the selected frame from it's maximized state.
     */
    public void restore() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        if (activeframe != null) {
            if (activeframe.isMaximum() || activeframe.isIcon()) {
                // if it is minimized, it must be deiconified
                deiconify_frame(activeframe);

                restore_frame(activeframe);
            }
        }
    }

    /**
     * Restores all frames from their maximized state.
     */
    public void restoreAll() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not restore it!
            if (!frames[i].isVisible())
                continue;

            if (frames[i].isMaximum() || frames[i].isIcon()) {
                // if it is minimized, it must be deiconified
                deiconify_frame(frames[i]);

                restore_frame(frames[i]);
            }
        }
    }

    /**
     * Maximizes the selected frame if it is not already maximized.
     */
    public void maximize() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        maximize_frame(activeframe);
    }

    /**
     * Maximizes all frames that are not already maximized.
     */
    public void maximizeAll() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not maximize it!
            if (!frames[i].isVisible())
                continue;

            maximize_frame(frames[i]);
        }
    }

    /**
     * Resets the frame to it's original preferred size of its components.
     */
    public void reset() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        reset_frame(activeframe);
    }

    /**
     * Resets all frames to their original preferred size of their components.
     */
    public void resetAll() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not pack() it!
            if (!frames[i].isVisible())
                continue;

            reset_frame(frames[i]);
        }
    }

    /**
     * Hides a frame. The hidden frame is added to the menu with
     * disabled-like color.
     */
    public void hide() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        if (m_windowsMenu == null) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        hide_frame(activeframe);
    }

    /**
     * Hides all frames. The hidden frames are added to the menu with
     * disabled-like color.
     */
    public void hideAll() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        if (m_windowsMenu == null) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        for (int i = 0; i < frames.length; i++) {
            // this frame is already hidden!
            if (!frames[i].isVisible())
                continue;

            hide_frame(frames[i]);
        }
    }

    /**
     * Selects the next internal frame.
     */
    public void selectNext() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        // we need at least 2 frames to perform next window!
        if (frames.length < 2) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        if (activeframe == null) {
            return;
        }

        // NOTE: IT SEEMS IF WE DO NOT SORT THE FRAMES WE CANNOT SELECT
        //       THE NEXT FRAME CORRECTLY!!!
        // sort frames by title
        Arrays.sort(frames, m_frameComparator);

        int next_frame = -1;
        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not select it!
            if (!frames[i].isVisible())
                continue;

            // find index of selected frame
            if (frames[i] != activeframe)
                continue;

            // check all remaining frames ahead, if they can be selected
            for (int j = i + 1; j < frames.length; j++) {
                if (!frames[j].isIcon()) {
                    next_frame = j;
                    break;
                }
            }

            // if no frame found, check from the start
            if (next_frame == -1) {
                for (int k = 0; k < i; k++) {
                    if (!frames[k].isIcon()) {
                        next_frame = k;
                        break;
                    }
                }
            }

            if (next_frame != -1)
                break;
        }

        if (next_frame != -1) {
            select_frame(frames[next_frame]);
        }
    }

    /**
     * Selects the previous internal frame.
     */
    public void selectPrevious() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        // we need at least 2 frames to perform previous window!
        if (frames.length < 2) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();

        if (activeframe == null) {
            return;
        }

        // NOTE: IT SEEMS IF WE DO NOT SORT THE FRAMES WE CANNOT SELECT
        //       THE PREVIOUS FRAME CORRECTLY!!!
        // sort frames by title
        Arrays.sort(frames, m_frameComparator);

        int previous_frame = -1;
        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not select it!
            if (!frames[i].isVisible())
                continue;

            // find index of selected frame
            if (frames[i] != activeframe)
                continue;

            // check all previous frames, if they can be selected
            for (int j = i - 1; j >= 0; j--) {
                if (!frames[j].isIcon()) {
                    previous_frame = j;
                    break;
                }
            }

            // if no frame found, check from the end
            if (previous_frame == -1) {
                for (int k = frames.length - 1; k > i; k--) {
                    if (!frames[k].isIcon()) {
                        previous_frame = k;
                        break;
                    }
                }
            }

            if (previous_frame != -1)
                break;
        }

        if (previous_frame != -1) {
            select_frame(frames[previous_frame]);
        }
    }

    /**
     * Cascades all frames. If De-iconifiablePolicy is false, minimized frames
     * stay minimized.During cascade, it also sorts frames based on their title.
     */
    public void cascade() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame activeframe = m_desktop.getSelectedFrame();
        JInternalFrame[] frames = m_desktop.getAllFrames();

        // sort frames by title
        Arrays.sort(frames, m_frameComparator);

        int x = 0;
        int y = 0;
        int width = m_desktop.getWidth() / 2;
        int height = m_desktop.getHeight() / 2;

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not cascade it!
            if (!frames[i].isVisible())
                continue;

            // should we cascade also the iconified frames?
            if (m_deiconifiablePolicy == false && frames[i].isIcon())
                continue;

            deiconify_frame(frames[i]);

            frames[i].reshape(x, y, width, height);

            frames[i].moveToFront();

            if (frames[i] != activeframe) {
                int next_pos = frames[i].getHeight()
                        - frames[i].getContentPane().getHeight();

                frames[i].setLocation(x, y);
                x += next_pos;
                y += next_pos;
            }

            // wrap around at the desktop edge
            if ((x + width) > m_desktop.getWidth()) {
                x = 0;
            }
            if ((y + height) > m_desktop.getHeight()) {
                y = 0;
            }
        }

        // last frame will be the selected frame, if any.
        if (activeframe != null) {
            activeframe.moveToFront();
            activeframe.setLocation(x, y);
        }
    }

    /**
     * Tiles all windows vertically.
     */
    public void tileVertically() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        int visibleFrames = 0;
        for (int i = 0; i < frames.length; i++) {
            if (!frames[i].isVisible())
                continue;

            visibleFrames++;
        }

        if (visibleFrames == 0)
            return;

        // sort frames by title
        Arrays.sort(frames, m_frameComparator);

        int width = m_desktop.getWidth() / visibleFrames;
        int height = m_desktop.getHeight();
        int x = 0;

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not tile it!
            if (!frames[i].isVisible())
                continue;

            try {
                frames[i].setMaximum(false);
                frames[i].setIcon(false);
                frames[i].moveToFront();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            frames[i].reshape(x, 0, width, height);

            x += width;
        }
    }

    /**
     * Tiles all windows horizontally.
     */
    public void tileHorizontally() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        int visibleFrames = 0;
        for (int i = 0; i < frames.length; i++) {
            if (!frames[i].isVisible())
                continue;

            visibleFrames++;
        }

        if (visibleFrames == 0)
            return;

        // sort frames by title
        Arrays.sort(frames, m_frameComparator);

        int width = m_desktop.getWidth();
        int height = m_desktop.getHeight() / visibleFrames;
        int y = 0;

        for (int i = 0; i < frames.length; i++) {
            // if DefaultCloseOperation for a JInternalFrame is HIDE_ON_CLOSE
            // this frame is invisible and so we should not tile it!
            if (!frames[i].isVisible())
                continue;

            try {
                frames[i].setMaximum(false);
                frames[i].setIcon(false);
                frames[i].moveToFront();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            frames[i].reshape(0, y, width, height);

            y += height;
        }
    }

    /**
     * Tiles all windows equally.
     */
    public void tile() {
        if (m_desktop == null || m_desktop.getAllFrames() == null
                || m_desktop.getAllFrames().length < 1) {
            return;
        }

        JInternalFrame[] frames = m_desktop.getAllFrames();

        int visibleFrames = 0;
        for (int i = 0; i < frames.length; i++) {
            if (!frames[i].isVisible())
                continue;

            visibleFrames++;
        }

        if (visibleFrames == 0)
            return;

        // sort frames by title
        Arrays.sort(frames, m_frameComparator);

        // create a matrix
        int sqrt = (int) Math.sqrt(frames.length);
        int numRows = sqrt;
        int numCols = sqrt;

        // because of possible precision loss, fix the matrix size
        if (numRows * numCols < frames.length) {
            numCols++;
            if (numRows * numCols < frames.length) {
                numRows++;
            }
        }

        int width = m_desktop.getWidth() / numCols;
        int height = m_desktop.getHeight() / numRows;
        int x = 0;
        int y = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int index = (i * numCols) + j;

                if (index >= frames.length)
                    break;

                // if DefaultCloseOperation for a JInternalFrame is
                // HIDE_ON_CLOSE then this frame is invisible and so
                // we should not tile it!
                if (!frames[index].isVisible())
                    continue;

                try {
                    frames[index].setMaximum(false);
                    frames[index].setIcon(false);
                    frames[index].moveToFront();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                frames[index].reshape(x, y, width, height);

                x += width;
            }

            y += height;
            x = 0;
        }
    }

    /**
     * A ContainerListener for <code>JDesktopPane</code> whenever a frame is
     * added/removed from the desktop, the windows menu is being rebuilt to
     * add/remove the new frame as a radio button. Also has a
     * <code>manualFireEvent()</code> method to manually rebuild the menu.
     */
    private final class FrameListener implements ContainerListener {

        /**
         * Rebuilds the menu.
         */
        private void rebuild_menu() {
            if (m_desktop != null && m_windowsMenu != null) {
                // remove old items
                while (m_windowsMenu.getMenuComponentCount() > m_lastMenuItemsCount) {
                    m_windowsMenu
                            .remove(m_windowsMenu.getMenuComponentCount() - 1);
                }

                m_radioMenuItemsAndFrames.clear();
                m_framesAndRadioMenuItems.clear();

                // create a radio button for each frame in desktop
                JInternalFrame[] frames = m_desktop.getAllFrames();

                // sort frames by title
                Arrays.sort(frames, m_frameComparator);
                
                // added by SWI
                if (frames.length > 0) {
                    m_windowsMenu.addSeparator();
                }

                for (int i = 0; i < frames.length; i++) {
                    JRadioButtonMenuItem item = new JRadioButtonMenuItem(""
                            + (i + 1) + ": " + frames[i].getTitle(), frames[i]
                            .isSelected());
                    
                    // added by SWI
                    item.setMnemonic(Integer.toString(i + 1).toCharArray()[0]);

                    // change color for hidden frames
                    if (!frames[i].isVisible()) {
                        item
                                .setForeground(UIManager
                                        .getColor("RadioButtonMenuItem.disabledForeground"));
                    }
                    /*else
                    {
                        item.setForeground( UIManager.getColor(
                            "RadioButtonMenuItem.foreground") );
                    }*/

                    item.addActionListener(m_radioMenuItemListener);
                    m_windowsMenu.add(item);

                    m_radioMenuItemsAndFrames.put(item, frames[i]);
                    m_framesAndRadioMenuItems.put(frames[i], item);
                }
            }
        }

        /**
         * Manually rebuilds the menu.
         */
        public void manualFireEvent() {
            rebuild_menu();
        }

        /**
         * Component added <code>Container</code> event.
         * <p>
         * @param ce    The <code>ContainerEvent</code>.
         */
        public void componentAdded(ContainerEvent ce) {
            // auto position new frames?
            if (m_desktop != null && m_autoPositionPolicy == true
                    && ce.getChild() != null
                    && ce.getChild() instanceof JInternalFrame) {
                JInternalFrame jif = (JInternalFrame) ce.getChild();
                int w = jif.getWidth();
                int h = jif.getHeight();

                //---
                // fix suggested by Kostas Filippaios
                // if this is the first JInternalFrame added,
                // reset the position to 0,0
                if (countFrames() == 1) {
                    m_nextFramePos.setLocation(0, 0);
                }
                //---

                jif.setLocation(m_nextFramePos);

                int next_pos = h - jif.getContentPane().getHeight();
                m_nextFramePos.x += next_pos;
                m_nextFramePos.y += next_pos;

                // wrap around at the desktop edge
                if ((m_nextFramePos.x + w) > m_desktop.getWidth()) {
                    m_nextFramePos.x = 0;
                }
                if ((m_nextFramePos.y + h) > m_desktop.getHeight()) {
                    m_nextFramePos.y = 0;
                }
            }

            // attach listener to new frame
            if (m_desktop != null) {
                JInternalFrame[] frames = m_desktop.getAllFrames();
                for (int i = 0; frames != null && i < frames.length; i++) {
                    // skip if frame already attached!
                    if (is_internal_frame_listener_attached(frames[i]))
                        continue;

                    frames[i].addInternalFrameListener(m_selectFrameListener);
                }
            }

            rebuild_menu();
        }

        /**
         * Component removed <code>Container</code> event.
         * <p>
         * @param ce    The <code>ContainerEvent</code>.
         */
        public void componentRemoved(ContainerEvent ce) {
            rebuild_menu();
        }

    }

    /**
     * Whenever a radio button is selected, the related frame
     * will be made visible and selected too.
     */
    private final class MenuItemActionListener implements ActionListener {

        /**
         * Selects the related frame and makes it visible.
         * <p>
         * @param ae    The <code>ActionEvent</code>.
         */
        public void actionPerformed(ActionEvent ae) {
            Object o = ae.getSource();

            if (o instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem) o;

                JInternalFrame frame = (JInternalFrame) m_radioMenuItemsAndFrames
                        .get(item);

                if (item.isSelected()) {
                    try {
                        if (!frame.isVisible()) {
                            frame.setVisible(true);
                            frame.toFront();
                        }

                        if (!frame.isSelected()) {
                            frame.setSelected(true);
                        }

                        if (frame.isIcon()) {
                            frame.setIcon(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // we cannot deselect the radio button, makes no sense...
                    item.setSelected(true);
                }

                // rebuild the windows menu
                m_frameListener.manualFireEvent();
            }
        }

    }

    /**
     * Internal frame listener.
     */
    private final class SelectFrameListener implements InternalFrameListener {

        /**
         * Internal frame activated event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameActivated(InternalFrameEvent ife) {
            JInternalFrame jif = ife.getInternalFrame();

            JRadioButtonMenuItem item = (JRadioButtonMenuItem) m_framesAndRadioMenuItems
                    .get(jif);

            item.setSelected(true);
        }

        /**
         * Internal frame closed event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameClosed(InternalFrameEvent ife) {
        }

        /**
         * Internal frame closing event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameClosing(InternalFrameEvent ife) {
        }

        /**
         * Internal frame deactivated event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameDeactivated(InternalFrameEvent ife) {
            JInternalFrame jif = ife.getInternalFrame();

            JRadioButtonMenuItem item = (JRadioButtonMenuItem) m_framesAndRadioMenuItems
                    .get(jif);

            item.setSelected(false);

            // if this is a normal frame e.g. DISPOSE_ON_CLOSE and we close
            // it from X then a ContainerListener componentRemoved() will fire
            // so we will rebuild the windows menu BUT if this is a
            // HIDE_ON_CLOSE frame componentRemoved() is never invoked so we
            // need to call this in order or apply the disabled color to this
            // hidden frame...
            if (jif.getDefaultCloseOperation() == WindowConstants.HIDE_ON_CLOSE) {
                // rebuild the windows menu
                m_frameListener.manualFireEvent();
            }
        }

        /**
         * Internal frame deiconified event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameDeiconified(InternalFrameEvent ife) {
        }

        /**
         * Internal frame iconified event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameIconified(InternalFrameEvent ife) {
        }

        /**
         * Internal frame opened event method.
         * <p>
         * @param ife   The <code>InternalFrameEvent</code>.
         */
        public void internalFrameOpened(InternalFrameEvent ife) {
        }

    }

}
