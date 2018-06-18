/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

/**
 * Class for representing the Sensors and the Variables available in the
 * Lubrication Unit class as JSON
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class LubricationUnitSensors {

    String id;
    boolean isSimulated;
    boolean isRunning;
    long simulationTime;
    double inFlowSensorValue;
    double outFlowSensorValue;
    boolean outFlowMaximum;
    boolean outFlowMinimum;
    double levelSensorValue;
    boolean levelMaximum;
    boolean levelMinimum;
    double temperatureSensorValue;
    boolean temperatureMaximum;
    boolean temperatureMinimum;
    double waterContentSensorValue;
    String particleCount;
    double filterCapacity;
    boolean filterClog;

    public LubricationUnitSensors() {
    }

    public LubricationUnitSensors(String id, boolean isSimulated, boolean isRunning, long simulationTime, double inFlowSensorValue, double outFlowSensorValue, boolean outFlowMaximum, boolean outFlowMinimum, double levelSensorValue, boolean levelMaximum, boolean levelMinimum, double temperatureSensorValue, boolean temperatureMaximum, boolean temperatureMinimum, double waterContentSensorValue, String particleCount, double filterCapacity, boolean filterClog) {
        this.id = id;
        this.isSimulated = isSimulated;
        this.isRunning = isRunning;
        this.simulationTime = simulationTime;
        this.inFlowSensorValue = inFlowSensorValue;
        this.outFlowSensorValue = outFlowSensorValue;
        this.outFlowMaximum = outFlowMaximum;
        this.outFlowMinimum = outFlowMinimum;
        this.levelSensorValue = levelSensorValue;
        this.levelMaximum = levelMaximum;
        this.levelMinimum = levelMinimum;
        this.temperatureSensorValue = temperatureSensorValue;
        this.temperatureMaximum = temperatureMaximum;
        this.temperatureMinimum = temperatureMinimum;
        this.waterContentSensorValue = waterContentSensorValue;
        this.particleCount = particleCount;
        this.filterCapacity = filterCapacity;
        this.filterClog = filterClog;
    }
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsSimulated() {
        return isSimulated;
    }

    public void setIsSimulated(boolean isSimulated) {
        this.isSimulated = isSimulated;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }

    public String getParticleCount() {
        return particleCount;
    }

    public void setParticleCount(String particleCount) {
        this.particleCount = particleCount;
    }

    public double getFilterCapacity() {
        return filterCapacity;
    }

    public void setFilterCapacity(double filterCapacity) {
        this.filterCapacity = filterCapacity;
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

    public double getWaterContentSensorValue() {
        return waterContentSensorValue;
    }

    public void setWaterContentSensorValue(double waterContentSensorValue) {
        this.waterContentSensorValue = waterContentSensorValue;
    }

    public double getLevelSensorValue() {
        return levelSensorValue;
    }

    public void setLevelSensorValue(double levelSensorValue) {
        this.levelSensorValue = levelSensorValue;
    }

    public boolean getLevelMaximum() {
        return levelMaximum;
    }

    public void setLevelMaximum(boolean levelMaximum) {
        this.levelMaximum = levelMaximum;
    }

    public boolean getLevelMinimum() {
        return levelMinimum;
    }

    public void setLevelMinimum(boolean levelMinimum) {
        this.levelMinimum = levelMinimum;
    }

    public boolean getFilterClog() {
        return filterClog;
    }

    public void setFilterClog(boolean filterClog) {
        this.filterClog = filterClog;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
