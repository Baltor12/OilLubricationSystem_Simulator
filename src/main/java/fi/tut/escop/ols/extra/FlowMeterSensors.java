/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

import java.text.DecimalFormat;

/**
 * Class for representing the Sensors and the Variables available in the Flow
 * Meter class as JSON
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class FlowMeterSensors {

    String id;
    long simulationTime;
    double inFlowSensorValue;
    double outFlowSensorValue;
    boolean outFlowMaximum;
    boolean outFlowMinimum;

    public FlowMeterSensors() {
    }

    public FlowMeterSensors(String id, long simulationTime, double inFlowSensorValue, double outFlowSensorValue, boolean outFlowMaximum, boolean outFlowMinimum) {
        this.id = id;
        this.simulationTime = simulationTime;
        this.inFlowSensorValue = inFlowSensorValue - inFlowSensorValue % .01;
        this.outFlowSensorValue = outFlowSensorValue - outFlowSensorValue % .01;
        this.outFlowMaximum = outFlowMaximum;
        this.outFlowMinimum = outFlowMinimum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getInFlowSensorValue() {
        return inFlowSensorValue;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }

    public void setInFlowSensorValue(double inFlowSensorValue) {
        this.inFlowSensorValue = inFlowSensorValue;
    }

    public double getOutFlowSensorValue() {
        return outFlowSensorValue;
    }

    public void setOutFlowSensorValue(double outFlowSensorValue) {
        this.outFlowSensorValue = outFlowSensorValue;
    }

    public boolean getOutFlowMaximum() {
        return outFlowMaximum;
    }

    public void setOutFlowMaximum(boolean outFlowMaximum) {
        this.outFlowMaximum = outFlowMaximum;
    }

    public boolean getOutFlowMinimum() {
        return outFlowMinimum;
    }

    public void setOutFlowMinimum(boolean outFlowMinimum) {
        this.outFlowMinimum = outFlowMinimum;
    }
}
