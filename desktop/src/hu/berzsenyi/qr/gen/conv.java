package hu.berzsenyi.qr.gen;


public class conv {
    
    public static boolean[] tobin (int a){
        boolean[] b = new boolean[8];
        
        for(int i = 0; i < 8; i++){
            if(a % 2 == 0){
                b[i] = false;
                a = a / 2;
            }
            else{
                b[i] = true;
                a = (a - 1)/2;
            }
        }
        
        return b;
    }
    
    public static boolean[] tobin2 (int a){
        boolean[] b = new boolean[8];
        
        for(int i = 0; i < 8; i++){
            if(a % 2 == 0){
                b[7 - i] = false;
                a = a / 2;
            }
            else{
                b[7 - i] = true;
                a = (a - 1)/2;
            }
        }
        
        return b;
    }
    
    public static int todec (boolean[] b){
        
        int a = 0;
        
        for(int i = 0 ; i < 8; i++){
            if(b[i]){
                a = a + (int)Math.pow(2, i);
            }
        }
        
        return a;        
    }
    
    public static int todec2 (boolean[] b) {
        int a = 0;
        
        for(int i = 0 ; i < 8; i++){
            if(b[i]){
                a = a + (int)Math.pow(2, 7-i);
            }
        }
        
        return a;    
    }
}
