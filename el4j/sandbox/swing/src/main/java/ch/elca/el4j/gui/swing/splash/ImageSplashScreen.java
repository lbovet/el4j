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
 * Shows an image as GUI splash screen.
 *  Typical usage pattern:
 *  	<ul>
 *  	  <li> Display splash screen (early to show something to user rapidly)
 *   	  <li> Make some application initialization, such as set up spring app context
 *   	  <li> Launch the main application window via GuiApplication.launch
 *  	</ul>
 * 
 * @author pos
 *
 */
public class ImageSplashScreen {

    /**
     * Default EL4J splash screen gif
     */
    public static final String DEFAULT_SPLASH_SCREEN = "/ch/elca/el4j/gui/swing/splash/resources/splash-screen.gif";
	
	
	private static class SplashWindow extends Window {

		public void paint(Graphics graphics) {
			if (image != null)
				graphics.drawImage(image, 0, 0, this);
		}

		private Image image;

		public SplashWindow(Frame parent, Image image) {
			super(parent);
			this.image = image;
			setSize(parent.getSize());
			setLocation(parent.getLocation());
			setVisible(true);
		}
	}

	/**
	 * Display splash screen with default this image
	 */
	public ImageSplashScreen() {
		this(DEFAULT_SPLASH_SCREEN);
	}	
	
	/**
	 * Display splash screen with this image
	 * @param imageResourcePath path to image file
	 */
	public ImageSplashScreen(String imageResourcePath) {
		this.imageResourcePath = imageResourcePath;
		splash();
	}

	/**
	 * Display splash screen with this image
	 * @param image image to show
	 */
	public ImageSplashScreen(Image image) {
		this.image = image;
		splash();
	}

	protected void splash() {
		frame = new Frame();
		if (image == null) {
			image = loadImage(imageResourcePath);
			if (image == null)
				return;
		}
		MediaTracker mediaTracker = new MediaTracker(frame);
		mediaTracker.addImage(image, 0);
		try {
			mediaTracker.waitForID(0);
		} catch (InterruptedException e) {
		}
		frame.setSize(image.getWidth(null), image.getHeight(null));
		center();
		new SplashWindow(frame, image);
	}

	/**
	 * Stop this splash screen in the EDT 
	 */
	public void dispose() {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
        		frame.dispose();
        		frame = null;
            }
        });
	}

	private Image loadImage(String path) {
		java.net.URL url = getClass().getResource(path);
		if (url == null) {
			return null;
		} else {
			return Toolkit.getDefaultToolkit().createImage(url);
		}
	}

	private void center() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle r = frame.getBounds();
		frame.setLocation((screen.width - r.width) / 2,
				(screen.height - r.height) / 2);
	}

	private Frame frame;
	private Image image;
	private String imageResourcePath;

}
