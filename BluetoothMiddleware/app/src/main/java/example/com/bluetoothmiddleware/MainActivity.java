package example.com.bluetoothmiddleware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    public static final boolean D = true;

    // Well known SPP UUID (will *probably* map to
    // RFCOMM channel 1 (default) if not in use);
    // see comments in onResume().
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //public static final UUID MY_UUID = UUID.fromString("00A98AC7-0000-1000-8000-00805F9B34FB");

    // ==> hardcode your server's MAC address here <==
    //public static String address = "11:11:11:11:11:11";
    public static String address = "90:00:4E:9E:53:F0";

    public Button On, Off, btnCalc, btnOn, btnOff, btnAtualizar;

    public BluetoothAdapter mBluetoothAdapter = null;
    public BluetoothSocket btSocket = null;
    public OutputStream outStream = null;
    public InputStream inStream = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        On = (Button)findViewById(R.id.button1);
        Off = (Button)findViewById(R.id.button2);

        Toast.makeText(getApplicationContext(), "create", Toast.LENGTH_LONG).show();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/
        DesativaBotoes();
        /* BOTOES E FUNCOES */
        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        btnCalc = (Button)findViewById(R.id.buttonCalc);
        btnAtualizar = (Button)findViewById(R.id.atualizar);

        btnOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("LIGALED1;");
                RecebeConfirmacao();
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("DESLIGALED1;");
                RecebeConfirmacao();
            }
        });
        btnCalc.setOnClickListener(new View.OnClickListener() {
            EditText text = (EditText) findViewById(R.id.enviaString);
            public void onClick(View v) {
                sendData(text.getText().toString());
                RecebeCalc();
            }
        });
        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData("STATUSLED1;");
                Recebe();
            }
        });
    }

    public void on(View view) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), "ERROR 7: " + e, Toast.LENGTH_LONG).show();
        }
        boolean conectado = false;
        if (Connect()) {
            AtivaBotoes();
            conectado = true;
        }
        if(!conectado) {
            Toast.makeText(getApplicationContext(), "Servidor não encontrado! ", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View view) {
        DesativaBotoes();
        mBluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Desligado" , Toast.LENGTH_LONG).show();
    }

    private void sendData(String message) {
        message = message+"\n";
        byte[] msgBuffer = message.getBytes();
        try {
            outStream.write(msgBuffer);
            outStream.flush();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "ERROR WRITE: "+e, Toast.LENGTH_LONG).show();
            DesativaBotoes();
        }
    }

    public boolean Connect() {
        // When this returns, it will 'know' about the server,
        // via it's MAC address.
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Socket creation failed: "+e, Toast.LENGTH_LONG).show();
            return false;
        }
        mBluetoothAdapter.cancelDiscovery();
        if (!btSocket.isConnected()) {
            try {
                btSocket.connect();
                Toast.makeText(getApplicationContext(), "Conexão Estabelecida", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR 1:" + e, Toast.LENGTH_LONG).show();
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Toast.makeText(getApplicationContext(), "Unable to close socket during connection failure. " + e2, Toast.LENGTH_LONG).show();
                    return false;
                }
                return false;
            }
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Conexão de envio de dados nao foi criada. "+e, Toast.LENGTH_LONG).show();
                return false;
            }
            try {
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Conexão de recebimento de dados nao foi criada. "+e, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public void Recebe()
    {
        TextView text = (TextView) findViewById(R.id.resposta);
        if (mBluetoothAdapter.isEnabled())
        {
            try {
                while(true) {
                    int bytesAvailable = 0;
                    while(bytesAvailable == 0) {
                        bytesAvailable = inStream.available();
                    }
                    byte[] packetBytes = new byte[bytesAvailable];
                    if (bytesAvailable > 0) {
                        inStream.read(packetBytes);
                        text.setText(new String(packetBytes));
                    }
                    if(!new String(packetBytes).equalsIgnoreCase("")){
                        break;
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR READ: "+e, Toast.LENGTH_LONG).show();
                DesativaBotoes();
            }
        }
    }

    public void RecebeConfirmacao()
    {
        TextView text = (TextView) findViewById(R.id.resposta);
        if (mBluetoothAdapter.isEnabled())
        {
            try {
                while(true) {
                    int bytesAvailable = 0;
                    while(bytesAvailable == 0) {
                        bytesAvailable = inStream.available();
                    }
                    byte[] packetBytes = new byte[bytesAvailable];
                    if (bytesAvailable > 0) {
                        inStream.read(packetBytes);
                    }
                    if(!new String(packetBytes).equalsIgnoreCase("")){
                        break;
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR READ: "+e, Toast.LENGTH_LONG).show();
                DesativaBotoes();
            }
        }
    }

    public void RecebeCalc()
    {
        TextView text = (TextView) findViewById(R.id.respostaCalc);

        if (mBluetoothAdapter.isEnabled())
        {
            try {
                while(true) {
                    int bytesAvailable = 0;
                    while(bytesAvailable == 0) {
                        bytesAvailable = inStream.available();
                    }
                    byte[] packetBytes = new byte[bytesAvailable];
                    if (bytesAvailable > 0) {
                        inStream.read(packetBytes);
                        text.setText(new String(packetBytes));
                    }
                    if(!new String(packetBytes).equalsIgnoreCase("")){
                        break;
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR READ: "+e, Toast.LENGTH_LONG).show();
                DesativaBotoes();
            }
        }
    }

    public void AtivaBotoes()
    {
        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        btnAtualizar = (Button)findViewById(R.id.atualizar);
        btnOn.setEnabled(true);
        btnOff.setEnabled(true);
        btnAtualizar.setEnabled(true);
    }

    public void DesativaBotoes()
    {
        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        btnAtualizar = (Button)findViewById(R.id.atualizar);
        btnOn.setEnabled(false);
        btnOff.setEnabled(false);
        btnAtualizar.setEnabled(false);
    }
}