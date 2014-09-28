package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main {
	static JFrame frame = null;
	static String imagePath = "qr.jpg";
	
	static int width = 0, height = 0;
	static BufferedImage imgIn = null;
	static int[] imgDataIn = null;
	static BufferedImage imgGaus = null;
	static int[] imgDataGaus = null;
	static BufferedImage imgSobel = null;
	static int houghWidth = 0, houghHeight = 0;
	static int[] imgDataHough;
	static BufferedImage imgHough = null;
	
	static int[] gradientDir = null;
	static float[] gradientStrength = null;
	static int[] newGradientDir = null;
	static BufferedImage imgEdge = null;
	
	static Graphics startRender() {
		if(frame.getBufferStrategy() == null)
			frame.createBufferStrategy(2);
		return frame.getBufferStrategy().getDrawGraphics();
	}
	
	static void stopRender() {
		frame.getBufferStrategy().getDrawGraphics().dispose();
		frame.getBufferStrategy().show();
	}
	
//	public static void render() {
//		if(frame.getBufferStrategy() == null)
//			frame.createBufferStrategy(2);
//		Graphics g = frame.getBufferStrategy().getDrawGraphics();
//		
//		g.drawImage(imgIn, 0, 0, null);
//		
//		g.dispose();
//		frame.getBufferStrategy().show();
//	}
	
	static int index(int x, int y) {
		return width*y + x;
	}
	
	static void init() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			System.exit(1);
		}
		width = img.getWidth();
		height = img.getHeight();
		imgIn = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				imgIn.setRGB(x, y, img.getRGB(x, y));
		imgDataIn = ((DataBufferInt)imgIn.getData().getDataBuffer()).getData();
		
		frame = new JFrame("BDGQR") {
			@Override
			public void paint(Graphics g) {
				
			}
		};
		frame.setSize(width*2, height*2);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
	
	static int gausDiv = 159;
	static int[][] gausMask = new int[5][5];
	static {
		gausMask[0][0] = 2;	 gausMask[0][1] = 4;  gausMask[0][2] = 5;  gausMask[0][3] = 4;  gausMask[0][4] = 2;	
		gausMask[1][0] = 4;	 gausMask[1][1] = 9;  gausMask[1][2] = 12; gausMask[1][3] = 9;  gausMask[1][4] = 4;	
		gausMask[2][0] = 5;	 gausMask[2][1] = 12; gausMask[2][2] = 15; gausMask[2][3] = 12; gausMask[2][4] = 2;	
		gausMask[3][0] = 4;	 gausMask[3][1] = 9;  gausMask[3][2] = 12; gausMask[3][3] = 9;  gausMask[3][4] = 4;	
		gausMask[4][0] = 2;	 gausMask[4][1] = 4;  gausMask[4][2] = 5;  gausMask[4][3] = 4;  gausMask[4][4] = 2;
	}
	
	static void gaus() {
		imgDataGaus = new int[width*height];
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				if(2 <= x && x < width-2 && 2 <= y && y < height-2) {
					int color = 0;
					for(int ox = -2; ox <= 2; ox++)
						for(int oy = -2; oy <= 2; oy++) {
							int ccolor = imgDataIn[index(x+ox, y+oy)];
							color += (((ccolor >> 16) & 255) + ((ccolor >> 8) & 255) + (ccolor & 255))*gausMask[ox+2][oy+2];
						}
					color /= gausDiv*3;
					imgDataGaus[index(x, y)] = color;
				} else {
					int color = imgDataIn[index(x, y)];
					imgDataGaus[index(x, y)] = (byte)((((color >> 16) & 255) + ((color >> 8) & 255) + (color & 255))/3);
				}
	}
	
	static int[][] sobelMaskX = new int[3][3];
	static int[][] sobelMaskY = new int[3][3];
	static {
		sobelMaskX[0][0] = -1; sobelMaskX[1][0] = 0; sobelMaskX[2][0] = +1;
		sobelMaskX[0][1] = -2; sobelMaskX[1][1] = 0; sobelMaskX[2][1] = +2;
		sobelMaskX[0][2] = -1; sobelMaskX[1][2] = 0; sobelMaskX[2][2] = +1;
		
		sobelMaskY[0][0] = +1; sobelMaskY[1][0] = +2; sobelMaskY[2][0] = +1;
		sobelMaskY[0][1] = 0; sobelMaskY[1][1] = 0; sobelMaskY[2][1] = 0;
		sobelMaskY[0][2] = -1; sobelMaskY[1][2] = -2; sobelMaskY[2][2] = -1;
	}
	
	static float maxStrength = 0;
	
	static void sobel() {
		gradientDir = new int[width*height];
		gradientStrength = new float[width*height];
		newGradientDir = new int[width*height];
		for(int x = 1; x < width-1; x++)
			for(int y = 1; y < height-1; y++) {
				int gx = 0, gy = 0;
				for(int ox = -1; ox <= 1; ox++)
					for(int oy = -1; oy <= 1; oy++) {
						gx += imgDataGaus[index(x+ox, y+oy)]*sobelMaskX[ox+1][oy+1];
						gy += imgDataGaus[index(x+ox, y+oy)]*sobelMaskY[ox+1][oy+1];
					}
				gradientStrength[index(x, y)] = (float)Math.sqrt(gx*gx + gy*gy);
				if(gradientStrength[index(x, y)] > maxStrength)
					maxStrength = gradientStrength[index(x, y)];
				gradientDir[index(x, y)] = (int)(Math.atan2(gy, gx)*180/Math.PI);
				if((-22.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= 22.5) || (157.5 < gradientDir[index(x, y)] || gradientDir[index(x, y)] <= -157.5))
					gradientDir[index(x, y)] = 0;
				else if((22.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= 67.5) || (-157.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= -112.5))
					gradientDir[index(x, y)] = 45;
				else if((67.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= 112.5) || (-112.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= -67.5))
					gradientDir[index(x, y)] = 90;
				else if((112.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= 157.5) || (-67.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] <= -22.5))
					gradientDir[index(x, y)] = 135;
				newGradientDir[index(x, y)] = gradientDir[index(x, y)];
			}
	}
	
	static boolean edgeEnd;
	static float lowerThreshold, upperThreshold;
	
	static void findEdge(int dx, int dy, int x, int y, int dir, float lowerThreshold) {
		int nx = x+dx;
		int ny = y+dy;
		edgeEnd = false;
		
		if(nx < 0 || width <= nx || ny < 0 || height <= ny)
			edgeEnd = true;
		while(!edgeEnd && gradientDir[index(nx, ny)] == dir && lowerThreshold < gradientStrength[index(x, y)]) {
			newGradientDir[index(x, y)] = gradientDir[index(x, y)];
			nx += dx;
			ny += dy;
			if(nx < 0 || width <= nx || ny < 0 || height <= ny)
				edgeEnd = true;
		}
	}
	
	static void trace() {
		lowerThreshold = 30;
		upperThreshold = 60;
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				if(0 < x && x < width-1 && 0 < y && y < height-1) {
					edgeEnd = false;
					if(upperThreshold < gradientStrength[index(x, y)])
						if(gradientDir[index(x, y)] == 0)
							findEdge(1, 0, x, y, 0, lowerThreshold);
						else if(gradientDir[index(x, y)] == 45)
							findEdge(1, 1, x, y, 45, lowerThreshold);
						else if(gradientDir[index(x, y)] == 90)
							findEdge(0, 1, x, y, 90, lowerThreshold);
						else if(gradientDir[index(x, y)] == 135)
							findEdge(-1, 1, x, y, 135, lowerThreshold);
						else
							newGradientDir[index(x, y)] = -1;
					else
						newGradientDir[index(x, y)] = -1;
				} else {
					newGradientDir[index(x, y)] = -1;
				}
		
//		for(int x = 0; x < width; x++)
//			for(int y = 0; y < height; y++)
//				if(gradientStrength[index(x, y)] != 0F && gradientStrength[index(x, y)] != maxStrength)
//					imgGradient[index(x, y)] = -1;
	}
	
	static void nonMax(int dx, int dy, int x, int y, int dir) {
		int nx = x+dx;
		int ny = y+dy;
		edgeEnd = false;
		//int edgeLength = 0;
		//int[] max = new int[3];
		//int[] nonMaxX = new int[width+height];
		//int[] nonMaxY = new int[width+height];
		//float[] nonMaxStrength = new float[width+height];
		
		if(nx < 0 || width <= nx || ny < 0 || height <= ny)
			edgeEnd = true;
		while(!edgeEnd && gradientDir[index(nx, ny)] == dir && newGradientDir[index(nx, ny)] != -1) {
//			nonMaxX[edgeLength] = nx;
//			nonMaxY[edgeLength] = ny;
//			nonMaxStrength[edgeLength] = gradientStrength[index(nx, ny)];
//			edgeLength++;
			newGradientDir[index(nx, ny)] = -1;
			
			nx += dx;
			ny += dy;
			if(nx < 0 || width <= nx || ny < 0 || height <= ny)
				edgeEnd = true;
		}
		
		edgeEnd = false;
		dx *= -1;
		dy *= -1;
		nx = x+dx;
		ny = y+dy;
		if(nx < 0 || width <= nx || ny < 0 || height <= ny)
			edgeEnd = true;
		while(!edgeEnd && gradientDir[index(nx, ny)] == dir && newGradientDir[index(nx, ny)] != -1) {
//			nonMaxX[edgeLength] = nx;
//			nonMaxY[edgeLength] = ny;
//			nonMaxStrength[edgeLength] = gradientStrength[index(x, y)];
//			edgeLength++;
			newGradientDir[index(nx, ny)] = -1;
			
			nx += dx;
			ny += dy;
			if(nx < 0 || width <= nx || ny < 0 || height <= ny)
				edgeEnd = true;
		}
	}
	
	static void nonMax() {
		for(int x = 1; x < width-1; x++)
			for(int y = 1; y < height-1; y++)
				if(newGradientDir[index(x, y)] != -1)
					if(gradientDir[index(x, y)] == 0)
						nonMax(0, 1, x, y, 0);
					else if(gradientDir[index(x, y)] == 45)
						nonMax(1, 1, x, y, 45);
					else if(gradientDir[index(x, y)] == 90)
						nonMax(1, 0, x, y, 90);
					else if(gradientDir[index(x, y)] == 135)
						nonMax(-1, 1, x, y, 135);
	}
	
	static void hough() {
		houghWidth = 360;
		houghHeight = (int)Math.sqrt(width*width + height*height);
		
	}
	
	public static void main(String[] args) {
		init();
		long time = System.currentTimeMillis();
		gaus();
		sobel();
		trace();
		nonMax();
		hough();
		time = System.currentTimeMillis()-time;
		System.out.println("Finished reading in "+time+" ms.");
		
		imgGaus = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				imgGaus.setRGB(x, y, (imgDataGaus[index(x, y)] << 16) + (imgDataGaus[index(x, y)] << 8) + imgDataGaus[index(x, y)]);
		
		imgSobel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				if(gradientDir[index(x, y)] == 0)
					imgSobel.setRGB(x, y, new Color(gradientStrength[index(x, y)]/maxStrength, 0F, 0F).getRGB());
				else if(gradientDir[index(x, y)] == 45)
					imgSobel.setRGB(x, y, new Color(0F, gradientStrength[index(x, y)]/maxStrength, 0F).getRGB());
				else if(gradientDir[index(x, y)] == 90)
					imgSobel.setRGB(x, y, new Color(0F, 0F, gradientStrength[index(x, y)]/maxStrength).getRGB());
				else if(gradientDir[index(x, y)] == 135)
					imgSobel.setRGB(x, y, new Color(gradientStrength[index(x, y)]/maxStrength, gradientStrength[index(x, y)]/maxStrength, 0F).getRGB());
			}
		
		imgEdge = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				if(newGradientDir[index(x, y)] == 0)
					imgEdge.setRGB(x, y, Color.red.getRGB());
				else if(newGradientDir[index(x, y)] == 45)
					imgEdge.setRGB(x, y, Color.green.getRGB());
				else if(newGradientDir[index(x, y)] == 90)
					imgEdge.setRGB(x, y, Color.blue.getRGB());
				else if(newGradientDir[index(x, y)] == 135)
					imgEdge.setRGB(x, y, Color.yellow.getRGB());
				else
					imgEdge.setRGB(x, y, Color.black.getRGB());
			}
		
		Graphics g = startRender();
		g.drawImage(imgIn, 0, 0, width, height, null);
		g.drawImage(imgGaus, width, 0, width, height, null);
		g.drawImage(imgSobel, 0, height, width, height, null);
		g.drawImage(imgEdge, width, height, width, height, null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Sans-Serif", Font.BOLD, 16));
		g.drawString("Finished in "+time+" ms.", 5, height*2-5);
		stopRender();
	}
}
