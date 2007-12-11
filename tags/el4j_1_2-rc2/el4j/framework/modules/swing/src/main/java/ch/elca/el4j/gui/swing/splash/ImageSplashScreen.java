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
package ch.elca.el4j.gui.swing.splash;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Shows an image as GUI splash screen. Typical usage pattern:
 * <ul>
 * <li> Display splash screen (early to show something to user rapidly)
 * <li> Make some application initialization, such as set up spring app context
 * <li> Launch the main application window via GuiApplication.launch
 * </ul>
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philipp Oser (POS)
 * @author Stefan Wismer (SWI)
 */
public class ImageSplashScreen {
    
    /**
     * Default EL4J splash screen gif.
     */
    public static final String DEFAULT_SPLASH_SCREEN
        = "/ch/elca/el4j/gui/swing/splash/resources/splash-screen.gif";
    
    /**
     * The frame containing the splash image.
     */
    private Frame m_frame;
    
    /**
     * The splash image.
     */
    private Image m_image;
    
    /**
     * The resource path where the image is located.
     */
    private String m_imageResourcePath;

    /**
     * The splash window.
     */
    private static class SplashWindow extends Window {
        /**
         * The splash image.
         */
        private Image m_splashImage;
        
        /**
         * @param parent    the parent frame
         * @param image     the splash image
         */
        public SplashWindow(Frame parent, Image image) {
            super(parent);
            this.m_splashImage = image;
            setSize(parent.getSize());
            setLocation(parent.getLocation());
            setVisible(true);
        }
        
        /** {@inheritDoc} */
        @Override
        public void paint(Graphics graphics) {
            if (m_splashImage != null) {
                graphics.drawImage(m_splashImage, 0, 0, this);
            }
        }
    }
    
    
    /**
     * Display splash screen with default this image.
     */
    public ImageSplashScreen() {
        this(DEFAULT_SPLASH_SCREEN);
    }

    /**
     * Display splash screen with this image.
     * 
     * @param imageResourcePath
     *            path to image file
     */
    public ImageSplashScreen(String imageResourcePath) {
        this.m_imageResourcePath = imageResourcePath;
        splash();
    }

    /**
     * Display splash screen with this image.
     * 
     * @param image
     *            image to show
     */
    public ImageSplashScreen(Image image) {
        this.m_image = image;
        splash();
    }

    /**
     * Show the splash screen.
     */
    protected void splash() {
        m_frame = new Frame();
        if (m_image == null) {
            m_image = loadImage(m_imageResourcePath);
            if (m_image == null) {
                return;
            }
        }
        MediaTracker mediaTracker = new MediaTracker(m_frame);
        mediaTracker.addImage(m_image, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            return;
        }
        m_frame.setSize(m_image.getWidth(null), m_image.getHeight(null));
        center();
        new SplashWindow(m_frame, m_image);
    }

    /**
     * Stop this splash screen in the EDT.
     */
    public void dispose() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                m_frame.dispose();
                m_frame = null;
            }
        });
    }

    /**
     * @param path    path to image file
     * @return        the loaded image
     */
    private Image loadImage(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            return null;
        } else {
            return Toolkit.getDefaultToolkit().createImage(url);
        }
    }

    /**
     * Center the splash screen.
     */
    private void center() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle r = m_frame.getBounds();
        m_frame.setLocation((screen.width - r.width) / 2,
            (screen.height - r.height) / 2);
    }
}
