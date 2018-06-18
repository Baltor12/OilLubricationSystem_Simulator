/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.controller.SimulatorRESTTemplate;
import fi.tut.escop.ols.jsonLinks.Links;
import fi.tut.escop.ols.jsonLinks.SensorLinks;
import fi.tut.escop.ols.ontology.OntologyManager;
import fi.tut.escop.ols.rtuJson.EventPayload;
import fi.tut.escop.ols.rtuJson.Payload;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.simulation.SimMath;
import fi.tut.escop.ols.simulation.SimValue;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class MeasuringStation implements Runnable {

    //------------------------------Declarations--------------------------------
    String id = "";
    boolean isSimulated = false;
    String parentId = "";
    Double oilAllocation = 0.0;
    Double oilRemaining = 0.0;
    String flowUnit = "l/min";
    long simTime = 1;
    HashMap<String, SensorLinks> sensors = new HashMap<>();
    HashMap<String, Links> flowMeters = new HashMap<>();
    Integer noFlowMeters = 0;
    
    @JsonIgnore
    Double inFlow = 0.0;
    @JsonIgnore
    Double outFlow = 0.0;
    @JsonIgnore
    boolean outFlowMaximum = false;
    @JsonIgnore
    boolean outFlowMinimum = false;
    @JsonIgnore
    Double temperature = 0.0;
    @JsonIgnore
    String temperatureUnit = "°C";
    @JsonIgnore
    boolean temperatureMinimum = false;
    @JsonIgnore
    boolean temperatureMaximum = false;
    @JsonIgnore
    Double pressure = 0.0;
    @JsonIgnore
    String pressureUnit = "bar";
    @JsonIgnore
    boolean pressureMinimum = false;
    @JsonIgnore
    boolean pressureMaximum = false;
    @JsonIgnore
    Double lastEmittedInFlow = 0.0;
    @JsonIgnore
    Double lastEmittedOutFlow = 0.0;
    @JsonIgnore
    String lastEmittedOutFlowState = "";
    @JsonIgnore
    Double lastEmittedTemperature = 0.0;
    @JsonIgnore
    String lastEmittedTemperatureState = "";
    @JsonIgnore
    Double lastEmittedPressure = 0.0;
    @JsonIgnore
    String lastEmittedPressureState = "";
    @JsonIgnore
    long lastEmittedOutFlowTimeStamp = 0;
    @JsonIgnore
    long lastEmittedInFlowTimeStamp = 0;
    @JsonIgnore
    long lastEmittedPressureTimeStamp = 0;
    @JsonIgnore
    long lastEmittedTemperatureTimeStamp = 0;
    @JsonIgnore
    int simulationMultiplier = 1;
    @JsonIgnore
    HashMap<String, FlowMeter> fmList = new HashMap<>();
    @JsonIgnore
    int incIdFM = 0;
    @JsonIgnore
    static final String FM_ID_PREFIX = "_FM_";
    @JsonIgnore
    private Thread thread;
    @JsonIgnore
    String self = "";
    @JsonIgnore
    String parent = "";
    @JsonIgnore
    String info = "";
    @JsonIgnore
    String parentType = "";
    @JsonIgnore
    boolean isLeaking = false;
    @JsonIgnore
    double leakageLevel = 0.0;
    @JsonIgnore
    boolean pipeLeak = false;
    @JsonIgnore
    boolean measuringStationLeak = false;
    @JsonIgnore
    boolean measuringStationPipeLeak = false;
    @JsonIgnore
    String parentOilType = "";
    @JsonIgnore
    int stepForwardMultiplier = 0;
    @JsonIgnore
    public SimValue.Mode mode = SimValue.Mode.SV_MODE_RUNNING;
    @JsonIgnore
    String myUrl = HostPortandConfig.ROOT_URL;
    @JsonIgnore
    private static final Logger LOG = Logger.getLogger(MeasuringStation.class.getName());
    @JsonIgnore
    int eventNotificationCounter = 0;
    @JsonIgnore
    boolean eventNotifiy = false;

    //------------------------------Constructors--------------------------------
    public MeasuringStation() {
    }

    public MeasuringStation(String id) {
        this.id = id;
    }

    public MeasuringStation(String id, HashMap<String, FlowMeter> fmList) {
        this.id = id;
        this.fmList = fmList;
        this.noFlowMeters = fmList.size();
    }

    public HashMap<String, FlowMeter> getFmList() {
        return fmList;
    }

    public void setFmList(HashMap<String, FlowMeter> fmList) {
        this.fmList = fmList;
        this.noFlowMeters = fmList.size();
    }

    public Integer getNoFlowMeters() {
        return fmList.size();
    }

    public void setNoFlowMeters() {
        this.noFlowMeters = fmList.size();
    }

    public void updateNoFlowMeters() {
        this.noFlowMeters = fmList.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsSimulated() {
        return isSimulated;
    }

    public void setIsSimulated(boolean isSimulated) {
        this.isSimulated = isSimulated;
    }

    public Double getOilAllocation() {
        return oilAllocation;
    }

    public void setOilAllocation(Double oilAllocation) {
        this.oilAllocation = oilAllocation;
    }

    public Double getOilRemaining() {
        return oilRemaining;
    }

    public void setOilRemaining(Double oilRemaining) {
        this.oilRemaining = oilRemaining;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public boolean getTemperatureMinimum() {
        return temperatureMinimum;
    }

    public void setTemperatureMinimum(boolean temperatureMinimum) {
        this.temperatureMinimum = temperatureMinimum;
    }

    public boolean getTemperatureMaximum() {
        return temperatureMaximum;
    }

    public void setTemperatureMaximum(boolean temperatureMaximum) {
        this.temperatureMaximum = temperatureMaximum;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public boolean getPressureMinimum() {
        return pressureMinimum;
    }

    public void setPressureMinimum(boolean pressureMinimum) {
        this.pressureMinimum = pressureMinimum;
    }

    public boolean getPressureMaximum() {
        return pressureMaximum;
    }

    public void setPressureMaximum(boolean pressureMaximum) {
        this.pressureMaximum = pressureMaximum;
    }

    public Double getLastEmittedInFlow() {
        return lastEmittedInFlow;
    }

    public void setLastEmittedInFlow(Double lastEmittedInFlow) {
        this.lastEmittedInFlow = lastEmittedInFlow;
    }

    public Double getLastEmittedOutFlow() {
        return lastEmittedOutFlow;
    }

    public void setLastEmittedOutFlow(Double lastEmittedOutFlow) {
        this.lastEmittedOutFlow = lastEmittedOutFlow;
    }

    public String getLastEmittedOutFlowState() {
        return lastEmittedOutFlowState;
    }

    public void setLastEmittedOutFlowState(String lastEmittedOutFlowState) {
        this.lastEmittedOutFlowState = lastEmittedOutFlowState;
    }

    public Double getLastEmittedTemperature() {
        return lastEmittedTemperature;
    }

    public void setLastEmittedTemperature(Double lastEmittedTemperature) {
        this.lastEmittedTemperature = lastEmittedTemperature;
    }

    public String getLastEmittedTemperatureState() {
        return lastEmittedTemperatureState;
    }

    public void setLastEmittedTemperatureState(String lastEmittedTemperatureState) {
        this.lastEmittedTemperatureState = lastEmittedTemperatureState;
    }

    public Double getLastEmittedPressure() {
        return lastEmittedPressure;
    }

    public void setLastEmittedPressure(Double lastEmittedPressure) {
        this.lastEmittedPressure = lastEmittedPressure;
    }

    public String getLastEmittedPressureState() {
        return lastEmittedPressureState;
    }

    public void setLastEmittedPressureState(String lastEmittedPressureState) {
        this.lastEmittedPressureState = lastEmittedPressureState;
    }

    public long getLastEmittedOutFlowTimeStamp() {
        return lastEmittedOutFlowTimeStamp;
    }

    public void setLastEmittedOutFlowTimeStamp(long lastEmittedOutFlowTimeStamp) {
        this.lastEmittedOutFlowTimeStamp = lastEmittedOutFlowTimeStamp;
    }

    public long getLastEmittedInFlowTimeStamp() {
        return lastEmittedInFlowTimeStamp;
    }

    public void setLastEmittedInFlowTimeStamp(long lastEmittedInFlowTimeStamp) {
        this.lastEmittedInFlowTimeStamp = lastEmittedInFlowTimeStamp;
    }

    public long getLastEmittedPressureTimeStamp() {
        return lastEmittedPressureTimeStamp;
    }

    public void setLastEmittedPressureTimeStamp(long lastEmittedPressureTimeStamp) {
        this.lastEmittedPressureTimeStamp = lastEmittedPressureTimeStamp;
    }

    public long getLastEmittedTemperatureTimeStamp() {
        return lastEmittedTemperatureTimeStamp;
    }

    public void setLastEmittedTemperatureTimeStamp(long lastEmittedTemperatureTimeStamp) {
        this.lastEmittedTemperatureTimeStamp = lastEmittedTemperatureTimeStamp;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getParentOilType() {
        return parentOilType;
    }

    public void setParentOilType(String parentOilType) {
        this.parentOilType = parentOilType;
    }

    public SimValue.Mode getMode() {
        return mode;
    }

    public void setMode(SimValue.Mode mode) {
        this.mode = mode;
        if (this.mode == SimValue.Mode.SV_MODE_RESET) {
            this.inFlow = 0.0;
            this.outFlow = 0.0;
            this.temperature = 0.0;
            this.pressure = 0.0;
        }
    }

    public HashMap<String, Links> getFlowMeters() {
        return flowMeters;
    }

    public void setFlowMeters(HashMap<String, Links> flowMeters) {
        this.flowMeters = flowMeters;
    }

    public HashMap<String, SensorLinks> getSensors() {
        return sensors;
    }

    public void setSensors(HashMap<String, SensorLinks> sensors) {
        this.sensors = sensors;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getMyUrl() {
        return myUrl;
    }

    public void setMyUrl(String myUrl) {
        this.myUrl = myUrl;
    }

    public boolean getIsLeaking() {
        return isLeaking;
    }

    public void setIsLeaking(boolean isLeaking) {
        this.isLeaking = isLeaking;
        this.measuringStationLeak = this.isLeaking;
        this.measuringStationPipeLeak = this.isLeaking;
    }

    public boolean getPipeLeak() {
        return pipeLeak;
    }

    public void setPipeLeak(boolean pipeLeak) {
        this.pipeLeak = pipeLeak;
    }

    public boolean getMeasuringStationLeak() {
        return measuringStationLeak;
    }

    public void setMeasuringStationLeak(boolean measuringStationLeak) {
        this.measuringStationLeak = measuringStationLeak;
    }

    public boolean getMeasuringStationPipeLeak() {
        return measuringStationPipeLeak;
    }

    public void setMeasuringStationPipeLeak(boolean measuringStationPipeLeak) {
        this.measuringStationPipeLeak = measuringStationPipeLeak;

        if (this.measuringStationPipeLeak) {
            //randomly selecting Flow Meters to set the pipe message
            for (String fmKey : this.fmList.keySet()) {
                FlowMeter fm = this.fmList.get(fmKey);
                if (Math.random() < 0.5) {
                    fm.setPipeLeak(this.measuringStationPipeLeak);
                }
            }
        } else {
            //stop pipe leak of all Flow Meters
            for (String fmKey : this.fmList.keySet()) {
                FlowMeter fm = this.fmList.get(fmKey);
                fm.setPipeLeak(this.measuringStationPipeLeak);
            }
        }
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getPressureUnit() {
        return pressureUnit;
    }

    public void setPressureUnit(String pressureUnit) {
        this.pressureUnit = pressureUnit;
    }

    public String getFlowUnit() {
        return flowUnit;
    }

    public void setFlowUnit(String flowUnit) {
        this.flowUnit = flowUnit;
    }

    public int getSimulationMultiplier() {
        return simulationMultiplier;
    }

    public void setSimulationMultiplier(int simulationMultiplier) {
        this.simulationMultiplier = simulationMultiplier;
    }

    public int getStepForwardMultiplier() {
        return stepForwardMultiplier;
    }

    public void setStepForwardMultiplier(int stepForwardMultiplier) {
        this.stepForwardMultiplier = stepForwardMultiplier;
        for (String fmKey : fmList.keySet()) {
            FlowMeter fm = fmList.get(fmKey);
            fm.setStepForwardMultiplier(stepForwardMultiplier);
        }
    }

    public long getSimTime() {
        return simTime;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }

    /**
     * Method to store the registered values in Registry Class
     */
    public void reg() {
        Registry.measuringStation.put(this.id, this);
    }

    /**
     * Method to analyze the outFlow in this Measuring Station
     *
     * @return
     */
    public Double measuringStationAnalyze() {
        this.oilAllocation = SimMath.msAnalyze(this.fmList);
        return this.oilAllocation;
    }

    /**
     * Function to Add single Flow Meter to the specified Measuring Station
     *
     * @param fmId
     * @return
     */
    public boolean addChild(String fmId) {
        boolean response;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            FlowMeter fm = Registry.flowMeter.get(fmId);
            if (fm.getIsSimulated()) {
                fm.interrupt();
            }
            if (fmId.substring(0, 2).equals("FM")) {
                this.fmList.put(this.generateFMId(), fm);
                LOG.log(Level.INFO, "Added Flow Meter {0}", fm.getId());
                Registry.flowMeter.remove(fmId);
            } else {
                this.fmList.put(fmId, fm);
            }
            if (!this.isSimulated) {
                this.start();
            }
            response = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, id);
            response = false;
        }
        return response;
    }

    /**
     * Function to Add the unallocated Flow Meter to the specified Measuring
     * Station
     *
     * @param fmIds
     * @return
     */
    public boolean addChildren(ArrayList<String> fmIds) {
        boolean response;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            for (String fmId : fmIds) {
                FlowMeter fm = Registry.flowMeter.get(fmId);
                if (fm.getIsSimulated()) {
                    fm.interrupt();
                }
                if (fmId.substring(0, 2).equals("FM")) {
                    this.fmList.put(this.generateFMId(), fm);
                    LOG.log(Level.INFO, "Added Flow Meter {0}", fm.getId());
                    Registry.flowMeter.remove(fmId);
                } else {
                    this.fmList.put(fmId, fm);
                }
            }
            this.start();
            response = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, id);
            response = false;
        }
        return response;
    }

    /**
     * Delete particular child Flow Meter
     *
     * @param fmId
     * @return
     */
    public boolean deleteChild(String fmId) {
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            FlowMeter fm = fmList.get(fmId);
            if (fm.getIsSimulated()) {
                fm.interrupt();
            }
            //Remove FM Link Details in current MS and Registry of FM
            flowMeters.remove(fmId);
            Registry.linkFM.remove(fmId);
            LOG.log(Level.INFO, "Deleted Flow Meter {0}", fm.getId());
            //Remove FM Details in current MS and Registry of FM
            fmList.remove(fmId);
            Registry.flowMeter.remove(fmId);
            setNoFlowMeters();
            this.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete all child Flow Meter
     *
     * @return
     */
    public boolean deleteChildren() {
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            for (String fmKey : fmList.keySet()) {
                FlowMeter fm = fmList.get(fmKey);
                if (fm.getIsSimulated()) {
                    fm.interrupt();
                }
                OntologyManager.deleteFlowMeter(fm);
                //Remove FM Link Details in current MS and Registry of FM
                flowMeters.remove(fmKey);
                Registry.linkFM.remove(fmKey);
                LOG.log(Level.INFO, "Deleted Flow Meter {0}", fm.getId());
                //Remove FM Details in current MS and Registry of FM
                Registry.flowMeter.remove(fmKey);
            }
            fmList.clear();
            this.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Function to save the Measuring Station to Ontology
     *
     * @param luId
     * @return
     */
    public boolean saveMeasuringStation() {
        boolean reply = true;
        try {
            OntologyManager.insertMeasuringStation(this);
            OntologyManager.insertMeasuringStationIntoLubricationUnit(this.parentId, this.id);
            reply = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, this);
            reply = false;
        }
        return reply;
    }

    /**
     * Function to Interrupt Children which are running
     *
     * @return
     */
    public boolean interruptChildren() {
        boolean result = false;
        for (String fmKey : fmList.keySet()) {
            FlowMeter fm = null;
            try {
                if ((fm = fmList.get(fmKey)) != null) {
                    if (fm.getIsSimulated()) {
                        fm.interrupt();
                    }
                }
                fm.reg();
                result = true;
            } catch (IllegalThreadStateException ex) {
                result = false;
                Logger.getLogger(LubricationSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    /**
     * Commands to be start while executing each Individual Measuring Station
     * thread
     */
    @Override
    public void run() {
        LOG.log(Level.INFO, "Starting Measurement station {0}", id);
        while (!Thread.interrupted()) {
            if (!this.parentId.equals("")) {
                monitorMS();
            }
            try {
                this.isSimulated = true;
                this.reg();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
        LOG.log(Level.INFO, "Stopping Measurement station {0}", id);
        isSimulated = false;
    }

    /**
     * Set Label, Minimum outFlow, Maximum outFlow, Nominal outFlow and register
     * each -Measuring Stations which is to be started.
     *
     */
    public void setFMValues() {
        for (String fmKey : fmList.keySet()) {
            FlowMeter fm = fmList.get(fmKey);
            fm.setId(fmKey);
            fm.setParentId(this.id);
            fm.setParent(this.self);
            fm.setSelf(this.self + "/fm/" + fm.getId());
            fm.setInfo(this.self + "/fm/" + fm.getId() + "/info");
            fm.setSimTime(this.simTime);
            fm.setSimulationMultiplier(this.simulationMultiplier);
            fm.reg();
            Links fmLink;
            fmLink = new Links(fm.getId(), fm.getSelf(), fm.getParent(), fm.getInfo());
            fmLink.regFM();
            this.flowMeters.put(fm.getId(), fmLink);
        }
        this.noFlowMeters = fmList.size();
    }

    /**
     * Method to Generate the IDs for Lubrication Units under this Flow Meter
     *
     * @return
     */
    public String generateFMId() {
        return id + FM_ID_PREFIX + (incIdFM++);
    }

    /**
     * Function to Generate ID for the removed Flow Meter
     *
     * @return
     */
    public String generateRemoveFMId() {
        String fmId = "";
        int noFM = 0;
        for (String unFMKey : Registry.flowMeter.keySet()) {
            if (Registry.flowMeter.get(unFMKey).getId().substring(0, 2).equals("FM")) {
                noFM = noFM + 1;
            }
        }
        noFM = noFM + 1;
        fmId = "FM_" + noFM;
        return fmId;
    }

    /**
     * Function to generate Values while monitoring in the Simulator
     */
    public void monitorMS() {
        if (this.mode.equals(SimValue.Mode.SV_MODE_RUNNING)) {
            if (this.eventNotificationCounter >= 5) {
                this.eventNotifiy = true;
                this.eventNotificationCounter = 0;
            } else {
                this.eventNotifiy = false;
                this.eventNotificationCounter++;
            }

            if (this.measuringStationLeak) {
                SimValue leak = new SimValue();
                this.leakageLevel = leak.randomLeakageAmount();
            } else {
                this.leakageLevel = 0.0;
            }
            SimValue simFlow = new SimValue(SimValue.SensorType.senMSFlow, this.mode);
            if (this.pipeLeak) {
                this.inFlow = simFlow.measuringStationInFlow(this.parentId, this.oilAllocation, this.leakageLevel);
            } else {
                this.inFlow = simFlow.measuringStationInFlow(this.parentId, this.oilAllocation, 0.0);
            }

            if (this.measuringStationLeak) {
                this.outFlow = simFlow.measuringStationOutFlow(this.inFlow, this.leakageLevel);
            } else {
                this.outFlow = simFlow.measuringStationOutFlow(this.inFlow, 0.0);
            }
            this.outFlowMaximum = simFlow.isSenMSFlow_Max_Alarm();
            this.outFlowMinimum = simFlow.isSenMSFlow_Min_Alarm();

            SimValue simTemp = new SimValue(SimValue.SensorType.senMSTemp, this.mode);
            this.temperature = (double) Math.round((simTemp.eval()) * 100) / 100;
            this.temperatureMaximum = simTemp.isSenMSTemp_Max_Alarm();
            this.temperatureMinimum = simTemp.isSenMSTemp_Min_Alarm();

            //Calculate the timing
            this.simTime += 60000 * (this.simulationMultiplier + this.stepForwardMultiplier);

            SimValue simPres = new SimValue(SimValue.SensorType.senMSPressure, this.mode);
            this.pressure = (double) Math.round((simPres.eval()) * 100) / 100;
            this.pressureMaximum = simPres.isSenMSPressure_Max_Alarm();
            this.pressureMinimum = simPres.isSenMSPressure_Min_Alarm();
            postSubscriber();
        } else if (this.mode.equals(SimValue.Mode.SV_MODE_SHUTDOWN)) {
            this.inFlow = 0.0;
            this.outFlow = 0.0;
            this.temperature = 0.0;
            this.pressure = 0.0;
            this.outFlowMaximum = false;
            this.outFlowMinimum = false;
            this.temperatureMaximum = false;
            this.temperatureMinimum = false;
            this.pressureMaximum = false;
            this.pressureMaximum = false;
            if (this.isSimulated) {
                this.simTime += 60000 * (this.simulationMultiplier + this.stepForwardMultiplier);
            }
        }
    }

    /**
     * POST sensor values to the eventSubscribers
     *
     */
    public void postSubscriber() {
        try {
            HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(Registry.eventSubscribers);
            if (this.eventNotifiy) {
                for (EventSubscriberInputs subscriber : subscriberList.values()) {
                    String state = "";
                    EventPayload eventPayload = new EventPayload();
                    eventPayload.setClientData(subscriber.getClientData());
                    eventPayload.setSenderID(this.id);
                    eventPayload.setLastEmit(this.simTime);
                    eventPayload.setId(subscriber.getEventId());
                    SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                    Payload payload = null;
                    if (subscriber.getComponentId().equals(this.id)) {
                        switch (subscriber.getSenType()) {
                            case "inFlow":
                                payload = new Payload(Double.toString(this.inFlow), "double", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedInFlow = this.inFlow;
                                this.lastEmittedInFlowTimeStamp = this.simTime;
                                break;
                            case "outFlow":
                                if (this.outFlowMaximum) {
                                    state = "maximum";
                                } else if (this.outFlowMinimum) {
                                    state = "minimum";
                                } else {
                                    state = "nominal";
                                }
                                payload = new Payload(Double.toString(this.outFlow), "double", state);
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedOutFlow = this.outFlow;
                                this.lastEmittedOutFlowState = state;
                                this.lastEmittedOutFlowTimeStamp = this.simTime;
                                break;
                            case "temperature":
                                if (this.temperatureMaximum) {
                                    state = "maximum";
                                } else if (this.temperatureMinimum) {
                                    state = "minimum";
                                } else {
                                    state = "nominal";
                                }
                                payload = new Payload(Double.toString(this.temperature), "double", state);
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedTemperature = this.temperature;
                                this.lastEmittedTemperatureState = state;
                                this.lastEmittedTemperatureTimeStamp = this.simTime;
                                break;
                            case "pressure":
                                if (this.pressureMaximum) {
                                    state = "maximum";
                                } else if (this.pressureMinimum) {
                                    state = "minimum";
                                } else {
                                    state = "nominal";
                                }
                                payload = new Payload(Double.toString(this.pressure), "double", state);
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedPressure = this.pressure;
                                this.lastEmittedPressureState = state;
                                this.lastEmittedPressureTimeStamp = this.simTime;
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                for (EventSubscriberInputs subscriber : subscriberList.values()) {
                    String state = "";
                    boolean setFlag = false;
                    EventPayload eventPayload = new EventPayload();
                    eventPayload.setClientData(subscriber.getClientData());
                    eventPayload.setSenderID(this.id);
                    eventPayload.setLastEmit(this.simTime);
                    eventPayload.setId(subscriber.getEventId());
                    SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                    Payload payload = null;
                    if (subscriber.getComponentId().equals(this.id)) {
                        switch (subscriber.getSenType()) {
                            case "inFlow":
//                            payload = new Payload(Double.toString(this.inFlow), "");
//                            eventPayload.setPayload(payload);
//                            SimulatorRESTTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
//                            this.lastEmittedInFlow = this.inFlow;
//                            this.lastEmittedInFlowTimeStamp = this.simTime;
//                            break;
                            case "outFlow":
                                if (this.outFlowMaximum) {
                                    if (!this.lastEmittedOutFlowState.equals("maximum")) {
                                        setFlag = true;
                                        state = "maximum";
                                    }
                                } else if (this.outFlowMinimum) {
                                    if (!this.lastEmittedOutFlowState.equals("minimum")) {
                                        setFlag = true;
                                        state = "minimum";
                                    }
                                    state = "minimum";
                                } else {
                                    if (!this.lastEmittedOutFlowState.equals("nominal")) {
                                        setFlag = true;
                                        state = "nominal";
                                    }
                                    state = "nominal";
                                }
                                if (setFlag) {
                                    payload = new Payload(Double.toString(this.outFlow), "double", state);
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedOutFlow = this.outFlow;
                                    this.lastEmittedOutFlowState = state;
                                    this.lastEmittedOutFlowTimeStamp = this.simTime;
                                    setFlag = false;
                                }
                                break;
                            case "temperature":
                                if (this.temperatureMaximum) {
                                    if (!this.lastEmittedTemperatureState.equals("maximum")) {
                                        setFlag = true;
                                        state = "maximum";
                                    }
                                } else if (this.temperatureMinimum) {
                                    if (!this.lastEmittedTemperatureState.equals("minimum")) {
                                        setFlag = true;
                                        state = "minimum";
                                    }
                                    state = "minimum";
                                } else {
                                    if (!this.lastEmittedTemperatureState.equals("nominal")) {
                                        setFlag = true;
                                        state = "nominal";
                                    }
                                    state = "nominal";
                                }
                                if (setFlag) {
                                    payload = new Payload(Double.toString(this.temperature), "double", state);
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedTemperature = this.temperature;
                                    this.lastEmittedTemperatureState = state;
                                    this.lastEmittedTemperatureTimeStamp = this.simTime;
                                    setFlag = false;
                                }
                                break;
                            case "pressure":
                                if (this.pressureMaximum) {
                                    if (!this.lastEmittedPressureState.equals("maximum")) {
                                        setFlag = true;
                                        state = "maximum";
                                    }
                                } else if (this.pressureMinimum) {
                                    if (!this.lastEmittedPressureState.equals("minimum")) {
                                        setFlag = true;
                                        state = "minimum";
                                    }
                                    state = "minimum";
                                } else {
                                    if (!this.lastEmittedPressureState.equals("nominal")) {
                                        setFlag = true;
                                        state = "nominal";
                                    }
                                    state = "nominal";
                                }
                                if (setFlag) {
                                    payload = new Payload(Double.toString(this.pressure), "double", state);
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedPressure = this.pressure;
                                    this.lastEmittedPressureState = state;
                                    this.lastEmittedPressureTimeStamp = this.simTime;
                                    setFlag = false;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, id);
        }
    }

    /**
     * Checks all the values and if empty, corrects it self by assigning the
     * default values
     */
    public void correctEmptyValues() {
        if (this.parentId.equals("")) {
            this.self = myUrl + "/ms/" + this.id;
            this.info = myUrl + "/ms/" + this.id + "/info";
            this.parent = "No Parent";
        }

        if (this.simTime == 1) {
            Date date = new Date();
            this.simTime = date.getTime();
        }

        //Flow Sensor Links
        SensorLinks flowSensor = new SensorLinks(this.myUrl + "/ms/" + this.id + "/sensor/flow", this.myUrl + "/ms/" + this.id + "/sensor/flow/info");
        this.sensors.put("flow", flowSensor);

        //Temperature Sensor Links
        SensorLinks temperatureSensor = new SensorLinks(this.myUrl + "/ms/" + this.id + "/sensor/temperature", this.myUrl + "/ms/" + this.id + "/sensor/temperature/info");
        this.sensors.put("temperature", temperatureSensor);

        //Pressure Sensor Links
        SensorLinks pressureSensor = new SensorLinks(this.myUrl + "/ms/" + this.id + "/sensor/pressure", this.myUrl + "/ms/" + this.id + "/sensor/pressure/info");
        this.sensors.put("pressure", pressureSensor);

    }

    /**
     * Function to check the ID of the child Flow Meters and correct it as per
     * the MeasuringStation unit ID. Mostly used for the Bottom Up approach
     * while Adding the parent to other Element.
     */
    public void correctChildrenID() {
        ArrayList<String> removeFM = new ArrayList<>();
        ArrayList<String> fmKeys = new ArrayList<>();
        for (String fmKey : fmList.keySet()) {
            fmKeys.add(fmKey);
        }
        for (String fmKey : fmKeys) {
            FlowMeter fm = fmList.get(fmKey);
            String checkId = fmKey.substring(0, fmKey.indexOf("_FM"));
            if (!checkId.equals(this.id)) {
                removeFM.add(fmKey);
                Registry.flowMeter.remove(fmKey);
                Registry.linkFM.remove(fmKey);
                flowMeters.remove(fmKey);
                if (fm.getIsSimulated()) {
                    fm.interrupt();
                }
                fmKey = fmKey.substring(fmKey.indexOf("_FM"));
                fmKey = this.id + fmKey;
                fmList.put(fmKey, fm);
            }
        }
        for (String fmKey : removeFM) {
            fmList.remove(fmKey);
        }
    }

    /**
     * Things to start without Thread
     */
    public void measuringStationGenerate() {
        correctEmptyValues();
        correctChildrenID();
        setFMValues();
        measuringStationAnalyze();
        LubricationUnit lubricationUnit = null;
        if ((lubricationUnit = Registry.lubricationUnit.get(this.parentId)) != null) {
            lubricationUnit.LubricationUnitAnalyze();
            lubricationUnit.reg();
        }
        for (String fmKey : fmList.keySet()) {
            FlowMeter fm = fmList.get(fmKey);
            fm.flowMeterGenerate();
        }
        incIdFM = this.fmList.size();
        this.reg();
//        LOG.log(Level.INFO, "Registered Measuring Station {0}", this.id);
    }

    //------------------------------Thread Commands-----------------------------
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            this.isSimulated = true;
            measuringStationGenerate();
            try {
                for (String fmKey : fmList.keySet()) {
                    FlowMeter fm = null;
                    try {
                        if ((fm = fmList.get(fmKey)) != null) {
                            if (!fm.getIsSimulated()) {
                                fm.start();
                            }
                        }
                    } catch (IllegalThreadStateException ex) {
                        Logger.getLogger(MeasuringStation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                thread.start();
                this.isSimulated = true;
                this.reg();
            } catch (ConcurrentModificationException e) {
                Logger.getLogger(MeasuringStation.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void interrupt() {
        thread.interrupt();
        thread = null;
        isSimulated = false;
    }

}
