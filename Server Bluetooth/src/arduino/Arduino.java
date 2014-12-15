/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author virtual
 */
public class Arduino{
    private final ControlePorta arduino;
    /**
    * Construtor da classe Arduino
    */
    public Arduino(){
        //arduino = new ControlePorta("COM3",9600);//Windows - porta e taxa de transmissão
        arduino = new ControlePorta("/dev/ttyACM0",9600);//Linux - porta e taxa de transmissão
    }
    /**
    * Envia o comando para a porta serial
     * @param valor
    */
    public void comunicacaoArduino(String valor) {
        arduino.enviaDados(valor);
        //System.out.println(valor);//Imprime o nome do botão pressionado
    //if(false){
        arduino.close();
      //  System.out.println(valor);//Imprime o nome do botão pressionado
    //}
    }
    public void comunicacaoArduinoRecebe(String valor) {
        try {
            //return arduino.recebeDados(valor);
            arduino.enviaDadosArduino(valor);
        } catch (InterruptedException ex) {
            Logger.getLogger(Arduino.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String comunicacaoEnviaRecebe(String valor) {
        return arduino.enviaRecebeDados(valor);
    }
}
