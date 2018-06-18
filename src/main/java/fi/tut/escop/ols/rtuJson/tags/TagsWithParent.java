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
public class TagsWithParent {
    String deviceId;
    String deviceType;
    String sensorType;
    String parentId;
    String parentType;
    String min;
    String max;
    String nom;
    String high;
    String low;
    String units;

    public TagsWithParent() {
    }

    public TagsWithParent(String deviceId, String deviceType, String sensorType, String parentId, String parentType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.sensorType = sensorType;
        this.parentId = parentId;
        this.parentType = parentType;
    }

    public TagsWithParent(String deviceId, String deviceType, String sensorType, String parentId, String parentType, String min, String max, String nom, String high, String low, String units) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.sensorType = sensorType;
        this.parentId = parentId;
        this.parentType = parentType;
        this.min = min;
        this.max = max;
        this.nom = nom;
        this.high = high;
        this.low = low;
        this.units = units;
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

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
    
    
}
