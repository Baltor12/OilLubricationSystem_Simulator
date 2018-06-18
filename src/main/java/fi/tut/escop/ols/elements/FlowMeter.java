/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.controller.SimulatorRESTTemplate;
import fi.tut.escop.ols.jsonLinks.SensorLinks;
import fi.tut.escop.ols.ontology.OntologyManager;
import fi.tut.escop.ols.rtuJson.EventPayload;
import fi.tut.escop.ols.rtuJson.Payload;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.simulation.SimValue;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class runs the Flow meters in different threads as per the request from
 * Measuring Station
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class FlowMeter implements Runnable {

    //------------------------------Declarations--------------------------------
    String id = "";
    boolean isSimulated = false;
    String parentId = "";
    Double nomFlow = 0.0;
    String flowUnit = "l/min";
    long simTime = 1;

    HashMap<String, SensorLinks> sensors = new HashMap<>();

    @JsonIgnore
    Double maxFlow = 0.0;
    @JsonIgnore
    Double minFlow = 0.0;
    @JsonIgnore
    boolean isAlive = false;
    @JsonIgnore
    Double outFlow = 0.0;
    @JsonIgnore
    Double inFlow = 0.0;
    @JsonIgnore
    boolean outFlowMaximum = false;
    @JsonIgnore
    boolean outFlowMinimum = false;
    @JsonIgnore
    Double lastEmittedInFlow = 0.0;
    @JsonIgnore
    Double lastEmittedOutFlow = 0.0;
    @JsonIgnore
    String lastEmittedOutFlowState = "";
    @JsonIgnore
    long lastEmittedOutFlowTimeStamp = 0;
    @JsonIgnore
    long lastEmittedInFlowTimeStamp = 0;
    @JsonIgnore
    int simulationMultiplier = 1;
    @JsonIgnore
    boolean isLeaking = false;
    @JsonIgnore
    boolean pipeLeak = false;
    @JsonIgnore
    boolean flowMeterLeak = false;
    @JsonIgnore
    private Thread thread;
    @JsonIgnore
    String self = "";
    @JsonIgnore
    String parent = "";
    @JsonIgnore
    String info = "";
    @JsonIgnore
    double particles4um = 0.0;
    @JsonIgnore
    double particles6um = 0.0;
    @JsonIgnore
    double particles14um = 0.0;
    @JsonIgnore
    Double cumilativeFlow = 0.0;
    @JsonIgnore
    Double leakageLevel = 0.0;
    @JsonIgnore
    int stepForwardMultiplier = 0;
    @JsonIgnore
    public SimValue.Mode mode = SimValue.Mode.SV_MODE_RUNNING;
    @JsonIgnore
    String myUrl = HostPortandConfig.ROOT_URL;
    @JsonIgnore
    private static final Logger LOG = Logger.getLogger(FlowMeter.class.getName());
    @JsonIgnore
    int eventNotificationCounter = 0;
    @JsonIgnore
    boolean eventNotifiy = false;

    //------------------------------Constructors--------------------------------
    public FlowMeter() {

    }

    public FlowMeter(String id) {
        this.id = id;
    }

    public String FlowMeterValue() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getMinFlow() {
        return minFlow;
    }

    public void setMinFlow(Double minFlow) {
        this.minFlow = minFlow;
    }

    public Double getNomFlow() {
        return nomFlow;
    }

    public void setNomFlow(Double nomFlow) {
        this.nomFlow = nomFlow;
    }

    public Double getMaxFlow() {
        return maxFlow;
    }

    public void setMaxFlow(Double maxFlow) {
        this.maxFlow = maxFlow;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean getIsSimulated() {
        return isSimulated;
    }

    public void setIsSimulated(boolean isSimulated) {
        this.isSimulated = isSimulated;
    }

    public Double flowMeterAnalyze() {
        return nomFlow;
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

    public SimValue.Mode getMode() {
        return mode;
    }

    public void setMode(SimValue.Mode mode) {
        this.mode = mode;
        if (this.mode == SimValue.Mode.SV_MODE_RESET) {
            this.inFlow = 0.0;
            this.outFlow = 0.0;
        }
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

    public Double getCumilativeFlow() {
        return cumilativeFlow;
    }

    public void setCumilativeFlow(Double cumilativeFlow) {
        this.cumilativeFlow = cumilativeFlow;
    }

    public String getFlowUnit() {
        return flowUnit;
    }

    public void setFlowUnit(String flowUnit) {
        this.flowUnit = flowUnit;
    }

    public HashMap<String, SensorLinks> getSensors() {
        return sensors;
    }

    public void setSensors(HashMap<String, SensorLinks> sensors) {
        this.sensors = sensors;
    }

    public Double getLeakageLevel() {
        return leakageLevel;
    }

    public void setLeakageLevel(Double leakageLevel) {
        this.leakageLevel = leakageLevel;
    }

    public boolean getIsLeaking() {
        return isLeaking;
    }

    public void setIsLeaking(boolean isLeaking) {
        this.isLeaking = isLeaking;
        this.flowMeterLeak = this.isLeaking;
    }

    public boolean getPipeLeak() {
        return pipeLeak;
    }

    public void setPipeLeak(boolean pipeLeak) {
        this.pipeLeak = pipeLeak;
    }

    public boolean getFlowMeterLeak() {
        return flowMeterLeak;
    }

    public void setFlowMeterLeak(boolean flowMeterLeak) {
        this.flowMeterLeak = flowMeterLeak;
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
    }

    public long getSimTime() {
        return simTime;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }

    /**
     * Function to save the Flow Meter to Ontology
     *
     * @return
     */
    public boolean saveFlowMeter() {
        boolean reply = true;
        try {
            OntologyManager.insertFlowMeter(this);
            OntologyManager.insertFlowMeterIntoMeasuringStation(this.parentId, this.id);
            reply = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, this);
            reply = false;
        }
        return reply;
    }

    /**
     * Function to store the registered values in Registry Class
     */
    public void reg() {
        Registry.flowMeter.put(this.id, this);
    }

    /**
     * Commands to be start while executing each Individual Flow meter thread
     */
    @Override
    public void run() {
        int i = 1;
        while (!Thread.interrupted()) {
            monitorFM();
            if (i == 1) {
                LOG.log(Level.INFO, "Starting the FM {0}", id);
                i = 2;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
        this.isSimulated = false;
        LOG.log(Level.INFO, "Stopping the FM {0}", id);
    }

    /**
     * Function to generate Flow Values while monitoring in the Simulator
     */
    public void monitorFM() {
        if (this.mode.equals(SimValue.Mode.SV_MODE_RUNNING)) {
            int flagStepForward = 0;

            if (this.eventNotificationCounter >= 5) {
                this.eventNotifiy = true;
                this.eventNotificationCounter = 0;
            } else {
                this.eventNotifiy = false;
                this.eventNotificationCounter++;
            }

            if (this.flowMeterLeak) {
                SimValue leak = new SimValue();
                this.leakageLevel = leak.randomLeakageAmount();
            } else {
                this.leakageLevel = 0.0;
            }

            if (this.stepForwardMultiplier != 0) {
                flagStepForward = 1;
            }

            SimValue simFlow = new SimValue(SimValue.SensorType.senFMFlow, this.mode);
            simFlow.setRangeHI(this.maxFlow);
            if (this.pipeLeak) {
                this.inFlow = simFlow.flowMeterInFlow(this.parentId, this.leakageLevel);
            } else {
                this.inFlow = simFlow.flowMeterInFlow(this.parentId, 0.0);
            }
            if (this.flowMeterLeak) {
                this.outFlow = simFlow.flowMeterOutFlow(this.inFlow, this.leakageLevel);
            } else {
                this.outFlow = simFlow.flowMeterOutFlow(this.inFlow, 0.0);
            }
            this.cumilativeFlow += this.outFlow;
            this.outFlowMaximum = simFlow.isSenFMFlow_Max_Alarm();
            this.outFlowMinimum = simFlow.isSenFMFlow_Min_Alarm();

            //Calculate the timing
            this.simTime += 60000 * (this.simulationMultiplier + this.stepForwardMultiplier);

            // Particles are generated as per the flow 
            SimValue simParticleCount = new SimValue(SimValue.SensorType.senLUParticleCount, this.mode);
            this.particles4um = simParticleCount.particleIncrementFlowMeter(this.outFlow, this.particles4um, "particles4um", this.simulationMultiplier, this.outFlow);
            this.particles6um = simParticleCount.particleIncrementFlowMeter(this.outFlow, this.particles6um, "particles6um", this.simulationMultiplier, this.outFlow);
            this.particles14um = simParticleCount.particleIncrementFlowMeter(this.outFlow, this.particles14um, "particles14um", this.simulationMultiplier, this.outFlow);

            if (flagStepForward == 1) {
                this.stepForwardMultiplier = 0;
            }
            postSubscriber();
        } else if (this.mode.equals(SimValue.Mode.SV_MODE_SHUTDOWN)) {
            this.inFlow = 0.0;
            this.outFlow = 0.0;
            this.outFlowMaximum = false;
            this.outFlowMinimum = false;
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
            HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs> (Registry.eventSubscribers);
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
        //----------------------------------------------------------------------    
        if (this.nomFlow == 0.0) {
            this.nomFlow = 10.0;
        }
        this.maxFlow = this.nomFlow * 2;
        this.minFlow = this.maxFlow * 0.10;

        //Flow Sensor Links
        SensorLinks flowSensor = new SensorLinks(this.myUrl + "/fm/" + this.id + "/sensor/flow", this.myUrl + "/fm/" + this.id + "/sensor/flow/info");
        this.sensors.put("flow", flowSensor);
    }

    /**
     * Things to start without Thread
     */
    public void flowMeterGenerate() {
        correctEmptyValues();
        this.reg();
        //LOG.log(Level.INFO, "Registered Flow Meter {0}", this.id);
    }

    //------------------------------Thread Commands-----------------------------
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            flowMeterGenerate();
            this.isSimulated = true;
            reg();
        }
    }

    public void interrupt() {
        thread.interrupt();
        this.isSimulated = false;
        thread = null;
    }
}
