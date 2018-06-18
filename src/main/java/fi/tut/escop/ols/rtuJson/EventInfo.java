/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson;

/**
 * Class to represent the event payload in JSON format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class EventInfo {
    String id;
    String senderID;
    Object payload;
    Object meta; 

    public EventInfo() {
    }

    public EventInfo(String id, String senderID, Object payload, Object meta) {
        this.id = id;
        this.senderID = senderID;
        this.payload = payload;
        this.meta = meta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }
    
    
}
