//input
package generate;

import java.util.Scanner;

public class Input {
    public static void beolvas() {
        System.out.print("Text: ");
        String charset = "ISO-8859-1";
        Scanner be = new Scanner (System.in, charset);
        Generate.S = be.nextLine();
        System.out.print("Error correction (L, M, Q or H)?");
        
        boolean rossz; //nem L-et, M-et, Q-t vagy H-t ad meg
        do {
            rossz = false;
            char c = be.next().charAt(0);
            if (c == 'L') {
                Generate.e = 0;
            }
            else if (c == 'M') {
                Generate.e = 1;
            }
            else if (c == 'Q') {
                Generate.e = 2;
            }
            else if (c == 'H') {
                Generate.e = 3;
            }
            else {
                rossz = true;
                System.out.println("L, M, Q or H?");
            }
        }
        while (rossz);
        be.close();
    }    
}