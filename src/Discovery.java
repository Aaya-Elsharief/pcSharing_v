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
import java.util.Map;

import javax.jmdns.ServiceInfo;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.*;

public class Discovery{
    public static void start() throws InterruptedException {
        Listener app = new Listener();
        app.start();
        Register reg = new Register();
        reg.start();
    }
}

class Register extends Thread{

    public void run(){
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
            Map <String, String> m = new HashMap<String, String>();
            m.put("name", "Bateekh");
            m.put("date", "lala");
            //final String type, final String name, final int port, final String text
            //final String type, final String name, final int port, final int weight, final int priority, final Map<String, ?> props
            ServiceInfo serviceInfo = ServiceInfo.create("_aaya._tcp.local.", "example", 1234,0,0, m);
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

class Listener extends Thread{
    public void run(){
        try {
            amain();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
 
    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
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