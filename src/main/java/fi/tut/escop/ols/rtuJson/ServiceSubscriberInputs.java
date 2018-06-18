/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.tut.escop.ols.elements.Registry;
import java.util.HashMap;

/**
 * Class used during the post to have the subscriber details registered
 *
 * @author Balaji Gopalakrishnan
 */
public class ServiceSubscriberInputs {
    String id;
    HashMap<String,String> links = new HashMap<>();
    
    @JsonProperty("class")
    String group;
    
    String serviceId;
    String destUrl;
    String clientData;
    
    @JsonIgnore
    String componentId;
    @JsonIgnore
    String senType;

    public ServiceSubscriberInputs() {
    }

    public ServiceSubscriberInputs(String id, HashMap<String,String> links, String group, String eventId, String destUrl, String clientData, String componentId, String senType) {
        this.id = id;
        this.links = links;
        this.group = group;
        this.serviceId = eventId;
        this.destUrl = destUrl;
        this.clientData = clientData;
        this.componentId = componentId;
        this.senType = senType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(HashMap<String,String> links) {
        this.links = links;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public void setDestUrl(String destUrl) {
        this.destUrl = destUrl;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getSenType() {
        return senType;
    }

    public void setSenType(String senType) {
        this.senType = senType;
    }
    
    public void reg(){
        Registry.serviceSubscribers.put(this.id, this);
    }
    
    
}
