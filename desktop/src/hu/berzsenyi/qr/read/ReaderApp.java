package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

public class ReaderApp extends Frame implements WindowListener, Runnable {
	private static final long serialVersionUID = 1L;
	
	static ReaderApp instance;

	public static void main(String[] args) throws Exception {
		instance = new ReaderApp();
		instance.setVisible(true);
		new Thread(instance, "QR Thread").start();
	}
	
	String imgFilePath = "qr2.png";
	int width, height;
	int[] pixels;
	
	BufferedImage img, imgBlurred, imgSobelMasked;
	BufferedImage imgEdges/*, imgHough*/, imgQR;
	
	Robot robot;
	CannyEdgeDetector edgeDetector;
//	HoughTransform houghTransform;
	FinderPatternFinder finderPatternFinder;
	List<FinderPattern> finderPatterns;
	QRParser parser;
	
	int qrSize;
	boolean[][] qrData;
	
	public ReaderApp() throws Exception {
		super("QR Reader");
		this.addWindowListener(this);
	}
	
	public void preProcess() {
		this.pixels = new int[this.width*this.height];
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++) {
				int rgb = this.img.getRGB(x, y);
				this.pixels[y*this.width+x] = (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255))/3;
			}
	}
	
	public void process() {
		this.edgeDetector = new CannyEdgeDetector();
		this.edgeDetector.setData(this.width, this.height, this.pixels);
		this.edgeDetector.process();
//		this.houghTransform = new HoughTransform();
//		this.houghTransform.setData(this.width, this.height, this.edgeDetector.getOutput());
//		this.houghTransform.transform();
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
		//this.imgHough = this.houghTransform.getOutputAsImg();
		this.imgQR = this.parser.getOutputAsImg();
	}
	
	public void init() throws Exception {
		this.robot = new Robot();
		
		this.img = ImageIO.read(new File(imgFilePath));
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();
		
		this.setSize(this.width*3, this.height*2);
		this.setResizable(false);
		
		this.preProcess();
		long time = System.currentTimeMillis();
		this.process();
		time = System.currentTimeMillis()-time;
		this.postProcess();
		this.setTitle("QR Reader - "+time+" ms");
	}
	
	public void render() {
		if(this.getBufferStrategy() == null)
			this.createBufferStrategy(2);
		Graphics g = this.getBufferStrategy().getDrawGraphics();
		
		g.drawImage(this.img, 0, 0, null);
		g.drawImage(this.imgBlurred, this.width, 0, null);
		g.drawImage(this.imgSobelMasked, this.width*2, 0, null);
		g.drawImage(this.imgEdges, 0, this.height, null);
		//g.drawImage(this.imgHough, this.width, this.height, this.width*2, this.height*2, 0, 0, imgHough.getWidth(), imgHough.getHeight(), null);
		//g.drawImage(this.imgHough, this.width, this.height, null);
		g.drawImage(this.imgBlurred, this.width, this.height, null);
		g.setColor(Color.red);
		for(FinderPattern pattern : this.finderPatterns)
			for(int i = 0; i < 3; i++)
				g.drawRect(this.width+(int)(pattern.x-pattern.one*7/2)-i, this.height+(int)(pattern.y-pattern.one*7/2)-i, (int)(pattern.one*7)+2*i, (int)(pattern.one*7)+2*i);
			//g.drawArc(this.width+(int)(pattern.x-pattern.one*7/2), this.height+(int)(pattern.y-pattern.one*7/2), (int)(pattern.one*7), (int)(pattern.one*7), 0, 360);
		g.setColor(Color.green);
		for(int i = 0; i < this.width; i++) {
			for(int j = 0; j < this.height; j++) {
				if(this.parser.examined[j*this.width+i])
					g.fillArc(this.width+i-1, this.height+j-1, 3, 3, 0, 360);
			}
		}
		
		g.drawImage(this.imgQR, this.width*2, this.height, this.width*2+this.height, this.height*2, 0, 0, this.qrSize, this.qrSize, null);
		
		g.dispose();
		this.getBufferStrategy().show();
	}
	
	@Override
	public void run() {
		try {
			this.init();
			while(true) {
				long time = System.currentTimeMillis();
				this.render();
				time = System.currentTimeMillis()-time;
				time = 1000/25-time;
				if(time > 0)
					Thread.sleep(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
