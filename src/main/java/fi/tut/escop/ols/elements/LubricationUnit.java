/*
 * To change this license header, choose License Headers in Project Properties.
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
import fi.tut.escop.ols.rtuJson.ServicePayload;
import fi.tut.escop.ols.rtuJson.ServiceSubscriberInputs;
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
 * @author Balaji Gopalakrishnan (TUT)
 */
public final class LubricationUnit implements Runnable {

    //------------------------------Declarations--------------------------------
    String id = "";
    boolean isSimulated = false;
    boolean isRunning = false;
    String parentId = "";
    double maxOilFlow = 0.0;
    double oilAllocation = 0.0;
    double oilRemaining = 0.0;
    double tankCapacity = 0.0;
    double filterCapacity = 0.0;
    String flowUnit = "l/min";
    long simTime = 1;
    Integer noMeasuringStations = 0;

    HashMap<String, SensorLinks> sensors = new HashMap<>();
    HashMap<String, Links> measuringStations = new HashMap<>();

    @JsonIgnore
    HMI hmi = new HMI();
    @JsonIgnore
    double shutTankCapacity = 0.0;
    @JsonIgnore
    double shutFilterCapacity = 0.0;
    @JsonIgnore
    double shutParticle4um = 0.0;
    @JsonIgnore
    double shutParticle6um = 0.0;
    @JsonIgnore
    double shutParticle14um = 0.0;
    @JsonIgnore
    boolean shutFilterClog = false;
    @JsonIgnore
    boolean hmiEnabled = true;
    @JsonIgnore
    String self = "";
    @JsonIgnore
    String parent = "";
    @JsonIgnore
    String info = "";
    @JsonIgnore
    HashMap<String, MeasuringStation> msList = new HashMap<>();
    @JsonIgnore
    double outFlow = 0.0;
    @JsonIgnore
    double inFlow = 0.0;
    @JsonIgnore
    boolean outFlowMinimum = false;
    @JsonIgnore
    boolean outFlowMaximum = false;
    @JsonIgnore
    double level = 0.0;
    @JsonIgnore
    String levelUnit = "dm3";
    @JsonIgnore
    boolean levelMinimum = false;
    @JsonIgnore
    boolean levelMaximum = false;
    @JsonIgnore
    Double temperature = 0.0;
    @JsonIgnore
    String temperatureUnit = "Â°C";
    @JsonIgnore
    boolean temperatureMinimum = false;
    @JsonIgnore
    boolean temperatureMaximum = false;
    @JsonIgnore
    double waterContent = 0.0;
    @JsonIgnore
    String waterContentUnit = "%";
    @JsonIgnore
    String particleCount = "";
    @JsonIgnore
    boolean filterClog = false;
    @JsonIgnore
    boolean oilChange = false;
    @JsonIgnore
    boolean filterChange = false;
    @JsonIgnore
    boolean maintenance = false;
    @JsonIgnore
    String message = "";
    @JsonIgnore
    double lastEmittedOutFlow = 0.0;
    @JsonIgnore
    double lastEmittedInFlow = 0.0;
    @JsonIgnore
    String lastEmittedOutFlowState = "";
    @JsonIgnore
    double lastEmittedLevel = 0.0;
    @JsonIgnore
    String lastEmittedLevelState = "";
    @JsonIgnore
    Double lastEmittedTemperature = 0.0;
    @JsonIgnore
    String lastEmittedTemperatureState = "";
    @JsonIgnore
    double lastEmittedWaterContent = 0.0;
    @JsonIgnore
    String lastEmittedParticleCount = "";
    @JsonIgnore
    boolean lastEmittedFilterClog = false;
    @JsonIgnore
    boolean lastEmittedOilChange = false;
    @JsonIgnore
    boolean lastEmittedFilterChange = false;
    @JsonIgnore
    boolean lastEmittedMaintenance = false;
    @JsonIgnore
    String lastEmittedMessage = "";
    @JsonIgnore
    SimValue.Mode lastEmittedMode = SimValue.Mode.SV_MODE_SHUTDOWN;
    @JsonIgnore
    boolean lastEmittedStart = false;
    @JsonIgnore
    boolean lastEmittedStop = false;
    @JsonIgnore
    long lastEmittedOilChangeTimeStamp = 0;
    @JsonIgnore
    long lastEmittedFilterChangeTimeStamp = 0;
    @JsonIgnore
    long lastEmittedMaintenanceTimeStamp = 0;
    @JsonIgnore
    long lastEmittedMessageTimeStamp = 0;
    @JsonIgnore
    long lastEmittedOutFlowTimeStamp = 0;
    @JsonIgnore
    long lastEmittedInFlowTimeStamp = 0;
    @JsonIgnore
    long lastEmittedLevelTimeStamp = 0;
    @JsonIgnore
    long lastEmittedTemperatureTimeStamp = 0;
    @JsonIgnore
    long lastEmittedWaterContentTimeStamp = 0;
    @JsonIgnore
    long lastEmittedParticleCountTimeStamp = 0;
    @JsonIgnore
    long lastEmittedFilterClogTimeStamp = 0;
    @JsonIgnore
    long lastEmittedModeTimeStamp = 0;
    @JsonIgnore
    int simulationMultiplier = 1;
    @JsonIgnore
    double particles4um = 0.0;
    @JsonIgnore
    double particles6um = 0.0;
    @JsonIgnore
    double particles14um = 0.0;
    @JsonIgnore
    double avgParticles4um = 0.0;
    @JsonIgnore
    double avgParticles6um = 0.0;
    @JsonIgnore
    double avgParticles14um = 0.0;
    @JsonIgnore
    double timeParticles4um = 0.0;
    @JsonIgnore
    double timeParticles6um = 0.0;
    @JsonIgnore
    double timeParticles14um = 0.0;
    @JsonIgnore
    double filterEfficiency = 0.0;
    @JsonIgnore
    boolean isLeaking = false;
    @JsonIgnore
    double leakageLevel = 0.0;
    @JsonIgnore
    boolean levelCorrector = false;
    @JsonIgnore
    double litresFilterClog = 0.0; //litre of flow after which the clogging must happen
    @JsonIgnore
    public SimValue.Mode mode = SimValue.Mode.SV_MODE_RUNNING;
    @JsonIgnore
    private static final Logger LOG = Logger.getLogger(LubricationUnit.class.getName());
    @JsonIgnore
    int stepForwardMultiplier = 0;
    @JsonIgnore
    String myUrl = HostPortandConfig.ROOT_URL;
    @JsonIgnore
    private Thread thread;
    @JsonIgnore
    int incIdMS = 0;
    @JsonIgnore
    int noChildFlowMeters = 0;
    @JsonIgnore
    static final String MS_ID_PREFIX = "_MS_";
    @JsonIgnore
    int noEntry = 0;// dummy variable to make the progrum enter the loop in Monitoring
    @JsonIgnore
    int eventNotificationCounter = 0;
    @JsonIgnore
    boolean eventNotifiy = false;
    @JsonIgnore
    int shutEntry = 0;
    double prevFilt = 0.0;

    //------------------------------Constructors--------------------------------
    public LubricationUnit() {
    }

    public LubricationUnit(String id) {
        this.id = id;
    }

