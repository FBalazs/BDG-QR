package hu.berzsenyi.qr.gen;

//egyelore csak Byte kódolás (ISO 8859-1)

import java.util.ArrayList;
import java.util.Arrays;


public class Generate {
    public static String S; //a kodolando uzenet
    public static int e; //a hibajvitas szintje (L: 0, M: 1, Q: 2, H: 3)
    public static int V; //verzio    
    public static int N; //N*N-es lesz a QR-kod
    public static boolean QR[][]; //OR[i][j]: true: fekete, false: fehér
    public static boolean foglalt[][]; //QR[i][j]-ben már van valami, oda nem kerülhet az adat
    public static boolean szele[][]; //a QR-kód sarkában lévő minta
    public static int m = 0; //0 <= maszk id <= 7
    public static boolean FS[] = new boolean [15];//Format String
    public static boolean VS[] = new boolean [18];//Version Information String megfordítva
    public static ArrayList<Boolean> B = new ArrayList<Boolean>(); //a bitek
    public static int capacity;
    public static int t; //blokkonként ennyi hibajavító bájt kell
    public static boolean Bytes[][][]; //Byte[i][j][0...7]: az i-edik blokk j-edik bájtjának bitjei    
    public static boolean ECCBytes[][][];//az egyes blokkokhoz tartozó hibajavítások
    public static int bitNumber; //a végső bitsorozat hossza
    public static boolean Bitek[]; //ebben van minden adat és hiabajavítása, amit a kódba írunk
    public static int ge[]; //a generátorpolinom együtthatói decimálisan
    
    public static void main(String[] args) {
        Input.beolvas();
        Szamol.verzio();
        Bits.modeIndicator();
        Bits.characterCountIndicator();
        Bits.encode();
        Bits.feltolt();
        Bits.bajtok();
        ECCBytes = new boolean [Bytes.length][t][8];
        ge = g.g(t);
        //kiszámoljuk a t-ed fokú g(x) generátorpolinomot
        for (int i = 0; i < Bytes.length; i++) {
            ECCBytes[i] = hiba.hiba(Bytes[i]);
            //ECCBytes[i]-be beírjuk a Bytes[i]-hez tarozó hibajavító bájtokat (vagyis a Bytes[i]-hez hozzárendelhető polinom maradékát vesszük g(x)-szel és beírjuk az együtthatókat ECCBytes[i]-be)
        }
        Szamol.bitNumber();
        Bits.vegso();
        Minta.letrehoz();
        Minta.sarkok();
        Minta.separators();
        Minta.kisNegyzetek();
        Minta.szaggatott();
        Minta.feketeNegyzet();
        Szamol.maszk();
        Szamol.format();
        Minta.format();
        if (V >= 7) {
            Szamol.verzioInformacio();
            Minta.verzio();
        }
        
        Minta.adat();
        Minta.maszk();
        
        Output.kirajzol();
    }
}
