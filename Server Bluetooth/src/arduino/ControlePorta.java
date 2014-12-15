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
import add.Cronometro;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
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
    
    private static final String PORT_NAMES[] = {
        "/dev/ttyACM0", // Raspberry Pi
    };

    /**
     * Médoto que verifica se a comunicação com a porta serial está ok
     */
    private void initialize() {
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
        try {
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
            //First, Find an instance of serial port as set in PORT_NAMES.
            while (portEnum.hasMoreElements()) {
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();

                for (String portName : PORT_NAMES) {

                    if (currPortId.getName().equals(portName)) {
                        portId = currPortId;
                        break;
                    }
                }
            }
            if (portId == null) {
                System.out.println("Could not find COM port.");
                return;
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
            if(Str.equalsIgnoreCase(";")){
                System.out.println("CERTO");
            }else{
                System.out.println("ERRO");
            }
            
        } catch (IOException ex) {
            /*JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ",
                    "Enviar dados", JOptionPane.PLAIN_MESSAGE);*/
            System.out.println("ERRO: " + ex.getMessage());
        }
    }

    public String enviaRecebeDados(String opcao) {       
        try {
            String stringToConvert = opcao;
            byte[] theByteArray = stringToConvert.getBytes();
            serialOut.write(theByteArray);//escreve o valor na porta serial para ser enviado
        } catch (IOException ex) {
            /*JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ", "Enviar dados", JOptionPane.PLAIN_MESSAGE);*/
            System.out.println("ERRO: " + ex.getMessage());
        }
        String texto = "";
        int caracter;
        int available = 0;
        while(true){
            if(available==0){
                while(available==0){
                    try {
                        available = serialIn.available();
                    } catch (IOException ex) {
                        Logger.getLogger(ControlePorta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            try {
                caracter = serialIn.read();
                texto += (char)caracter;
                if(caracter == ';'){
                    break;
                }
            } catch (IOException ex) {
                System.out.println("ERRO: " + ex.getMessage());
            }
        }
        return texto;
    }    
    
    public void enviaDadosArduino(String valor) throws InterruptedException {
        EnviaComando envia = new EnviaComando(serialOut, valor);
        Thread t = new Thread(envia);
        t.start();
        //Thread.sleep(0, 1);
        t.interrupt();
    }
    
}
