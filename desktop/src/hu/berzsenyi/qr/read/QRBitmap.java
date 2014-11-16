package hu.berzsenyi.qr.read;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class QRBitmap {
	public int size;
	public boolean[][] bitmap;
	public List<Vector2F> from;
	
	public QRBitmap(int size) {
		this.size = size;
		this.bitmap = new boolean[this.size][this.size];
		this.from = new ArrayList<Vector2F>();
	}
	
	public BufferedImage getAsImage() {
		BufferedImage ret = new BufferedImage(this.size+4, this.size+4, BufferedImage.TYPE_BYTE_BINARY);
		for(int i = -2; i < this.size+2; i++)
			for(int j = -2; j < this.size+2; j++)
				ret.setRGB(i+2, j+2, (0 <= i && i < this.size && 0 <= j && j < this.size ? (this.bitmap[i][j] ? Color.black.getRGB() : Color.white.getRGB()) : Color.white.getRGB()));
		return ret;
	}
}
