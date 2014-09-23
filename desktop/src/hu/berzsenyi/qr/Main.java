package hu.berzsenyi.qr;

import java.awt.Color;
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
	public static JFrame frame = null;
	public static String imagePath = "qr.jpg";
	
	public static int width = 0, height = 0;
	public static BufferedImage imgIn = null, imgGaus = null, imgSobel = null;
	public static int[] imgDataIn = null, imgDataGaus = null;
	
	public static int[] gradientDir = null;
	public static float[] gradientStrength = null;
	
	public static Graphics startRender() {
		if(frame.getBufferStrategy() == null)
			frame.createBufferStrategy(2);
		return frame.getBufferStrategy().getDrawGraphics();
	}
	
	public static void stopRender() {
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
	
	public static int index(int x, int y) {
		return width*y + x;
	}
	
	public static void init() {
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
	
	public static int gausDiv = 159;
	public static int[][] gausMask = new int[5][5];
	static {
		gausMask[0][0] = 2;	 gausMask[0][1] = 4;  gausMask[0][2] = 5;  gausMask[0][3] = 4;  gausMask[0][4] = 2;	
		gausMask[1][0] = 4;	 gausMask[1][1] = 9;  gausMask[1][2] = 12; gausMask[1][3] = 9;  gausMask[1][4] = 4;	
		gausMask[2][0] = 5;	 gausMask[2][1] = 12; gausMask[2][2] = 15; gausMask[2][3] = 12; gausMask[2][4] = 2;	
		gausMask[3][0] = 4;	 gausMask[3][1] = 9;  gausMask[3][2] = 12; gausMask[3][3] = 9;  gausMask[3][4] = 4;	
		gausMask[4][0] = 2;	 gausMask[4][1] = 4;  gausMask[4][2] = 5;  gausMask[4][3] = 4;  gausMask[4][4] = 2;
	}
	
	public static void gaus() {
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
	
	public static int[][] sobelMaskX = new int[3][3];
	public static int[][] sobelMaskY = new int[3][3];
	static {
		sobelMaskX[0][0] = -1; sobelMaskX[1][0] = 0; sobelMaskX[2][0] = +1;
		sobelMaskX[0][1] = -2; sobelMaskX[1][1] = 0; sobelMaskX[2][1] = +2;
		sobelMaskX[0][2] = -1; sobelMaskX[1][2] = 0; sobelMaskX[2][2] = +1;
		
		sobelMaskY[0][0] = +1; sobelMaskY[1][0] = +2; sobelMaskY[2][0] = +1;
		sobelMaskY[0][1] = 0; sobelMaskY[1][1] = 0; sobelMaskY[2][1] = 0;
		sobelMaskY[0][2] = -1; sobelMaskY[1][2] = -2; sobelMaskY[2][2] = -1;
	}
	
	static float maxStrength = 0;
	
	public static void sobel() {
		gradientDir = new int[width*height];
		gradientStrength = new float[width*height];
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
				if(-22.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] < 22.5)
					gradientDir[index(x, y)] = 0;
				else if(22.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] < 67.5)
					gradientDir[index(x, y)] = 45;
				else if(67.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] < 112.5)
					gradientDir[index(x, y)] = 90;
				else if(112.5 < gradientDir[index(x, y)] && gradientDir[index(x, y)] < 157.5)
					gradientDir[index(x, y)] = 135;
			}
	}
	
	public static void main(String[] args) {
		init();
		long time = System.currentTimeMillis();
		gaus();
		sobel();
		System.out.println("Finished reading in "+(System.currentTimeMillis()-time)+" ms.");
		
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
		Graphics g = startRender();
		g.drawImage(imgIn, 0, 0, width, height, null);
		g.drawImage(imgGaus, width, 0, width, height, null);
		g.drawImage(imgSobel, 0, height, width, height, null);
		stopRender();
	}
}
