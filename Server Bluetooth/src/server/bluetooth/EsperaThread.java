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
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class EsperaThread implements Runnable {

    /**
     * Constructor
     */
    public EsperaThread() {
    }

    @Override
    public void run() {
        esperandoConexao();
    }

    /**
     * Waiting for connection from devices
     */
    private void esperandoConexao() {
        // retrieve the local Bluetooth device object
        LocalDevice local = null;

        StreamConnectionNotifier notifier;
        StreamConnection connection = null;

        // setup the server to listen for connection
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID(11111111); // "04c6093b-0000-1000-8000-00805f9b34fb"
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            return;
        }
        // waiting for connection
        while (true) {
            try {
                System.out.println("Aguardando conexão...");
                connection = notifier.acceptAndOpen();
                Thread processThread = new Thread(new ProcessoConexaoThread(connection));
                processThread.start();
            } catch (Exception e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }
    }
}
