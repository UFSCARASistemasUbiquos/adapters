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

    private int numRequisicoes = 20;
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
                Cronometro tempo_mili;
                tempo_mili= new Cronometro();
                for (int i = 1; i <= this.numRequisicoes; i++) {
                    processaCommando2(texto);
                    System.out.println("PROCESSO: "+i+" -- TEMPO DECORRIDO: "+tempo_mili.getParcial()+" TOTAL: "+tempo_mili.getAtual() );
                    tempo_mili.setParcial(tempo_mili.getAtual());
                }
                /*for (int i = 0; i < 3; i++) {
                    //texto = texto.equals("0") ? "1-"+i : "0-"+i;
                    processaCommando(texto+"-"+i);
                    /*Cronometro tempo_mili = new Cronometro();
                    String resp = processaCommando(texto);
                    if (resp.equalsIgnoreCase("*")) {
                        System.out.println("TEMPO DECORRIDO: "+tempo_mili.getAtual());
                        //break;
                    }
                }*/            
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
    
    public int count = 0;
    
    private void processaCommando2(String comando) {
        try {
            String a = arduino.comunicacaoEnviaRecebe(comando);
            //System.out.println("RESULTADO: "+a);
            if(this.count == 0){
                System.out.println("ENTROU: "+this.count);
                EnviaMensagemAndroid(a);
                this.count++;
            }else{
                this.count++;
                if(this.count == this.numRequisicoes){
                    this.count = 0;
                }
            }
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }
    
    private void EnviaMensagemAndroid(String mensagem){
        //System.out.println("MENSAGEM:" + mensagem);
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
