package berton.sbr;

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

public class HC05 extends Thread {
    private static final String mac = "00211303D07A";
    private static final String url = "btspp://" + mac + ":1;authenticate=false;encrypt=false;master=false";
    private volatile String recvString;
    private volatile boolean scanFinished;
    private volatile static RemoteDevice device;
    private volatile StreamConnection s;
    private volatile OutputStream os;
    private volatile InputStream is;
    private volatile boolean toConnect;
    private volatile boolean toDisconnect;
    private volatile boolean onConnect;
    private volatile boolean quit;

    public static final char END_CH = ';';

    private void setup() {
        recvString = "";
        toConnect = false;
        toDisconnect = false;
        onConnect = false;
        quit = false;
        device = null;
        s = null;
        is = null;
        os = null;
        scanFinished = false;
    }

    public HC05() {
        setup();
    }

    public void connect() throws Exception {
        toConnect = true;
    }

    public void disconnect() throws IOException {
        toDisconnect = true;
    }

    public String getStringFromHC05() throws IOException {
        String s = recvString.toString();
        recvString = "";
        return s;
    }

    public void sendString(String text) throws IOException {
        if (device != null && text != null) {
            os.write(text.getBytes());
        } else {
            System.out.println("Device: " + device + "; text: " + text);
        }
    }

    public boolean isConnected() {
        return (device != null);
    }

    public void run() {
        System.out.println("Started hc05 thread");
        while (!quit) {

            if (toConnect) {
                System.out.println("Connecting...");
                scanFinished = false;
                try {
                    LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC,
                            new DiscoveryListener() {
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Connection successful");
                    onConnect = true;
                } else {
                    System.out.println("Connection unsuccesful/Device not found. Retry.");
                }

                toConnect = false;
            }

            if (toDisconnect) {
                if (device != null) {
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

            if (device != null) {
                try {
                    if (is.available() > 0) {
                        char ch = '\0';
                        recvString = "";
                        // read the first recognizable string
                        while (is.available() > 0 && ch != END_CH) {
                            ch = (char) is.read();
                            if (ch == '\n' || ch == '\r')
                                ch = '\0';
                            recvString += ch;
                        }

                        // empty buffer
                        while (is.available() > 0)
                            is.read();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Stopped hc05 thread");
    }

    public boolean onConnect() {
        if (onConnect) {
            onConnect = false;
            return true;
        }
        return false;
    }

    public void enableThread() {
        quit = false;
    }

    public void disableThread() {
        quit = true;
    }
}