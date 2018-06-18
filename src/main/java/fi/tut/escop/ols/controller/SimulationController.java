/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.extra.FlowMeterSensors;
import fi.tut.escop.ols.extra.LubricationUnitSensors;
import fi.tut.escop.ols.extra.MeasuringStationSensors;
import fi.tut.escop.ols.extra.Response;
import fi.tut.escop.ols.rtuJson.EventPayload;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.rtuJson.Payload;
import fi.tut.escop.ols.simulation.SimValue;
import fi.tut.escop.ols.simulation.TimeSettings;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class that has Services with respect to sensors in Lubrication
 * System.
 *
 * Balaji Gopalakrishnan(TUT)
 */
@RestController
public class SimulationController {

    String myUrl = HostPortandConfig.ROOT_URL;
    public static ResponseEntity<Response> RESPONSE;
    private static final Logger LOG = Logger.getLogger(SimulationController.class.getName());
    static LubricationSystem ls;
    static LubricationUnit lu;
    static MeasuringStation ms;
    static FlowMeter fm;

    //---------------------------------POST Methods For Json----------------------------------------
    /**
     * Function to generate REST method to simulate as per the prefered Timing
     *
     * @param day
     * @param hour
     * @param minute
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/timescale", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Response> simulationTimeSetting(
            @RequestParam(value = "day", defaultValue = "0") int day,
            @RequestParam(value = "hour", defaultValue = "0") int hour,
            @RequestParam(value = "minute", defaultValue = "0") int minute,
            @PathVariable String lsId) {
        LubricationSystem ls = null;
        boolean checkLUMode = false;
        String luList = "";
        if (((ls = Registry.lubricationSystem.get(lsId)) != null)) {
            for (String luKey : ls.getLuList().keySet()) {
                LubricationUnit lu = Registry.lubricationUnit.get(luKey);
                if (lu.getMode() == SimValue.Mode.SV_MODE_RUNNING) {
                    checkLUMode = true;
                    luList = lu.getId() + ", ";
                }
            }
            if (checkLUMode) {
                RESPONSE = new ResponseEntity<>(new Response("Please shutdown the respective Lubrication Units {" + luList + "} and try again"), HttpStatus.BAD_REQUEST);
            } else {
                TimeSettings time = new TimeSettings();
                time.setLubricationSystemId(lsId);
                if (day > 0) {
                    time.setDayMultiplier(day);
                }
                if (hour > 0) {
                    time.setHourMultiplier(hour);
                }
                if (minute > 0) {
                    time.setMinuteMultiplier(minute);
                }
                if (time.setLubricationSystemTime()) {
                    RESPONSE = new ResponseEntity<>(new Response("Succesfully changed the Simulation interval for Lubrication System " + lsId), HttpStatus.ACCEPTED);
                } else {
                    RESPONSE = new ResponseEntity<>(new Response("Failed changing the Simulation interval for Lubrication System " + lsId), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication System does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Function to generate REST method to fast forward the simulation
     *
     * @param day
     * @param hour
     * @param minute
     * @param quantity
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{luId}/steps", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Response> stepForwardTimeSettingLU(
            @RequestParam(value = "day", defaultValue = "0") int day,
            @RequestParam(value = "hour", defaultValue = "0") int hour,
            @RequestParam(value = "minute", defaultValue = "0") int minute,
            @RequestParam(value = "quantity", defaultValue = "0") int quantity,
            @PathVariable String luId) {
        LubricationUnit lu = null;
        String luList = "";
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            TimeSettings time = new TimeSettings();
            time.setLubricationUnitId(luId);
            if (day > 0) {
                time.setDayMultiplier(day);
            }
            if (hour > 0) {
                time.setHourMultiplier(hour);
            }
            if (minute > 0) {
                time.setMinuteMultiplier(minute);
            }
            if (quantity > 0) {
                time.setPointMultiplier(quantity);
            }
            if (time.setLubricationUnitStepForward()) {
                RESPONSE = new ResponseEntity<>(new Response("Successfully fast forwarded simulation"), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Failed fast Forwarding Simulation"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication Unit does not exist"), HttpStatus.BAD_REQUEST);
        }
        return RESPONSE;
    }

    /**
     * Function to generate REST method to simulate as per the prefered
     * scenarios
     *
     * @param action
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Response> scenario(@RequestParam(value = "action", defaultValue = "none") String action, @PathVariable String luId) {
        LubricationUnit lu = null;
        boolean leakage = false;
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            if (lu.getMode() == SimValue.Mode.SV_MODE_SHUTDOWN) {
                switch (action) {
                    case "leakage":
                        leakage = true;
                        double random = (double) Math.random();
                        if ((random < 0.3)) {
                            lu.setIsLeaking(leakage);
                        } else if (random > 0.7) {
                            for (String msKey : lu.getMsList().keySet()) {
                                MeasuringStation ms = lu.getMsList().get(msKey);
                                if (Math.random() < 0.5) {
                                    ms.setIsLeaking(leakage);
                                }
                            }
                        } else {
                            for (String msKey : lu.getMsList().keySet()) {
                                MeasuringStation ms = lu.getMsList().get(msKey);
                                for (String fmKey : ms.getFmList().keySet()) {
                                    FlowMeter fm = ms.getFmList().get(fmKey);
                                    if (Math.random() < 0.5) {
                                        fm.setIsLeaking(leakage);
                                    }
                                }
                            }
                        }
                        RESPONSE = new ResponseEntity<>(new Response("Successfully performed action " + action), HttpStatus.OK);
                        break;
                    case "stopLeakage":
                        leakage = false;
                        lu.setIsLeaking(leakage);
                        for (String msKey : lu.getMsList().keySet()) {
                            MeasuringStation ms = lu.getMsList().get(msKey);
                            ms.setIsLeaking(leakage);
                            for (String fmKey : ms.getFmList().keySet()) {
                                FlowMeter fm = ms.getFmList().get(fmKey);
                                fm.setIsLeaking(leakage);
                            }
                        }
                        HashMap<String, EventSubscriberInputs> subscriberList = new HashMap<String, EventSubscriberInputs>(Registry.eventSubscribers);
                        for (EventSubscriberInputs subscriber : subscriberList.values()) {
                            EventPayload eventPayload = new EventPayload();
                            eventPayload.setClientData(subscriber.getClientData());
                            eventPayload.setSenderID(lu.getHmi().getId());
                            eventPayload.setLastEmit(lu.getSimTime());
                            eventPayload.setId(subscriber.getEventId());
                            SimulatorRESTTemplate restTemplate = new SimulatorRESTTemplate();
                            Payload payload = null;
                            if (subscriber.getSenType().equals("maintenanceDone")) {
                                payload = new Payload(Boolean.toString(true), "boolean", "");
                                eventPayload.setPayload(payload);
                                restTemplate.sensorValuePOST(eventPayload, subscriber.getDestUrl());
                                break;
                            }
                        }
                        RESPONSE = new ResponseEntity<>(new Response("Successfully performed action " + action), HttpStatus.OK);
                        break;
                    case "filterChange":
                        RESPONSE = new ResponseEntity<>(new Response(lu.changeFilter()), HttpStatus.OK);
                        break;
                    case "oilChange":
                        RESPONSE = new ResponseEntity<>(new Response(lu.changeOil()), HttpStatus.OK);
                        break;
                    case "none":
                        RESPONSE = new ResponseEntity<>(new Response("Success! no action taken"), HttpStatus.OK);
                        break;
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Please ShutDown the lubrication Unit " + luId + " and try again"), HttpStatus.BAD_REQUEST);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Failed to perform action, since lubrication Unit " + luId + " not found"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    //---------------------------------PUT Methods For Json-----------------------------------------
    /**
     * Simple PUT RESTful service for changing the mode of simulation while -
     * monitoring the whole system
     *
     * @param mode
     * @param luId
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/lu/{luId}", method = RequestMethod.PUT)
    public ResponseEntity<Response> SetMode(@RequestParam(value = "mode", defaultValue = "on") String mode, @PathVariable String luId) throws IOException {
        SimValue.Mode modeValue = null;
        LubricationUnit lu = null;
        boolean leakage = false;
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            switch (mode) {
                case "on":
                    modeValue = SimValue.Mode.SV_MODE_RUNNING;
                    lu.setMode(modeValue);
                    RESPONSE = new ResponseEntity<>(new Response("Successfully changed the Mode"), HttpStatus.CREATED);
                    break;
                case "disturb":
                    modeValue = SimValue.Mode.SV_MODE_DISTURBANCE;
                    lu.setMode(modeValue);
                    RESPONSE = new ResponseEntity<>(new Response("Successfully changed the Mode"), HttpStatus.CREATED);
                    break;
                case "off":
                    modeValue = SimValue.Mode.SV_MODE_SHUTDOWN;
                    lu.setMode(modeValue);
                    RESPONSE = new ResponseEntity<>(new Response("Successfully changed the Mode"), HttpStatus.CREATED);
                    break;
                default:
                    RESPONSE = new ResponseEntity<>(new Response("Modes available are 'on', 'disturb' and 'off'"), HttpStatus.CREATED);
                    break;
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Failed to change mode, lubrication Unit " + luId + " not found"), HttpStatus.CREATED);
        }
        return RESPONSE;
    }

    //---------------------------------GET Methods For Json-----------------------------------------
    /**
     * Function to Get all Sensor Values of the particular Lubrication Unit
     *
     * @return
     */
    @RequestMapping(value = "/url", method = RequestMethod.GET)
    public ResponseEntity<Response> getURL() {
        return new ResponseEntity<>(new Response(HostPortandConfig.ROOT_URL), HttpStatus.OK);
    }

