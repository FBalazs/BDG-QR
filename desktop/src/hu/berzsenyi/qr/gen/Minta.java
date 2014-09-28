package hu.berzsenyi.qr.gen;

public class Minta {
    public static void letrehoz() {
        Generate.QR = new boolean [Generate.N][Generate.N];
        Generate.foglalt = new boolean [Generate.N][Generate.N];
        Generate.szele = new boolean[Generate.N][Generate.N];
        for (int i = 0; i < Generate.N; i++) {
            for (int j = 0; j < Generate.N; j++) {
                Generate.foglalt[i][j] = false;
            }
        }
    }
    
    static void sarok (int x, int y) {//(x,y) a minta közepe
        for (int i = x - 3; i <= x + 3; i++) {
            for (int j = y - 3; j <= y + 3; j++ ) {
                Generate.foglalt[i][j] = true;
                Generate.szele[i][j] = true;
            }
        }
        for (int i = x - 3; i <= x + 3; i++) {
            for (int j = y - 3; j <= y + 3; j++) {
                Generate.QR[i][j] = true;
            }
        }
        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = y - 2; j <= y + 2; j++) {
                Generate.QR[i][j] = false;
            }
        }
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                Generate.QR[i][j] = true;
            }
        }
    }
    
    public static void sarkok() {
        sarok(3,3);
        sarok(3, Generate.N - 4);
        sarok(Generate.N - 4, 3);
    }
    
    public static void separators() {
        for (int i = 0; i < 8; i++) {
            Generate.QR[i][7] = false;
            Generate.foglalt[i][7] = true;
            Generate.szele[i][7] = true;
            Generate.QR[7][i] = false;
            Generate.foglalt[7][i] = true;
            Generate.szele[7][i] = true;
            
            Generate.QR[i][Generate.N - 8] = false;
            Generate.foglalt[i][Generate.N - 8] = true;
            Generate.szele[i][Generate.N - 8] = true;
            Generate.QR[7][Generate.N - i - 1] = false;
            Generate.foglalt[7][Generate.N - i - 1] = true;
            Generate.szele[7][Generate.N - i -1] = true;
            
            Generate.QR[Generate.N - i - 1][7] = false;
            Generate.foglalt[Generate.N - i - 1][7] = true;
            Generate.szele[Generate.N - i - 1][7] = true;
            Generate.QR[Generate.N - 8][i] = false;
            Generate.foglalt[Generate.N - 8][i] = true;
            Generate.szele[Generate.N - 8][i] = true;
        }
    }
    
    static void kisNegyzet(int x, int y) {
        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = y - 2; j <= y + 2; j++) {
                Generate.foglalt[i][j] = true;
            }
        }
        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = y - 2; j <= y + 2; j++) {
                Generate.QR[i][j] = true;
            }
        }
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                Generate.QR[i][j] = false;
            }
        }
        Generate.QR[x][y] = true;
    }
    
    public static void kisNegyzetek() {
        if (Generate.V == 1) {
            return;
        }
        int[][] locations = {//V verziónál locatons[V-2]-ben vannak a lehetséges koordináták
            {6, 18},
            {6, 22},
            {6, 26},
            {6, 30},
            {6, 34},
            {6, 22, 38},
            {6, 24, 42},
            {6, 26, 46},
            {6, 28, 50},
            {6, 30, 54},
            {6, 32, 58},
            {6, 34, 6},
            {6, 26, 46, 66},
            {6, 26, 48, 70},
            {6, 26, 50, 74},
            {6, 30, 54, 78},
            {6, 30, 56, 82},
            {6, 30, 58, 86},
            {6, 34, 62, 90},
            {6, 28, 50, 72, 94},
            {6, 26, 50, 74, 98},
            {6, 30, 54, 78, 102},
            {6, 28, 54, 80, 106},
            {6, 32, 58, 84, 110},
            {6, 30, 58, 86, 114},
            {6, 34, 62, 90, 118},
            {6, 26, 50, 74, 98, 122},
            {6, 30, 54, 78, 102, 126},
            {6, 26, 52, 78, 104, 130},
            {6, 30, 56, 82, 108, 134},
            {6, 34, 60, 86, 112, 138},
            {6, 30, 58, 86, 114, 142},
            {6, 34, 62, 90, 118, 146},
            {6, 30, 54, 78, 102, 126, 150},
            {6, 24, 50, 76, 102, 128, 154},
            {6, 28, 54, 80, 106, 132, 158},
            {6, 32, 58, 84, 110, 136, 162},
            {6, 26, 54, 82, 110, 138, 166},
            {6, 30, 58, 86, 114, 142, 170}
        };
        for (int i = 0; i < locations[Generate.V - 2].length; i++) {
            for (int j = 0; j < locations[Generate.V - 2].length; j++) {
                int x = locations[Generate.V - 2][i];
                int y = locations[Generate.V - 2][j];
                boolean lehet = true; //a kis négyzet nem lóg bele semmibe
                for (int ii = x - 2; ii <= x + 2; ii++) {
                    for (int jj = y - 2; jj <= y + 2; jj++) {
                        if (Generate.foglalt[ii][jj]) {
                            lehet = false;
                        }
                    }   
                }
                if (lehet) {
                    kisNegyzet(x, y);
                }
            }
        }
    }
    
    public static void szaggatott() {
        boolean szin = true;
        for (int i = 8; i < Generate.N - 8; i++) {
            Generate.foglalt[i][6] = true;
            Generate.QR[i][6] = szin;
            Generate.foglalt[6][i] = true;
            Generate.QR[6][i] = szin;
            szin = !szin;
        }
    }
    
    public static void feketeNegyzet() {
        Generate.QR[8][Generate.N-8] = true;
        Generate.foglalt[8][Generate.N-8] = true;
        Generate.szele[8][Generate.N - 8] = true;
    }
    
    public static void format() {
        for (int i = 0; i < 6; i++) {//14...9
            Generate.QR[i][8] = Generate.FS[i];
            Generate.foglalt[i][8] = true;
            Generate.szele[i][8] = true;
        }
        for (int i = 6; i < 8; i++) {//8,7
            Generate.QR[i+1][8] = Generate.FS[i];
            Generate.foglalt[i+1][8] = true;
            Generate.szele[i+1][8] = true;
        }
        Generate.QR[8][7] = Generate.FS[8];//6
        Generate.foglalt[8][7] = true;
        for (int i = 5; -1 < i; i--) {
            Generate.QR[8][i] = Generate.FS[14 - i];
            Generate.foglalt[8][i] = true;
            Generate.szele[8][i] = true;
        }
        
        for (int i = 0; i < 7; i++) {
            Generate.QR[8][Generate.N - 1 - i] = Generate.FS[i];
            Generate.foglalt[8][Generate.N - 1 - i] = true;
            Generate.szele[8][Generate.N - 1- i] = true;
        }
        for (int i = 0; i < 8; i++) {
            Generate.QR[Generate.N - 1 - i][8] = Generate.FS[14-i];
            Generate.foglalt[Generate.N - 1 - i][8] = true;
            Generate.szele[Generate.N - 1 - i][8] = true;
        }
    }
    
    public static void verzio() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                Generate.QR[i][Generate.N - 11 + j] = Generate.VS[3 * i + j];
                Generate.foglalt[i][Generate.N - 11 + j] = true;
                Generate.szele[i][Generate.N - 11 + j] = true;
                
                Generate.QR[Generate.N - 11 + j][i] = Generate.VS[3 * i + j];
                Generate.foglalt[Generate.N - 11 + j][i] = true;
                Generate.szele[Generate.N - 11 + j][i] = true;
            }
        }
                
    }
    
    public static void adat() {
        int i = 0;
        boolean fel = true; //felfele haladunk-e a következő bithez
        boolean jobb = true; //a 2 vastagságú oszlop jobb oldalán lévő-e a következő
        int x = Generate.N - 1;
        int y = Generate.N - 1;
        //int tmp[][] = new int [Generate.N][Generate.N];
        //System.out.println(Generate.bitNumber);
        while (i < Generate.bitNumber) {            
            if (x == 6) {//elértük a függőleges timing patternt
                x--;
            }
            else if (fel && (y == -1 || (Generate.szele[y][x] && !(x <= 8 && y >= Generate.N - 8)))) {//fent a szélébe ütköztünk
                if (!jobb) {
                    System.out.println("Mi van????");
                }
                fel = false;
                if (Generate.V >= 7 && x == Generate.N - 9) {
                    y = 0;
                }
                else {
                    y++;
                }
                x -= 2;
            }
            else if (!fel && (y == Generate.N || (Generate.szele[y][x] && 9 < y))) {//lent a szélébe ütköztünk
                if (!jobb) {
                    System.out.println("Mi van????");
                }
                y--;
                x-= 2;
                fel = true;
            }
            else {
                if (!Generate.foglalt[y][x]) {
                    //tmp[y][x] = i+1;
                    Generate.QR[x][y] = Generate.Bitek[i];
                    //Generate.foglalt[x][y] = true;
                    i++;
                }
                if (jobb) {
                    x--;
                }
                else {
                    x++;
                }
                if (!jobb) {
                    if (fel) {
                        y--;
                    }
                    else {
                        y++;
                    }
                }
                jobb = !jobb;
            }
        }
        /*
        for (int ii = 0; ii < Generate.N; ii++) {
            for (int jj = 0; jj < Generate.N; jj++) {
                System.out.print(tmp[ii][jj]+" ");
            }
            System.out.println();
        }
        System.out.println();
        */
    }
    
    public static void maszk() {
        for (int i = 0; i < Generate.N; i++) {
            for (int j = 0; j < Generate.N; j++) {
                if (i % 3 == 0 && !Generate.foglalt[i][j] ) {
                    Generate.QR[i][j] = !Generate.QR[i][j];
                }
            }
        }
    }
}
