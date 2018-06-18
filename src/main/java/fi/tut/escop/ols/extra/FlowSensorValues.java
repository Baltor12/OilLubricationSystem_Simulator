/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

import java.util.logging.Logger;

/**
 * Class used in order to represent the sensor values in JSON
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class FlowSensorValues {
    String type;
    Double inFlow;
    Double outFlow;
    String unit;
    Boolean outFlowMaximumReached;
    Boolean outFlowMinimumReached;

    public FlowSensorValues(String type, Double inFlow, Double outFlow, String unit, Boolean outFlowMaximumReached, Boolean outFlowMinimumReached) {
        this.type = type;
        this.inFlow = inFlow;
        this.outFlow = outFlow;
        this.unit = unit;
        this.outFlowMaximumReached = outFlowMaximumReached;
        this.outFlowMinimumReached = outFlowMinimumReached;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getInFlow() {
        return inFlow;
    }

    public void setInFlow(Double inFlow) {
        this.inFlow = inFlow;
    }

    public Double getOutFlow() {
        return outFlow;
    }

    public void setOutFlow(Double outFlow) {
        this.outFlow = outFlow;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getOutFlowMaximumReached() {
        return outFlowMaximumReached;
    }

    public void setOutFlowMaximumReached(Boolean outFlowMaximumReached) {
        this.outFlowMaximumReached = outFlowMaximumReached;
    }

    public Boolean getOutFlowMinimumReached() {
        return outFlowMinimumReached;
    }

    public void setOutFlowMinimumReached(Boolean outFlowMinimumReached) {
        this.outFlowMinimumReached = outFlowMinimumReached;
    }
    
}