    /**
     * Function to Get all Sensor Values of the particular Lubrication Unit
     *
     * @return
     */
    @RequestMapping(value = "/message/event", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Object>> getMessageFormats() {
        ResponseEntity<HashMap<String, Object>> response;
        HashMap<String, Object> meta = new HashMap<>();
        try {
            meta.put("valueFormat", "value");
            meta.put("stateFormat", "state");
            meta.put("dataTypeFormat", "type");
            meta.put("timeFormat", "time");
            response = new ResponseEntity<>(meta, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e, "");
            response = new ResponseEntity<>(meta, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Function to Get all Sensor Values of the particular Lubrication Unit
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/sensor", method = RequestMethod.GET)
    public ResponseEntity<LubricationUnitSensors> getLUSensor(@PathVariable String luId) {
        LubricationUnitSensors luSensor;
        LubricationUnit lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            luSensor = new LubricationUnitSensors(lu.getId(), lu.getIsSimulated(), lu.isIsRunning(), lu.getSimTime(), lu.getInFlow(), lu.getOutFlow(), lu.getOutFlowMaximum(), lu.getOutFlowMinimum(), lu.getLevel(), lu.getLevelMaximum(), lu.getLevelMinimum(), lu.getTemperature(), lu.getTemperatureMaximum(), lu.getTemperatureMinimum(), lu.getWaterContent(), lu.getParticleCount(), lu.getLitresFilterClog(), lu.getFilterClog());
        } else {
            luSensor = new LubricationUnitSensors("", false, false, 0, 0.0, 0.0, false, false, 0.0, false, false, 0.0, false, false, 0.0, "", 0.0, false);
        }
        return new ResponseEntity<>(luSensor, HttpStatus.OK);
    }

    /**
     * Function to Get all Sensor Values of the particular Measuring Station
     *
     * @param msId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}/sensor", method = RequestMethod.GET)
    public ResponseEntity<MeasuringStationSensors> getMSSensor(@PathVariable String msId) {
        MeasuringStationSensors msSensor;
        MeasuringStation ms = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            msSensor = new MeasuringStationSensors(ms.getId(), ms.getSimTime(), ms.getInFlow(), ms.getOutFlow(), ms.getOutFlowMaximum(), ms.getOutFlowMinimum(), ms.getTemperature(), ms.getTemperatureMaximum(), ms.getTemperatureMinimum(), ms.getPressure(), ms.getPressureMaximum(), ms.getPressureMinimum());
        } else {
            msSensor = new MeasuringStationSensors("", 0, 0.0, 0.0, false, false, 0.0, false, false, 0.0, false, false);
        }
        return new ResponseEntity<>(msSensor, HttpStatus.OK);
    }

    /**
     * Function to Get all Sensor Values of the particular Flow Meter
     *
     * @param fmId
     * @return
     */
    @RequestMapping(value = "/fm/{fmId}/sensor", method = RequestMethod.GET)
    public ResponseEntity<FlowMeterSensors> getFMSensor(@PathVariable String fmId) {
        FlowMeterSensors fmSensor;
        FlowMeter fm = null;
        if ((fm = Registry.flowMeter.get(fmId)) != null) {
            fmSensor = new FlowMeterSensors(fm.getId(), fm.getSimTime(), fm.getInFlow(), fm.getOutFlow(), fm.getOutFlowMaximum(), fm.getOutFlowMinimum());
        } else {
            fmSensor = new FlowMeterSensors("", 0, 0.0, 0.0, false, false);
        }
        return new ResponseEntity<>(fmSensor, HttpStatus.OK);
    }

    //---------------------------------DELETE Methods For Json-----------------------------------------
    /**
     * Remove message in component
     *
     * @param Id
     * @return
     */
    @RequestMapping(value = "/{Id}/leakage", method = RequestMethod.DELETE)
    public ResponseEntity<Response> removeLeakageinComponent(@PathVariable String Id
    ) {
        lu = null;
        ms = null;
        fm = null;
        if ((lu = Registry.lubricationUnit.get(Id)) != null) {
            lu.setIsLeaking(false);
            RESPONSE = new ResponseEntity<>(new Response("Successfully Removed leakage in Lubrication Unit " + Id), HttpStatus.ACCEPTED);
        } else if ((ms = Registry.measuringStation.get(Id)) != null) {
            ms.setIsLeaking(false);
            RESPONSE = new ResponseEntity<>(new Response("Successfully Removed leakage in Measuring Station " + Id), HttpStatus.ACCEPTED);
        } else if ((fm = Registry.flowMeter.get(Id)) != null) {
            fm.setIsLeaking(false);
            RESPONSE = new ResponseEntity<>(new Response("Successfully Removed leakage in Flow Meter " + Id), HttpStatus.ACCEPTED);
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Component with id: " + Id + " does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }
}
