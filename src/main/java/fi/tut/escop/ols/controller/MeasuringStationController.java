/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import fi.tut.escop.ols.constants.HostPortandConfig;
import static fi.tut.escop.ols.controller.FlowMeterController.fm;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Restful controller for services with respect to MeasurementStation
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
public class MeasuringStationController {

    String myUrl = HostPortandConfig.ROOT_URL;
    public static ResponseEntity<Response> RESPONSE;
    static LubricationSystem ls;
    static LubricationUnit lu;
    static MeasuringStation ms;
    static FlowMeter fm;
    private static final Logger LOG = Logger.getLogger(LubricationSystem.class.getName());

    //---------------------------------POST Methods For Json-----------------------------------------
    /**
     * Adding Measuring Station in particular Lubrication Unit in a Lubrication
     * System through JSON
     *
     * @param msRequest
     * @param lsId
     * @param luId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/ms", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String, String>> createMeasuringStation(@RequestBody MeasuringStation msRequest, @PathVariable String lsId, @PathVariable String luId) {
        String url = myUrl + "/lu/" + luId + "/ms/";
        HashMap<String, String> msDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        ls = null;
        lu = null;
        ms = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = Registry.lubricationUnit.get(luId)) != null) {
                if (lu.getMsList().size() < 21) {
                    String id = lu.generateMSId();
                    Registry.measuringStation.put(id, msRequest);
                    if (lu.addChild(id)) {
                        ms = Registry.measuringStation.get(id);
                        ms.saveMeasuringStation();
                        msDetails.put("id", id);
                        msDetails.put("response", url + id);
                        response = new ResponseEntity<>(msDetails, HttpStatus.CREATED);
                    } else {
                        msDetails.put("id", "");
                        msDetails.put("response", "Creation of Measuring Station failed");
                        response = new ResponseEntity<>(msDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    msDetails.put("id", "");
                    msDetails.put("response", "Maximum number of Measuring Stations reached");
                    response = new ResponseEntity<>(msDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                LOG.log(Level.SEVERE, "Lubrication Unit does not exist", luId);
                msDetails.put("id", "");
                msDetails.put("response", "Lubrication Unit does not exist");
                response = new ResponseEntity<>(msDetails, HttpStatus.NOT_FOUND);
            }
        } else {
            LOG.log(Level.SEVERE, "Lubrication System does not exist", lsId);
            msDetails.put("id", "");
            msDetails.put("response", "Lubrication Unit does not exist");
            response = new ResponseEntity<>(msDetails, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Adding Measuring Station in particular Lubrication Unit through JSON
     *
     * @param msRequest
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String, String>> createMeasuringStationAlternate(@RequestBody MeasuringStation msRequest, @PathVariable String luId) {
        String url = myUrl + "/lu/" + luId + "/ms/";
        HashMap<String, String> msDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        String id = "";
        lu = null;
        ms = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if (lu.getMsList().size() < 21) {
                id = lu.generateMSId();
                if (!id.equals("")) {
                    Registry.measuringStation.put(id, msRequest);
                    if (lu.addChild(id)) {
                        ms = Registry.measuringStation.get(id);
                        ms.setId(id);
                        ms.saveMeasuringStation();
                        msDetails.put("id", id);
                        msDetails.put("response", url + id);
                        response = new ResponseEntity<>(msDetails, HttpStatus.CREATED);
                    } else {
                        msDetails.put("id", "");
                        msDetails.put("response", "Creation of Measuring Station failed");
                        response = new ResponseEntity<>(msDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    msDetails.put("id", "");
                    msDetails.put("response", "Creation of Measuring Station failed");
                    response = new ResponseEntity<>(msDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                msDetails.put("id", "");
                msDetails.put("response", "Maximum number of Measuring Stations reached");
                response = new ResponseEntity<>(msDetails, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            LOG.log(Level.SEVERE, "Lubrication Unit does not exist", luId);
            msDetails.put("id", "");
            msDetails.put("response", "Lubrication Unit does not exist");
            response = new ResponseEntity<>(msDetails, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    //---------------------------------GET Methods For Json-----------------------------------------
    /**
     * Getting Json of list of Measurement Stations
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/ms", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<MeasuringStation>> getMeasuringStationTotal(@RequestParam(value = "category", defaultValue = "none") String category) {
        ArrayList<MeasuringStation> measuringStations = new ArrayList<>();
        for (String msKey : Registry.measuringStation.keySet()) {
            ms = null;
            ms = Registry.measuringStation.get(msKey);
            measuringStations.add(ms);
        }
        return new ResponseEntity<>(measuringStations, HttpStatus.OK);
    }

    /**
     * Getting Json of list of Measuring Stations
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/ms/ids", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<String>> getMeasuringStationIds() {
        ArrayList<String> measuringStationDetails = new ArrayList<>();
        for (String msKey : Registry.measuringStation.keySet()) {
            measuringStationDetails.add(msKey);
        }
        return new ResponseEntity<>(measuringStationDetails, HttpStatus.OK);
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param msId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getMeasuringStationLinks(@PathVariable String msId
    ) {
        ResponseEntity<Links> response;
        Links msLink = null;
        if ((msLink = Registry.linkMS.get(msId)) != null) {
            response = new ResponseEntity<>(msLink, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param msId
     * @return
     */
    @RequestMapping(value = "/{msId}/info", method = RequestMethod.GET)
    public ResponseEntity<MeasuringStation> getMeasuringStationInfo(@PathVariable String msId
    ) {
        ResponseEntity<MeasuringStation> response;
        ms = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            response = new ResponseEntity<>(ms, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Measuring Station
     *
     * @param msId
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms/{msId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getMeasuringStationinLubricationUnitLinks(@PathVariable String msId, @PathVariable String luId
    ) {
        ResponseEntity<Links> response;
        Links msLink = null;
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if ((msLink = Registry.linkMS.get(msId)) != null) {
                response = new ResponseEntity<>(msLink, HttpStatus.OK);
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
     * @param msId
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms/{msId}/info", method = RequestMethod.GET)
    public ResponseEntity<MeasuringStation> getMeasuringStationinLubricationUnitInfo(@PathVariable String msId, @PathVariable String luId
    ) {
        ResponseEntity<MeasuringStation> response;
        ms = null;
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if ((ms = Registry.measuringStation.get(msId)) != null) {
                response = new ResponseEntity<>(ms, HttpStatus.OK);
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
     * @param msId
     * @param luId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/ms/{msId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getMeasuringStationinLubricationSystemLinks(@PathVariable String msId, @PathVariable String luId, @PathVariable String lsId
    ) {
        ResponseEntity<Links> response;
        Links msLink = null;
        ls = null;
        lu = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = Registry.lubricationUnit.get(luId)) != null) {
                if ((msLink = Registry.linkMS.get(msId)) != null) {
                    response = new ResponseEntity<>(msLink, HttpStatus.OK);
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
     * @param msId
     * @param luId
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/ms/{msId}/info", method = RequestMethod.GET)
    public ResponseEntity<MeasuringStation> getMeasuringStationinLubricationSystemInfo(@PathVariable String msId, @PathVariable String luId, @PathVariable String lsId
    ) {
        ResponseEntity<MeasuringStation> response;
        ls = null;
        lu = null;
        ms = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = Registry.lubricationUnit.get(luId)) != null) {
                if ((ms = Registry.measuringStation.get(msId)) != null) {
                    response = new ResponseEntity<>(ms, HttpStatus.OK);
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
     * Deleting particular Measuring Station and its components
     *
     * @param msId
     * @return
     */
    @RequestMapping(value = "/ms/{msId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteMeasuringStation(@PathVariable String msId) {
        ms = null;
        if ((ms = Registry.measuringStation.get(msId)) != null) {
            if (ms.deleteChildren()) {
                Registry.measuringStation.remove(msId);
                RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Measuring Station " + msId), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Failure in deleting of Measuring Station " + msId), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Measuring Station does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting all Measuring Stations in particular Lubrication Unit
     *
     * @param luId
     * @param msId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms/{msId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteMeasuringStationInLubricationUnit(@PathVariable String luId, @PathVariable String msId
    ) {
        lu = null;
        ms = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if ((ms = lu.getMsList().get(msId)) != null) {
                if (lu.deleteChild(msId)) {
                    RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Measuring Station in Lubrication Unit " + luId), HttpStatus.ACCEPTED);
                } else {
                    RESPONSE = new ResponseEntity<>(new Response("Failure in deleting of Measuring Station in Lubrication Unit " + luId), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Measuring Station does not exist"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication Unit does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting all Measuring Stations in particular Lubrication Unit
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/ms/all", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteAllMeasuringStationsInLubricationUnit(@PathVariable String luId
    ) {
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if (lu.deleteChildren()) {
                RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted all Measuring Stations in Lubrication Unit " + luId), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Failure in deleting of all Measuring Stations in Lubrication Unit " + luId), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication Unit does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

}
