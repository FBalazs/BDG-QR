package generate;

import java.util.Arrays;

public class hiba {
    static int[] e;
    static int efox;
    static boolean ez = false;
    
    public static void hibaj(){
        int szorzottge[] = new int [Generate.ge.length];
        for (int i = 0; i < Generate.ge.length; i++) {
            szorzottge[i] = g.toint((g.toexp(e[efox]) + Generate.ge[i]) % 255);
        }
        
        for(int i = efox - 1; i >= 0; i--){
            if (i >= efox - Generate.t) {
                boolean[] a;
                boolean[] b;
                boolean[] c = new boolean[8];
                a = conv.tobin(szorzottge[i - efox + Generate.t]);
                b = conv.tobin(e[i]);
                for(int j = 0; j < 8; j++){
                    c[j] = a[j]^b[j];
                }
                e[i] = conv.todec(c);
            }
        }
        e[efox] = 0;
        efox--;
    }
    
    
    public static boolean[][] hiba (boolean[][] mB) {
        int m[] = new int [mB.length];
        for (int i = 0; i < mB.length; i++) {
            m[i] = conv.todec2(mB[i]);
            if (m[0] == 70 && m[i] == 224) {
                System.out.println(Arrays.toString(mB[i]));
            }
        }
        if (m[0] == 70) {
            ez = true;
            System.out.println(Arrays.toString(m));
        }
        //
        efox = Generate.t + m.length - 1;
        e = new int[efox + 1];
        for (int i = 0; i < m.length; i++) {
            e[e.length - 1 -i] = m[i];
        }
        if (ez) {
            System.out.println(Arrays.toString(e)+"*");
        }
        
        int uj[] = new int [m.length+Generate.t];
        for (int i = m.length - 1; 0 <= i; i--) {
            uj[i+Generate.t] = m[i];
        }
        
        while (efox > Generate.t - 1) {
            hibaj();
            if (ez) {
                System.out.println(Arrays.toString(e));
            }
        }
        
        int mo[] = new int [Generate.t];
        
        for (int i = efox; 0 <= i; i--) {
            mo[efox-i] = e[i];
        }
        if (ez) {
            System.out.println(Arrays.toString(mo));
            System.out.println();
        }
        
        boolean b[][] = new boolean [mo.length][8];
        for (int i = 0; i < b.length; i++) {
            b[i] = conv.tobin2(mo[i]);
        }
        return b;
        
    }
    
}
