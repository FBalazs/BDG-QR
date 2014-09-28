//a kesz QR-kod kirajzolasa
package generate;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;


public class Output {
    static Color feher = new Color(255, 255, 255);
    static Color fekete = new Color(0, 0, 0);
    static Color piros = new Color(255, 0, 0);
    
    public static void kirajzol() {
        int px = 500 / Generate.N;
        
        BufferedImage kep = new BufferedImage(Generate.N * px, Generate.N * px,BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < Generate.N; i++) {
            for (int j = 0; j < Generate.N; j++) {
                for (int ii = 0; ii < px; ii++) {
                    for (int jj = 0; jj < px; jj++) {
                        //if (Generate.foglalt[i][j]) {
                            if (Generate.QR[i][j]) {
                                kep.setRGB(px * i + ii, px * j + jj, fekete.getRGB());
                            }
                            else {
                                kep.setRGB(px * i + ii, px * j + jj, feher.getRGB());
                            }
                        /*
                        }
                        else {
                            kep.setRGB(px * i + ii, px * j + jj, piros.getRGB());
                        }
                        */  
                    }
                }
            }
        }
        try {
            ImageIO.write(kep, "png", new File("QR.png"));
            } catch (IOException e) {System.err.println("Hiba egy fájl írásánál");
        };
    }
}
