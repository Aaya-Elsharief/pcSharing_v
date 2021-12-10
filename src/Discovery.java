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
import java.nio.charset.Charset;
import java.util.Map;

import javax.jmdns.ServiceInfo;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

public class Discovery {

    static JList list = new JList();
    static JPanel panel = new JPanel();

    public static void start() throws InterruptedException {
        String instantName = generateString();
        Listener app = new Listener(list, panel);
        app.setInstantName(instantName);
        app.start();
        Register reg = new Register();
        reg.setInstantName(instantName);
        reg.start();
    }

    Discovery(JList list, JPanel jPanel1) {
        this.list = list;
        this.panel = jPanel1;

    }

    public static String generateString() {
        int min = 0;
        int max = 4;
        String[] array = {"aswd", "hp", "apple", "qwer", "rand"}; 
        int rand = (int) (Math.random() * (max - min + 1) + min);
        return array[rand];

    }
}

class Register extends Thread {

    static String instantName;

    public static void setInstantName(String instantName) {
        Register.instantName = instantName;
    }
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

            // Register a service
            Map<String, String> m = new HashMap<String, String>();
            m.put("name", "Bateekh");
            m.put("date", "lala");
            //final String type, final String name, final int port, final String text
            //final String type, final String name, final int port, final int weight, final int priority, final Map<String, ?> props
            ServiceInfo serviceInfo = ServiceInfo.create("_aaya._tcp.local.", instantName, 1234, 0, 0, m);
            jmdns.registerService(serviceInfo);

            // Wait a bit
            Thread.sleep(25000);

            // Unregister all services
            jmdns.unregisterAllServices();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

class Listener extends Thread {

    static String instantName;
    static JList list = new JList();
    static JPanel panel = new JPanel();

    Listener(JList list, JPanel panel) {
        this.list = list;
        this.panel = panel;
    }

    public static void setInstantName(String instantName) {
        Listener.instantName = instantName;
    }

    
    public void run() {
        try {
            amain();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static class SampleListener implements ServiceListener {

        DefaultListModel DLM = new DefaultListModel();

        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());

        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());

            DLM.addElement("Hello");
            DLM.addElement("from");
            DLM.addElement("removed");

            list.setModel(DLM);

            panel.add(list);
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());

            System.out.println("event.getName() é_│" +event.getName());
            System.out.println("instantName ☺" +instantName);
            if(event.getName() != instantName){
                
                
           // DLM.addElement(list.getModel());
            DLM.addElement("name " +event.getName());
          //  DLM.addElement("from");
           // DLM.addElement("resolved");

            list.setBounds(187, 51, 179, 167);
            list.setAutoscrolls(true);
            list.setModel(DLM);
            panel.add(list);

        }
            System.out.println("♣");
    }
    }

    public static void amain() throws InterruptedException {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_aaya._tcp.local.", new SampleListener());

            // Wait a bit
            Thread.sleep(30000);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
