/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arduino;

/**
 *
 * @author virtual
 */
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class ControlePorta {

    private OutputStream serialOut;
    private InputStream serialIn;
    private final int taxa;
    private final String portaCOM;

    /**
     * Construtor da classe ControlePorta
     *
     * @param portaCOM - Porta COM que será utilizada para enviar os dados para
     * o arduino
     * @param taxa - Taxa de transferência da porta serial geralmente é 9600
     */
    public ControlePorta(String portaCOM, int taxa) {
        this.portaCOM = portaCOM;
        this.taxa = taxa;
        this.initialize();
    }

    /**
     * Médoto que verifica se a comunicação com a porta serial está ok
     */
    private void initialize() {
        try {
            //Define uma variável portId do tipo CommPortIdentifier para realizar a comunicação serial
            CommPortIdentifier portId = null;
            try {
                //Tenta verificar se a porta COM informada existe
                portId = CommPortIdentifier.getPortIdentifier(this.portaCOM);
            } catch (NoSuchPortException npe) {
                //Caso a porta COM não exista será exibido um erro
                JOptionPane.showMessageDialog(null, "Porta COM não encontrada.",
                        "Porta COM", JOptionPane.PLAIN_MESSAGE);
            }
            //Abre a porta COM
            SerialPort port = (SerialPort) portId.open("Comunicação serial", this.taxa);
            serialOut = port.getOutputStream();
            serialIn = port.getInputStream();
            port.setSerialPortParams(this.taxa, //taxa de transferência da porta serial
                    SerialPort.DATABITS_8, //taxa de 10 bits 8 (envio)
                    SerialPort.STOPBITS_1, //taxa de 10 bits 1 (recebimento)
                    SerialPort.PARITY_NONE); //receber e enviar dados
        } catch (Exception e) {
            System.out.println("ERRO: " + e);
        }
    }

    /**
     * Método que fecha a comunicação com a porta serial
     */
    public void close() {
        try {
            serialOut.close();
        } catch (IOException e) {
            /*JOptionPane.showMessageDialog(null, "Não foi possível fechar porta COM.",
                    "Fechar porta COM", JOptionPane.PLAIN_MESSAGE);*/
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    /**
     * @param opcao - Valor a ser enviado pela porta serial
     */
    public void enviaDados(String opcao) {
        
        try {
            String stringToConvert = opcao;
            byte[] theByteArray = stringToConvert.getBytes();
            serialOut.write(theByteArray);//escreve o valor na porta serial para ser enviado
        } catch (IOException ex) {
            /*JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ",
                    "Enviar dados", JOptionPane.PLAIN_MESSAGE);*/
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    public String recebeDados(String opcao) {
        System.out.println("OPCAO: "+opcao);
        opcao = opcao.split("-")[0];
        System.out.println("OPCAO: "+opcao);
        try {
            String stringToConvert = opcao;
            byte[] theByteArray = stringToConvert.getBytes();
            serialOut.write(theByteArray);//escreve o valor na porta serial para ser enviado
        } catch (IOException ex) {
            /*JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ", "Enviar dados", JOptionPane.PLAIN_MESSAGE);*/
            System.out.println("ERRO: " + ex.getMessage());
        }
        int available = 0;
        /* FICA ESPERANDO A RESPOSTA */
        while(available==0){
            try {
                available = serialIn.available();
            } catch (IOException ex) {
                Logger.getLogger(ControlePorta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        byte[] msgBuffer = new byte[available];
        
        if (available > 0) {
            try {
                serialIn.read(msgBuffer);
            } catch (IOException ex) {
                System.out.println("ERRO: " + ex.getMessage());
                /*Logger.getLogger(ControlePorta.class.getName()).log(Level.SEVERE, null, ex);*/
            }
        }
        String Str = new String(msgBuffer);
        /*int valor = Integer.parseInt(Str);
         System.out.println("TESTE: "+valor );*/
        return Str;
    }
    
    public void enviaDadosArduino(String valor) throws InterruptedException {
        EnviaComando envia = new EnviaComando(serialOut, valor);
        Thread t = new Thread(envia);
        t.start();
        //Thread.sleep(0, 1);
        t.interrupt();
        /*
        try {
            String stringToConvert = valor;
            byte[] theByteArray = stringToConvert.getBytes();
            serialOut.write(theByteArray);//escreve o valor na porta serial para ser enviado
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ", "Enviar dados", JOptionPane.PLAIN_MESSAGE);
        }
                */
    }
    
}
