/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.tut.escop.ols.controller.SimulatorRESTTemplate;
import fi.tut.escop.ols.rtuJson.EventPayload;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.rtuJson.Payload;
import fi.tut.escop.ols.rtuJson.ServicePayload;
import fi.tut.escop.ols.rtuJson.ServiceSubscriberInputs;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that generates the HMI element for each lubrication System
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class HMI {

    String id;
    String parentId;
    boolean isRunning;
    long simTime;
    boolean filterChange = false;
    boolean oilChange = false;
    boolean maintenance = false;
    ArrayList<String> messages = new ArrayList<>();

    @JsonIgnore
    boolean oilChanged = false;
    @JsonIgnore
    boolean filterChanged = false;
    @JsonIgnore
    boolean maintenanceDone = false;
    @JsonIgnore
    boolean lastEmittedOilChanged = false;
    @JsonIgnore
    boolean lastEmittedFilterChanged = false;
    @JsonIgnore
    boolean lastEmittedMaintenanceDone = false;
    @JsonIgnore
    String startUrl = "";
    @JsonIgnore
    String message = "";
    @JsonIgnore
    String stopUrl = "";
    @JsonIgnore
    String filterChangeUrl = "";
    @JsonIgnore
    String oilChangeUrl = "";
    @JsonIgnore
    String maintenanceUrl = "";
    @JsonIgnore
    String messageUrl = "";

    public HMI() {
    }

    public HMI(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isFilterChange() {
        return filterChange;
    }

    public void setFilterChange(boolean filterChange) {
        for (String subKey : Registry.serviceSubscribers.keySet()) {
            ServiceSubscriberInputs serSubscribers = Registry.serviceSubscribers.get(subKey);
            if (serSubscribers.getServiceId().equals("filterChange")) {
                EventPayload eventPayload = new EventPayload();
                eventPayload.setClientData(serSubscribers.getClientData());
                eventPayload.setSenderID(this.id);
                eventPayload.setLastEmit(this.simTime);
                eventPayload.setId(serSubscribers.getServiceId());
                ServicePayload payload = new ServicePayload(Integer.toString(Registry.filterChange.get(id).getCount()), Long.toString(Registry.filterChange.get(id).getLastRun()));
                eventPayload.setPayload(payload);
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                restTemplate.sensorValuePOST(eventPayload, serSubscribers.getDestUrl());
            }
        }
        this.filterChange = filterChange;
    }

    public boolean isOilChange() {
        return oilChange;
    }

    public void setOilChange(boolean oilChange) {
        for (String subKey : Registry.serviceSubscribers.keySet()) {
            ServiceSubscriberInputs serSubscribers = Registry.serviceSubscribers.get(subKey);
            if (serSubscribers.getServiceId().equals("oilChange")) {
                EventPayload eventPayload = new EventPayload();
                eventPayload.setClientData(serSubscribers.getClientData());
                eventPayload.setSenderID(this.id);
                eventPayload.setLastEmit(this.simTime);
                eventPayload.setId(serSubscribers.getServiceId());
                ServicePayload payload = new ServicePayload(Integer.toString(Registry.oilChange.get(id).getCount()), Long.toString(Registry.oilChange.get(id).getLastRun()));
                eventPayload.setPayload(payload);
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                restTemplate.sensorValuePOST(eventPayload, serSubscribers.getDestUrl());
            }
        }
        this.oilChange = oilChange;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getStopUrl() {
        return stopUrl;
    }

    public void setStopUrl(String stopUrl) {
        this.stopUrl = stopUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        for (String subKey : Registry.serviceSubscribers.keySet()) {
            ServiceSubscriberInputs serSubscribers = Registry.serviceSubscribers.get(subKey);
            if (serSubscribers.getServiceId().equals("message")) {
                EventPayload eventPayload = new EventPayload();
                eventPayload.setClientData(serSubscribers.getClientData());
                eventPayload.setSenderID(this.id);
                eventPayload.setLastEmit(this.simTime);
                eventPayload.setId(serSubscribers.getServiceId());
                ServicePayload payload = new ServicePayload(Integer.toString(Registry.message.get(id).getCount()), Long.toString(Registry.message.get(id).getLastRun()));
                eventPayload.setPayload(payload);
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                restTemplate.sensorValuePOST(eventPayload, serSubscribers.getDestUrl());
            }
        }
        HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(Registry.eventSubscribers);
        for (EventSubscriberInputs subscriber : subscriberList.values()) {
            EventPayload eventPayload = new EventPayload();
            eventPayload.setClientData(subscriber.getClientData());
            eventPayload.setSenderID(this.id);
            eventPayload.setLastEmit(this.simTime);
            eventPayload.setId(subscriber.getEventId());
            SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
            Payload payload = null;
            if (subscriber.getSenType().equals("report")) {
                payload = new Payload(message, "string", "");
                eventPayload.setPayload(payload);
                System.out.println("url: " + subscriber.getDestUrl() + " || clientData: " + subscriber.getClientData());
                System.out.println("payload: " + payload.getValue());
                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                break;
            }
        }
        String time = new SimpleDateFormat("HH:mm:ss").format(this.simTime);
        if (this.messages.size() < 10) {
            if (!this.messages.contains((time + " : " + this.message))) {
                this.messages.add(time + " : " + this.message);
            }
        } else {
            this.messages.remove(0);
            if (!this.messages.contains((time + " : " + this.message))) {
                this.messages.add(time + " : " + this.message);
            }
        }
    }

    public String getFilterChangeUrl() {
        return filterChangeUrl;
    }

    public void setFilterChangeUrl(String filterChangeUrl) {
        this.filterChangeUrl = filterChangeUrl;
    }

    public String getOilChangeUrl() {
        return oilChangeUrl;
    }

    public void setOilChangeUrl(String oilChangeUrl) {
        this.oilChangeUrl = oilChangeUrl;
    }

    public String getMaintenanceUrl() {
        return maintenanceUrl;
    }

    public void setMaintenanceUrl(String maintenanceUrl) {
        this.maintenanceUrl = maintenanceUrl;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public long getSimTime() {
        return simTime;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public boolean isOilChanged() {
        return oilChanged;
    }

    public void setOilChanged(boolean oilChanged) {
        this.oilChanged = oilChanged;
    }

    public boolean isFilterChanged() {
        return filterChanged;
    }

    public void setFilterChanged(boolean filterChanged) {
        this.filterChanged = filterChanged;
    }

    public boolean isMaintenanceDone() {
        return maintenanceDone;
    }

    public void setMaintenanceDone(boolean maintenanceDone) {
        this.maintenanceDone = maintenanceDone;
    }

    public boolean isLastEmittedOilChanged() {
        return lastEmittedOilChanged;
    }

    public void setLastEmittedOilChanged(boolean lastEmittedOilChanged) {
        this.lastEmittedOilChanged = lastEmittedOilChanged;
    }

    public boolean isLastEmittedFilterChanged() {
        return lastEmittedFilterChanged;
    }

    public void setLastEmittedFilterChanged(boolean lastEmittedFilterChanged) {
        this.lastEmittedFilterChanged = lastEmittedFilterChanged;
    }

    public boolean isLastEmittedMaintenanceDone() {
        return lastEmittedMaintenanceDone;
    }

    public void setLastEmittedMaintenanceDone(boolean lastEmittedMaintenanceDone) {
        this.lastEmittedMaintenanceDone = lastEmittedMaintenanceDone;
    }

    //Registers the HMI in the registry
    public void reg() {
        Registry.hmi.put(this.id, this);
    }

}
