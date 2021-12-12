/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
public class DeviceInfo {

    private String deviceName;
    private String ip;
    private int port;

    public DeviceInfo(String deviceName, String ip, int port) {
        this.deviceName = deviceName;
        this.ip = ip;
        this.port = port;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

}
