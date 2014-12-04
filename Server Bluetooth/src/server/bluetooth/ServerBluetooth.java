/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.bluetooth;

/**
 *
 * @author virtual
 */
public class ServerBluetooth {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //while(true){
            Thread esperaThread = new Thread(new EsperaThread());
            esperaThread.start();
        //}
    }
    
}
