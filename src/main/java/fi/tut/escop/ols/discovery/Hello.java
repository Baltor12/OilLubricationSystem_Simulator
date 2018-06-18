/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.discovery;

import fi.tut.escop.ols.extra.JsonConverters;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to Multicast the swagger files to the RPL for saying hello
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Hello {
    private static final Logger LOG = Logger.getLogger(Hello.class.getName());
    public Hello() {
    }
    
    public void multiCast(String group, Integer port, RegisterationMessage reg) throws IOException {
        String message = "";
        JsonConverters jc = new JsonConverters();
        message = jc.toJson(reg);
        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(group), port);
        DatagramSocket serverSocket = new DatagramSocket(null);
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port + 11));
        LOG.log(Level.INFO, "\n\\--Multicasted hello message "
                + "\n   +--Message: {0}"
                + "\n   +--Sender: {1}"
                + "\n   +--Socket: {2}"
                + "\n   \\--Group: {3}",
                new Object[]{message, serverSocket.getLocalAddress().toString(), port, group});
        serverSocket.send(dp);
        serverSocket.close();
    }
    
}
