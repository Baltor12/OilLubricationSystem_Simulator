/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.HMI;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.extra.Response;
import fi.tut.escop.ols.rtuJson.BasicTree;
import fi.tut.escop.ols.rtuJson.EventInfo;
import fi.tut.escop.ols.rtuJson.tags.ElementServiceTags;
import fi.tut.escop.ols.rtuJson.EventServiceTree;
import fi.tut.escop.ols.rtuJson.Links.LinkWithoutParent;
import fi.tut.escop.ols.rtuJson.RTUResponse;
import fi.tut.escop.ols.rtuJson.Payload;
import fi.tut.escop.ols.rtuJson.SensorInformation;
import fi.tut.escop.ols.rtuJson.ServiceDetails;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.rtuJson.ServiceSubscriberInputs;
import fi.tut.escop.ols.simulation.SimValue;
import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class which acts as a rest controller to provide RTU related information
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
public class RTUController {

    static String myUrl = HostPortandConfig.ROOT_URL;
    public static ResponseEntity<Response> RESPONSE;
    static LubricationSystem ls;
    static LubricationUnit lu;
    static MeasuringStation ms;
    static FlowMeter fm;
    static HMI hmi;
    private static final Logger LOG = Logger.getLogger(RTUController.class.getName());

    //-------------------------------------POST---------------------------------
    /**
     * Post method which registers the subscriber URL to the system for
     * notification while that particular event happens
     *
     * @param id
     * @param senId
     * @param json
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/{senId}/notifs", method = RequestMethod.POST)
    public static ResponseEntity<EventSubscriberInputs> postSenIdEventsNotifs(@PathVariable String id, @PathVariable String senId, @RequestBody String json, @PathVariable String lsId) {
        ResponseEntity<EventSubscriberInputs> response;
        String subscriberId = "";
        String clientdata = "";
        String destUrl = "";
        String sensor = "";
        String basePath = myUrl + "/" + lsId + "/RTU";
        ms = null;
        hmi= null;
        HashMap<String, String> links = new HashMap<>();
        EventSubscriberInputs subscriber = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            try {
                Map<String, String> map = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                map = mapper.readValue(json,
                        new TypeReference<HashMap<String, String>>() {
                        });
                subscriberId = Registry.generateSubscriberID();
                if ((clientdata = map.get("clientData")) == null) {
                    clientdata = "";
                } else {
                    clientdata = map.get("clientData");
                }
                if ((destUrl = map.get("destUrl")) == null) {
                    destUrl = "";
                } else {
                    destUrl = map.get("destUrl");
                }
                links.put("self", basePath + "/" + id + "/events/" + senId + "/notifs/" + subscriberId);
                switch (senId) {
                    case "inFlow_Changed":
                        sensor = "inFlow";
                        break;
                    case "outFlow_Changed":
                        sensor = "outFlow";
                        break;
                    case "temperature_Changed":
                        sensor = "temperature";
                        break;
                    case "pressure_Changed":
                        sensor = "pressure";
                        break;
                    case "level_Changed":
                        sensor = "level";
                        break;
                    case "waterContent_Changed":
                        sensor = "waterContent";
                        break;
                    case "particleCount_Changed":
                        sensor = "particleCount";
                        break;
                    case "filterClog_Changed":
                        sensor = "filterClog";
                        break;
                    case "systemStart":
                        sensor = "start";
                        break;
                    case "systemStop":
                        sensor = "stop";
                        break;
                    case "oil_Changed":
                        sensor = "oilChanged";
                        break;
                    case "filter_Changed":
                        sensor = "filterChanged";
                        break;
                    case "maintenance_Done":
                        sensor = "maintenanceDone";
                        break;
                    case "report":
                        sensor = "report";
                        break;
                }
                if (senId.substring(0, 2).equals("fm")) {
                    if ((ms = Registry.measuringStation.get(id)) != null) {
                        String senNo = senId.substring(senId.indexOf("_") + 1);
                        senNo = "_" + senNo.substring(0, senId.indexOf("_") - 1);
                        for (String fmKey : ms.getFmList().keySet()) {
                            String fmId = "";
                            fmId = fmKey.substring(fmKey.lastIndexOf("_"));
                            if (fmId.equals(senNo)) {
                                id = fmKey;
                                if (senId.substring(senId.lastIndexOf("_") - 6, senId.lastIndexOf("_")).equals("InFlow")) {
                                    sensor = "inFlow";
                                } else if (senId.substring(senId.lastIndexOf("_") - 7, senId.lastIndexOf("_")).equals("OutFlow")) {
                                    sensor = "outFlow";
                                }
                            }
                        }
                    }
                }
                subscriber = new EventSubscriberInputs(subscriberId, links, "eventNotification", senId, destUrl, clientdata, id, sensor);
                subscriber.reg();
                response = new ResponseEntity<>(subscriber, HttpStatus.ACCEPTED);
            } catch (IOException e) {
                e.printStackTrace();
                LOG.log(Level.SEVERE, "Exception " + e);
                response = new ResponseEntity<>(subscriber, HttpStatus.FORBIDDEN);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        return response;
    }

    /**
     * Perform the Service based operation and Post the JSON representation of
     * particular sensor services of particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @param json
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}", method = RequestMethod.POST)
    public ResponseEntity<Object> postSenIdServices(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId, @RequestBody String json) throws IOException {
        ResponseEntity<Object> response;
        RTUResponse serviceResponse = null;
        ServiceDetails service = null;
        String reply = "";
        String basePath = myUrl + "/" + lsId + "/RTU";
        lu = null;
        ms = null;
        Map<Object, Object> map = new HashMap<>();
        HashMap<String, String> nestedMapMeta = new HashMap<>();
        Map<String, Object> payloadMap = new HashMap<>();
        HashMap<String, Object> sensorValue = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(json,
                new TypeReference<HashMap>() {
                });
        payloadMap = (Map<String, Object>) map.get("payload");
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if (((lu = Registry.lubricationUnit.get(id)) != null) || ((hmi = Registry.hmi.get(id))) != null) {
                switch (senId) {
                    case "start":
                        try {
                            nestedMapMeta.clear();
                            nestedMapMeta.put("contextId", "TBD");
                            nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                            nestedMapMeta.put("deviceId", lu.getId());
                            nestedMapMeta.put("deviceType", "LubricationUnit");
                            if ((service = Registry.start.get(id)) == null) {
                                service = new ServiceDetails(senId, "process", nestedMapMeta, 1, lu.getSimTime(), id);
                                service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                                service.reg(senId);
                            } else {
                                service.setCount(service.getCount() + 1);
                                service.setLastRun(lu.getSimTime());
                                service.reg(senId);
                            }
                            lu.setMode(SimValue.Mode.SV_MODE_RUNNING);
                            serviceResponse = new RTUResponse("202", "Accepted", "Services is Accepted", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.ACCEPTED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.log(Level.SEVERE, "Exception " + e);
                            serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
                        }
                        break;
                    case "stop":
                        try {
                            nestedMapMeta.clear();
                            nestedMapMeta.put("contextId", "TBD");
                            nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                            nestedMapMeta.put("deviceId", lu.getId());
                            nestedMapMeta.put("deviceType", "LubricationUnit");
                            if ((service = Registry.stop.get(id)) == null) {
                                service = new ServiceDetails(senId, "process", nestedMapMeta, 1, lu.getSimTime(), id);
                                service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                                service.reg(senId);
                            } else {
                                service.setCount(service.getCount() + 1);
                                service.setLastRun(lu.getSimTime());
                                service.reg(senId);
                            }
                            lu.setMode(SimValue.Mode.SV_MODE_SHUTDOWN);
                            serviceResponse = new RTUResponse("202", "Accepted", "Services is Accepted", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.ACCEPTED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.log(Level.SEVERE, "Exception " + e);
                            serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
                        }
                        break;
                    case "oilChange":
                        try {
                            nestedMapMeta.clear();
                            nestedMapMeta.put("contextId", "TBD");
                            nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                            nestedMapMeta.put("parentId", hmi.getParentId());
                            nestedMapMeta.put("parentType", "LubricationUnit");
                            nestedMapMeta.put("deviceId", hmi.getId());
                            nestedMapMeta.put("deviceType", "HMI");
                            if ((service = Registry.oilChange.get(id)) == null) {
                                service = new ServiceDetails(senId, "operation", nestedMapMeta, 1, Registry.lubricationUnit.get(hmi.getParentId()).getSimTime(), id);
                                service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                                service.reg(senId);
                            } else {
                                service.setCount(service.getCount() + 1);
                                service.setLastRun(lu.getSimTime());
                                service.reg(senId);
                            }
                            hmi.setOilChange(Boolean.parseBoolean(payloadMap.get("value").toString()));
                            serviceResponse = new RTUResponse("202", "Accepted", "Services is Accepted", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.ACCEPTED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.log(Level.SEVERE, "Exception " + e);
                            serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
                        }
                        break;
                    case "filterChange":
                        try {
                            nestedMapMeta.clear();
                            nestedMapMeta.put("contextId", "TBD");
                            nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                            nestedMapMeta.put("parentId", hmi.getParentId());
                            nestedMapMeta.put("parentType", "LubricationUnit");
                            nestedMapMeta.put("deviceId", hmi.getId());
                            nestedMapMeta.put("deviceType", "HMI");
                            if ((service = Registry.filterChange.get(id)) == null) {
                                service = new ServiceDetails(senId, "operation", nestedMapMeta, 1, Registry.lubricationUnit.get(hmi.getParentId()).getSimTime(), id);
                                service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                                service.reg(senId);
                            } else {
                                service.setCount(service.getCount() + 1);
                                service.setLastRun(lu.getSimTime());
                                service.reg(senId);
                            }
                            hmi.setFilterChange(Boolean.parseBoolean(payloadMap.get("value").toString()));
                            serviceResponse = new RTUResponse("202", "Accepted", "Services is Accepted", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.ACCEPTED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.log(Level.SEVERE, "Exception " + e);
                            serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
                        }
                        break;
                    case "message":
                        try {
                            nestedMapMeta.clear();
                            nestedMapMeta.put("contextId", "TBD");
                            nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                            nestedMapMeta.put("parentId", hmi.getParentId());
                            nestedMapMeta.put("parentType", "LubricationUnit");
                            nestedMapMeta.put("deviceId", hmi.getId());
                            nestedMapMeta.put("deviceType", "HMI");
                            if ((service = Registry.message.get(id)) == null) {
                                service = new ServiceDetails(senId, "operation", nestedMapMeta, 1, Registry.lubricationUnit.get(hmi.getParentId()).getSimTime(), id);
                                service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                                service.reg(senId);
                            } else {
                                service.setCount(service.getCount() + 1);
                                service.setLastRun(lu.getSimTime());
                                service.reg(senId);
                            }
                            hmi.setMessage(payloadMap.get("value").toString());
                            serviceResponse = new RTUResponse("202", "Accepted", "Services is Accepted", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.ACCEPTED);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LOG.log(Level.SEVERE, "Exception " + e);
                            serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                            response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
                        }
                        break;
                    case "level":
                        sensorValue.put("value", Double.toString(lu.getLevel()));
                        sensorValue.put("time", Long.toString(lu.getSimTime()));
                        sensorValue.put("type", "double");
                        nestedMapMeta.clear();
                        nestedMapMeta.put("contextId", "TBD");
                        nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                        nestedMapMeta.put("deviceId", lu.getId());
                        nestedMapMeta.put("deviceType", "LubricationUnit");
                        sensorValue.put("meta", nestedMapMeta);
                        response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                        break;
                    case "filterClog":
                        sensorValue.put("value", Boolean.toString(lu.getFilterClog()));
                        sensorValue.put("time", Long.toString(lu.getSimTime()));
                        sensorValue.put("type", "boolean");
                        nestedMapMeta.clear();
                        nestedMapMeta.put("contextId", "TBD");
                        nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                        nestedMapMeta.put("deviceId", lu.getId());
                        nestedMapMeta.put("deviceType", "LubricationUnit");
                        sensorValue.put("meta", nestedMapMeta);
                        response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                        break;
                    case "tankCapacity":
                        sensorValue.put("value", Double.toString(lu.getTankCapacity()));
                        sensorValue.put("time", Long.toString(lu.getSimTime()));
                        sensorValue.put("type", "double");
                        nestedMapMeta.clear();
                        nestedMapMeta.put("contextId", "TBD");
                        nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                        nestedMapMeta.put("deviceId", lu.getId());
                        nestedMapMeta.put("deviceType", "LubricationUnit");
                        sensorValue.put("meta", nestedMapMeta);
                        response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                        break;
                    case "filterCapacity":
                        sensorValue.put("value", Double.toString(lu.getFilterCapacity()));
                        sensorValue.put("time", Long.toString(lu.getSimTime()));
                        sensorValue.put("type", "double");
                        nestedMapMeta.clear();
                        nestedMapMeta.put("contextId", "TBD");
                        nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                        nestedMapMeta.put("deviceId", lu.getId());
                        nestedMapMeta.put("deviceType", "LubricationUnit");
                        sensorValue.put("meta", nestedMapMeta);
                        response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                        break;
                    case "maxOilFlow":
                        sensorValue.put("value", Double.toString(lu.getMaxOilFlow()));
                        sensorValue.put("time", Long.toString(lu.getSimTime()));
                        sensorValue.put("type", "double");
                        nestedMapMeta.clear();
                        nestedMapMeta.put("contextId", "TBD");
                        nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                        nestedMapMeta.put("deviceId", lu.getId());
                        nestedMapMeta.put("deviceType", "LubricationUnit");
                        sensorValue.put("meta", nestedMapMeta);
                        response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                        break;
                    case "oilAllocation":
                        sensorValue.put("value", Double.toString(lu.getOilAllocation()));
                        sensorValue.put("time", Long.toString(lu.getSimTime()));
                        sensorValue.put("type", "double");
                        nestedMapMeta.clear();
                        nestedMapMeta.put("contextId", "TBD");
                        nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                        nestedMapMeta.put("deviceId", lu.getId());
                        nestedMapMeta.put("deviceType", "LubricationUnit");
                        sensorValue.put("meta", nestedMapMeta);
                        response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                        break;
                    default:
                        serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                        response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
                        break;
                }
            } else if ((ms = Registry.measuringStation.get(id)) != null) {
                serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
            } else {
                serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
                response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            serviceResponse = new RTUResponse("404", "Not_found", "Services is not available", "");
            response = new ResponseEntity<>(serviceResponse, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Post method which registers the subscriber URL to the system for
     * notification while that particular event happens
     *
     * @param id
     * @param senId
     * @param json
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}/notifs", method = RequestMethod.POST)
    public static ResponseEntity<ServiceSubscriberInputs> postSenIdServicesNotifs(@PathVariable String id, @PathVariable String senId, @RequestBody String json, @PathVariable String lsId) {
        ResponseEntity<ServiceSubscriberInputs> response;
        String subscriberId = "";
        String clientdata = "";
        String destUrl = "";
        String sensor = "";
        String basePath = myUrl + "/" + lsId + "/RTU";
        HashMap<String, String> links = new HashMap<>();
        ServiceSubscriberInputs subscriber = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if (((lu = Registry.lubricationUnit.get(id)) != null) || ((hmi = Registry.hmi.get(id))) != null) {
                try {
                    Map<String, String> map = new HashMap<>();
                    ObjectMapper mapper = new ObjectMapper();
                    map = mapper.readValue(json,
                            new TypeReference<HashMap<String, String>>() {
                            });
                    subscriberId = Registry.generateSubscriberID();
                    if ((clientdata = map.get("clientData")) == null) {
                        clientdata = "";
                    } else {
                        clientdata = map.get("clientData");
                    }
                    if ((destUrl = map.get("destUrl")) == null) {
                        destUrl = "";
                    } else {
                        destUrl = map.get("destUrl");
                    }
                    links.put("self", basePath + "/" + id + "/services/" + senId + "/notifs/" + subscriberId);
                    subscriber = new ServiceSubscriberInputs(subscriberId, links, "serviceNotification", senId, destUrl, clientdata, id, senId);
                    subscriber.reg();
                    response = new ResponseEntity<>(subscriber, HttpStatus.ACCEPTED);
                } catch (IOException e) {
                    e.printStackTrace();
                    LOG.log(Level.SEVERE, "Exception " + e);
                    response = new ResponseEntity<>(subscriber, HttpStatus.BAD_REQUEST);
                }
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //-------------------------------------GET----------------------------------
    /**
     * Getting the JSON representation of complete RTU
     *
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getRTU(@PathVariable String lsId
    ) {
        ls = null;
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            BasicTree mainRTU = new BasicTree("OilLubricationSystemSimulator", "node", basePath);
            mainRTU.createLinkWithoutParent(basePath, "");
            mainRTU.createMainChildren(lsId);
            response = new ResponseEntity<>(mainRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of complete RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/api/swagger.json", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getApiDocs(@PathVariable String id, @PathVariable String lsId
    ) throws UnsupportedEncodingException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        ResponseEntity<JSONObject> response;
        String basePath = lsId + "/RTU";
        String json = "";
        HashMap<String, Object> meta = new HashMap<>();
        HashMap<String, String> metaParent = new HashMap<>();
        ArrayList<Object> children = new ArrayList<>();
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream stream = classLoader.getResourceAsStream("min_swagger.json");
            JSONObject jsonObject = (JSONObject) parser.parse(
                    new InputStreamReader(stream, "UTF-8"));
            jsonObject.put("host", HostPortandConfig.ROOT_ADDRESS + ":" + HostPortandConfig.ROOT_PORT);
            jsonObject.put("basePath", basePath + '/' + id);
            JSONObject infoObject = (JSONObject) jsonObject.get("info");
            if ((lu = Registry.lubricationUnit.get(id)) != null) {
                meta.put("deviceType", "LubricationUnit");
                meta.put("deviceId", id);
                meta.put("parentType", "LubricationSystem");
                meta.put("parentId", lsId);
                for (MeasuringStation msValues : lu.getMsList().values()) {
                    HashMap<String, String> metaChild = new HashMap<>();
                    metaChild.put("id", msValues.getId());
                    metaChild.put("type", "MeasuringStation");
                    children.add(metaChild);
                }
                HashMap<String, String> metaChildHMI = new HashMap<>();
                metaChildHMI.put("id", lu.getHmi().getId());
                metaChildHMI.put("type", "HMI");
                children.add(metaChildHMI);
                meta.put("children", children);
                infoObject.put("x-meta", meta);
            } else if ((ms = Registry.measuringStation.get(id)) != null) {
                meta.put("deviceType", "MeasuringStation");
                meta.put("deviceId", id);
                meta.put("parentType", "LubricationUnit");
                meta.put("parentId", ms.getParentId());
                for (FlowMeter fmValues : ms.getFmList().values()) {
                    HashMap<String, String> metaChild = new HashMap<>();
                    metaChild.put("id", fmValues.getId());
                    metaChild.put("type", "FlowMeter");
                    children.add(metaChild);
                }
                meta.put("children", children);
                infoObject.put("x-meta", meta);
            } else if ((hmi = Registry.hmi.get(id)) != null) {
                meta.put("deviceType", "HMI");
                meta.put("deviceId", id);
                meta.put("parentType", "LubricationUnit");
                meta.put("parentId", hmi.getParentId());
                meta.put("children", children);
                infoObject.put("x-meta", meta);
            }
//            String swagger = getStringFromInputStream(stream);
//            swagger = swagger.replaceAll("___host___", HostPortandConfig.ROOT_ADDRESS + ":" + HostPortandConfig.ROOT_PORT);
//            swagger = swagger.replaceAll("___path___", basePath + '/' + id);
//            swagger = swagger.replaceAll("___meta___", meta.toString());
//            response = new ResponseEntity<>(swagger, HttpStatus.OK);
            response = new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    /**
     * Getting the JSON representation of complete RTU
     *
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/info", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getRTUInfo(@PathVariable String lsId) {
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree mainRTU = new BasicTree("OilLubricationSystemSimulator", "node", basePath);
        ResponseEntity<BasicTree> response;
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            mainRTU.createLinkWithoutParent(basePath, "");
            mainRTU.createMainChildren(lsId);
            response = new ResponseEntity<>(mainRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular component in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getRTUById(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "eScopRTU", basePath + "/" + id);
            elementRTU.createLinkWithParent(basePath, id);
            elementRTU.createElementsChildren();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular component information in
     * RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/info", method = RequestMethod.GET)
    public ResponseEntity<BasicTree> getRTUByIdInfo(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "eScopRTU", basePath + "/" + id);
            elementRTU.createLinkWithParent(basePath, id);
            elementRTU.createElementsChildren();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular component in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/notifs", method = RequestMethod.GET)
    public static ResponseEntity<BasicTree> getIdNotifs(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "notifs", basePath + "/" + id + "/notifs");
            elementRTU.createLinkWithParent(basePath + "/" + id, "notifs");
            elementRTU.createElementsNotifs();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular component information in
     * RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/notifs/info", method = RequestMethod.GET)
    public static ResponseEntity<BasicTree> getIdNotifsInfo(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "notifs", basePath + "/" + id + "/notifs");
            elementRTU.createLinkWithParent(basePath + "/" + id, "notifs");
            elementRTU.createElementsNotifs();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of sensor datas of particular component
     * in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/data", method = RequestMethod.GET)
    public static ResponseEntity<BasicTree> getIdDatas(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "data", basePath + "/" + id + "/data");
            elementRTU.createLinkWithParent(basePath + "/" + id, "data");
            elementRTU.createElementsData();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of sensor datas of particular component
     * information in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/data/info", method = RequestMethod.GET)
    public static ResponseEntity<BasicTree> getIdDatasInfo(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "data", basePath + "/" + id + "/data");
            elementRTU.createLinkWithParent(basePath + "/" + id, "data");
            elementRTU.createElementsData();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of sensor events of particular component
     * in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events", method = RequestMethod.GET)
    public ResponseEntity<EventServiceTree> getIdEvents(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        EventServiceTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            ElementServiceTags elementTags;
            if (lu != null) {
                elementTags = new ElementServiceTags(id, "LubricationUnit");
            } else if (ms != null) {
                elementTags = new ElementServiceTags(id, "MeasuringStation", id.substring(id.lastIndexOf("MS")));
            } else {
                elementTags = new ElementServiceTags(id, "HMI");
            }
            elementRTU = new EventServiceTree(id, "events", basePath + "/" + id + "/events", elementTags);
            elementRTU.createLinkWithParent(basePath + "/" + id, "events");
            elementRTU.createElementsEvents();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of sensor events of particular component
     * information in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/info", method = RequestMethod.GET)
    public static ResponseEntity<EventServiceTree> getIdEventsInfo(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        EventServiceTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            ElementServiceTags elementTags;
            if (lu != null) {
                elementTags = new ElementServiceTags(id, "LubricationUnit");
            } else if (ms != null) {
                elementTags = new ElementServiceTags(id, "MeasuringStation", id.substring(id.lastIndexOf("MS")));
            } else {
                elementTags = new ElementServiceTags(id, "HMI");
            }
            elementRTU = new EventServiceTree(id, "events", basePath + "/" + id + "/events", elementTags);
            elementRTU.createLinkWithParent(basePath + "/" + id, "events");
            elementRTU.createElementsEvents();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of sensor events of particular component
     * in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services", method = RequestMethod.GET)
    public static ResponseEntity<EventServiceTree> getIdServices(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        EventServiceTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            ElementServiceTags serviceTags;
            if (lu != null) {
                serviceTags = new ElementServiceTags(id, "LubricationUnit");
            } else if (ms != null) {
                serviceTags = new ElementServiceTags(id, "MeasuringStation", id.substring(id.lastIndexOf("MS")));
            } else {
                serviceTags = new ElementServiceTags(id, "HMI");
            }
            elementRTU = new EventServiceTree(id, "services", basePath + "/" + id + "/services", serviceTags);
            elementRTU.createLinkWithParent(basePath + "/" + id, "services");
            elementRTU.createElementsServices();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of sensor events of particular component
     * information in RTU
     *
     * @param id
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/info", method = RequestMethod.GET)
    public static ResponseEntity<EventServiceTree> getIdServicesInfo(@PathVariable String id, @PathVariable String lsId
    ) {
        ResponseEntity<EventServiceTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        EventServiceTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            ElementServiceTags serviceTags;
            if (lu != null) {
                serviceTags = new ElementServiceTags(id, "LubricationUnit");
            } else if (ms != null) {
                serviceTags = new ElementServiceTags(id, "MeasuringStation", id.substring(id.lastIndexOf("MS")));
            } else {
                serviceTags = new ElementServiceTags(id, "HMI");
            }
            elementRTU = new EventServiceTree(id, "services", basePath + "/" + id + "/services", serviceTags);
            elementRTU.createLinkWithParent(basePath + "/" + id, "services");
            elementRTU.createElementsServices();
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor datas of particular
     * component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/data/{senId}", method = RequestMethod.GET)
    public static ResponseEntity<HashMap<String, Object>> getSenIdDatas(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<HashMap<String, Object>> response;
        HashMap<String, Object> sensorValue = new HashMap<>();
        sensorValue = sensorData(id, senId);
        if (!sensorValue.isEmpty()) {
            response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(sensorValue, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor datas info of
     * particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/data/{senId}/info", method = RequestMethod.GET)
    public static ResponseEntity<SensorInformation> getSenIdDatasInfo(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<SensorInformation> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        HashMap<String, Object> sensorValue = new HashMap<>();
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            LinkWithoutParent link = new LinkWithoutParent(basePath + "/" + id + "/data/" + senId, basePath + "/" + id + "/data/" + senId + "/info");
            sensorValue = sensorData(id, senId);
            SensorInformation sensor = new SensorInformation(id, link, "input", "string", sensorValue);
            if (!sensorValue.isEmpty()) {
                response = new ResponseEntity<>(sensor, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(sensor, HttpStatus.BAD_REQUEST);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor event of particular
     * component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/{senId}", method = RequestMethod.GET)
    public static ResponseEntity<EventInfo> getSenIdEvents(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<EventInfo> response;
        Payload payload = null;
        HashMap<String, Object> meta = null;
        long timeStamp = 0;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            timeStamp = sensorEventTimeStamp(id, senId);
            payload = sensorEventPayload(id, senId);
            meta = sensorEventTags(id, senId);
            if (payload != null) {
                EventInfo eventPayload = new EventInfo(senId, id, payload, meta);
                response = new ResponseEntity<>(eventPayload, HttpStatus.OK);
            } else {
                EventInfo eventPayload = new EventInfo(senId, id, payload, meta);
                response = new ResponseEntity<>(eventPayload, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor event info of
     * particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/{senId}/info", method = RequestMethod.GET)
    public static ResponseEntity<EventInfo> getSenIdEventsInfo(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<EventInfo> response;
        Payload payload = null;
        HashMap<String, Object> meta = new HashMap<>();
        long timeStamp = 0;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            timeStamp = sensorEventTimeStamp(id, senId);
            payload = sensorEventPayload(id, senId);
            meta = sensorEventTags(id, senId);
            if (payload != null) {
                EventInfo eventPayload = new EventInfo(senId, id, payload, meta);
                response = new ResponseEntity<>(eventPayload, HttpStatus.OK);
            } else {
                EventInfo eventPayload = new EventInfo(senId, id, payload, meta);
                response = new ResponseEntity<>(eventPayload, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor event notification
     * list of particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/{senId}/notifs", method = RequestMethod.GET)
    public static ResponseEntity<BasicTree> getSenIdEventsNotifs(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "notifs", basePath + "/" + id + "/notifs");
            elementRTU.createLinkWithoutParent(basePath + "/" + id, "notifs");
            elementRTU.createElementsIDNotifs(senId);
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor services of
     * particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}", method = RequestMethod.GET)
    public static ResponseEntity<Object> getSenIdServices(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<Object> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        ServiceDetails service = null;
        HashMap<String, Object> sensorValue = new HashMap<>();
        HashMap<String, String> nestedMapMeta = new HashMap<>();
        int count = 0;
        long lastRun = 0;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            switch (senId) {
                case "start":
                    if (Registry.start.get(id) != null) {
                        count = Registry.start.get(id).getCount();
                        lastRun = Registry.start.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    service = new ServiceDetails(senId, "process", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "stop":
                    if (Registry.stop.get(id) != null) {
                        count = Registry.stop.get(id).getCount();
                        lastRun = Registry.stop.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/service");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    service = new ServiceDetails(senId, "process", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "filterChange":
                    if (Registry.filterChange.get(id) != null) {
                        count = Registry.filterChange.get(id).getCount();
                        lastRun = Registry.filterChange.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("parentId", hmi.getParentId());
                    nestedMapMeta.put("parentType", "LubricationUnit");
                    nestedMapMeta.put("deviceId", hmi.getId());
                    nestedMapMeta.put("deviceType", "HMI");
                    service = new ServiceDetails(senId, "operation", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "oilChange":
                    if (Registry.oilChange.get(id) != null) {
                        count = Registry.oilChange.get(id).getCount();
                        lastRun = Registry.oilChange.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("parentId", hmi.getParentId());
                    nestedMapMeta.put("parentType", "LubricationUnit");
                    nestedMapMeta.put("deviceId", hmi.getId());
                    nestedMapMeta.put("deviceType", "HMI");
                    service = new ServiceDetails(senId, "operation", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "message":
                    if (Registry.message.get(id) != null) {
                        count = Registry.message.get(id).getCount();
                        lastRun = Registry.message.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("parentId", hmi.getParentId());
                    nestedMapMeta.put("parentType", "LubricationUnit");
                    nestedMapMeta.put("deviceId", hmi.getId());
                    nestedMapMeta.put("deviceType", "HMI");
                    service = new ServiceDetails(senId, "operation", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "level":
                    sensorValue.put("value", Double.toString(lu.getLevel()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "filterClog":
                    sensorValue.put("value", Boolean.toString(lu.getFilterClog()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "boolean");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "tankCapacity":
                    sensorValue.put("value", Double.toString(lu.getTankCapacity()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "filterCapacity":
                    sensorValue.put("value", Double.toString(lu.getFilterCapacity()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "maxOilFlow":
                    sensorValue.put("value", Double.toString(lu.getMaxOilFlow()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "oilAllocation":
                    sensorValue.put("value", Double.toString(lu.getOilAllocation()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                default:
                    response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
                    break;
            }
        } else {
            response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor services info of
     * particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}/info", method = RequestMethod.GET)
    public static ResponseEntity<Object> getSenIdServicesInfo(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<Object> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        ServiceDetails service = null;
        HashMap<String, Object> sensorValue = new HashMap<>();
        HashMap<String, String> nestedMapMeta = new HashMap<>();
        int count = 0;
        long lastRun = 0;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            switch (senId) {
                case "start":
                    if (Registry.start.get(id) != null) {
                        count = Registry.start.get(id).getCount();
                        lastRun = Registry.start.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    service = new ServiceDetails(senId, "process", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "stop":
                    if (Registry.stop.get(id) != null) {
                        count = Registry.stop.get(id).getCount();
                        lastRun = Registry.stop.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/service");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    service = new ServiceDetails(senId, "process", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "filterChange":
                    if (Registry.filterChange.get(id) != null) {
                        count = Registry.filterChange.get(id).getCount();
                        lastRun = Registry.filterChange.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("parentId", hmi.getParentId());
                    nestedMapMeta.put("parentType", "LubricationUnit");
                    nestedMapMeta.put("deviceId", hmi.getId());
                    nestedMapMeta.put("deviceType", "HMI");
                    service = new ServiceDetails(senId, "operation", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "oilChange":
                    if (Registry.oilChange.get(id) != null) {
                        count = Registry.oilChange.get(id).getCount();
                        lastRun = Registry.oilChange.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("parentId", hmi.getParentId());
                    nestedMapMeta.put("parentType", "LubricationUnit");
                    nestedMapMeta.put("deviceId", hmi.getId());
                    nestedMapMeta.put("deviceType", "HMI");
                    service = new ServiceDetails(senId, "operation", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "message":
                    if (Registry.message.get(id) != null) {
                        count = Registry.message.get(id).getCount();
                        lastRun = Registry.message.get(id).getLastRun();
                    }
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("parentId", hmi.getParentId());
                    nestedMapMeta.put("parentType", "LubricationUnit");
                    nestedMapMeta.put("deviceId", hmi.getId());
                    nestedMapMeta.put("deviceType", "HMI");
                    service = new ServiceDetails(senId, "operation", nestedMapMeta, count, lastRun, id);
                    service.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
                    service.reg(senId);
                    response = new ResponseEntity<>(service, HttpStatus.OK);
                    break;
                case "level":
                    sensorValue.put("value", Double.toString(lu.getLevel()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "filterClog":
                    sensorValue.put("value", Boolean.toString(lu.getFilterClog()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "boolean");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "tankCapacity":
                    sensorValue.put("value", Double.toString(lu.getTankCapacity()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "filterCapacity":
                    sensorValue.put("value", Double.toString(lu.getFilterCapacity()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "maxOilFlow":
                    sensorValue.put("value", Double.toString(lu.getMaxOilFlow()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                case "oilAllocation":
                    sensorValue.put("value", Double.toString(lu.getOilAllocation()));
                    sensorValue.put("time", Long.toString(lu.getSimTime()));
                    sensorValue.put("type", "double");
                    nestedMapMeta.clear();
                    nestedMapMeta.put("contextId", "TBD");
                    nestedMapMeta.put("messageFormat", myUrl + "/message/event");
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    response = new ResponseEntity<>(sensorValue, HttpStatus.OK);
                    break;
                default:
                    response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
                    break;
            }
        } else {
            response = new ResponseEntity<>(service, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor services
     * notification list of particular component in RTU
     *
     * @param id
     * @param senId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}/notifs", method = RequestMethod.GET)
    public static ResponseEntity<BasicTree> getSenIdServicesNotifs(@PathVariable String id, @PathVariable String senId, @PathVariable String lsId
    ) {
        ResponseEntity<BasicTree> response;
        String basePath = myUrl + "/" + lsId + "/RTU";
        BasicTree elementRTU = null;
        ls = null;
        lu = null;
        ms = null;
        if (((ls = Registry.lubricationSystem.get(lsId)) != null) && (((lu = Registry.lubricationUnit.get(id)) != null) || ((ms = Registry.measuringStation.get(id)) != null) || ((hmi = Registry.hmi.get(id)) != null))) {
            elementRTU = new BasicTree(id, "process", basePath + "/" + id + "/services");
            elementRTU.createLinkWithNotifs(basePath + "/" + id + "/services", senId);
            elementRTU.createElementsIDNotifs(senId);
            response = new ResponseEntity<>(elementRTU, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(elementRTU, HttpStatus.NOT_FOUND);
        }
        return response;
    }
    
    /**
     * Getting the JSON representation of particular sensor event notification
     * list of particular component in RTU
     *
     * @param notId
     * @param id
     * @param senId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/{senId}/notifs/{notId}", method = RequestMethod.GET)
    public static ResponseEntity<EventSubscriberInputs> getSenIdEventsNotifs(@PathVariable String notId
    ) {
       ResponseEntity<EventSubscriberInputs> response;
        EventSubscriberInputs subscriber = null;
        if ((subscriber = Registry.eventSubscribers.get(notId)) != null) {
            response = new ResponseEntity<>(subscriber, HttpStatus.ACCEPTED);
        } else {
            response = new ResponseEntity<>(subscriber, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor event notification
     * list of particular component in RTU
     *
     * @param notId
     * @param id
     * @param senId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}/notifs/{notId}", method = RequestMethod.GET)
    public static ResponseEntity<ServiceSubscriberInputs> getSenIdServicesNotifs(@PathVariable String notId
    ) {
        ResponseEntity<ServiceSubscriberInputs> response;
        ServiceSubscriberInputs subscriber = null;
        RTUResponse notifier = null;
        if ((subscriber = Registry.serviceSubscribers.get(notId)) != null) {
            response = new ResponseEntity<>(subscriber, HttpStatus.ACCEPTED);
        } else {
            response = new ResponseEntity<>(subscriber, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //------------------------------Delete--------------------------------------
    /**
     * Getting the JSON representation of particular sensor event notification
     * list of particular component in RTU
     *
     * @param notId
     * @param id
     * @param senId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/events/{senId}/notifs/{notId}", method = RequestMethod.DELETE)
    public static ResponseEntity<RTUResponse> deleteSenIdEventsNotifs(@PathVariable String notId
    ) {
        ResponseEntity<RTUResponse> response;
        EventSubscriberInputs subscriber = null;
        RTUResponse notifier = null;
        if ((subscriber = Registry.eventSubscribers.get(notId)) != null) {
            Registry.eventSubscribers.remove(notId);
            System.out.println(Registry.eventSubscribers.get(notId));
            notifier = new RTUResponse("202", "accepted", "Services is Accepted", "");
            response = new ResponseEntity<>(notifier, HttpStatus.ACCEPTED);
        } else {
            notifier = new RTUResponse("404", "Not_found", "Services is not available", "");
            response = new ResponseEntity<>(notifier, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of particular sensor event notification
     * list of particular component in RTU
     *
     * @param notId
     * @param id
     * @param senId
     * @return
     */
    @RequestMapping(value = "/{lsId}/RTU/{id}/services/{senId}/notifs/{notId}", method = RequestMethod.DELETE)
    public static ResponseEntity<RTUResponse> deleteSenIdServicesNotifs(@PathVariable String notId
    ) {
        ResponseEntity<RTUResponse> response;
        ServiceSubscriberInputs subscriber = null;
        RTUResponse notifier = null;
        if ((subscriber = Registry.serviceSubscribers.get(notId)) != null) {
            Registry.serviceSubscribers.remove(notId);
            notifier = new RTUResponse("202", "accepted", "Services is Accepted", "");
            response = new ResponseEntity<>(notifier, HttpStatus.ACCEPTED);
        } else {
            notifier = new RTUResponse("404", "Not_found", "Services is not available", "");
            response = new ResponseEntity<>(notifier, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //--------------------------------------------------------------------------
    public static HashMap<String, Object> sensorData(String id, String senId) {
        HashMap<String, Object> sensorValue = new HashMap<>();
        HashMap<String, String> nestedMap = new HashMap<>();
        HashMap<String, String> nestedMapMeta = new HashMap<>();
        lu = null;
        ms = null;
        hmi = null;
        if ((lu = Registry.lubricationUnit.get(id)) != null) {
            switch (senId) {
                case "tankCapacity":
                    nestedMap.clear();
                    nestedMap.put("value", Double.toString(lu.getTankCapacity()));
                    nestedMap.put("time", Long.toString(lu.getSimTime()));
                    nestedMap.put("type", "double");
                    sensorValue.put("payload", nestedMap);
                    nestedMapMeta.clear();
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    break;
                case "filterCapacity":
                    nestedMap.clear();
                    nestedMap.put("value", Double.toString(lu.getFilterCapacity()));
                    nestedMap.put("time", Long.toString(lu.getSimTime()));
                    nestedMap.put("type", "double");
                    sensorValue.put("payload", nestedMap);
                    nestedMapMeta.clear();
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    break;
                case "maxOilFlow":
                    nestedMap.clear();
                    nestedMap.put("value", Double.toString(lu.getMaxOilFlow()));
                    nestedMap.put("time", Long.toString(lu.getSimTime()));
                    nestedMap.put("type", "double");
                    sensorValue.put("payload", nestedMap);
                    nestedMapMeta.clear();
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    break;
                case "oilAllocation":
                    nestedMap.clear();
                    nestedMap.put("value", Double.toString(lu.getOilAllocation()));
                    nestedMap.put("time", Long.toString(lu.getSimTime()));
                    nestedMap.put("type", "double");
                    sensorValue.put("payload", nestedMap);
                    nestedMapMeta.clear();
                    nestedMapMeta.put("deviceId", lu.getId());
                    nestedMapMeta.put("deviceType", "LubricationUnit");
                    sensorValue.put("meta", nestedMapMeta);
                    break;
                case "inFlow":
                    sensorValue.put("value", Double.toString(lu.getInFlow()));
                    break;
                case "outFlow":
                    sensorValue.put("value", Double.toString(lu.getOutFlow()));
                    break;
                case "level":
                    sensorValue.put("value", Double.toString(lu.getLevel()));
                    break;
                case "temperature":
                    sensorValue.put("value", Double.toString(lu.getTemperature()));
                    break;
                case "waterContent":
                    sensorValue.put("value", Double.toString(lu.getWaterContent()));
                    break;
                case "particleCount":
                    sensorValue.put("value", lu.getParticleCount());
                    break;
                case "message":
                    sensorValue.put("value", lu.getMessage());
                    break;
            }
            sensorValue.put("time", Long.toString(lu.getSimTime()));
        } else if ((ms = Registry.measuringStation.get(id)) != null) {
            switch (senId) {
                case "inFlow":
                    sensorValue.put("value", Double.toString(ms.getInFlow()));
                    break;
                case "outFlow":
                    sensorValue.put("value", Double.toString(ms.getOutFlow()));
                    break;
                case "pressure":
                    sensorValue.put("value", Double.toString(ms.getPressure()));
                    break;
                case "temperature":
                    sensorValue.put("value", Double.toString(ms.getTemperature()));
                    break;
            }
            if (senId.substring(0, 2).equals("fm")) {
                String senNo = senId.substring(senId.indexOf("_") + 1);
                senNo = "_" + senNo.substring(0, senId.indexOf("_") - 1);
                for (String fmKey : ms.getFmList().keySet()) {
                    String fmId = "";
                    fmId = fmKey.substring(fmKey.lastIndexOf("_"));
                    if (fmId.equals(senNo)) {
                        fm = Registry.flowMeter.get(fmKey);
                        if (senId.substring(senId.lastIndexOf("_") + 1).equals("InFlow")) {
                            sensorValue.put("value", Double.toString(fm.getInFlow()));
                        } else if (senId.substring(senId.lastIndexOf("_") + 1).equals("OutFlow")) {
                            sensorValue.put("value", Double.toString(fm.getOutFlow()));
                        }
                    }
                }
            } else if ((hmi = Registry.hmi.get(id)) != null) {
                switch (senId) {
                    case "oil_Changed":
                        sensorValue.put("value", Boolean.toString(hmi.isOilChanged()));
                        break;
                    case "filter_Changed":
                        sensorValue.put("value", Boolean.toString(hmi.isFilterChanged()));
                        break;
                    case "maintenance_Done":
                        sensorValue.put("value", Boolean.toString(hmi.isMaintenanceDone()));
                        break;
                    case "report":
                        sensorValue.put("value", hmi.getMessage());
                        break;
                }
            }
            sensorValue.put("time", Long.toString(ms.getSimTime()));
        } else {
        }
        return sensorValue;
    }

    public static Payload sensorEventPayload(String id, String senId) {
        Payload payLoad = null;
        lu = null;
        ms = null;
        hmi = null;
        boolean state = false;
        if ((lu = Registry.lubricationUnit.get(id)) != null) {
            switch (senId) {
                case "inFlow_Changed":
                    payLoad = new Payload(Double.toString(lu.getInFlow()), "double", "");
                    break;
                case "outFlow_Changed":
                    payLoad = new Payload(Double.toString(lu.getOutFlow()), "double", lu.getLastEmittedOutFlowState());
                    break;
                case "level_Changed":
                    payLoad = new Payload(Double.toString(lu.getLevel()), "double", lu.getLastEmittedLevelState());
                    break;
                case "temperature_Changed":
                    payLoad = new Payload(Double.toString(lu.getTemperature()), "double", lu.getLastEmittedTemperatureState());
                    break;
                case "waterContent_Changed":
                    payLoad = new Payload(Double.toString(lu.getWaterContent()), "double", "");
                    break;
                case "particleCount_Changed":
                    payLoad = new Payload(lu.getParticleCount(), "string", "");
                    break;
                case "filterClog_Changed":
                    payLoad = new Payload(Boolean.toString(lu.getFilterClog()), "boolean", "");
                    break;
                case "systemStart":
                    if (lu.getMode() == SimValue.Mode.SV_MODE_RUNNING) {
                        state = true;
                        lu.setLastEmittedStart(state);
                    }
                    payLoad = new Payload(Boolean.toString(state), "boolean", "");
                    break;
                case "systemStop":
                    if (lu.getMode() == SimValue.Mode.SV_MODE_SHUTDOWN) {
                        state = true;
                        lu.setLastEmittedStop(state);
                    }
                    payLoad = new Payload(Boolean.toString(state), "boolean", "");
                    break;
            }
        } else if ((ms = Registry.measuringStation.get(id)) != null) {
            switch (senId) {
                case "inFlow_Changed":
                    payLoad = new Payload(Double.toString(ms.getInFlow()), "double", "");
                    break;
                case "outFlow_Changed":
                    payLoad = new Payload(Double.toString(ms.getOutFlow()), "double", ms.getLastEmittedOutFlowState());
                    break;
                case "pressure_Changed":
                    payLoad = new Payload(Double.toString(ms.getPressure()), "double", ms.getLastEmittedPressureState());
                    break;
                case "temperature_Changed":
                    payLoad = new Payload(Double.toString(ms.getTemperature()), "double", ms.getLastEmittedTemperatureState());
                    break;
            }
            if (senId.substring(0, 2).equals("fm")) {
                String senNo = senId.substring(senId.indexOf("_") + 1);
                senNo = "_" + senNo.substring(0, senId.indexOf("_") - 1);
                for (String fmKey : ms.getFmList().keySet()) {
                    String fmId = "";
                    fmId = fmKey.substring(fmKey.lastIndexOf("_"));
                    if (fmId.equals(senNo)) {
                        fm = Registry.flowMeter.get(fmKey);
                        if (senId.substring(senId.lastIndexOf("_") - 6, senId.lastIndexOf("_")).equals("InFlow")) {
                            payLoad = new Payload(Double.toString(fm.getInFlow()), "double", "");
                        } else if (senId.substring(senId.lastIndexOf("_") - 7, senId.lastIndexOf("_")).equals("OutFlow")) {
                            payLoad = new Payload(Double.toString(fm.getOutFlow()), "double", fm.getLastEmittedOutFlowState());
                        }
                    }
                }
            }
        } else if ((hmi = Registry.hmi.get(id)) != null) {
            switch (senId) {
                case "oil_Changed":
                    payLoad = new Payload(Boolean.toString(hmi.isOilChanged()), "boolean", Boolean.toString(hmi.isLastEmittedOilChanged()));
                    break;
                case "filter_Changed":
                    payLoad = new Payload(Boolean.toString(hmi.isFilterChanged()), "boolean", Boolean.toString(hmi.isLastEmittedFilterChanged()));
                    break;
                case "maintenance_Done":
                    payLoad = new Payload(Boolean.toString(hmi.isMaintenanceDone()), "boolean", Boolean.toString(hmi.isLastEmittedMaintenanceDone()));
                    break;
                case "report":
                    payLoad = new Payload(hmi.getMessage(), "string", "");
                    break;
            }
        }
        return payLoad;
    }

    public static HashMap<String, Object> sensorEventTags(String id, String senId) {
        HashMap<String, Object> meta = new HashMap<>();
        lu = null;
        ms = null;
        hmi = null;
        meta.put("contextId", "TBD");
        if ((lu = Registry.lubricationUnit.get(id)) != null) {
            meta.put("deviceId", lu.getId());
            meta.put("deviceType", "LubricationUnit");
            meta.put("messageFormat", myUrl + "/message/event");
        } else if ((ms = Registry.measuringStation.get(id)) != null) {
            if (senId.substring(0, 2).equals("fm")) {
                String senNo = senId.substring(senId.indexOf("_") + 1);
                senNo = "_" + senNo.substring(0, senId.indexOf("_") - 1);
                for (String fmKey : ms.getFmList().keySet()) {
                    String fmId = "";
                    fmId = fmKey.substring(fmKey.lastIndexOf("_"));
                    if (fmId.equals(senNo)) {
                        fm = Registry.flowMeter.get(fmKey);
                        meta.put("deviceId", fm.getId());
                        meta.put("deviceType", "FlowMeter");
                        meta.put("parentId", fm.getParentId());
                        meta.put("parentType", "MeasuringStation");
                        meta.put("messageFormat", myUrl + "/message/event");
                        meta.put("min", Double.toString(0.0));
                        meta.put("nom", Double.toString(fm.getNomFlow()));
                        meta.put("max", Double.toString(fm.getMaxFlow()));
                        meta.put("low", Double.toString(fm.getNomFlow() * 0.5));
                        meta.put("high", Double.toString(fm.getNomFlow() * 1.5));
                        meta.put("units", fm.getFlowUnit());
                    }
                }
            } else {
                meta.put("deviceId", ms.getId());
                meta.put("deviceType", "MeasuringStation");
                meta.put("parentId", ms.getParentId());
                meta.put("parentType", "LubricationUnit");
                meta.put("messageFormat", myUrl + "/message/event");
            }
        } else if ((hmi = Registry.hmi.get(id)) != null) {
            meta.put("deviceId", hmi.getId());
            meta.put("deviceType", "HMI");
            meta.put("parentId", hmi.getParentId());
            meta.put("parentType", "LubricationUnit");
            meta.put("messageFormat", myUrl + "/message/event");
        }
        return meta;
    }

    public static long sensorEventTimeStamp(String id, String senId) {
        long timeStamp = 0;
        lu = null;
        ms = null;
        if ((lu = Registry.lubricationUnit.get(id)) != null) {
            switch (senId) {
                case "inFlow_Changed":
                    timeStamp = lu.getLastEmittedInFlowTimeStamp();
                    break;
                case "outFlow_Changed":
                    timeStamp = lu.getLastEmittedOutFlowTimeStamp();
                    break;
                case "level_Changed":
                    timeStamp = lu.getLastEmittedLevelTimeStamp();
                    break;
                case "temperature_Changed":
                    timeStamp = lu.getLastEmittedTemperatureTimeStamp();
                    break;
                case "waterContent_Changed":
                    timeStamp = lu.getLastEmittedWaterContentTimeStamp();
                    break;
                case "particleCount_Changed":
                    timeStamp = lu.getLastEmittedParticleCountTimeStamp();
                    break;
                case "filterClog_Changed":
                    timeStamp = lu.getLastEmittedFilterClogTimeStamp();
                    break;
                case "systemStart":
                    timeStamp = lu.getLastEmittedModeTimeStamp();
                    break;
                case "systemStop":
                    timeStamp = lu.getLastEmittedModeTimeStamp();
                    break;
            }
        } else if ((ms = Registry.measuringStation.get(id)) != null) {
            switch (senId) {
                case "inFlow_Changed":
                    timeStamp = ms.getLastEmittedInFlowTimeStamp();
                    break;
                case "outFlow_Changed":
                    timeStamp = ms.getLastEmittedOutFlowTimeStamp();
                    break;
                case "pressure_Changed":
                    timeStamp = ms.getLastEmittedPressureTimeStamp();
                    break;
                case "temperature_Changed":
                    timeStamp = ms.getLastEmittedTemperatureTimeStamp();
                    break;
            }
            if (senId.substring(0, 2).equals("fm")) {
                String senNo = senId.substring(senId.indexOf("_") + 1);
                senNo = "_" + senNo.substring(0, senId.indexOf("_") - 1);
                for (String fmKey : ms.getFmList().keySet()) {
                    String fmId = "";
                    fmId = fmKey.substring(fmKey.lastIndexOf("_"));
                    if (fmId.equals(senNo)) {
                        fm = Registry.flowMeter.get(fmKey);
                        if (senId.substring(senId.lastIndexOf("_") - 6, senId.lastIndexOf("_")).equals("InFlow")) {
                            timeStamp = fm.getLastEmittedInFlowTimeStamp();
                        } else if (senId.substring(senId.lastIndexOf("_") - 6, senId.lastIndexOf("_")).equals("OutFlow")) {
                            timeStamp = fm.getLastEmittedOutFlowTimeStamp();
                        }
                    }
                }
            }
        } else {
        }
        return timeStamp;
    }

    private String getFile(String fileName) {
        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
