package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
//import java.awt.Robot;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ReaderApp extends Frame implements WindowListener, Runnable {
	private static final long serialVersionUID = 1L;

	static ReaderApp instance;

	public static void main(String[] args){
		instance = new ReaderApp(JOptionPane.showInputDialog(null, "Please type in the file's path!"));
		instance.setVisible(true);
		new Thread(instance, "QR Thread").start();
	}

	String imgFilePath = "qr0.png";
	int width, height;
	int[] pixels;

	int displayWidth, displayHeight;
	BufferedImage img, imgBlurred, imgSobelMasked;
	BufferedImage imgEdges/* , imgHough */, imgDebug, imgQR;

//	Robot robot;
	CannyEdgeDetector edgeDetector;
	// HoughTransform houghTransform;
	FinderPatternFinder finderPatternFinder;
	List<FinderPattern> finderPatterns;
	QRParser parser;

	int qrSize;
	boolean[][] qrData;

	public ReaderApp(String fileName) {
		super("QR Reader");
		this.imgFilePath = fileName;
		this.addWindowListener(this);
	}

	public void preProcess() {
		this.pixels = new int[this.width * this.height];
		for (int x = 0; x < this.width; x++)
			for (int y = 0; y < this.height; y++) {
				int rgb = this.img.getRGB(x, y);
				this.pixels[y * this.width + x] = (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 3;
			}
	}

	public void process() {
		this.edgeDetector = new CannyEdgeDetector();
		this.edgeDetector.setData(this.width, this.height, this.pixels);
		this.edgeDetector.process();
		// this.houghTransform = new HoughTransform();
		// this.houghTransform.setData(this.width, this.height,
		// this.edgeDetector.getOutput());
		// this.houghTransform.transform();
		this.finderPatternFinder = new FinderPatternFinder();
		this.finderPatternFinder.setData(this.width, this.height, this.edgeDetector.getOutput());
		this.finderPatternFinder.process();
		this.finderPatterns = this.finderPatternFinder.getOutput();
		this.parser = new QRParser();
		this.parser.setData(this.width, this.height, this.pixels, this.edgeDetector.getOutput(), this.finderPatterns);
		this.parser.parse();
		this.qrSize = this.parser.getOutputSize();
		this.qrData = this.parser.getOutput();
	}

	public void postProcess() {
		this.imgBlurred = this.edgeDetector.getBlured();
		this.imgSobelMasked = this.edgeDetector.getSobelMasked();
		this.imgEdges = this.edgeDetector.getTraced();
		// this.imgHough = this.houghTransform.getOutputAsImg();
		this.imgQR = this.parser.getOutputAsImg();
		this.imgDebug = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = this.imgDebug.createGraphics();
		g.drawImage(this.imgBlurred, 0, 0, this.width, this.height, null);
		g.setColor(Color.red);
		for (FinderPattern pattern : this.finderPatterns)
			for (int i = 0; i < 3; i++)
				g.drawRect((int) (pattern.x - pattern.one * 7 / 2) - i, (int) (pattern.y - pattern.one * 7 / 2) - i,
						(int) (pattern.one * 7) + 2 * i, (int) (pattern.one * 7) + 2 * i);
		if(this.parser.examined != null) {
			g.setColor(Color.green);
			for (int i = 0; i < this.width; i++)
				for (int j = 0; j < this.height; j++)
					if (this.parser.examined[j * this.width + i])
						g.fillArc(i - 1, j - 1, 3, 3, 0, 360);
		}
	}

	public void init() throws Exception {
//		this.robot = new Robot();

		this.img = ImageIO.read(new File(imgFilePath));
		if(500 < this.img.getWidth() || 500 < this.img.getHeight()) {
			float ratio = Math.min(500F/this.img.getWidth(), 500F/this.img.getHeight());
			Image scaledImg = this.img.getScaledInstance((int)(this.img.getWidth()*ratio), (int)(this.img.getHeight()*ratio), Image.SCALE_SMOOTH);
			this.img = new BufferedImage((int)(this.img.getWidth()*ratio), (int)(this.img.getHeight()*ratio), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = this.img.createGraphics();
			g.drawImage(scaledImg, 0, 0, null);
		}
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();
		this.displayWidth = this.width;
		this.displayHeight = this.height;

		this.setSize(this.width * 3, this.height * 2);
		// this.setResizable(false);

		this.preProcess();
		long time = System.currentTimeMillis();
		this.process();
		time = System.currentTimeMillis() - time;
		this.postProcess();
		this.setTitle("QR Reader - " + time + " ms");
	}

	public void render() {
		if (this.getBufferStrategy() == null)
			this.createBufferStrategy(2);
		Graphics g = this.getBufferStrategy().getDrawGraphics();

		if (this.getWidth() / 3 < this.displayWidth || this.getHeight() / 2 < this.displayHeight
				|| (this.displayWidth < this.getWidth() / 3 && this.displayHeight < this.getHeight() / 2)) {
			float ratio = Math.min(this.getWidth() / 3 / (float) this.displayWidth, this.getHeight() / 2 / (float) this.displayHeight);
			this.displayWidth *= ratio;
			this.displayHeight *= ratio;
		}
		g.drawImage(this.img, 0, 0, this.displayWidth, this.displayHeight, null);
		g.drawImage(this.imgBlurred, this.displayWidth, 0, this.displayWidth, this.displayHeight, null);
		g.drawImage(this.imgSobelMasked, this.displayWidth * 2, 0, this.displayWidth, this.displayHeight, null);
		g.drawImage(this.imgEdges, 0, this.displayHeight, this.displayWidth, this.displayHeight, null);
		// g.drawImage(this.imgHough, this.displayWidth, this.displayHeight,
		// this.displayWidth, this.displayHeight, null);
		// g.drawImage(this.imgHough, this.displayWidth, this.displayHeight,
		// null);
		g.drawImage(this.imgDebug, this.displayWidth, this.displayHeight, this.displayWidth, this.displayHeight, null);
		g.drawImage(this.imgQR, this.displayWidth * 2, this.displayHeight, this.displayHeight, this.displayHeight, null);

		g.dispose();
		this.getBufferStrategy().show();
	}

	@Override
	public void run() {
		try {
			this.init();
			while (true) {
				long time = System.currentTimeMillis();
				this.render();
				time = System.currentTimeMillis() - time;
				time = 1000 / 25 - time;
				if (time > 0)
					Thread.sleep(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "UNEXPECTED ERROR!", "BDG-QR ERROR", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}

	@Override
	public void paint(Graphics g) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
