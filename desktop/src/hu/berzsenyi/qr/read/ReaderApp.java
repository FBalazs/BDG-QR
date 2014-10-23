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
		instance = new ReaderApp();
		instance.setVisible(true);
		new Thread(instance, "QR Thread").start();
	}
	
	Configuration config;
	
	int width, height;
	int[] pixels;

	int displayWidth, displayHeight;
	BufferedImage img, imgBlurred, imgSobelMasked;
	BufferedImage imgEdges/* , imgHough */, imgDebug;

//	Robot robot;
	CannyEdgeDetector edgeDetector;
	// HoughTransform houghTransform;
	FinderPatternFinder finderPatternFinder;
	List<FinderPattern> finderPatterns;
	QRParser parser;
	
	List<QRBitmap> qrCodes;
	BufferedImage[] qrImgs;

	public ReaderApp() {
		super("QR Reader");
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
		System.out.println("Detecting edges...");
		this.edgeDetector = new CannyEdgeDetector(this.cannyGausR, this.cannyGausT, this.cannyLT, this.cannyHT);
		this.edgeDetector.setData(this.width, this.height, this.pixels);
		this.edgeDetector.process();
		System.out.println("Edge detection complete.");
		// this.houghTransform = new HoughTransform();
		// this.houghTransform.setData(this.width, this.height,
		// this.edgeDetector.getOutput());
		// this.houghTransform.transform();
		System.out.println("Finding the finder patterns...");
		this.finderPatternFinder = new FinderPatternFinder(this.finderPatternFinderRange, this.finderPatternFinderRange2);
		this.finderPatternFinder.setData(this.width, this.height, this.edgeDetector.getOutput());
		this.finderPatternFinder.process();
		this.finderPatterns = this.finderPatternFinder.getOutput();
		System.out.println("Found "+this.finderPatterns.size()+" finder patterns.");
		System.out.println("Parsing qr codes...");
		this.parser = new QRParser();
		this.parser.setData(this.width, this.height, this.pixels, this.edgeDetector.getOutput(), this.finderPatterns);
		this.parser.parse();
		this.qrCodes = this.parser.getOutput();
		System.out.println("Parsing done.");
	}

	public void postProcess() {
		this.imgBlurred = this.edgeDetector.getBlured();
		this.imgSobelMasked = this.edgeDetector.getSobelMasked();
		this.imgEdges = this.edgeDetector.getTraced();
		// this.imgHough = this.houghTransform.getOutputAsImg();
		this.qrImgs = new BufferedImage[this.qrCodes.size()];
		for(int i = 0; i < this.qrCodes.size(); i++)
			this.qrImgs[i] = this.qrCodes.get(i).getAsImage();
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
	
	int maxSize = 720;
	float finderPatternFinderRange = 0.5F;
	int finderPatternFinderRange2 = 100;
	int cannyGausR = 2;
	float cannyGausT = 1F;
	float cannyLT = 150F;
	float cannyHT = 250F;
	
	public void init() throws Exception {
//		this.robot = new Robot();
		
		this.config = new Configuration("config.txt");
		this.config.read();
		String file = this.config.getValue("img", "");
		this.maxSize = Integer.parseInt(this.config.getValue("maxSize", ""+this.maxSize));
		this.finderPatternFinderRange = Float.parseFloat(this.config.getValue("finderPatternFinderRange", ""+this.finderPatternFinderRange));
		this.finderPatternFinderRange2 = Integer.parseInt(this.config.getValue("finderPatternFinderRange2", ""+this.finderPatternFinderRange2));
		this.cannyGausR = Integer.parseInt(this.config.getValue("cannyGausR", ""+this.cannyGausR));
		this.cannyGausT = Float.parseFloat(this.config.getValue("cannyGausT", ""+this.cannyGausT));
		this.cannyLT = Float.parseFloat(this.config.getValue("cannyLT", ""+this.cannyLT));
		this.cannyHT = Float.parseFloat(this.config.getValue("cannyHT", ""+this.cannyHT));
		this.config.write();
		
		if(file.equals(""))
			file = JOptionPane.showInputDialog(null, "Please type in the file's path!");
		this.img = ImageIO.read(new File(file));
		if(this.maxSize < this.img.getWidth() || this.maxSize < this.img.getHeight()) {
			float ratio = Math.min(this.maxSize/(float)this.img.getWidth(), this.maxSize/(float)this.img.getHeight());
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
		int s;
		for(s = 1; s*s < this.qrImgs.length; s++);
		int whm = Math.min(this.displayWidth, this.displayHeight);
		for(int i = 0; i < this.qrImgs.length; i++)
			g.drawImage(this.qrImgs[i], this.displayWidth*2 + whm*(i%s)/s, this.displayHeight + whm*(i/s)/s, whm/s, whm/s, null);
		//g.drawImage(this.imgQR, this.displayWidth * 2, this.displayHeight, this.displayHeight, this.displayHeight, null);

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
