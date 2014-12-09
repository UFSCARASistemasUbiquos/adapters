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
/**
 * @author klauder
 */
public class Test {
    private ControlePorta arduino;
    /**
    * Construtor da classe Arduino
    */
    public Test(){
        arduino = new ControlePorta("COM3",9600);//Windows - porta e taxa de transmissão
        //arduino = new ControlePorta("/dev/ttyUSB0",9600);//Linux - porta e taxa de transmissão
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
    public String comunicacaoArduinoRecebe(String valor) {
        return arduino.recebeDados(valor);
    }
}
