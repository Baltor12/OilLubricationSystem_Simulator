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
import fi.tut.escop.ols.jsonLinks.Links;
import fi.tut.escop.ols.extra.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Restful controller for service with respect to Flow Meter
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
public class FlowMeterController {

    String myUrl = HostPortandConfig.ROOT_URL;
    public static ResponseEntity<Response> RESPONSE;
    static LubricationSystem ls;
    static LubricationUnit lu;
    static MeasuringStation ms;
    static FlowMeter fm;
    private static final Logger LOG = Logger.getLogger(LubricationSystem.class.getName());

    //---------------------------------POST Methods For Json-----------------------------------------
    /**
     * Adding Flow meter in particular Measuring Station in a Lubrication Unit
     * through JSON
     *
     * @param fmRequest
     * @param lsId
     * @param luId
     * @param msId
     * @return
     * @throws InterruptedException
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/ms/{msId}/fm", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String, String>> createFlowMeter(@RequestBody FlowMeter fmRequest, @PathVariable String lsId, @PathVariable String luId, @PathVariable String msId) throws InterruptedException {
        String url = myUrl + "/lu/" + luId + "/ms/" + msId + "/fm/";
        HashMap<String, String> fmDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        ls = null;
        lu = null;
        ms = null;
        fm = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = ls.getLuList().get(luId)) != null) {
                if ((ms = lu.getMsList().get(msId)) != null) {
                    if (ms.getFmList().size() < 91) {
                        String id = ms.generateFMId();
                        Registry.flowMeter.put(id, fmRequest);
                        if (ms.addChild(id)) {
                            fm = Registry.flowMeter.get(id);
                            // just flow meter is saved seperately
                            fm.saveFlowMeter();
                            fmDetails.put("id", id);
                            fmDetails.put("response", url + id);
                            response = new ResponseEntity<>(fmDetails, HttpStatus.CREATED);
                        } else {
                            fmDetails.put("id", "");
                            fmDetails.put("response", "Creation of Flow Meter failed");
                            response = new ResponseEntity<>(fmDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        fmDetails.put("id", "");
                        fmDetails.put("response", "Maximum number of Flow Meters reached");
                        response = new ResponseEntity<>(fmDetails, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    LOG.log(Level.SEVERE, "Measuring Station does not exist", msId);
                    fmDetails.put("id", "");
                    fmDetails.put("response", "Measuring Station does not exist");
                    response = new ResponseEntity<>(fmDetails, HttpStatus.NOT_FOUND);
                }
            } else {
                LOG.log(Level.SEVERE, "Lubrication Unit does not exist", luId);
                fmDetails.put("id", "");
                fmDetails.put("response", "Lubrication Unit does not exist");
                response = new ResponseEntity<>(fmDetails, HttpStatus.NOT_FOUND);
            }
        } else {
            LOG.log(Level.SEVERE, "Lubrication System does not exist", lsId);
            fmDetails.put("id", "");
            fmDetails.put("response", "Lubrication Unit does not exist");
            response = new ResponseEntity<>(fmDetails, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Adding Flow meter in particular Measuring Station in a Lubrication Unit
     * through JSON
     *
     * @param fmRequest
     * @param luId
     * @param msId
     * @return
     * @throws InterruptedException
     */
    @RequestMapping(value = "/lu/{luId}/ms/{msId}/fm", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String, String>> createFlowMeterAlternate(@RequestBody FlowMeter fmRequest, @PathVariable String luId, @PathVariable String msId) throws InterruptedException {
        String url = myUrl + "/lu/" + luId + "/ms/" + msId + "/fm/";
        HashMap<String, String> fmDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        lu = null;
        ms = null;
        fm = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if ((ms = lu.getMsList().get(msId)) != null) {
                if (ms.getFmList().size() < 91) {
                    String id = ms.generateFMId();
                    Registry.flowMeter.put(id, fmRequest);
                    if (ms.addChild(id)) {
                        fm = Registry.flowMeter.get(id);
                        fm.setId(id);
                        // just flow meter is saved seperately
                        fm.saveFlowMeter();
                        fmDetails.put("id", id);
                        fmDetails.put("response", url + id);
                        response = new ResponseEntity<>(fmDetails, HttpStatus.CREATED);
                    } else {
                        fmDetails.put("id", "");
                        fmDetails.put("response", "Creation of Flow Meter failed");
                        response = new ResponseEntity<>(fmDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    fmDetails.put("id", "");
                    fmDetails.put("response", "Maximum number of Flow Meters reached");
                    response = new ResponseEntity<>(fmDetails, HttpStatus.BAD_REQUEST);
                }
            } else {
                LOG.log(Level.SEVERE, "Measuring Station does not exist", msId);
                fmDetails.put("id", "");
                fmDetails.put("response", "Measuring Station does not exist");
                response = new ResponseEntity<>(fmDetails, HttpStatus.NOT_FOUND);
            }
        } else {
            LOG.log(Level.SEVERE, "Lubrication Unit does not exist", luId);
            fmDetails.put("id", "");
            fmDetails.put("response", "Lubrication Unit does not exist");
            response = new ResponseEntity<>(fmDetails, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //---------------------------------GET Methods For Json-----------------------------------------
    /**
     * Getting Json of list of Flow meters
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/fm", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<FlowMeter>> getFlowMeterTotal() {
        ArrayList<FlowMeter> flowMeters = new ArrayList<>();
        for (String fmKey : Registry.flowMeter.keySet()) {
            fm = null;
            fm = Registry.flowMeter.get(fmKey);
            flowMeters.add(fm);
        }
        return new ResponseEntity<>(flowMeters, HttpStatus.OK);
    }

    /**
     * Getting Json of list of Flow Meters
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/fm/ids", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<String>> getFlowMeterIds() {
        ArrayList<String> flowMeterDetails = new ArrayList<>();
        for (String fmKey : Registry.flowMeter.keySet()) {
            flowMeterDetails.add(fmKey);
        }
        return new ResponseEntity<>(flowMeterDetails, HttpStatus.OK);
    }

    /**
     * Getting the JSON representation of a particular Flow Meter
     *
     * @param fmId
     * @return
     */
    @RequestMapping(value = "/fm/{fmId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getFlowMeterLinks(@PathVariable String fmId) {
        ResponseEntity<Links> response;
        Links fmLink = null;
        if ((fmLink = Registry.linkFM.get(fmId)) != null) {
            response = new ResponseEntity<>(fmLink, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @return
     */
    @RequestMapping(value = "/fm/{fmId}/info", method = RequestMethod.GET)
    public ResponseEntity<FlowMeter> getFlowMeterInfo(@PathVariable String fmId) {
        ResponseEntity<FlowMeter> response;
        fm = null;
        if ((fm = Registry.flowMeter.get(fmId)) != null) {
            response = new ResponseEntity<>(fm, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @param msId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}/fm/{fmId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getFlowMeterInMeasuringStationLinks(@PathVariable String fmId, @PathVariable String msId) {
        ResponseEntity<Links> response;
        Links fmLink = null;
        ms = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            if ((fmLink = Registry.linkFM.get(fmId)) != null) {
                response = new ResponseEntity<>(fmLink, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @param msId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}/fm/{fmId}/info", method = RequestMethod.GET)
    public ResponseEntity<FlowMeter> getFlowMeterInMeasuringStationInfo(@PathVariable String fmId, @PathVariable String msId) {
        ResponseEntity<FlowMeter> response;
        fm = null;
        ms = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            if ((fm = Registry.flowMeter.get(fmId)) != null) {
                response = new ResponseEntity<>(fm, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @param msId
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms/{msId}/fm/{fmId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getFlowMeterInLubricationUnitLinks(@PathVariable String fmId, @PathVariable String msId, @PathVariable String luId) {
        ResponseEntity<Links> response;
        Links fmLink = null;
        lu = null;
        ms = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if ((ms = Registry.measuringStation.get(msId)) != null) {
                if ((fmLink = Registry.linkFM.get(fmId)) != null) {
                    response = new ResponseEntity<>(fmLink, HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @param msId
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms/{msId}/fm/{fmId}/info", method = RequestMethod.GET)
    public ResponseEntity<FlowMeter> getFlowMeterInLubricationUnitInfo(@PathVariable String fmId, @PathVariable String msId, @PathVariable String luId) {
        ResponseEntity<FlowMeter> response;
        fm = null;
        lu = null;
        ms = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if ((ms = Registry.measuringStation.get(msId)) != null) {
                if ((fm = Registry.flowMeter.get(fmId)) != null) {
                    response = new ResponseEntity<>(fm, HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @param msId
     * @param luId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/ms/{msId}/fm/{fmId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getFlowMeterInLubricationSystemLinks(@PathVariable String fmId, @PathVariable String msId, @PathVariable String luId, @PathVariable String lsId) {
        ResponseEntity<Links> response;
        Links fmLink = null;
        ls = null;
        lu = null;
        ms = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = Registry.lubricationUnit.get(luId)) != null) {
                if ((ms = Registry.measuringStation.get(msId)) != null) {
                    if ((fmLink = Registry.linkFM.get(fmId)) != null) {
                        response = new ResponseEntity<>(fmLink, HttpStatus.OK);
                    } else {
                        response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                    }
                } else {
                    response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param fmId
     * @param msId
     * @param luId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/ms/{msId}/fm/{fmId}/info", method = RequestMethod.GET)
    public ResponseEntity<FlowMeter> getFlowMeterLinksInLubricationSystemInfo(@PathVariable String fmId, @PathVariable String msId, @PathVariable String luId, @PathVariable String lsId) {
        ResponseEntity<FlowMeter> response;
        fm = null;
        ls = null;
        lu = null;
        ms = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = Registry.lubricationUnit.get(luId)) != null) {
                if ((ms = Registry.measuringStation.get(msId)) != null) {
                    if ((fm = Registry.flowMeter.get(fmId)) != null) {
                        response = new ResponseEntity<>(fm, HttpStatus.OK);
                    } else {
                        response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                    }
                } else {
                    response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //---------------------------------DELETE Methods For Json-----------------------------------------
    /**
     * Deleting particular Flow Meter and its components
     *
     * @param fmId
     * @return
     */
    @RequestMapping(value = "/fm/{fmId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteFlowMeter(@PathVariable String fmId) {
        fm = null;
        if ((fm = Registry.flowMeter.get(fmId)) != null) {
            ms = null;
            if (fm.getIsSimulated()) {
                fm.interrupt();
            }
            Registry.flowMeter.remove(fmId);
            RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Flow Meter " + fmId), HttpStatus.ACCEPTED);
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Flow Meter does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting all Flow Meter in particular Measuring Station
     *
     * @param msId
     * @param fmId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}/fm/{fmId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteFlowMeterInMeasuringStation(@PathVariable String msId, @PathVariable String fmId) {
        ms = null;
        fm = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            if ((fm = ms.getFmList().get(fmId)) != null) {
                if (ms.deleteChild(fmId)) {
                    RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Flow Meter in Measuring Station " + msId), HttpStatus.ACCEPTED);
                } else {
                    RESPONSE = new ResponseEntity<>(new Response("Failure Deleting Flow Meter in Measuring Station " + msId), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Flow Meter does not exist"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Measuring Station does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting all Flow Meter in particular Measuring Station
     *
     * @param msId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}/fm/all", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteAllFlowMeterInMeasuringStation(@PathVariable String msId
    ) {
        ms = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            if (ms.deleteChildren()) {
                RESPONSE = new ResponseEntity<>(new Response("Deleted all Flow Meters in Measuring Station " + msId), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Deletion of all Flow Meters in Measuring Station " + msId + " failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Measuring Station " + msId + " does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }
}
