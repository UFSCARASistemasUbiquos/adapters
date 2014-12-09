package bluetooth;

import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;

public class EchoServer {

    public final UUID uuid = new UUID( //the uid of the service, it has to be unique,
            "27012f0c68af4fbf8dbe6bbaf7aa432a", false); //it can be generated randomly
    public final String name = "Echo Server";                       //the name of the service
    public final String url = "btspp://localhost:" + uuid //the service url
            + ";name=" + name
            + ";authenticate=false;encrypt=false;";
    LocalDevice local = null;
    StreamConnectionNotifier server = null;
    StreamConnection conn = null;
    OutputStream outStream = null;
    DataInputStream inStream = null;
    PrintWriter pWriter = null;
    public int a = 0;

    public EchoServer() {
        try {
            System.out.println("Setting device to be discoverable...");
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
            System.out.println("Start advertising service...");
            server = (StreamConnectionNotifier) Connector.open(url);
            
        } catch (Exception e) {
            System.out.println("Exception Occured: " + e.toString());
        } 
        
        while(true)
            RecebeMensagem();
            
        /*for(;;){
            this.EnviaMensagem("as");
        }*/
    }

    public static void main(String args[]) {
        EchoServer echoserver = new EchoServer();
    }
    /**
     *
     */
    public void RecebeMensagem() {
        
        Test arduino2 = new Test();
        try {
            
            System.out.println("\nWaiting for incoming connection...");
            Cronometro cron1 = new Cronometro();
            conn = server.acceptAndOpen();
            System.out.println("TEMPO DECORRIDO: "+cron1.getAtual());
            System.out.println("Client Connected...");
            outStream = conn.openOutputStream();
            DataInputStream din = new DataInputStream(conn.openInputStream());
            pWriter = new PrintWriter(new OutputStreamWriter(outStream));
            
            int c;
            String texto = "";
            while(true){
                Cronometro cron2 = new Cronometro();
                while (((c = din.read()) != -1) && (c != '\n')) {
                    texto += (char)c;
                }
                if(texto.equalsIgnoreCase("LED")){
                    String b = arduino2.comunicacaoArduinoRecebe(texto);
                    EnviaMensagem(b);
                }
                arduino2.comunicacaoArduino(texto);
                
                if(texto.equalsIgnoreCase("")){
                    break;
                }
                texto = "";
                System.out.println("TEMPO DECORRIDO: "+cron2.getAtual());
            }
        } catch (Exception e) {
            System.out.println("Exception Occured: " + e.toString());
        }
        
    }

    
    private String readData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void EnviaMensagem(String mensagem) {
        mensagem = mensagem+"\r\n";
        try {
            //send response to spp client
            pWriter.write(mensagem);
            pWriter.flush();
            //pWriter.close();
            //return true;
        } catch (Exception e) {
            System.out.println("sendMessage(): " + e);
            //return false;
        }
    }

    public class Cronometro {
        private long inicio = 0;
        // Construtor - também ativa o cronometro.  
        public Cronometro(){
            inicio = System.currentTimeMillis();  
        }  
        // retorna tempo em segundos   
        // não interrompe o cronometro, pode ser chamado várias vezes  
        public long getAtual(){
            long mili = System.currentTimeMillis() - inicio;  
            //return Math.round(mili / 1000.0);  
            return mili;
        }  
    }
    
}