/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author virtual
 */
public class EnviaComando implements Runnable {
    
    private final OutputStream serialOut;
    private String mensagem;
    byte[] theByteArray;
    
    @Override
    public void run() {
        try {
            System.out.println("OPCAO2: "+this.mensagem);
            this.mensagem = this.mensagem.split("-")[0];
            //System.out.println("OPCAO: "+this.mensagem);
            theByteArray = this.mensagem.getBytes();
            serialOut.write(theByteArray);//escreve o valor na porta serial para ser enviado
        } catch (IOException ex) {
            //JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ", "Enviar dados", JOptionPane.PLAIN_MESSAGE);
            System.out.println("Erro: " + ex.getMessage());
        }
    }
    
    public EnviaComando(OutputStream serialOut, String mensagem){
        this.serialOut = serialOut;
        this.mensagem = mensagem;
    }
    
}
