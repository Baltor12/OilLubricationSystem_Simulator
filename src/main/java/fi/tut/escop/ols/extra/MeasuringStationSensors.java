/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

import java.text.DecimalFormat;

/**
 * Class for representing the Sensors and the Variables available in the 
 * Measuring Station class as JSON 
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class MeasuringStationSensors {
    
    String id;
    long simulationTime;
    double inFlowSensorValue;    
    double outFlowSensorValue;
    boolean outFlowMaximum;
    boolean outFlowMinimum;
    double temperatureSensorValue;
    boolean temperatureMaximum;
    boolean temperatureMinimum;
    double pressureSensorValue;
    boolean pressureMaximum;
    boolean pressureMinimum;
    
    public MeasuringStationSensors() {
    }

    public MeasuringStationSensors(String id, long simulationTime, double inFlowSensorValue, double outFlowSensorValue, boolean outFlowMaximum, boolean outFlowMinimum, double temperatureSensorValue, boolean temperatureMaximum, boolean temperatureMinimum, double pressurreSensorValue, boolean pressureMaximum, boolean pressureMinimum) {
        this.id = id;
        this.simulationTime = simulationTime;
        this.inFlowSensorValue = inFlowSensorValue - inFlowSensorValue % .01;
        this.outFlowSensorValue = outFlowSensorValue - outFlowSensorValue % .01;
        this.outFlowMaximum = outFlowMaximum;
        this.outFlowMinimum = outFlowMinimum;
        this.temperatureSensorValue = temperatureSensorValue;
        this.temperatureMaximum = temperatureMaximum;
        this.temperatureMinimum = temperatureMinimum;
        this.pressureSensorValue = pressurreSensorValue;
        this.pressureMaximum = pressureMaximum;
        this.pressureMinimum = pressureMinimum;
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

    public double getTemperatureSensorValue() {
        return temperatureSensorValue;
    }

    public void setTemperatureSensorValue(double temperatureSensorValue) {
        this.temperatureSensorValue = temperatureSensorValue;
    }

    public boolean getTemperatureMaximum() {
        return temperatureMaximum;
    }

    public void setTemperatureMaximum(boolean temperatureMaximum) {
        this.temperatureMaximum = temperatureMaximum;
    }

    public boolean getTemperatureMinimum() {
        return temperatureMinimum;
    }

    public void setTemperatureMinimum(boolean temperatureMinimum) {
        this.temperatureMinimum = temperatureMinimum;
    }

    public double getPressureSensorValue() {
        return pressureSensorValue;
    }

    public void setPressureSensorValue(double pressureSensorValue) {
        this.pressureSensorValue = pressureSensorValue;
    }

    public boolean getPressureMaximum() {
        return pressureMaximum;
    }

    public void setPressureMaximum(boolean pressureMaximum) {
        this.pressureMaximum = pressureMaximum;
    }

    public boolean getPressureMinimum() {
        return pressureMinimum;
    }

    public void setPressureMinimum(boolean pressureMinimum) {
        this.pressureMinimum = pressureMinimum;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }
    
}
