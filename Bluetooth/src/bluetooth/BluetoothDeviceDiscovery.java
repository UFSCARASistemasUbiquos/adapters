/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * Class that discovers all bluetooth devices in the neighbourhood and displays
 * their name and bluetooth address.
 */
public class BluetoothDeviceDiscovery implements DiscoveryListener {

//object used for waiting
    private static Object lock = new Object();

//vector containing the devices discovered
    private static Vector vecDevices = new Vector();

//main method of the application
    public static void main(String[] args) throws IOException {

//create an instance of this class
        BluetoothDeviceDiscovery bluetoothDeviceDiscovery = new BluetoothDeviceDiscovery();

//display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: " + localDevice.getBluetoothAddress());
        System.out.println("Name: " + localDevice.getFriendlyName());

//find devices
        DiscoveryAgent agent = localDevice.getDiscoveryAgent();

        System.out.println("Starting device inquiry…");
        agent.startInquiry(DiscoveryAgent.GIAC, bluetoothDeviceDiscovery);

        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Device Inquiry Completed. ");

//print all devices in vecDevices
        int deviceCount = vecDevices.size();

        if (deviceCount <= 0) {
            System.out.println("No Devices Found .");
        } else {
//print bluetooth device addresses and names in the format [ No. address (name) ]
            System.out.println("Bluetooth Devices: ");
            for (int i = 0; i < deviceCount; i++) {
                RemoteDevice remoteDevice = (RemoteDevice) vecDevices.elementAt(i);
                System.out.println((i + 1) + ". " + remoteDevice.getBluetoothAddress() + " (" + remoteDevice.getFriendlyName(true) + ")");
            }
        }

    }//end main
    private String connectionURL;

//methods of DiscoveryListener
    /**
     * This call back method will be called for each discovered bluetooth
     * devices.
     */
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        System.out.println("Device discovered: " + btDevice.getBluetoothAddress());
//add the device to the vector
        if (!vecDevices.contains(btDevice)) {
            vecDevices.addElement(btDevice);
        }
    }

//no need to implement this method since services are not being discovered
    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        if (servRecord.length>0 && servRecord!=null)
        {
            connectionURL=servRecord[0].getConnectionURL(0, false);
            //The Service's name can be retrieved as follows
            //All the attributes are retrieved
            int[] ids=servRecord[0].getAttributeIDs();  
 
            //For each attribute
            for(int j=0;j<ids.length;j++)
            {
                DataElement ServiceName;
                try{
                    ServiceName=servRecord[0].getAttributeValue(ids[j]);
                    //If this is a String Attribute
                    if(ServiceName.getDataType()==DataElement.STRING)
                    {
                        System.out.println("The Service name is: "+ServiceName.getValue());
                    }                    
                }
                catch(Exception e){}
            }
 
        }
    }

//no need to implement this method since services are not being discovered
    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        switch(respCode) {
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                System.out.println("Services discovering Complete!");
 
                StreamConnection strcon;
                try {
                    strcon = (StreamConnection) Connector.open(connectionURL);
 
                    OutputStream outp   = strcon.openOutputStream();
                     System.out.println("Sending...\n");
                     String message="Just a message\n";
                     outp.write(message.length());
                     outp.write(message.getBytes());
                     outp.flush();
                     outp.close();
                     System.out.println("Sent!\n");
                } catch (IOException e) {                    
                    e.printStackTrace();
                }
                break;
            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                System.out.println("Services discovering terminated!");
                break;
            case DiscoveryListener.SERVICE_SEARCH_ERROR:
                System.out.println("Sevice discovering error!");
                break;
            default:
                break;
        }
        /*form.removeCommand(cancelServicesDiscoverCommand);
        transactionID = -1;*/
    }

    /**
     * This callback method will be called when the device discovery is
     * completed.
     */
    @Override
    public void inquiryCompleted(int discType) {
        synchronized (lock) {
            lock.notify();
        }

        switch (discType) {
            case DiscoveryListener.INQUIRY_COMPLETED:
                System.out.println("INQUIRY_COMPLETED");
                break;

            case DiscoveryListener.INQUIRY_TERMINATED:
                System.out.println("INQUIRY_TERMINATED");
                break;

            case DiscoveryListener.INQUIRY_ERROR:
                System.out.println("INQUIRY_ERROR");
                break;

            default:
                System.out.println("Unknown Response Code");
                break;
        }
    }//end method
}//end class