/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson.tags;

/**
 * Class to generate the tag object for events and services
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ServiceTagsWithParent {
    String deviceId;
    String deviceType;
    String sensorType;
    String serviceType;
    String parentId;
    String parentType;

    public ServiceTagsWithParent() {
    }

    public ServiceTagsWithParent(String deviceId, String deviceType, String sensorType, String serviceType, String parentId, String parentType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.sensorType = sensorType;
        this.serviceType = serviceType;
        this.parentId = parentId;
        this.parentType = parentType;
    }    

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }    
    
}
