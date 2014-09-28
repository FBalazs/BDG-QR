package generate;

public class Bits {
    public static void modeIndicator() {
        //Byte Mode: 0100
        Generate.B.add(false);
        Generate.B.add(true);
        Generate.B.add(false);
        Generate.B.add(false);
        
    }
    
    public static void characterCountIndicator() {
        int meret;
        //Byte Mode
        if (Generate.V < 10) {
            meret = 8;
        }
        else {
            meret = 16;
        }
        
        boolean a[] = new boolean [meret];
        int c = Generate.S.length();
        for (int i = meret - 1; 0 <= i; i--) {
            a[i] = (c % 2 == 1);
            c /= 2;
        }
        
        for (int i = 0; i < meret; i++) {
            Generate.B.add(a[i]);
        }
    }
    
    static void addBinary(int n) {
        boolean b[] = new boolean [8];
        for (int i = 7; 0 <= i; i--) {
            b[i] = (n % 2 == 1);
            n /= 2;
        }
        for (int i = 0; i < 8; i++) {
            Generate.B.add(b[i]);
        }
    }
    
    public static void encode() {
        for (int i = 0; i < Generate.S.length(); i++) {
            addBinary((int)Generate.S.charAt(i));
        }
    }
    
    public static void feltolt() {
        int C[][] = {
            {19, 34, 55, 80, 108, 136, 156, 194, 232, 274, 324, 370, 428, 461, 523, 589, 647, 721, 795, 861, 932, 1006, 1094, 1174, 1276, 1370, 1468, 1531, 1631, 1735, 1843, 1955, 2071, 2191, 2306, 2434, 2566, 2702, 2812, 2956},
            {16, 28, 44, 64, 86, 108, 124, 154, 182, 216, 254, 290, 334, 365, 415, 453, 507, 563, 627, 669, 714, 782, 860, 914, 1000, 1062, 1128, 1193, 1267, 1373, 1455, 1541, 1631, 1725, 1812, 1914, 1992, 2102, 2216, 2334},
            {13, 22, 34, 48, 62, 76, 88, 110, 132, 154, 180, 206, 244, 261, 295, 325, 367, 397, 445, 485, 512, 568, 614, 664, 718, 754, 808, 871, 911, 985, 1033, 1115, 1171, 1231, 1286, 1354, 1426, 1502, 1582, 1666},
            {9, 16, 26, 36, 46, 60, 66, 86, 100, 122, 140, 158, 180, 197, 223, 253, 283, 313, 341, 385, 406, 442, 464, 514, 538, 596, 628, 661, 701, 745, 793, 845, 901, 961, 986, 1054, 1096, 1142, 1222, 1276}
        };
        int bitNumber = 8 * C[Generate.e][Generate.V-1];
        Generate.bitNumber = bitNumber;
        int k = bitNumber - Generate.B.size();
        if (0 < k) {
            Generate.B.add(false);
            if (1 < k) {
                Generate.B.add(false);
            }
            if (2 < k) {
                Generate.B.add(false);
            }
            if (3 < k) {
                Generate.B.add(false);
            }
        }
        while (!(Generate.B.size() % 8 == 0)) {
            Generate.B.add(false);
        }
        String repeat = "1110110000010001";
        int db = bitNumber - Generate.B.size(); //ennyi Pad Bit kell még
        for (int i = 0; i < db; i++) {
            Generate.B.add(repeat.charAt(i % 16) == '1');
        }
    }
    
