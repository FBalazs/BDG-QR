package hu.berzsenyi.qr.read;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
		new Thread(instance, "QR Thread").start();
	}
	
	Configuration config;
	
	int width, height;
	int[] grayScale;
	GaussianBlur blur;
	BitExtractor bitExtractor;
	boolean[][] bitmap;
	FinderPatternFinder finderPatternFinder;
	List<Vector2F> finderPatterns;
	AligmentPatternFinder aligmentPatternFinder;
	List<Vector2F> aligmentPatterns;
	QRParser qrParser;
	List<QRBitmap> qrCodes;
	
	int displayWidth, displayHeight;
	BufferedImage img, imgBlackWhite, imgDebug;
	BufferedImage[] qrImgs;

	public ReaderApp() {
		super("QR Reader");
		this.addWindowListener(this);
	}

	public void preProcess() {
		this.grayScale = new int[this.width * this.height];
		for (int x = 0; x < this.width; x++)
			for (int y = 0; y < this.height; y++) {
				int rgb = this.img.getRGB(x, y);
				this.grayScale[y * this.width + x] = (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 3;
			}
	}

	public void process() {
		if(this.gausRadius != 0) {
			this.blur = new GaussianBlur(this.gausRadius, this.gausThreshold);
			this.grayScale = this.blur.blur(this.width, this.height, this.grayScale);
		}
		
		this.bitExtractor = new BitExtractor();
		this.bitmap = this.bitExtractor.extract(this.width, this.height, this.grayScale);
		
		this.finderPatternFinder = new FinderPatternFinder(this.finderPatternFinderRange, this.finderPatternFinderRange2, this.finderPatternFinderRange3);
		this.finderPatterns = this.finderPatternFinder.findPatterns(this.width, this.height, this.bitmap);
		
		this.aligmentPatternFinder = new AligmentPatternFinder(this.aligmentPatternFinderRange, this.aligmentPatternFinderRange2, this.aligmentPatternFinderRange3);
		this.aligmentPatterns = this.aligmentPatternFinder.findPatterns(this.width, this.height, this.bitmap);
		
		this.qrParser = new QRParser();
		this.qrCodes = this.qrParser.parse(this.width, this.height, this.bitmap, this.finderPatterns, this.aligmentPatterns);
		
//		System.out.println("Finding the finder patterns...");
//		this.finderPatternFinder = new FinderPatternFinder(this.finderPatternFinderRange, this.finderPatternFinderRange2);
//		this.finderPatternFinder.setData(this.width, this.height, this.edgeDetector.getOutput());
//		this.finderPatternFinder.process();
//		this.finderPatterns = this.finderPatternFinder.getOutput();
//		System.out.println("Found "+this.finderPatterns.size()+" finder patterns.");
//		System.out.println("Parsing qr codes...");
//		this.parser = new QRParser();
//		this.parser.setData(this.width, this.height, this.pixels, this.edgeDetector.getOutput(), this.finderPatterns);
//		this.parser.parse();
//		this.qrCodes = this.parser.getOutput();
//		System.out.println("Parsing done.");
	}

	public void postProcess() {
		this.imgBlackWhite = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < this.width; x++)
			for(int y = 0; y < this.height; y++)
				this.imgBlackWhite.setRGB(x, y, this.bitmap[x][y] ? Color.white.getRGB() : Color.black.getRGB());
		
		this.imgDebug = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = this.imgDebug.createGraphics();
		g.drawImage(this.imgBlackWhite, 0, 0, this.width, this.height, null);
		
		g.setStroke(new BasicStroke(3F));
		g.setColor(Color.blue);
		for(ShapeF shape : this.qrParser.getPositions())
			for(int i = 0; i < shape.vertices.length; i++)
				g.drawLine(Math.round(shape.vertices[i].x), Math.round(shape.vertices[i].y), Math.round(shape.vertices[(i+1)%shape.vertices.length].x), Math.round(shape.vertices[(i+1)%shape.vertices.length].y));
		
		g.setStroke(new BasicStroke(1F));
		
//		g.setColor(new Color(0F, 1F, 0F, 0.5F));
//		for(LineF line : this.aligmentPatternFinder.getHorizontalLines())
//			g.fillRect((int)line.v1.x, (int)line.v1.y, (int)(line.v2.x-line.v1.x)+1, (int)(line.v2.y-line.v1.y)+1);
//		g.setColor(new Color(0F, 0F, 1F, 0.5F));
//		for(LineF line : this.aligmentPatternFinder.getVerticalLines())
//			g.fillRect((int)line.v1.x, (int)line.v1.y, (int)(line.v2.x-line.v1.x)+1, (int)(line.v2.y-line.v1.y)+1);
		
//		g.setColor(new Color(1F, 1F, 0F, 0.5F));
//		for(LineF line : this.finderPatternFinder.getHorizontalLines())
//			g.fillRect((int)line.v1.x, (int)line.v1.y, (int)(line.v2.x-line.v1.x)+1, (int)(line.v2.y-line.v1.y)+1);
//		g.setColor(new Color(1F, 0.5F, 0F, 0.5F));
//		for(LineF line : this.finderPatternFinder.getVerticalLines())
//			g.fillRect((int)line.v1.x, (int)line.v1.y, (int)(line.v2.x-line.v1.x)+1, (int)(line.v2.y-line.v1.y)+1);
		
		g.setColor(new Color(0F, 0.5F, 1F, 1F));
		for(Vector2F vec : this.aligmentPatternFinder.getResult())
			g.fillOval(Math.round(vec.x)-3, Math.round(vec.y)-3, 7, 7);
		
		g.setColor(new Color(1F, 0F, 0F, 1F));
		for(Vector2F vec : this.finderPatternFinder.getResult())
			g.fillOval(Math.round(vec.x)-10, Math.round(vec.y)-10, 21, 21);
		
		this.qrImgs = new BufferedImage[this.qrCodes.size()];
		for(int i = 0; i < this.qrCodes.size(); i++)
			this.qrImgs[i] = this.qrCodes.get(i).getAsImage();
	}
	
	int maxSize = 480;
	float finderPatternFinderRange = 0.4F;
	float finderPatternFinderRange2 = 0.25F;
	float finderPatternFinderRange3 = 0.33F;
	float aligmentPatternFinderRange = 0.25F;
	float aligmentPatternFinderRange2 = 0.2F;
	float aligmentPatternFinderRange3 = 0.25F;
	int gausRadius = 1;
	float gausThreshold = 1F;
	
	public void init() throws Exception {
		this.config = new Configuration("config.txt");
		this.config.read();
		String file = this.config.getValue("img", "");
		this.maxSize = Integer.parseInt(this.config.getValue("maxSize", ""+this.maxSize));
		this.finderPatternFinderRange = Float.parseFloat(this.config.getValue("finderPatternFinderRange", ""+this.finderPatternFinderRange));
		this.finderPatternFinderRange2 = Float.parseFloat(this.config.getValue("finderPatternFinderRange2", ""+this.finderPatternFinderRange2));
		this.finderPatternFinderRange3 = Float.parseFloat(this.config.getValue("finderPatternFinderRange3", ""+this.finderPatternFinderRange3));
		this.aligmentPatternFinderRange = Float.parseFloat(this.config.getValue("aligmentPatternFinderRange", ""+this.aligmentPatternFinderRange));
		this.aligmentPatternFinderRange2 = Float.parseFloat(this.config.getValue("aligmentPatternFinderRange2", ""+this.aligmentPatternFinderRange2));
		this.aligmentPatternFinderRange3 = Float.parseFloat(this.config.getValue("aligmentPatternFinderRange3", ""+this.aligmentPatternFinderRange3));
		this.gausRadius = Integer.parseInt(this.config.getValue("gausRadius", ""+this.gausRadius));
		this.gausThreshold = Float.parseFloat(this.config.getValue("gausThreshold", ""+this.gausThreshold));
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

		this.setSize(this.width * 2, this.height * 2);
		// this.setResizable(false);

		this.preProcess();
		long time = System.currentTimeMillis();
		this.process();
		time = System.currentTimeMillis() - time;
		this.postProcess();
		this.setTitle("QR Reader - " + time + " ms");
		
		this.setVisible(true);
	}

	public void render() {
		try {
			if (this.getBufferStrategy() == null)
				this.createBufferStrategy(2);
			Graphics g = this.getBufferStrategy().getDrawGraphics();

			if (this.getWidth() / 2 < this.displayWidth || this.getHeight() / 2 < this.displayHeight
					|| (this.displayWidth < this.getWidth() / 2 && this.displayHeight < this.getHeight() / 2)) {
				float ratio = Math.min(this.getWidth() / 2 / (float) this.displayWidth, this.getHeight() / 2 / (float) this.displayHeight);
				this.displayWidth *= ratio;
				this.displayHeight *= ratio;
			}
			g.drawImage(this.img, 0, 0, this.displayWidth, this.displayHeight, null);
			g.drawImage(this.imgBlackWhite, this.displayWidth, 0, this.displayWidth, this.displayHeight, null);
			g.drawImage(this.imgDebug, 0, this.displayHeight, this.displayWidth, this.displayHeight, null);
			int s;
			for(s = 1; s*s < this.qrImgs.length; s++);
			int whm = Math.min(this.displayWidth, this.displayHeight);
			for(int i = 0; i < this.qrImgs.length; i++)
				g.drawImage(this.qrImgs[i], this.displayWidth + whm*(i%s)/s, this.displayHeight + whm*(i/s)/s, whm/s, whm/s, null);
			
			g.dispose();
			this.getBufferStrategy().show();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
