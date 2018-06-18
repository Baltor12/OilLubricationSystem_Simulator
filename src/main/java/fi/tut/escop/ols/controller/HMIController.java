/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import fi.tut.escop.ols.constants.HostPortandConfig;
import static fi.tut.escop.ols.controller.LubricationUnitController.RESPONSE;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.HMI;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.extra.Response;
import fi.tut.escop.ols.rtuJson.EventPayload;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.rtuJson.Payload;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class to generate REST web services for the HMI part
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
@RestController
@RequestMapping("/lu")
public class HMIController {

    String myUrl = HostPortandConfig.ROOT_URL;
    public static ResponseEntity<Response> RESPONSE;
    static LubricationSystem ls;
    static LubricationUnit lu;
    static MeasuringStation ms;
    static FlowMeter fm;
    static HMI hmi;
    private static final Logger LOG = Logger.getLogger(HMIController.class.getName());

    //---------------------------------POST Methods For Json----------------------------------------
    /**
     * POST method to receive the event message for oilChange variable in HMI
     *
     * @param luId
     * @param notification
     * @return
     */
    @RequestMapping(value = "/{luId}/hmi/oilChange", method = RequestMethod.POST)
    public ResponseEntity<Response> oilChangeNotificationfromEvent(@PathVariable String luId, @RequestBody EventPayload notification) {
        lu = null;
        hmi = null;
        Payload payload = null;
        payload = (Payload) notification.getPayload();
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            hmi = lu.getHmi();
            if (hmi != null) {
                try {
                    hmi.setOilChange(Boolean.valueOf(payload.getValue()));
                    RESPONSE = new ResponseEntity<>(new Response("OK"), HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    RESPONSE = new ResponseEntity<>(new Response("Payload value does not match the requirement it must be 'true' or 'false'"), HttpStatus.BAD_REQUEST);
                    Logger.getLogger(HMIController.class.getName()).log(Level.SEVERE, null, e);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("No HMI exists"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("LubricationUnit doesnot exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * POST method to receive the event message for filterChange variable in HMI
     *
     * @param luId
     * @param notification
     * @return
     */
    @RequestMapping(value = "/{luId}/hmi/filterChange", method = RequestMethod.POST)
    public ResponseEntity<Response> filterChangeNotificationfromEvent(@PathVariable String luId, @RequestBody EventPayload notification) {
        lu = null;
        hmi = null;
        Payload payload = null;
        payload = (Payload) notification.getPayload();
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            hmi = lu.getHmi();
            if (hmi != null) {
                try {
                    hmi.setFilterChange(Boolean.valueOf(payload.getValue()));
                    RESPONSE = new ResponseEntity<>(new Response("OK"), HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    RESPONSE = new ResponseEntity<>(new Response("Payload value does not match the requirement it must be 'true' or 'false'"), HttpStatus.BAD_REQUEST);
                    Logger.getLogger(HMIController.class.getName()).log(Level.SEVERE, null, e);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("No HMI exists"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("LubricationUnit doesnot exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * POST method to receive the event message for maintenance variable in HMI
     *
     * @param luId
     * @param notification
     * @return
     */
    @RequestMapping(value = "/{luId}/hmi/maintenance", method = RequestMethod.POST)
    public ResponseEntity<Response> maintenanceNotificationfromEvent(@PathVariable String luId, @RequestBody EventPayload notification) {
        lu = null;
        hmi = null;
        Payload payload = null;
        payload = (Payload) notification.getPayload();
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            hmi = lu.getHmi();
            if (hmi != null) {
                try {
                    hmi.setMaintenance(Boolean.valueOf(payload.getValue()));
                    RESPONSE = new ResponseEntity<>(new Response("OK"), HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    RESPONSE = new ResponseEntity<>(new Response("Payload value does not match the requirement it must be 'true' or 'false'"), HttpStatus.BAD_REQUEST);
                    Logger.getLogger(HMIController.class.getName()).log(Level.SEVERE, null, e);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("No HMI exists"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("LubricationUnit doesnot exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * POST method to receive the event message for message variable in HMI
     *
     * @param luId
     * @param notification
     * @return
     */
    @RequestMapping(value = "/{luId}/hmi/message", method = RequestMethod.POST)
    public ResponseEntity<Response> messageNotificationfromEvent(@PathVariable String luId, @RequestBody EventPayload notification) {
        lu = null;
        hmi = null;
        Payload payload = null;
        payload = (Payload) notification.getPayload();
        if (((lu = Registry.lubricationUnit.get(luId)) != null)) {
            hmi = lu.getHmi();
            if (hmi != null) {
                try {
                    hmi.setMessage(payload.getValue());
                    RESPONSE = new ResponseEntity<>(new Response("OK"), HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    RESPONSE = new ResponseEntity<>(new Response("Payload value does not match the requirement it must be 'String'"), HttpStatus.BAD_REQUEST);
                    Logger.getLogger(HMIController.class.getName()).log(Level.SEVERE, null, e);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("No HMI exists"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("LubricationUnit doesnot exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    //---------------------------------PUT Methods For Json-----------------------------------------
    /**
     * PUT method to generate new connections (web services) to the variables of
     * HMI
     *
     * @param refresh
     * @param luId
     * @return
     */
    @RequestMapping(value = "/{luId}/hmi", method = RequestMethod.PUT)
    public ResponseEntity<Response> correctLinkstoHMIVariables(@RequestParam(value = "refresh", defaultValue = "true") boolean refresh, @PathVariable String luId) {
        RESPONSE = new ResponseEntity<>(new Response("Failed"), HttpStatus.OK);
        RESPONSE = new ResponseEntity<>(new Response("Successfull"), HttpStatus.OK);
        return RESPONSE;
    }

    //---------------------------------GET Methods For Json-----------------------------------------
    /**
     * GET method to get the details for the HMI
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/{luId}/hmi/sensor", method = RequestMethod.GET)
    public ResponseEntity<HMI> getHMIVariables(@PathVariable String luId) {
        lu = null;
        hmi = null;
        ResponseEntity<HMI> response;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            hmi = lu.getHmi();
            if (hmi != null) {
                response = new ResponseEntity<>(hmi, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(hmi, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(hmi, HttpStatus.NOT_FOUND);
        }
        return response;
    }
    
    
}
