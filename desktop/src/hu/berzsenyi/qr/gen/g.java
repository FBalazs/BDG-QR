package hu.berzsenyi.qr.gen;

public class g {
    
    static int[] al;
    
    public static int toint (int i){
       logantilop();
       if(i == -1){
           return 0;
       }
       else{
            i = al[i];

            return i;
       }
    }
    
    public static int toexp (int i){
       logantilop();
       
       if(i == 0){
           return -1;
       }
       else{
            for(int j = 0; j < 256; j++){
                if(al[j] == i % 256){
                    i = j;
                    break;
                }
            }
       }
       return i;
    }
    
    public static int[] pol (int[] a){
        
        logantilop();
        
        int t = 0;
        
        for(int i = 29; i >= 0; i--){
            if(a[i] != -1){
                t = i;
                break;
            }
        }
        
        int b[] = new int [31];
        
        for(int i = t; i >= 0; i--){
            b[i+1] = a[i];
        }
        
        for(int i = t; i >= 0; i--){
            a[i] = a[i] + t;
        }
        
        for(int i = t + 1; i >= 0; i--){ // öszzeg
            
            int a1;
            if (a[i] == -1) {
                a1 = 0;
            }
            else {
                a1 = al[a[i] % 255];
            }
            
            int b1;
            
            if(i != 0){
                b1 = al[b[i]];
            }
            else{
                b1 = 0;
            }
           
            boolean[] a2 = new boolean[8];
            boolean[] b2 = new boolean[8];

            a2 = conv.tobin(a1);
            b2 = conv.tobin(b1);

            boolean[] harmadik = new boolean[8];
            
            for(int j = 0; j < 8; j++){
                harmadik[j] = a2[j]^b2[j];
            }
            
            int o = conv.todec(harmadik);
            
            for(int j = 0; j < 256; j++){
                if(al[j] == o % 256){
                    a[i] = j;
                    break;
                }
            }
        }
        return a;    
    }
    
    public static void logantilop(){
        
        boolean[] alpha = new boolean[8];
        boolean[] alpha1 = new boolean[8];
        
        alpha[1] = true;
        alpha1 = alpha;
        
        al = new int[256];
        al[0] = 1;
        al[1] = 2;
        
        for(int i = 0; i < 254; i++){
            alpha = rijndael.XOR(alpha, alpha1);
            al[i + 2] = conv.todec(alpha);
        }
    }
    
    public static int[] g(int t){
        boolean[][] b = new boolean[31][8];
        
        int a[] = new int [31]; 
        for (int i = 0; i < a.length; i++) {
            a[i] = -1;
        }
        
        a[1] = 0; //alpha^0*x
        a[0] = 0; //+alpha^0
        //x + 1
        
        for(int i = 0; i < t - 1; i++){
            a = pol(a);
        }
        
        int u = a.length - 1;
        while (0 <= u && a[u] == -1) {
            u--;
        }
        int uj[] = new int [u+1];
        for (int i = 0; i <= u; i++) {
            uj[i] = a[i];
        }
        
        /*for(int i = 0; i < t; i++){
            b[i] = conv.tobin(a[i]);
        }
        */
        int x[] = {1,2};
        return uj;
    }
    
}