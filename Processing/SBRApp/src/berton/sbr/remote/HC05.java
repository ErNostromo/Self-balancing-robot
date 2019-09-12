package berton.sbr.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.Connector;

public class HC05 {
    public static String mac = "00211303D07A";
    public static String url = "btspp://" + mac + ":1;authenticate=false;encrypt=false;master=false";
    public static char END_CH = ';';

    private String recvString;
    private String bufferString;
    private boolean scanFinished;
    private static RemoteDevice device;
    private StreamConnection s;
    private OutputStream os;
    private InputStream is;
    private boolean onConnect;

    private void setup() {
        recvString = "";
        bufferString = "";
        device = null;
        s = null;
        is = null;
        os = null;
        scanFinished = false;
        onConnect = false;
    }

    public HC05() {
        setup();
    }

    public int connect() {
        System.out.println("Connecting...");
        scanFinished = false;
        try {
            LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
                @Override
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    try {
                        if (mac == null) {
                            String name = btDevice.getFriendlyName(false);
                            System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
                            if (name.matches("HC.*")) {
                                System.out.println("Device found by name.");
                                device = btDevice;
                                scanFinished = true;
                                return;
                            }
                        } else if (btDevice.getBluetoothAddress().equals(mac)) {
                            System.out.println("Device found by mac.");
                            device = btDevice;
                            scanFinished = true;
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void inquiryCompleted(int discType) {
                    scanFinished = true;
                }

                @Override
                public void serviceSearchCompleted(int transID, int respCode) {
                }

                @Override
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                }
            });
        } catch (BluetoothStateException e) {
            e.printStackTrace();
            return 1;
        }
        while (!scanFinished)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        if (device != null) {
            try {
                s = (StreamConnection) Connector.open(url);
                is = s.openInputStream();
                os = s.openOutputStream();
                onConnect = true;
                System.out.println("Connection successful");
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Connection unsuccessful");
                return 2;
            }
        } else {
            System.out.println("Connection unsuccesful/Device not found. Retry.");
            return 3;
        }
    }

    public void disconnect() throws IOException {
        if (isConnected()) {
            try {
                s.close();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setup();
        System.out.println("Disconnected");
    }

    public String getStringFromHC05() throws IOException {
        try {
            if (is.available() > 0) {
                char ch = '\0';
                bufferString = "";
                // read the first recognizable string
                while (ch != END_CH) {
                    ch = (char) is.read();
                    if (ch == '\n' || ch == '\r')
                        ch = '\0';
                    bufferString += ch;
                }

                recvString = bufferString.toString();
                // empty buffer
                while (is.available() > 0)
                    is.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = recvString.toString();
        recvString = "";
        return s;
    }

    public void sendString(String text) {
        if (isConnected() && text != null) {
            // System.out.println("Sending " + text);
            try {
                os.write(text.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Device: " + device + "; text: " + text);
        }
    }

    public boolean isConnected() {
        return device != null && os != null && is != null;
    }

    public boolean onConnect() {
        if (onConnect) {
            onConnect = false;
            return true;
        }
        return false;
    }
}