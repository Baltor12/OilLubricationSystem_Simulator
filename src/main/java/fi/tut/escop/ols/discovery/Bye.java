package fi.tut.escop.ols.discovery;

import fi.tut.escop.ols.extra.JsonConverters;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Class to Represent JSON of Bye Message and Multi cast it
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Bye {

    String id;
    Integer status;
    private static final Logger LOG = Logger.getLogger(Bye.class.getName());

    public Bye() {
    }

    public Bye(String id, Integer status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void multiCast(String group, Integer port) throws IOException {
        String message = "";
        JsonConverters jc = new JsonConverters();
        message = jc.toJson(this);
        DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(group), port);
        DatagramSocket serverSocket = new DatagramSocket(null);
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port + 11));
        LOG.log(Level.INFO, "\n\\--Multicasted a bye message "
                + "\n   +--Message: {0}"
                + "\n   +--Sender: {1}"
                + "\n   +--Socket: {2}"
                + "\n   \\--Group: {3}",
                new Object[]{message, serverSocket.getLocalAddress().toString(), port, group});
        serverSocket.send(dp);
        serverSocket.close();
    }
}
