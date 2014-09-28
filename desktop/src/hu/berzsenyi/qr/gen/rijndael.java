package hu.berzsenyi.qr.gen;

public class rijndael {
    
    public static boolean[] diff(boolean[] x, boolean[] y){
        
        int PIV1 = 0;// x legnagyobb helyiértékű 1-esének heyiértéke
        int PIV2 = 0;// y legnagyobb helyiértékű 1-esének heyiértéke
        
        for(int i = 14; i >= 0; i--){// helyiérték számítás: x
            if(x[i]){
                PIV1 = i;
                break;
            }
        }
        
        for(int i = 14; i >= 0; i--){// helyiérték számítás: x
            if(y[i]){
                PIV2 = i;
                break;
            }
        }
        
        int KUL = PIV1 - PIV2;// különbség az eltoláshoz
        boolean[] tmp = new boolean[16];
       
        for(int i = 14; i-KUL >= 0; i--){// helyiérték "összeillesztés"
            tmp[i] = y[i-KUL];
        }
        
        for(int i = 14; i >= 0; i--){// kivonás/xorzás/összeadás
            if(x[i]){
                if(tmp[i]){
                    x[i] = false;
                }
                else{
                    x[i] = true;
                }
            }
            else{
                if(tmp[i]){
                    x[i] = true;
                }
                else{
                    x[i] = false;
                }
            }
        }
        
        return x;
    }
    
    public static boolean[] multiply (boolean[] x, boolean[] y){
        
        boolean g[] = new boolean [16];
        boolean[] RET = new boolean[15];
        
        //generátor polinom megadása
        g[0] = true;
        g[1] = false;
        g[2] = true;
        g[3] = true;
        g[4] = true;
        g[5] = false;
        g[6] = false;
        g[7] = false;
        g[8] = true;
        //-----------------------------
        
        for(int i = 0; i < 8; i++){//a*b 15 bites eredménnyel
            for(int j = 0; j < 8; j++){
                if(x[i] && y[j]){
                    if(RET[i+j]){
                        RET[i+j] = false;
                    }
                    else{
                        RET[i+j] = true;
                    }
                }
            }
        }
        
        while(RET[8] || RET[9] || RET[10] || RET[11] || RET[12] || RET[13] || RET[14]){// amíg van értelme maradékot nézni
            RET = diff(RET, g);// RET-g
        }
        
        return RET;
    }
    
    public static boolean[] XOR(boolean[] a, boolean[] b){
        
        boolean[] r = new boolean[16]; //16 bites eredmény tömb
        r = rijndael.multiply(a, b);//multiply(a, b): összeszorozza a-t, b-vel
        
        boolean[] sol = new boolean[8]; //8 bites eredmény tömb
        
        for(int i = 0; i < 8; i++){//sol = r
                sol[i] = r[i];
        }
        
        return sol;
    }
}
