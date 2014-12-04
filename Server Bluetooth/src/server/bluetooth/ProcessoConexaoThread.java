/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.bluetooth;

/**
 *
 * @author root
 */
import add.Cronometro;
import arduino.Arduino;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.microedition.io.StreamConnection;

public class ProcessoConexaoThread implements Runnable {

    private final Arduino arduino = new Arduino();
    private final StreamConnection mConnection;
    PrintWriter pWriter = null;
    OutputStream outputStream = null;

    // Constant that indicate command from devices
    private int comando;

    public ProcessoConexaoThread(StreamConnection connection) {
        mConnection = connection;
    }

    @Override
    public void run() {
        try {
            // prepare to receive data
            InputStream inputStream = mConnection.openInputStream();
            outputStream = mConnection.openOutputStream();
            pWriter = new PrintWriter(new OutputStreamWriter(outputStream));
            System.out.println("Esperando dados...");
            String texto = "";
            while (true) {
                while (((comando = inputStream.read()) != -1) && (comando != '\n')) {
                    texto += (char)comando;
                }
                if (comando < 0) {
                    System.out.println("Processo Finalizado");
                    break;
                }
                
                for (int i = 0; i < 3; i++) {
                    //texto = texto.equals("0") ? "1-"+i : "0-"+i;
                    processaCommando(texto+"-"+i);
                    /*Cronometro tempo_mili = new Cronometro();
                    String resp = processaCommando(texto);
                    if (resp.equalsIgnoreCase("*")) {
                        System.out.println("TEMPO DECORRIDO: "+tempo_mili.getAtual());
                        //break;
                    }*/
                }                
                texto = "";
            }
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private void processaCommando(String comando) {
        try {
            arduino.comunicacaoArduinoRecebe(comando);
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }
    
    private void EnviaMensagemAndroid(String mensagem){
        System.out.println("MENSAGEM:" + mensagem);
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

}
