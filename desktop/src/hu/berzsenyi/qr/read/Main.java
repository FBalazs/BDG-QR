package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Main {
	static Configuration config;
	static String imgPath = "";
	static int maxSize = 1920;
	static BufferedImage img;
	static int width, height;
	static int[] grayScale;
	static QRReader reader;
	
	static void readInput() throws Exception {
		imgPath = config.getValue("img", imgPath);
		maxSize = Integer.parseInt(config.getValue("maxSize", ""+maxSize));
		if(imgPath.equals(""))
			imgPath = JOptionPane.showInputDialog(null, "Please type in the file's path!");
		if(imgPath.equals("") || imgPath.equals("screen")) {
			Robot robot = new Robot();
			img = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		}
		else
			img = ImageIO.read(new File(imgPath));
		if(maxSize < img.getWidth() || maxSize < img.getHeight()) {
			float ratio = Math.min(maxSize/(float)img.getWidth(), maxSize/(float)img.getHeight());
			Image scaledImg = img.getScaledInstance((int)(img.getWidth()*ratio), (int)(img.getHeight()*ratio), Image.SCALE_SMOOTH);
			img = new BufferedImage((int)(img.getWidth()*ratio), (int)(img.getHeight()*ratio), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();
			g.drawImage(scaledImg, 0, 0, null);
		}
		
		width = img.getWidth();
		height = img.getHeight();
		grayScale = new int[width * height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int rgb = img.getRGB(x, y);
				grayScale[y * width + x] = (((rgb >> 16) & 255) + ((rgb >> 8) & 255) + (rgb & 255)) / 3;
			}
	}
	
	static void writeImages() throws Exception {
		new File("out").mkdir();
		
		BufferedImage tmpImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				tmpImg.setRGB(x, y, reader.getBitExtractor().getBitmap()[x][y] ? Color.white.getRGB() : Color.black.getRGB());
		ImageIO.write(tmpImg, "png", new File("out/bitmap.png"));
		
//		for(int x = 0; x < width; x++)
//			for(int y = 0; y < height; y++)
//				tmpImg.setRGB(x, y, reader.getEdgeMap()[x][y] ? Color.white.getRGB() : Color.black.getRGB());
//		ImageIO.write(tmpImg, "png", new File("out/edgemap.png"));
		
		for(int c = 0; c < reader.getCodes().size(); c++) {
			QRBitmap qr = reader.getCodes().get(c);
			tmpImg = new BufferedImage(qr.size, qr.size, BufferedImage.TYPE_BYTE_BINARY);
			for(int x = 0; x < qr.size; x++)
				for(int y = 0; y < qr.size; y++)
					tmpImg.setRGB(x, y, qr.bitmap[x][y] ? Color.black.getRGB() : Color.white.getRGB());
			ImageIO.write(tmpImg, "png", new File("out/qr"+c+".png"));
		}
	}
	
	public static void main(String[] args) {
		try {
			config = new Configuration("config.txt");
			config.read();
			
			readInput();
			
			reader = new QRReader(new QRReader.Options(config));
			long time = System.nanoTime();
			reader.read(width, height, grayScale);
			time = System.nanoTime()-time;
			System.out.println("Done reading in "+(time/1000000000D)+" s!");
			
			writeImages();
			
			config.write();
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "UNEXPECTED ERROR!", "BDG-QR ERROR", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}
}
