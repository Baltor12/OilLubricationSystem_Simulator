/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

/**
 * Class used in order to represent the sensor values in JSON
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class SensorValues {
    String type;
    Double value;
    String unit;
    Boolean maximumReached;
    Boolean minimumReached;

    public SensorValues(String type, Double value, String unit, Boolean maximumReached, Boolean minimumReached) {
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.maximumReached = maximumReached;
        this.minimumReached = minimumReached;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getMaximumReached() {
        return maximumReached;
    }

    public void setMaximumReached(Boolean maximumReached) {
        this.maximumReached = maximumReached;
    }

    public Boolean getMinimumReached() {
        return minimumReached;
    }

    public void setMinimumReached(Boolean minimumReached) {
        this.minimumReached = minimumReached;
    }
    
}