    public LubricationUnit(String id, HashMap<String, MeasuringStation> msList) {
        this.id = id;
        this.msList = msList;
        this.noMeasuringStations = msList.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getMaxOilFlow() {
        return maxOilFlow;
    }

    public void setMaxOilFlow(double maxOilFlow) {
        this.maxOilFlow = maxOilFlow;
    }

    public HashMap<String, MeasuringStation> getMsList() {
        return msList;
    }

    public void setMsList(HashMap<String, MeasuringStation> msList) {
        this.msList = msList;
    }

    public boolean getIsSimulated() {
        return isSimulated;
    }

    public void setIsSimulated(boolean isSimulated) {
        this.isSimulated = isSimulated;
    }

    public Integer getNoMeasuringStations() {
        return noMeasuringStations;
    }

    public void setNoMeasuringStations(Integer noMeasuringStations) {
        this.noMeasuringStations = noMeasuringStations;
    }

    public void updateNoMeasuringStations() {
        this.noMeasuringStations = msList.size();
    }

    public HashMap<String, SensorLinks> getSensors() {
        return sensors;
    }

    public void setSensors(HashMap<String, SensorLinks> sensors) {
        this.sensors = sensors;
    }

    public double getOilAllocation() {
        return oilAllocation;
    }

    public void setOilAllocation(double oilAllocation) {
        this.oilAllocation = oilAllocation;
    }

    public double getOilRemaining() {
        return oilRemaining;
    }

    public void setOilRemaining(double oilRemaining) {
        this.oilRemaining = oilRemaining;
    }

    public double getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(double tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    public double getFilterCapacity() {
        return filterCapacity;
    }

    public void setFilterCapacity(double filterCapacity) {
        this.filterCapacity = filterCapacity;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public double getOutFlow() {
        return outFlow;
    }

    public void setOutFlow(double outFlow) {
        this.outFlow = outFlow;
    }

    public boolean getOutFlowMinimum() {
        return outFlowMinimum;
    }

    public void setOutFlowMinimum(boolean outFlowMinimum) {
        this.outFlowMinimum = outFlowMinimum;
    }

    public boolean getOutFlowMaximum() {
        return outFlowMaximum;
    }

    public void setOutFlowMaximum(boolean outFlowMaximum) {
        this.outFlowMaximum = outFlowMaximum;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public boolean getLevelMinimum() {
        return levelMinimum;
    }

    public void setLevelMinimum(boolean levelMinimum) {
        this.levelMinimum = levelMinimum;
    }

    public boolean getLevelMaximum() {
        return levelMaximum;
    }

    public void setLevelMaximum(boolean levelMaximum) {
        this.levelMaximum = levelMaximum;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double Temperature) {
        this.temperature = Temperature;
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

    public double getWaterContent() {
        return waterContent;
    }

    public void setWaterContent(double waterContent) {
        this.waterContent = waterContent;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public String getWaterContentUnit() {
        return waterContentUnit;
    }

    public void setWaterContentUnit(String waterContentUnit) {
        this.waterContentUnit = waterContentUnit;
    }

    public boolean getFilterClog() {
        return filterClog;
    }

    public void setFilterClog(boolean filterClog) {
        this.filterClog = filterClog;
    }

    public double getLastEmittedOutFlow() {
        return lastEmittedOutFlow;
    }

    public void setLastEmittedOutFlow(double lastEmittedOutFlow) {
        this.lastEmittedOutFlow = lastEmittedOutFlow;
    }

    public double getLastEmittedInFlow() {
        return lastEmittedInFlow;
    }

    public void setLastEmittedInFlow(double lastEmittedInFlow) {
        this.lastEmittedInFlow = lastEmittedInFlow;
    }

    public String getLastEmittedOutFlowState() {
        return lastEmittedOutFlowState;
    }

    public void setLastEmittedOutFlowState(String lastEmittedOutFlowState) {
        this.lastEmittedOutFlowState = lastEmittedOutFlowState;
    }

    public double getLastEmittedLevel() {
        return lastEmittedLevel;
    }

    public void setLastEmittedLevel(double lastEmittedLevel) {
        this.lastEmittedLevel = lastEmittedLevel;
    }

    public String getLastEmittedLevelState() {
        return lastEmittedLevelState;
    }

    public void setLastEmittedLevelState(String lastEmittedLevelState) {
        this.lastEmittedLevelState = lastEmittedLevelState;
    }

    public Double getLastEmittedTemperature() {
        return lastEmittedTemperature;
    }

    public void setLastEmittedTemperature(double lastEmittedTemperature) {
        this.lastEmittedTemperature = lastEmittedTemperature;
    }

    public String getLastEmittedTemperatureState() {
        return lastEmittedTemperatureState;
    }

    public void setLastEmittedTemperatureState(String lastEmittedTemperatureState) {
        this.lastEmittedTemperatureState = lastEmittedTemperatureState;
    }

    public double getLastEmittedWaterContent() {
        return lastEmittedWaterContent;
    }

    public void setLastEmittedWaterContent(double lastEmittedWaterContent) {
        this.lastEmittedWaterContent = lastEmittedWaterContent;
    }

    public String getLastEmittedParticleCount() {
        return lastEmittedParticleCount;
    }

    public void setLastEmittedParticleCount(String lastEmittedParticleCount) {
        this.lastEmittedParticleCount = lastEmittedParticleCount;
    }

    public boolean isLastEmittedFilterClog() {
        return lastEmittedFilterClog;
    }

    public void setLastEmittedFilterClog(boolean lastEmittedFilterClog) {
        this.lastEmittedFilterClog = lastEmittedFilterClog;
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

    public long getLastEmittedLevelTimeStamp() {
        return lastEmittedLevelTimeStamp;
    }

    public void setLastEmittedLevelTimeStamp(long lastEmittedLevelTimeStamp) {
        this.lastEmittedLevelTimeStamp = lastEmittedLevelTimeStamp;
    }

    public long getLastEmittedTemperatureTimeStamp() {
        return lastEmittedTemperatureTimeStamp;
    }

    public void setLastEmittedTemperatureTimeStamp(long lastEmittedTemperatureTimeStamp) {
        this.lastEmittedTemperatureTimeStamp = lastEmittedTemperatureTimeStamp;
    }

    public long getLastEmittedWaterContentTimeStamp() {
        return lastEmittedWaterContentTimeStamp;
    }

    public void setLastEmittedWaterContentTimeStamp(long lastEmittedWaterContentTimeStamp) {
        this.lastEmittedWaterContentTimeStamp = lastEmittedWaterContentTimeStamp;
    }

    public long getLastEmittedParticleCountTimeStamp() {
        return lastEmittedParticleCountTimeStamp;
    }

    public void setLastEmittedParticleCountTimeStamp(long lastEmittedParticleCountTimeStamp) {
        this.lastEmittedParticleCountTimeStamp = lastEmittedParticleCountTimeStamp;
    }

    public long getLastEmittedFilterClogTimeStamp() {
        return lastEmittedFilterClogTimeStamp;
    }

    public void setLastEmittedFilterClogTimeStamp(long lastEmittedFilterClogTimeStamp) {
        this.lastEmittedFilterClogTimeStamp = lastEmittedFilterClogTimeStamp;
    }

    public double getFilterEfficiency() {
        return filterEfficiency;
    }

    public void setFilterEfficiency(double filterEfficiency) {
        this.filterEfficiency = filterEfficiency;
    }

    public double getLitresFilterClog() {
        return litresFilterClog;
    }

    public void setLitresFilterClog(double litresFilterClog) {
        this.litresFilterClog = litresFilterClog;
    }

    public int getSimulationMultiplier() {
        return simulationMultiplier;
    }

    public void setSimulationMultiplier(int simulationMultiplier) {
        this.simulationMultiplier = simulationMultiplier;
        //----------------------------------------------------------------------
        //this.level = (double) Math.round(LubricationUnitType.getLUOilLevel(this.type, this.oilType));
        //this.litresFilterClog = (double) LubricationUnitType.getLUFilterCapacity(type, oilType);
        this.particles4um = 0.0;
        this.particles6um = 0.0;
        this.particles14um = 0.0;
        this.noEntry = 0;
    }

    public SimValue.Mode getLastEmittedMode() {
        return lastEmittedMode;
    }

    public void setLastEmittedMode(SimValue.Mode lastEmittedMode) {
        this.lastEmittedMode = lastEmittedMode;
    }

    public long getLastEmittedModeTimeStamp() {
        return lastEmittedModeTimeStamp;
    }

    public void setLastEmittedModeTimeStamp(long lastEmittedModeTimeStamp) {
        this.lastEmittedModeTimeStamp = lastEmittedModeTimeStamp;
    }

    public boolean isLastEmittedStart() {
        return lastEmittedStart;
    }

    public void setLastEmittedStart(boolean lastEmittedStart) {
        this.lastEmittedStart = lastEmittedStart;
    }

    public boolean isLastEmittedStop() {
        return lastEmittedStop;
    }

    public void setLastEmittedStop(boolean lastEmittedStop) {
        this.lastEmittedStop = lastEmittedStop;
    }

    public SimValue.Mode getMode() {
        return mode;
    }

    public void setMode(SimValue.Mode mode) {
        this.mode = mode;
        if (this.mode == SimValue.Mode.SV_MODE_RESET) {
            this.noEntry = 0;
            this.litresFilterClog = this.filterCapacity;
            this.level = this.tankCapacity;
            this.inFlow = 0.0;
            this.outFlow = 0.0;
            this.temperature = 0.0;
            this.waterContent = 0.0;
        } else if (this.mode == SimValue.Mode.SV_MODE_RUNNING) {
            if (this.hmiEnabled) {
                this.hmi.setMessage("Started Lubrication Unit");
            }
            this.message = "Started Lubrication Unit";
            if (this.level == 0.0) {
                this.level = this.shutTankCapacity;
            }
            if (this.litresFilterClog == 0.0) {
                this.litresFilterClog = this.shutFilterCapacity;
            }
            if (this.particles4um == 0.0) {
                this.particles4um = this.shutParticle4um;
            }
            if (this.particles6um == 0.0) {
                this.particles6um = this.shutParticle6um;
            }
            if (this.particles14um == 0.0) {
                this.particles14um = this.shutParticle14um;
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
                if ((subscriber.getComponentId().equals(this.id)) && subscriber.getSenType().equals("start")) {
                    payload = new Payload("true", "boolean", "");
                    eventPayload.setPayload(payload);
                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                }
            }
            for (String subKey : Registry.serviceSubscribers.keySet()) {
                ServiceSubscriberInputs serSubscribers = Registry.serviceSubscribers.get(subKey);
                if (serSubscribers.getServiceId().equals("start")) {
                    EventPayload eventPayload = new EventPayload();
                    eventPayload.setClientData(serSubscribers.getClientData());
                    eventPayload.setSenderID(this.id);
                    eventPayload.setLastEmit(this.simTime);
                    eventPayload.setId(serSubscribers.getServiceId());
                    ServicePayload payload = new ServicePayload(Integer.toString(Registry.start.get(id).getCount()), Long.toString(Registry.start.get(id).getLastRun()));
                    eventPayload.setPayload(payload);
                    SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                    restTemplate.sensorValuePOST(eventPayload, serSubscribers.getDestUrl());
                }
            }
        } else if (this.mode == SimValue.Mode.SV_MODE_SHUTDOWN) {
            if (this.hmiEnabled) {
                this.hmi.setMessage("Stopped Lubrication Unit");
            }
            this.message = "Stopped Lubrication Unit";
            HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(Registry.eventSubscribers);
            for (EventSubscriberInputs subscriber : subscriberList.values()) {
                EventPayload eventPayload = new EventPayload();
                eventPayload.setClientData(subscriber.getClientData());
                eventPayload.setSenderID(this.id);
                eventPayload.setLastEmit(this.simTime);
                eventPayload.setId(subscriber.getEventId());
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                Payload payload = null;
                if ((subscriber.getComponentId().equals(this.id)) && subscriber.getSenType().equals("stop")) {
                    payload = new Payload("true", "boolean", "");
                    eventPayload.setPayload(payload);
                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                }
            }
            for (String subKey : Registry.serviceSubscribers.keySet()) {
                ServiceSubscriberInputs serSubscribers = Registry.serviceSubscribers.get(subKey);
                if (serSubscribers.getServiceId().equals("stop")) {
                    EventPayload eventPayload = new EventPayload();
                    eventPayload.setClientData(serSubscribers.getClientData());
                    eventPayload.setSenderID(this.id);
                    eventPayload.setLastEmit(this.simTime);
                    eventPayload.setId(serSubscribers.getServiceId());
                    ServicePayload payload = new ServicePayload(Integer.toString(Registry.stop.get(id).getCount()), Long.toString(Registry.stop.get(id).getLastRun()));
                    eventPayload.setPayload(payload);
                    SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                    restTemplate.sensorValuePOST(eventPayload, serSubscribers.getDestUrl());
                }
            }
        }
        for (String msKey : this.msList.keySet()) {
            MeasuringStation measuringStation = this.msList.get(msKey);
            measuringStation.setMode(mode);
            for (String fmKey : measuringStation.getFmList().keySet()) {
                FlowMeter flowMeter = Registry.flowMeter.get(fmKey);
                flowMeter.setMode(mode);
            }
        }
    }

    public HashMap<String, Links> getMeasuringStations() {
        return measuringStations;
    }

    public void setMeasuringStations(HashMap<String, Links> measuringStations) {
        this.measuringStations = measuringStations;
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

    public String getFlowUnit() {
        return flowUnit;
    }

    public void setFlowUnit(String flowUnit) {
        this.flowUnit = flowUnit;
    }

    public String getLevelUnit() {
        return levelUnit;
    }

    public void setLevelUnit(String levelUnit) {
        this.levelUnit = levelUnit;
    }

    public double getInFlow() {
        return inFlow;
    }

    public void setInFlow(double inFlow) {
        this.inFlow = inFlow;
    }

    public String getParticleCount() {
        return particleCount;
    }

    public void setParticleCount(String particleCount) {
        this.particleCount = particleCount;
    }

    public double getParticles4um() {
        return particles4um;
    }

    public void setParticles4um(double particles4um) {
        this.particles4um = particles4um;
    }

    public double getParticles6um() {
        return particles6um;
    }

    public void setParticles6um(double particles6um) {
        this.particles6um = particles6um;
    }

    public double getParticles14um() {
        return particles14um;
    }

    public void setParticles14um(double particles14um) {
        this.particles14um = particles14um;
    }

    public boolean getIsLeaking() {
        return isLeaking;
    }

    public void setIsLeaking(boolean isLeaking) {
        this.isLeaking = isLeaking;
        if (this.isLeaking) {
            //randomly selecting Measuring stations to set the pipe message
            for (String msKey : this.msList.keySet()) {
                MeasuringStation ms = this.msList.get(msKey);
                if (Math.random() < 0.5) {
                    ms.setPipeLeak(this.isLeaking);
                }
            }
        } else {
            //stop pipe leak of all Measuring Stations
            for (String msKey : this.msList.keySet()) {
                MeasuringStation ms = this.msList.get(msKey);
                ms.setPipeLeak(this.isLeaking);
            }
        }
    }

    public double getLeakageLevel() {
        return leakageLevel;
    }

    public void setLeakageLevel(double leakageLevel) {
        this.leakageLevel = leakageLevel;
    }

    public boolean isLevelCorrector() {
        return levelCorrector;
    }

    public void setLevelCorrector(boolean levelCorrector) {
        this.levelCorrector = levelCorrector;
    }

    public int getStepForwardMultiplier() {
        return stepForwardMultiplier;
    }

    public void setStepForwardMultiplier(int stepForwardMultiplier) {
        this.stepForwardMultiplier = stepForwardMultiplier;
        for (String msKey : msList.keySet()) {
            MeasuringStation ms = msList.get(msKey);
            ms.setStepForwardMultiplier(stepForwardMultiplier);
        }
    }

    public int getNoChildFlowMeters() {
        return noChildFlowMeters;
    }

    public void setNoChildFlowMeters(int noChildFlowMeters) {
        this.noChildFlowMeters = noChildFlowMeters;
    }

    public long getSimTime() {
        return simTime;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }

    public boolean isOilChange() {
        return oilChange;
    }

    public void setOilChange(boolean oilChange) {
        this.oilChange = oilChange;
    }

    public boolean isFilterChange() {
        return filterChange;
    }

    public void setFilterChange(boolean filterChange) {
        this.filterChange = filterChange;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLastEmittedOilChange() {
        return lastEmittedOilChange;
    }

    public void setLastEmittedOilChange(boolean lastEmittedOilChange) {
        this.lastEmittedOilChange = lastEmittedOilChange;
    }

    public boolean isLastEmittedFilterChange() {
        return lastEmittedFilterChange;
    }

    public void setLastEmittedFilterChange(boolean lastEmittedFilterChange) {
        this.lastEmittedFilterChange = lastEmittedFilterChange;
    }

    public boolean isLastEmittedMaintenance() {
        return lastEmittedMaintenance;
    }

    public void setLastEmittedMaintenance(boolean lastEmittedMaintenance) {
        this.lastEmittedMaintenance = lastEmittedMaintenance;
    }

    public String getLastEmittedMessage() {
        return lastEmittedMessage;
    }

    public void setLastEmittedMessage(String lastEmittedMessage) {
        this.lastEmittedMessage = lastEmittedMessage;
    }

    public long getLastEmittedOilChangeTimeStamp() {
        return lastEmittedOilChangeTimeStamp;
    }

    public void setLastEmittedOilChangeTimeStamp(long lastEmittedOilChangeTimeStamp) {
        this.lastEmittedOilChangeTimeStamp = lastEmittedOilChangeTimeStamp;
    }

    public long getLastEmittedFilterChangeTimeStamp() {
        return lastEmittedFilterChangeTimeStamp;
    }

    public void setLastEmittedFilterChangeTimeStamp(long lastEmittedFilterChangeTimeStamp) {
        this.lastEmittedFilterChangeTimeStamp = lastEmittedFilterChangeTimeStamp;
    }

    public long getLastEmittedMaintenanceTimeStamp() {
        return lastEmittedMaintenanceTimeStamp;
    }

    public void setLastEmittedMaintenanceTimeStamp(long lastEmittedMaintenanceTimeStamp) {
        this.lastEmittedMaintenanceTimeStamp = lastEmittedMaintenanceTimeStamp;
    }

    public long getLastEmittedMessageTimeStamp() {
        return lastEmittedMessageTimeStamp;
    }

    public void setLastEmittedMessageTimeStamp(long lastEmittedMessageTimeStamp) {
        this.lastEmittedMessageTimeStamp = lastEmittedMessageTimeStamp;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public HMI getHmi() {
        return hmi;
    }

    public void setHmi(HMI hmi) {
        this.hmi = hmi;
    }

    public double getShutTankCapacity() {
        return shutTankCapacity;
    }

    public void setShutTankCapacity(double shutTankCapacity) {
        this.shutTankCapacity = shutTankCapacity;
    }

    public double getShutFilterCapacity() {
        return shutFilterCapacity;
    }

    public void setShutFilterCapacity(double shutFilterCapacity) {
        this.shutFilterCapacity = shutFilterCapacity;
    }

    public double getShutParticle4um() {
        return shutParticle4um;
    }

    public void setShutParticle4um(double shutParticle4um) {
        this.shutParticle4um = shutParticle4um;
    }

    public double getShutParticle6um() {
        return shutParticle6um;
    }

    public void setShutParticle6um(double shutParticle6um) {
        this.shutParticle6um = shutParticle6um;
    }

    public double getShutParticle14um() {
        return shutParticle14um;
    }

    public void setShutParticle14um(double shutParticle14um) {
        this.shutParticle14um = shutParticle14um;
    }

    public boolean isShutFilterClog() {
        return shutFilterClog;
    }

    public void setShutFilterClog(boolean shutFilterClog) {
        this.shutFilterClog = shutFilterClog;
    }

    public boolean isHmiEnabled() {
        return hmiEnabled;
    }

    public void setHmiEnabled(boolean hmiEnabled) {
        this.hmiEnabled = hmiEnabled;
    }

    /**
     * Method to store the registered values in Registry Class
     */
    public void reg() {
        Registry.lubricationUnit.put(id, this);
    }

    /**
     * Method to analyze the outFlow in this Measuring Station
     *
     */
    public void LubricationUnitAnalyze() {
        HashMap<String, Double> values = new HashMap<>();
        values = SimMath.luAnalyze(this.maxOilFlow, this.msList);
        this.oilAllocation = values.get("oilAllocation");
        this.oilRemaining = values.get("oilRemaining");
        LubricationSystem lubricationSystem = null;
        if ((lubricationSystem = Registry.lubricationSystem.get(this.parentId)) != null) {
            lubricationSystem.LubricationSystemAnalyze();
            lubricationSystem.reg();
        }
        for (String msKey : this.measuringStations.keySet()) {
            MeasuringStation measuringStation = this.msList.get(msKey);
            measuringStation.setOilRemaining(oilRemaining);
            measuringStation.reg();
        }
    }

    /**
     * Function to Add single Lubrication Units to the specified Lubrication
     * System
     *
     * @param msId
     * @return
     */
    public boolean addChild(String msId) {
        boolean response;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            MeasuringStation ms = Registry.measuringStation.get(msId);
            if (ms.getIsSimulated()) {
                ms.interrupt();
            }
            if (msId.substring(0, 2).equals("MS")) {
                this.msList.put(this.generateMSId(), ms);
                LOG.log(Level.INFO, "Added Measuring Station {0}", ms.getId());
                Registry.measuringStation.remove(msId);
            } else {
                this.msList.put(msId, ms);
            }
            if (!this.isSimulated) {
                this.start();
            }
            response = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, id);
            response = false;
        }
        return response;
    }

    /**
     * Function to Add the unallocated Lubrication Units to the specified
     * Lubrication System
     *
     * @param msIds
     * @return
     */
    public boolean addChildren(ArrayList<String> msIds) {
        boolean response;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            for (String msId : msIds) {
                MeasuringStation ms = Registry.measuringStation.get(msId);
                if (ms.getIsSimulated()) {
                    ms.interrupt();
                }
                if (msId.substring(0, 2).equals("MS")) {
                    this.msList.put(this.generateMSId(), ms);
                    LOG.log(Level.INFO, "Added Measuring Station {0}", ms.getId());
                    Registry.measuringStation.remove(msId);
                } else {
                    this.msList.put(msId, ms);
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
     * Delete particular child Measuring Station
     *
     * @param msId
     * @return
     */
    public boolean deleteChild(String msId) {
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            MeasuringStation ms = msList.get(msId);
            if (ms.getIsSimulated()) {
                ms.interrupt();
            }
            ms.deleteChildren();
            //Remove MS Link Details in current LU and Registry of MS
            measuringStations.remove(msId);
            Registry.linkMS.remove(msId);
            LOG.log(Level.INFO, "Deleted Measuring Station {0}", ms.getId());
            //Remove MS Details in current LU and Registry of MS
            msList.remove(msId);
            Registry.measuringStation.remove(msId);

            this.start();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete all child Measuring Station
     *
     * @return
     */
    public boolean deleteChildren() {
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            for (String msKey : msList.keySet()) {
                MeasuringStation ms = msList.get(msKey);
                if (ms.getIsSimulated()) {
                    ms.interrupt();
                }
                ms.deleteChildren();
                OntologyManager.deleteMeasuringStation(ms);
                //Remove MS Link Details in current LU and Registry of MS
                measuringStations.remove(msKey);
                Registry.linkMS.remove(msKey);
                LOG.log(Level.INFO, "Deleted Measuring Station {0}", ms.getId());
                //Remove MS Details in current LU and Registry of MS
                Registry.measuringStation.remove(msKey);
            }
            msList.clear();

            this.start();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Function to save the Lubrication Unit to Ontology
     *
     * @param lsId
     * @return
     */
    public boolean saveLubricationUnit() {
        boolean reply = true;
        try {
            OntologyManager.insertLubricationUnit(this);
            OntologyManager.insertLubricationUnitIntoLubricationSystem(this.parentId, this.id);
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
        for (String msKey : msList.keySet()) {
            MeasuringStation ms = null;
            try {
                if ((ms = msList.get(msKey)) != null) {
                    if (ms.getIsSimulated()) {
                        ms.interrupt();
                        ms.interruptChildren();
                    }
                }
                ms.reg();
                result = true;
            } catch (IllegalThreadStateException ex) {
                result = false;
                Logger.getLogger(LubricationSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    /**
     * Commands to be start while executing each Individual Lubrication Unit
     * thread
     */
    @Override
    public void run() {
        LOG.log(Level.INFO, "Starting lubrication Unit {0}", id);
        while (!Thread.interrupted()) {
            monitorLU();
            try {
                if ((this.mode == SimValue.Mode.SV_MODE_RUNNING) || (this.mode == SimValue.Mode.SV_MODE_DISTURBANCE)) {
                    this.isRunning = true;
                } else {
                    this.isRunning = false;
                }
                this.isSimulated = true;
                this.reg();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
        this.isSimulated = false;
        LOG.log(Level.INFO, "Stopping lubrication Unit {0}", id);
    }

    /**
     * Set Label and register each Measuring Stations which is to be started.
     *
     */
    public void setMSValues() {
        for (String msKey : msList.keySet()) {
            MeasuringStation ms = msList.get(msKey);
            ms.setId(msKey);
            ms.setParentId(this.id);
            ms.setParent(this.self);
            ms.setSelf(this.self + "/ms/" + ms.getId());
            ms.setInfo(this.self + "/ms/" + ms.getId() + "/info");
            ms.setSimTime(this.simTime);
            ms.setSimulationMultiplier(this.simulationMultiplier);
            ms.setFMValues();
            Links msLink;
            msLink = new Links(ms.getId(), ms.getSelf(), ms.getParent(), ms.getInfo());
            msLink.regMS();
            this.measuringStations.put(ms.getId(), msLink);
            ms.reg();
        }
        this.noMeasuringStations = msList.size();
        reg();
    }

    /**
     * Method to Generate the IDs for Lubrication Units under this Lubrication
     * -Unit
     *
     * @return
     */
    public String generateMSId() {
        return id + MS_ID_PREFIX + (incIdMS++);
    }

    public void childrenFMs() {
        this.noChildFlowMeters = 0;
        for (String msKey : msList.keySet()) {
            MeasuringStation ms = msList.get(msKey);
            this.noChildFlowMeters += ms.getFmList().size();
        }
        this.reg();
    }

    /**
     * Function to generate Values while monitoring in the Simulator
     */
    public void monitorLU() {
        if (this.mode.equals(SimValue.Mode.SV_MODE_RUNNING)) {

            // Method to Determine the no of FlowMeters
            childrenFMs();

            int flagStepForward = 0;

            if (this.eventNotificationCounter >= 5) {
                this.eventNotifiy = true;
                this.eventNotificationCounter = 0;
            } else {
                this.eventNotifiy = false;
                this.eventNotificationCounter++;
            }

            if (this.stepForwardMultiplier != 0) {
                flagStepForward = 1;
            }

            SimValue simFlow = new SimValue(SimValue.SensorType.senLUFlow, this.mode);
            simFlow.setRangeHI(this.maxOilFlow);
            this.outFlow = simFlow.lubricationUnitOutFlow(this.oilAllocation, this.level);
            this.outFlowMaximum = simFlow.isSenLUFlow_Max_Alarm();
            this.outFlowMinimum = simFlow.isSenLUFlow_Min_Alarm();

//            System.out.println(this.id + ": " + "this.outFlow : " + this.outFlow);
            //Calculate the timing
            this.simTime += 60000 * (this.simulationMultiplier + this.stepForwardMultiplier);

            //System.out.println(this.id + ": " + "this.simTime : " + this.simTime);
            // The current inflow is added to level
            SimValue simLevel = new SimValue(SimValue.SensorType.senLULevel, this.mode);
            simLevel.setRangeHI(this.tankCapacity);
            this.inFlow = simLevel.lubricationUnitInFlow(this.msList);

//            System.out.println(this.id + ": " + "this.inFlow : " + this.inFlow);
            this.level = simLevel.lubricationUnitLevel(this.level, this.msList, this.inFlow, this.outFlow, this.simulationMultiplier, this.stepForwardMultiplier, this.noEntry);

            //System.out.println(this.id + ": " + "this.level : " + this.level);
            this.levelMaximum = simLevel.isSenLULevel_Max_Alarm();
            this.levelMinimum = simLevel.isSenLULevel_Min_Alarm();

            SimValue simWaterContent = new SimValue(SimValue.SensorType.senLUWaterContent, this.mode);
            this.waterContent = (double) Math.round((simWaterContent.eval()) * 100) / 100;

            SimValue simTemp = new SimValue(SimValue.SensorType.senLUTemp, this.mode);
            this.temperature = (double) Math.round((simTemp.eval()) * 100) / 100;
            this.temperatureMaximum = simTemp.isSenLUTemp_Max_Alarm();
            this.temperatureMinimum = simTemp.isSenLUTemp_Min_Alarm();

            this.noEntry++;

            SimValue simClog = new SimValue(SimValue.SensorType.senLUFilterClog, this.mode);
            if (this.inFlow != 0.0 && !String.valueOf(inFlow).equals("NaN")) {
                //First decreasing the filter efficiency as the filter is getting dirty
                if (this.filterCapacity != 0.0) {
                    this.filterEfficiency = simClog.lubricationUnitFilterEfficiency(this.filterCapacity, this.litresFilterClog);
                } else {
                    this.filterEfficiency = simClog.lubricationUnitFilterEfficiency((this.tankCapacity * 0.10), this.litresFilterClog);
                }

                //System.out.println(this.prevFilt - this.filterEfficiency);
                //System.out.println(this.id + "Filter Efficiency: " + this.filterEfficiency);
                // Calculating the average particles particles put by the total flow meter
                this.avgParticles4um = simClog.averageParticle("particles4um", this.msList);
                this.avgParticles6um = simClog.averageParticle("particles6um", this.msList);
                this.avgParticles14um = simClog.averageParticle("particles14um", this.msList);

                //System.out.println(this.avgParticles4um + " || " + this.avgParticles6um + " || " + this.avgParticles14um);
                //Calculating the particle according to the time step forward
                this.timeParticles4um = simClog.timeFilterPartice("particles4um", this.timeParticles4um, this.avgParticles4um, this.filterEfficiency, this.stepForwardMultiplier, this.inFlow);
                this.timeParticles6um = simClog.timeFilterPartice("particles6um", this.timeParticles6um, this.avgParticles6um, this.filterEfficiency, this.stepForwardMultiplier, this.inFlow);
                this.timeParticles14um = simClog.timeFilterPartice("particles14um", this.timeParticles14um, this.avgParticles14um, this.filterEfficiency, this.stepForwardMultiplier, this.inFlow);

//                System.out.println("time : " + this.timeParticles4um + " || " + this.timeParticles6um + " || " + this.timeParticles14um);
                // Calculating the total particle after it has been filtered
                this.particles4um = simClog.filterParticle("particles4um", this.particles4um, this.avgParticles4um, this.timeParticles4um, this.filterEfficiency, this.inFlow);
                this.particles6um = simClog.filterParticle("particles6um", this.particles6um, this.avgParticles6um, this.timeParticles6um, this.filterEfficiency, this.inFlow);
                this.particles14um = simClog.filterParticle("particles14um", this.particles14um, this.avgParticles14um, this.timeParticles14um, this.filterEfficiency, this.inFlow);

//                System.out.println(this.particles4um + " || " + this.particles6um + " || " + this.particles14um);
                this.avgParticles4um += this.avgParticles4um * this.stepForwardMultiplier;
                this.avgParticles6um += this.avgParticles4um * this.stepForwardMultiplier;
                this.avgParticles14um += this.avgParticles4um * this.stepForwardMultiplier;

                //Calculating the litres of particle collected by the filter and reducing the filter capacity.
                this.litresFilterClog = simClog.filterClog(this.avgParticles4um, this.avgParticles6um, this.avgParticles14um, this.filterEfficiency, this.noChildFlowMeters, this.litresFilterClog, this.simulationMultiplier, this.stepForwardMultiplier);
                //System.out.println(this.id + ": " + this.litresFilterClog);
                this.filterClog = simClog.isFilterClog();
            }

            SimValue simParticleCount = new SimValue(SimValue.SensorType.senLUParticleCount, this.mode);

            this.particleCount = simParticleCount.particleCountISO(this.particles4um, this.particles6um, this.particles14um);
            //System.out.println(this.id + ": " + this.particleCount);

            this.particles4um = this.particles4um - this.timeParticles4um;
            this.particles6um = this.particles6um - this.timeParticles6um;
            this.particles14um = this.particles14um - this.timeParticles14um;

            //Trigger to change oil.
            //It is doen when the level goes low.
            this.oilChange = this.levelMinimum;

            //Trigger to change Filter.
            //It is doen when the filter is clogged.
            this.filterChange = this.filterClog;

            //Trigger to Maintenance.
            //It is doen when the oil or temperature or outflow goes low or the filter is clogged.
            if (this.filterClog || this.levelMinimum || this.temperatureMinimum || this.outFlowMinimum || this.outFlowMaximum) {
                this.maintenance = true;
            } else {
                this.maintenance = false;
            }

            if (this.filterClog) {
                this.message = "Filter is clogged";
                if (this.hmiEnabled) {
                    this.hmi.setMessage("Filter is clogged");
                }
            } else if (this.levelMinimum) {
                this.message = "Level is minimum";
                if (this.hmiEnabled) {
                    this.hmi.setMessage("Level is minimum");
                }
            } else if (this.temperatureMinimum) {
                this.message = "Temperature is minimum";
                if (this.hmiEnabled) {
                    this.hmi.setMessage("Temperature is minimum");
                }
            } else if (this.outFlowMaximum) {
                this.message = "Output Flow is maximum";
                if (this.hmiEnabled) {
                    this.hmi.setMessage("Output Flow is maximum");
                }
            }

            if (flagStepForward == 1) {
                this.stepForwardMultiplier = 0;
            }

            this.prevFilt = this.filterEfficiency;

            postSubscriber();
            this.shutTankCapacity = this.level + this.outFlow;
            this.shutFilterCapacity = this.litresFilterClog;
            this.shutParticle4um = this.particles4um;
            this.shutParticle6um = this.particles6um;
            this.shutParticle14um = this.particles14um;
        } else if (this.mode.equals(SimValue.Mode.SV_MODE_SHUTDOWN)) {
            this.noEntry = 0;
            this.level = this.shutTankCapacity;
            this.litresFilterClog = this.shutFilterCapacity;
            this.inFlow = 0.0;
            this.outFlow = 0.0;
            this.temperature = 0.0;
            this.outFlowMaximum = false;
            this.outFlowMinimum = false;
            this.temperatureMaximum = false;
            this.temperatureMinimum = false;
            this.waterContent = 0.0;
            this.particleCount = "0/0/0";
            this.particles4um = 0.0;
            this.particles6um = 0.0;
            this.particles14um = 0.0;
            if (this.isSimulated) {
                this.simTime += 60000 * (this.simulationMultiplier + this.stepForwardMultiplier);
            }
            postSubscriber();
        }

        this.hmi.setSimTime(this.simTime);
        this.hmi.setIsRunning(this.isRunning);

        if (this.hmiEnabled) {
            if (this.levelMinimum) {
                this.hmi.setOilChange(this.levelMinimum);
            }
            if (this.filterClog) {
                this.hmi.setFilterChange(this.filterClog);
            }
        }
    }

    /**
     * Function to change the Oil of Lubrication Unit
     *
     * @return
     */
    public String changeOil() {
        String response = "";
        try {
            if (this.mode.equals(SimValue.Mode.SV_MODE_SHUTDOWN)) {
                this.levelMinimum = false;

                this.shutTankCapacity = this.tankCapacity;
                this.level = this.tankCapacity;
                //--------------------------------------------------------------
                //this.level = (double) Math.round(LubricationUnitType.getLUOilLevel(this.type, this.oilType));
                this.particles4um = 0.0;
                this.particles6um = 0.0;
                this.particles14um = 0.0;
                this.timeParticles4um = 0.0;
                this.timeParticles6um = 0.0;
                this.timeParticles14um = 0.0;
                response = "Successfully changed oil";
            } else {
                response = "Please change the mode to Shut Down and Again try to change Oil";
            }
        } catch (Exception e) {
            Logger.getLogger(LubricationUnit.class.getName()).log(Level.SEVERE, null, e);
            response = "Error while trying to change Oil";
        }
        try {
            HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(Registry.eventSubscribers);
            for (EventSubscriberInputs subscriber : subscriberList.values()) {
                EventPayload eventPayload = new EventPayload();
                eventPayload.setClientData(subscriber.getClientData());
                eventPayload.setSenderID(this.hmi.getId());
                eventPayload.setLastEmit(this.simTime);
                eventPayload.setId(subscriber.getEventId());
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                Payload payload = null;
                if (subscriber.getSenType().equals("oilChanged")) {
                    payload = new Payload(Boolean.toString(true), "boolean", "");
                    eventPayload.setPayload(payload);
                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                    break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LubricationUnit.class.getName()).log(Level.SEVERE, null, e);
        }
        if (this.hmiEnabled) {
            this.hmi.setMessage(response);
            this.hmi.setOilChange(false);
        }
        this.message = response;
        return response;
    }

    /**
     * Function to change the filter of Lubrication Unit
     *
     * @return
     */
    public String changeFilter() {
        String response = "";
        try {
            if (this.mode.equals(SimValue.Mode.SV_MODE_SHUTDOWN)) {
                this.filterClog = false;
                //--------------------------------------------------------------
                //this.litresFilterClog = (double) LubricationUnitType.getLUFilterCapacity(type, oilType);
                this.shutFilterCapacity = this.filterCapacity;

                // Change the number of timed particles as per the new filter. 
                SimValue simClog = new SimValue(SimValue.SensorType.senLUFilterClog, this.mode);
                this.timeParticles4um = simClog.filterChangeTimeParticleCancel("particles4um", this.timeParticles4um, 0.95);
                this.timeParticles6um = simClog.filterChangeTimeParticleCancel("particles6um", this.timeParticles6um, 0.95);
                this.timeParticles14um = simClog.filterChangeTimeParticleCancel("particles14um", this.timeParticles14um, 0.95);
//                 System.out.println("changed filter");
                response = "Successfully changed filter";
            } else {
                response = "Please change the mode to Shut Down and Again try to change Filter";
            }
        } catch (Exception e) {
            Logger.getLogger(LubricationUnit.class.getName()).log(Level.SEVERE, null, e);
            response = "Error while trying to replace Filter";
        }
        try {
            HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(Registry.eventSubscribers);
            for (EventSubscriberInputs subscriber : subscriberList.values()) {
                EventPayload eventPayload = new EventPayload();
                eventPayload.setClientData(subscriber.getClientData());
                eventPayload.setSenderID(this.hmi.getId());
                eventPayload.setLastEmit(this.simTime);
                eventPayload.setId(subscriber.getEventId());
                SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                Payload payload = null;
                if (subscriber.getSenType().equals("filterChanged")) {
                    payload = new Payload(Boolean.toString(true), "boolean", "");
                    eventPayload.setPayload(payload);
                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                    break;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LubricationUnit.class.getName()).log(Level.SEVERE, null, e);
        }
        if (this.hmiEnabled) {
            this.hmi.setMessage(response);
            this.hmi.setFilterChange(false);
        }
        this.message = response;
        return response;
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
                            case "level":
                                if (this.levelMaximum) {
                                    state = "maximum";
                                } else if (this.levelMinimum) {
                                    state = "minimum";
                                } else {
                                    state = "nominal";
                                }
                                payload = new Payload(Double.toString(this.level), "double", state);
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedLevel = this.level;
                                this.lastEmittedLevelState = state;
                                this.lastEmittedLevelTimeStamp = this.simTime;
                                break;
                            case "waterContent":
                                payload = new Payload(Double.toString(this.waterContent), "double", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedWaterContent = this.waterContent;
                                this.lastEmittedWaterContentTimeStamp = this.simTime;
                                break;
                            case "particleCount":
                                payload = new Payload(this.particleCount, "string", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedParticleCount = this.particleCount;
                                this.lastEmittedParticleCountTimeStamp = this.simTime;
                                break;
                            case "filterClog":
                                payload = new Payload(Boolean.toString(this.filterClog), "boolean", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedFilterClog = this.filterClog;
                                this.lastEmittedFilterClogTimeStamp = this.simTime;
                                break;
                            case "oilChange":
                                payload = new Payload(Boolean.toString(this.oilChange), "boolean", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedOilChange = this.oilChange;
                                this.lastEmittedOilChangeTimeStamp = this.simTime;
                                break;
                            case "filterChange":
                                payload = new Payload(Boolean.toString(this.filterChange), "boolean", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedFilterChange = this.filterChange;
                                this.lastEmittedFilterChangeTimeStamp = this.simTime;
                                break;
                            case "maintenance":
                                payload = new Payload(Boolean.toString(this.maintenance), "boolean", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedMaintenance = this.maintenance;
                                this.lastEmittedMaintenanceTimeStamp = this.simTime;
                                break;
                            case "message":
                                payload = new Payload(this.message, "string", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                this.lastEmittedMessage = this.message;
                                this.lastEmittedMessageTimeStamp = this.simTime;
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
                    boolean reply = false;
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
                                    payload = new Payload(Double.toString(this.outFlow), "string", state);
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
                                    payload = new Payload(Double.toString(this.temperature), "string", state);
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedTemperature = this.temperature;
                                    this.lastEmittedTemperatureState = state;
                                    this.lastEmittedTemperatureTimeStamp = this.simTime;
                                    setFlag = false;
                                }
                                break;
                            case "level":
                                if (this.levelMaximum) {
                                    if (!this.lastEmittedLevelState.equals("maximum")) {
                                        setFlag = true;
                                        state = "maximum";
                                    }
                                } else if (this.levelMinimum) {
                                    if (!this.lastEmittedLevelState.equals("minimum")) {
                                        setFlag = true;
                                        state = "minimum";
                                    }
                                    state = "minimum";
                                } else {
                                    if (!this.lastEmittedLevelState.equals("nominal")) {
                                        setFlag = true;
                                        state = "nominal";
                                    }
                                    state = "nominal";
                                }
                                if (setFlag) {
                                    payload = new Payload(Double.toString(this.level), "string", state);
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedLevel = this.level;
                                    this.lastEmittedLevelState = state;
                                    this.lastEmittedLevelTimeStamp = this.simTime;
                                    setFlag = false;
                                }
                                break;
                            case "waterContent":
//                            payload = new Payload(Double.toString(this.waterContent), "");
//                            eventPayload.setPayload(payload);
//                            SimulatorRESTTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
//                            this.lastEmittedWaterContent = this.waterContent;
//                            this.lastEmittedWaterContentTimeStamp = this.simTime;
                                break;
                            case "particleCount":
//                            payload = new Payload(this.particleCount, "");
//                            eventPayload.setPayload(payload);
//                            SimulatorRESTTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
//                            this.lastEmittedParticleCount = this.particleCount;
//                            this.lastEmittedParticleCountTimeStamp = this.simTime;
                                break;
                            case "filterClog":
                                if (this.filterClog) {
                                    if (!this.lastEmittedFilterClog) {
                                        setFlag = true;
                                    }
                                } else {
                                    if (this.lastEmittedFilterClog) {
                                        setFlag = true;
                                    }
                                }
                                if (setFlag) {
                                    payload = new Payload(Boolean.toString(this.filterClog), "boolean", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedFilterClog = this.filterClog;
                                    this.lastEmittedFilterClogTimeStamp = this.simTime;

                                }
                                break;
                            case "oilChange":
                                if (this.oilChange) {
                                    if (!this.lastEmittedOilChange) {
                                        setFlag = true;
                                    }
                                } else {
                                    if (this.lastEmittedOilChange) {
                                        setFlag = true;
                                    }
                                }
                                if (setFlag) {
                                    payload = new Payload(Boolean.toString(this.oilChange), "boolean", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedOilChange = this.oilChange;
                                    this.lastEmittedOilChangeTimeStamp = this.simTime;

                                }
                                break;
                            case "filterChange":
                                if (this.filterChange) {
                                    if (!this.lastEmittedFilterChange) {
                                        setFlag = true;
                                    }
                                } else {
                                    if (this.lastEmittedFilterChange) {
                                        setFlag = true;
                                    }
                                }
                                if (setFlag) {
                                    payload = new Payload(Boolean.toString(this.filterChange), "boolean", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedFilterChange = this.filterChange;
                                    this.lastEmittedFilterChangeTimeStamp = this.simTime;

                                }
                                break;
                            case "maintenance":
                                if (this.maintenance) {
                                    if (!this.lastEmittedMaintenance) {
                                        setFlag = true;
                                    }
                                } else {
                                    if (this.lastEmittedMaintenance) {
                                        setFlag = true;
                                    }
                                }
                                if (setFlag) {
                                    payload = new Payload(Boolean.toString(this.maintenance), "boolean", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedMaintenance = this.maintenance;
                                    this.lastEmittedMaintenanceTimeStamp = this.simTime;
                                    break;
                                }
                            case "message":
                                if (!this.message.equals(this.lastEmittedMessage)) {
                                    setFlag = true;
                                }
                                if (setFlag) {
                                    payload = new Payload(this.message, "string", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    this.lastEmittedMessage = this.message;
                                    this.lastEmittedMessageTimeStamp = this.simTime;
                                }
                                break;
                            case "start":
                                setFlag = false;
                                reply = this.lastEmittedStart;
                                if (this.mode == SimValue.Mode.SV_MODE_RUNNING) {
                                    if (this.lastEmittedMode != this.mode) {
                                        setFlag = true;
                                        reply = true;
                                    }
                                } else if (this.mode == SimValue.Mode.SV_MODE_SHUTDOWN) {
                                    if (this.lastEmittedStart == true) {
                                        setFlag = true;
                                        reply = false;
                                    }
                                }
                                if (setFlag) {
                                    payload = new Payload(Boolean.toString(reply), "boolean", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    if (reply) {
                                        this.lastEmittedMode = this.mode;
                                    }
                                    this.lastEmittedStart = reply;
                                    this.lastEmittedModeTimeStamp = this.simTime;
                                }
                                break;
                            case "stop":
                                setFlag = false;
                                reply = this.lastEmittedStop;
                                if (this.mode == SimValue.Mode.SV_MODE_SHUTDOWN) {
                                    if (this.lastEmittedMode != this.mode) {
                                        setFlag = true;
                                        reply = true;
                                    }
                                } else if (this.mode == SimValue.Mode.SV_MODE_RUNNING) {
                                    if (this.lastEmittedStop == true) {
                                        setFlag = true;
                                        reply = false;
                                    }
                                }
                                if (setFlag) {
                                    payload = new Payload(Boolean.toString(reply), "boolean", "");
                                    eventPayload.setPayload(payload);
                                    restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                    if (reply) {
                                        this.lastEmittedMode = this.mode;
                                    }
                                    this.lastEmittedStop = reply;
                                    this.lastEmittedModeTimeStamp = this.simTime;
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

        if (this.tankCapacity == 0.0) {
            this.tankCapacity = 9000.0;
        }

        if (this.shutTankCapacity == 0.0) {
            this.shutTankCapacity = this.tankCapacity;
        }

        if (this.filterCapacity == 0.0) {
            this.filterCapacity = this.tankCapacity * 0.15;
        }

        if (this.shutFilterCapacity == 0.0) {
            this.shutFilterCapacity = this.filterCapacity;
        }

        if (this.maxOilFlow == 0.0) {
            this.maxOilFlow = this.tankCapacity * 0.10;
        }

        if (this.simTime == 1) {
            Date date = new Date();
            this.simTime = date.getTime();
        }

        //Flow Sensor Links
        SensorLinks flowSensor = new SensorLinks(this.myUrl + "/lu/" + this.id + "/sensor/flow", this.myUrl + "/lu/" + this.id + "/sensor/flow/info");
        this.sensors.put("flow", flowSensor);

        //Level Sensor Links
        SensorLinks levelSensor = new SensorLinks(this.myUrl + "/lu/" + this.id + "/sensor/level", this.myUrl + "/lu/" + this.id + "/sensor/level/info");
        this.sensors.put("level", levelSensor);

        //Temperature Sensor Links
        SensorLinks temperatureSensor = new SensorLinks(this.myUrl + "/lu/" + this.id + "/sensor/temperature", this.myUrl + "/lu/" + this.id + "/sensor/temperature/info");
        this.sensors.put("temperature", temperatureSensor);

        //Particle Sensor Links
        SensorLinks particleSensor = new SensorLinks(this.myUrl + "/lu/" + this.id + "/sensor/particlecount", this.myUrl + "/lu/" + this.id + "/sensor/particlecount/info");
        this.sensors.put("particlecount", particleSensor);

        //Filter Sensor Links
        SensorLinks filterSensor = new SensorLinks(this.myUrl + "/lu/" + this.id + "/sensor/filterclog", this.myUrl + "/lu/" + this.id + "/sensor/filterclog/info");
        this.sensors.put("filterclog", filterSensor);

        //WaterContent Sensor Links
        SensorLinks waterSensor = new SensorLinks(this.myUrl + "/lu/" + this.id + "/sensor/watercontent", this.myUrl + "/lu/" + this.id + "/sensor/watercontent/info");
        this.sensors.put("watercontent", waterSensor);

        //configure HMI
        if (this.hmi != null) {
            if (this.hmi.getId() == null || this.hmi.getId().equals("")) {
                this.hmi.setId(this.id + "_HMI");
                this.hmi.setParentId(this.id);
                this.hmi.reg();
            }
        }
    }

    /**
     * Function to check the ID of the child Measuring Stations and correct it
     * as per the Lubrication unit ID. Mostly used for the Bottom Up approach
     * while Adding the parent to other Element.
     */
    public void correctChildrenID() {
        ArrayList<String> removeMS = new ArrayList<>();
        ArrayList<String> msKeys = new ArrayList<>();
        for (String msKey : msList.keySet()) {
            msKeys.add(msKey);
        }
        for (String msKey : msKeys) {
            MeasuringStation ms = msList.get(msKey);
            String checkId = msKey.substring(0, msKey.indexOf("_MS"));
            if (!checkId.equals(this.id)) {
                removeMS.add(msKey);
                Registry.measuringStation.remove(msKey);
                Registry.linkMS.remove(msKey);
                measuringStations.remove(msKey);
                if (ms.getIsSimulated()) {
                    ms.interrupt();
                }
                msKey = msKey.substring(msKey.indexOf("_MS"));
                msKey = this.id + msKey;
                msList.put(msKey, ms);
            }
        }
        for (String msKey : removeMS) {
            msList.remove(msKey);
        }
    }

    /**
     * Things to start without Thread
     */
    public void lubricationUnitGenerate() {
        this.noChildFlowMeters = 0;
        correctEmptyValues();
        correctChildrenID();
        setMSValues();
        LubricationUnitAnalyze();
        for (String msKey : msList.keySet()) {
            MeasuringStation ms = msList.get(msKey);
            this.noChildFlowMeters += ms.getFmList().size();
            ms.measuringStationGenerate();
        }
        this.reg();
//        LOG.log(Level.INFO, "Registered lubrication Unit {0}", this.id);
    }

    //------------------------------Thread Commands-----------------------------
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            this.isSimulated = true;
            lubricationUnitGenerate();
            if (this.hmiEnabled) {
                this.hmi.setMessage("Started Lubrication Unit");
            }
            this.message = "Started Lubrication Unit";
            try {
                for (String msKey : msList.keySet()) {
                    MeasuringStation ms = null;
                    try {
                        if ((ms = msList.get(msKey)) != null) {
                            if (!ms.getIsSimulated()) {
                                ms.start();
                            }
                        }
                    } catch (IllegalThreadStateException ex) {
                        Logger.getLogger(LubricationUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (ConcurrentModificationException e) {
                Logger.getLogger(LubricationUnit.class.getName()).log(Level.SEVERE, null, e);
            }
            thread.start();
            this.isSimulated = true;
            this.reg();
        }
    }

    public void interrupt() {
        thread.interrupt();
        this.isSimulated = false;
        this.isRunning = false;
        thread = null;
    }
}
