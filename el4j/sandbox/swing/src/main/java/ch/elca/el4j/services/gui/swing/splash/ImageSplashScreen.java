package ch.elca.el4j.services.gui.swing.splash;

import java.awt.*;

public class ImageSplashScreen implements SplashScreen {

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

	public ImageSplashScreen(String imageResourcePath) {
		setImageResourcePath(imageResourcePath);
	}

	public ImageSplashScreen(Image image) {
		this.image = image;
	}

	public void setImageResourcePath(String path) {
		imageResourcePath = path;
	}

	public void splash() {
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

	public void dispose() {
		frame.dispose();
		frame = null;
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
