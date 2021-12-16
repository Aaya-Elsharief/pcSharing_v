/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import javax.jmdns.ServiceInfo;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Discovery {

    public static void start() {
        System.out.println(Frame.myPublicKeyStr);
        Frame.jLabel3.setText("Discovery get started... ");
        String instantName = Frame.jtfDeviceName.getText();
        Listener app = new Listener();
//        app.setInstantName(instantName);
        app.start();
        Register reg = new Register();
        //   reg.setInstantName(instantName);
        reg.start();
    }

    /*  public static String generateString() {
        int min = 0;
        int max = 4;
        String[] array = {"aswd", "hp", "apple", "qwer", "rand"};
        int rand = (int) (Math.random() * (max - min + 1) + min);
        return array[rand];

    }*/
}

class Register extends Thread {

    static String instantName = Frame.jtfDeviceName.getText();
    static String k = Frame.myPublicKeyStr;

//    public static void setInstantName(String instantName) {
//        Register.instantName = instantName;
//    }
    @Override
    public void run() {

        try {
            amain();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public static void amain() throws InterruptedException {

        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
            System.out.println("k " + k);
            // Register a service
            Map<String, String> m = new HashMap<>();
            m.put("port", Frame.myPort);
            m.put("publicKey", "MIIBIjANBgkqhkiGAB/yKuCamd1QbRYYBLqFnYuehjsyOJKVNHllGP/WJrgW1vXNf1sgWU5o+wIDAQAB\n" +
"");

            //final String type, final String name, final int port, final String text
            //final String type, final String name, final int port, final int weight, final int priority, final Map<String, ?> props
            ServiceInfo serviceInfo = ServiceInfo.create("_aaya._tcp.local.", instantName, 1234, 0, 0, m);
            jmdns.registerService(serviceInfo);

            // Wait a bit
            Thread.sleep(2000);

            // Unregister all services
            //   jmdns.unregisterAllServices();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

class Listener extends Thread {

    static String instantName = Frame.jtfDeviceName.getText();

    public void run() {
        try {
            amain();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static class SampleListener implements ServiceListener {

        //DefaultListModel DLM = new DefaultListModel();
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());

        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());

            String deviceName = event.getName();
            System.out.println("deviceName " + deviceName);
            for (DeviceInfo dev : Frame.deviceInfoList) {

                if (deviceName.equals(dev.getDeviceName())) {
                    int index = Frame.deviceInfoList.indexOf(dev);
                    DeviceInfo redev = Frame.deviceInfoList.remove(0);
                    System.out.println("device removed " + redev.getDeviceName());
                    break;
                }

            }
            Frame.showDevicesList();
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            try {
                System.out.println("Service resolved: " + event.getInfo());
                
                String deviceName = event.getName();
                String ip = event.getInfo().getInetAddresses()[0].getHostAddress();
                int port = Integer.valueOf((event.getInfo().getPropertyString("port")));
                String devicePuplicKeyStr =  "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjaNrSrSWx7n85EMExv879KxurswGM5cShEy2npJUq6Pj0Wj4W3ddMhPk3Pb+wKIBsgrR9/AB3i0k8ZRCEL1V1go4CcDH/R2Iad96HsSp01YjzgoYys58xKg+OHX5Qm/L5LLnC0q8R95S1BgrAF7hCyn5ONSfwxRoLt4V0087uZKiq1xCx3aVSs39h5wsSdmg/asYOU9128ZsavHIrd2nCBqwn2KjGN37Ngj0WJLlo70qiJlhuh4xQ+hV4XbNFvknx1o0bgGh3l3rFICHJiLbR8+S5A23a/Rr41KP4xda+cDH513LmzICGUND74eRlMCeUEMqXsv1mp2LENx7271lFwIDAQAB";
                System.out.println("devicePuplicKeyStr "+devicePuplicKeyStr);
                
                PublicKey devicePublicKey = Frame.rsa.hostStrkey2PublicKey(devicePuplicKeyStr);
                
                System.out.println("devicePublicKey:  " + devicePublicKey);
                
                   DeviceInfo device = new DeviceInfo(deviceName, ip, port,devicePublicKey);
                  Frame.deviceInfoList.add(device);
                Frame.showDevicesList();
                
                if (!(instantName.equals(event.getName()))) {
                    
                }
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void amain() throws InterruptedException {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_aaya._tcp.local.", new SampleListener());

            // Wait a bit
            Thread.sleep(1);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
