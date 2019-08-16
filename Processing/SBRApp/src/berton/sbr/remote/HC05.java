package berton.sbr.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.Connector;

public class HC05 {
    private static final String mac = "00211303D07A";
    private static final String url = "btspp://" + mac + ":1;authenticate=false;encrypt=false;master=false";
    private boolean scanFinished;
    private static RemoteDevice device;
    private StreamConnection s;
    private OutputStream os;
    private InputStream is;

    public static final char END_CH = ';';

    public static void main(String[] args) {
        try {
            HC05 hc05 = new HC05();
            if (hc05.connect()) {
                System.out.println("Connected");
                System.out.println(hc05.getStringFromHC05());
                hc05.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        device = null;
        s = null;
        is = null;
        os = null;
        scanFinished = false;
    }

    public HC05() {
        setup();
    }

    public boolean connect() throws Exception {
        scanFinished = false;
        LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                try {
                    if (mac == null) {
                        String name = btDevice.getFriendlyName(false);
                        System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
                        if (name.matches("HC.*")) {
                            device = btDevice;
                            scanFinished = true;
                            return;
                        }
                    } else if (btDevice.getBluetoothAddress().equals(mac)) {
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

        while (!scanFinished)
            Thread.sleep(500);

        if (device != null) {
            s = (StreamConnection) Connector.open(url);
            is = s.openInputStream();
            os = s.openOutputStream();
        }

        return device != null;
    }

    public void disconnect() throws IOException {
        if (device != null) {
            s.close();
            os.close();
            is.close();
        }
        setup();
    }

    public String getStringFromHC05() throws IOException {
        if (device == null)
            return "Device is null";
        String recvString = "";
        char ch = '\0';
        while (is.available() == 0) {
        }
        if (is.available() > 0) {
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
            return recvString;
        }
        return "";
    }

    public void sendString(String text) throws IOException {
        if (device != null) {
            os.write(text.getBytes());
        }
    }

    public boolean isConnected() {
        return (device != null);
    }
}