    public static void bajtok() {
        int T[][][] = {
            {{7, 1, 19, 0, 0}, {10, 1, 34, 0, 0}, {15, 1, 55, 0, 0}, {20, 1, 80, 0, 0}, {26, 1, 108, 0, 0}, {18, 2, 68, 0, 0}, {20, 2, 78, 0, 0}, {24, 2, 97, 0, 0}, {30, 2, 116, 0, 0}, {18, 2, 68, 2, 69}, {20, 4, 81, 0, 0}, {24, 2, 92, 2, 93}, {26, 4, 107, 0, 0}, {30, 3, 115, 1, 116}, {22, 5, 87, 1, 88}, {24, 5, 98, 1, 99}, {28, 1, 107, 5, 108}, {30, 5, 120, 1, 121}, {28, 3, 113, 4, 114}, {28, 3, 107, 5, 108}, {28, 4, 116, 4, 117}, {28, 2, 111, 7, 112}, {30, 4, 121, 5, 122}, {30, 6, 117, 4, 118}, {26, 8, 106, 4, 107}, {28, 10, 114, 2, 115}, {30, 8, 122, 4, 123}, {30, 3, 117, 10, 118}, {30, 7, 116, 7, 117}, {30, 5, 115, 10, 116}, {30, 13, 115, 3, 116}, {30, 17, 115, 0, 0}, {30, 17, 115, 1, 116}, {30, 13, 115, 6, 116}, {30, 12, 121, 7, 122}, {30, 6, 121, 14, 122}, {30, 17, 122, 4, 123}, {30, 4, 122, 18, 123}, {30, 20, 117, 4, 118}, {30, 19, 118, 6, 119}},
            {{10, 1, 16, 0, 0}, {16, 1, 28, 0, 0}, {26, 1, 44, 0, 0}, {18, 2, 32, 0, 0}, {24, 2, 43, 0, 0}, {16, 4, 27, 0, 0}, {18, 4, 31, 0, 0}, {22, 2, 38, 2, 39}, {22, 3, 36, 2, 37}, {26, 4, 43, 1, 44}, {30, 1, 50, 4, 51}, {22, 6, 36, 2, 37}, {22, 8, 37, 1, 38}, {24, 4, 40, 5, 41}, {24, 5, 41, 5, 42}, {28, 7, 45, 3, 46}, {28, 10, 46, 1, 47}, {26, 9, 43, 4, 44}, {26, 3, 44, 11, 45}, {26, 3, 41, 13, 42}, {26, 17, 42, 0, 0}, {28, 17, 46, 0, 0}, {28, 4, 47, 14, 48}, {28, 6, 45, 14, 46}, {28, 8, 47, 13, 48}, {28, 19, 46, 4, 47}, {28, 22, 45, 3, 46}, {28, 3, 45, 23, 46}, {28, 21, 45, 7, 46}, {28, 19, 47, 10, 48}, {28, 2, 46, 29, 47}, {28, 10, 46, 23, 47}, {28, 14, 46, 21, 47}, {28, 14, 46, 23, 47}, {28, 12, 47, 26, 48}, {28, 6, 47, 34, 48}, {28, 29, 46, 14, 47}, {28, 13, 46, 32, 47}, {28, 40, 47, 7, 48}, {28, 18, 47, 31, 48}},
            {{13, 1, 13, 0, 0}, {22, 1, 22, 0, 0}, {18, 2, 17, 0, 0}, {26, 2, 24, 0, 0}, {18, 2, 15, 2, 16}, {24, 4, 19, 0, 0}, {18, 2, 14, 4, 15}, {22, 4, 18, 2, 19}, {20, 4, 16, 4, 17}, {24, 6, 19, 2, 20}, {28, 4, 22, 4, 23}, {26, 4, 20, 6, 21}, {24, 8, 20, 4, 21}, {20, 11, 16, 5, 17}, {30, 5, 24, 7, 25}, {24, 15, 19, 2, 20}, {28, 1, 22, 15, 23}, {28, 17, 22, 1, 23}, {26, 17, 21, 4, 22}, {30, 15, 24, 5, 25}, {28, 17, 22, 6, 23}, {30, 7, 24, 16, 25}, {30, 11, 24, 14, 25}, {30, 11, 24, 16, 25}, {30, 7, 24, 22, 25}, {28, 28, 22, 6, 23}, {30, 8, 23, 26, 24}, {30, 4, 24, 31, 25}, {30, 1, 23, 37, 24}, {30, 15, 24, 25, 25}, {30, 42, 24, 1, 25}, {30, 10, 24, 35, 25}, {30, 29, 24, 19, 25}, {30, 44, 24, 7, 25}, {30, 39, 24, 14, 25}, {30, 46, 24, 10, 25}, {30, 49, 24, 10, 25}, {30, 48, 24, 14, 25}, {30, 43, 24, 22, 25}, {30, 34, 24, 34, 25}},
            {{17, 1, 9, 0, 0}, {28, 1, 16, 0, 0}, {22, 2, 13, 0, 0}, {16, 4, 9, 0, 0}, {22, 2, 11, 2, 12}, {28, 4, 15, 0, 0}, {26, 4, 13, 1, 14}, {26, 4, 14, 2, 15}, {24, 4, 12, 4, 13}, {28, 6, 15, 2, 16}, {24, 3, 12, 8, 13}, {28, 7, 14, 4, 15}, {22, 12, 11, 4, 12}, {24, 11, 12, 5, 13}, {24, 11, 12, 7, 13}, {30, 3, 15, 13, 16}, {28, 2, 14, 17, 15}, {28, 2, 14, 19, 15}, {26, 9, 13, 16, 14}, {28, 15, 15, 10, 16}, {30, 19, 16, 6, 17}, {24, 34, 13, 0, 0}, {30, 16, 15, 14, 16}, {30, 30, 16, 2, 17}, {30, 22, 15, 13, 16}, {30, 33, 16, 4, 17}, {30, 12, 15, 28, 16}, {30, 11, 15, 31, 16}, {30, 19, 15, 26, 16}, {30, 23, 15, 25, 16}, {30, 23, 15, 28, 16}, {30, 19, 15, 35, 16}, {30, 11, 15, 46, 16}, {30, 59, 16, 1, 17}, {30, 22, 15, 41, 16}, {30, 2, 15, 64, 16}, {30, 24, 15, 46, 16}, {30, 42, 15, 32, 16}, {30, 10, 15, 67, 16}, {30, 20, 15, 61, 16}}
        };
        Generate.t = T[Generate.e][Generate.V-1][0];
        int blocksInGroup1 = T[Generate.e][Generate.V-1][1];
        int bytesInEachBlockOfGroup1 = T[Generate.e][Generate.V-1][2];
        int blocksInGroup2 = T[Generate.e][Generate.V-1][3];
        int bytesInEachBlockOfGroup2 = T[Generate.e][Generate.V-1][4];
        
        Generate.Bytes = new boolean[blocksInGroup1 + blocksInGroup2][][];
        int hanyadik = 0;
        for (int blokk = 0; blokk < blocksInGroup1; blokk++) {
            Generate.Bytes[blokk] = new boolean[bytesInEachBlockOfGroup1][8];
            for (int bajt = 0; bajt < bytesInEachBlockOfGroup1; bajt++) {
                for (int bit = 0; bit < 8; bit++) {
                    Generate.Bytes[blokk][bajt][bit] = Generate.B.get(hanyadik);
                    hanyadik++;
                }
            }
        }
        for (int blokk = blocksInGroup1; blokk < blocksInGroup1 + blocksInGroup2; blokk++) {
            Generate.Bytes[blokk] = new boolean[bytesInEachBlockOfGroup2][8];
            for (int bajt = 0; bajt < bytesInEachBlockOfGroup2; bajt++) {
                for (int bit = 0; bit < 8; bit++) {
                    Generate.Bytes[blokk][bajt][bit] = Generate.B.get(hanyadik);
                    hanyadik++;
                }
            }
        }
        if (hanyadik != Generate.B.size()) {
            System.out.println("Hiba!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        Generate.B = null; //kitörli B-t, mert már nem fog kelleni
    }
    
    public static void vegso() {
        Generate.Bitek = new boolean[Generate.bitNumber];
        int hanyadik = 0;
        for (int i = 0; i < Generate.Bytes[0].length; i++) {
            for (int j = 0; j < Generate.Bytes.length; j++) {
                for (int k = 0; k < 8; k++) {
                    Generate.Bitek[hanyadik] = Generate.Bytes[j][i][k];
                    hanyadik++;
                }
            }
        }
        for (int i = 0; i < Generate.Bytes.length; i++) {
            if (Generate.Bytes[i].length > Generate.Bytes[0].length) {
                for (int j = 0; j < 8; j++) {
                    Generate.Bitek[hanyadik] = Generate.Bytes[i][Generate.Bytes[i].length-1][j];
                    hanyadik++;
                }
            }
        }
        
        for (int i = 0; i < Generate.t; i++) {
            for (int j = 0; j < Generate.ECCBytes.length; j++) {
                for (int k = 0; k < 8; k++) {
                    Generate.Bitek[hanyadik] = Generate.ECCBytes[j][i][k];
                    hanyadik++;
                }
            }
        }
        while (hanyadik < Generate.bitNumber) {
            Generate.Bitek[hanyadik] = false;
            hanyadik++;
        }
    }
    
}
