/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bluetooth;

/**
 *
 * @author virtual
 */
public class Cliente implements Runnable{
    
    private int id;
    public Cliente(int id){
        this.id = id;
    }
    @Override
    public void run() {
        dizoi();
    }
    
    public void dizoi(){
        System.out.println("oi " + id);
    }
    
    public static void main (String agrs[]){
    
        for (int i = 0; i < 10; i++) {
            Cliente c = new Cliente(i);
            Thread t = new Thread(c);
            t.start();    
            
            Thread.sleep(100);
        }
        
    }
}
