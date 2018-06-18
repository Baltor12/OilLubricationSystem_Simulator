/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import fi.tut.escop.ols.constants.HostPortandConfig;
import static fi.tut.escop.ols.controller.FlowMeterController.fm;
import fi.tut.escop.ols.discovery.Bye;
import fi.tut.escop.ols.discovery.Hello;
import fi.tut.escop.ols.discovery.RegisterationMessage;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.extra.NumberAwareStringComparator;
import fi.tut.escop.ols.jsonLinks.Links;
import fi.tut.escop.ols.extra.Response;
import fi.tut.escop.ols.simulation.SimValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * Restful controller for services with respect to Lubrication Unit
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
public class LubricationUnitController {

    String myUrl = HostPortandConfig.ROOT_URL;
    public static ResponseEntity<Response> RESPONSE;
    static LubricationSystem ls;
    static LubricationUnit lu;
    static MeasuringStation ms;
    static FlowMeter fm;
    int incId = 0;
    private static final Logger LOG = Logger.getLogger(LubricationSystem.class.getName());

    //---------------------------------POST Methods For Json-----------------------------------------      
    /**
     * Adding Lubrication Unit in particular Lubrication System through JSON
     *
     * @param luRequest
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String, String>> createLubricationUnit(@RequestBody LubricationUnit luRequest, @PathVariable String lsId) {
        String url = myUrl + "/ls/" + lsId + "/lu/";
        HashMap<String, String> luDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        ls = null;
        lu = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if (ls.getLuList().size() < 1) {
                String id = ls.generateLUId();
                Registry.lubricationUnit.put(id, luRequest);
                if (ls.addChild(id)) {
                    lu = Registry.lubricationUnit.get(id);
                    lu.saveLubricationUnit();
                    luDetails.put("id", id);
                    luDetails.put("response", url + id);
                    response = new ResponseEntity<>(luDetails, HttpStatus.CREATED);
                } else {
                    luDetails.put("id", "");
                    luDetails.put("response", "Creation of Lubrication Unit failed");
                    response = new ResponseEntity<>(luDetails, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                luDetails.put("id", "");
                luDetails.put("response", "Lubrication Unit already Exist");
                response = new ResponseEntity<>(luDetails, HttpStatus.BAD_REQUEST);
            }
        } else {
            LOG.log(Level.SEVERE, "Lubrication System does not exist", lsId);
            luDetails.put("id", "");
            luDetails.put("response", "Lubrication System does not exist");
            response = new ResponseEntity<>(luDetails, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Adding Lubrication Unit in particular Lubrication System without 4 LU's
     * through JSON
     *
     * @param luRequest
     * @return
     */
    @RequestMapping(value = "/lu", method = RequestMethod.POST)
    public ResponseEntity<HashMap<String, String>> createLubricationUnitAlternate(@RequestBody LubricationUnit luRequest) {
        String url = "";
        String lsId = "";
        String id = "";
        ArrayList<String> lubricationSystemDetails = new ArrayList<>();
        HashMap<String, String> luDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        ls = null;
        lu = null;

        // Check if there exists 4 LU in an LS and if not then it will assign it to 'ls'
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            lubricationSystemDetails.add(lsKey);
            LubricationSystem lsTemp = Registry.lubricationSystem.get(lsKey);
            if (lsTemp.getLuList().size() < 4) {
                ls = Registry.lubricationSystem.get(lsKey);
            }
        }

        // IF there is no LS free or LS is empty, it creates a new 'ls'
        if (ls == null) {
            if (!lubricationSystemDetails.isEmpty()) {
                Comparator<CharSequence> comparator = new NumberAwareStringComparator();
                Collections.sort(lubricationSystemDetails, comparator);
                String tempId = lubricationSystemDetails.get(lubricationSystemDetails.size() - 1);
                incId = Integer.parseInt(tempId.substring(tempId.indexOf("_") + 1)) + 1;
                lsId = "LS_" + (incId);
                ls = new LubricationSystem(lsId);
                ls.lubricationSystemGenerate();
                ls.saveLubricationSystem();
            } else {
                incId = 0;
                lsId = "LS_" + (incId);
                ls = new LubricationSystem(lsId);
                ls.lubricationSystemGenerate();
                ls.saveLubricationSystem();
            }
        }
        url = myUrl + "/ls/" + ls.getId() + "/lu/";
        id = ls.generateLUId();
        Registry.lubricationUnit.put(id, luRequest);
        if (ls.addChild(id)) {
            lu = Registry.lubricationUnit.get(id);
            lu.setId(id);
            lu.setLevel(lu.getTankCapacity());
            lu.setLitresFilterClog(lu.getFilterCapacity());
            lu.saveLubricationUnit();
            luDetails.put("id", id);
            luDetails.put("response", url + id);
            response = new ResponseEntity<>(luDetails, HttpStatus.CREATED);
        } else {
            luDetails.put("id", "");
            luDetails.put("response", "Creation of Lubrication Unit failed");
            response = new ResponseEntity<>(luDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Action on Lubrication System (save/monitor)
     *
     * @param action
     * @param luId
     * @return
     */
    @RequestMapping(value = "lu/{luId}/simulation", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<Response> actionOnLubricationSystem(@RequestParam(value = "action", defaultValue = "noAction") String action, @PathVariable String luId) throws IOException {
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            switch (action) {
                case "simulate":
                    lu.setMode(SimValue.Mode.SV_MODE_RUNNING);
                    lu.start();
                    if (HostPortandConfig.DISCOVERY) {
                        Hello hello = new Hello();
                        RegisterationMessage regMessage = new RegisterationMessage();
                        regMessage.setCnt(-1);
                        regMessage.setId(luId);
                        regMessage.setUrl(HostPortandConfig.ROOT_URL + "/" + lu.getParentId() + "/RTU/" + luId + "/api/swagger.json");
                        hello.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT, regMessage);
                        regMessage.setCnt(-1);
                        regMessage.setId(lu.getHmi().getId());
                        regMessage.setUrl(HostPortandConfig.ROOT_URL + "/" + lu.getParentId() + "/RTU/" + lu.getHmi().getId() + "/api/swagger.json");
                        hello.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT, regMessage);
                        for (String msKey : lu.getMsList().keySet()) {
                            regMessage.setCnt(-1);
                            regMessage.setId(msKey);
                            regMessage.setUrl(HostPortandConfig.ROOT_URL + "/" + lu.getParentId() + "/RTU/" + msKey + "/api/swagger.json");
                            hello.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT, regMessage);
                        }
                    }
                    RESPONSE = new ResponseEntity<>(new Response("Simulating Lubrication Unit '" + luId + "'"), HttpStatus.ACCEPTED);
                    break;
                case "pause":
                    if (lu.getIsSimulated()) {
                        lu.interrupt();
                        lu.interruptChildren();
                        if (HostPortandConfig.DISCOVERY) {
                            Bye bye = new Bye();
                            bye.setId(luId);
                            bye.setStatus(HostPortandConfig.BYE_STATUS);
                            bye.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT);
                            Bye bye1 = new Bye();
                            bye1.setId(lu.getHmi().getId());
                            bye1.setStatus(HostPortandConfig.BYE_STATUS);
                            bye1.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT);
                            for (String msKey : lu.getMsList().keySet()) {
                                bye.setId(msKey);
                                bye.setStatus(HostPortandConfig.BYE_STATUS);
                                bye.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT);
                            }
                        }
                    }
                    lu.reg();
                    RESPONSE = new ResponseEntity<>(new Response("Stopped simulating Lubrication Unit '" + luId + "'"), HttpStatus.ACCEPTED);
                    break;
                case "reset":
                    if (lu.getIsSimulated()) {
                        lu.interrupt();
                        lu.interruptChildren();
                        if (HostPortandConfig.DISCOVERY) {
                            Bye bye = new Bye();
                            bye.setId(luId);
                            bye.setStatus(HostPortandConfig.BYE_STATUS);
                            bye.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT);
                            Bye bye1 = new Bye();
                            bye1.setId(lu.getHmi().getId());
                            bye1.setStatus(HostPortandConfig.BYE_STATUS);
                            bye1.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT);
                            for (String msKey : lu.getMsList().keySet()) {
                                bye.setId(msKey);
                                bye.setStatus(HostPortandConfig.BYE_STATUS);
                                bye.multiCast(HostPortandConfig.HELLO_GROUP, HostPortandConfig.HELLO_PORT);
                            }
                        }
                    }
                    SimValue.Mode modeValue = SimValue.Mode.SV_MODE_RESET;
                    lu.setMode(modeValue);
                    lu.reg();
                    RESPONSE = new ResponseEntity<>(new Response("Successfully resetted the Lubrication system"), HttpStatus.CREATED);
                    break;
                case "noAction":
                    RESPONSE = new ResponseEntity<>(new Response("No action has been performed. Available actions are 'simulate', 'pause' and 'reset'"), HttpStatus.ACCEPTED);
                    break;
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication Unit Does not Exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    //---------------------------------GET Methods For Json-----------------------------------------
    /**
     * Getting Json of list of Lubrication Units
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/lu", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<LubricationUnit>> getLubricationUnitTotal() {
        ArrayList<LubricationUnit> lubricationUnits = new ArrayList<>();
        for (String luKey : Registry.lubricationUnit.keySet()) {
            lu = null;
            lu = Registry.lubricationUnit.get(luKey);
            lubricationUnits.add(lu);
        }
        return new ResponseEntity<>(lubricationUnits, HttpStatus.OK);
    }

    /**
     * Getting Json of list of Lubrication Units
     *
     * @return
     */
    @RequestMapping(value = "/lu/ids", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<String>> getLubricationUnitIds() {
        ArrayList<String> lubricationUnitDetails = new ArrayList<>();
        for (String luKey : Registry.lubricationUnit.keySet()) {
            lubricationUnitDetails.add(luKey);
        }
        return new ResponseEntity<>(lubricationUnitDetails, HttpStatus.OK);
    }

    /**
     * Getting the JSON representation of a particular Lubrication Unit
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getLubricationUnitLinks(@PathVariable String luId) {
        ResponseEntity<Links> response;
        Links luLink = null;
        if ((luLink = Registry.linkLU.get(luId)) != null) {
            response = new ResponseEntity<>(luLink, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Lubrication Unit
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}/info", method = RequestMethod.GET)
    public ResponseEntity<LubricationUnit> getLubricationUnitInfo(@PathVariable String luId) {
        ResponseEntity<LubricationUnit> response;
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            response = new ResponseEntity<>(lu, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Lubrication Unit
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getLubricationUnitinLubricationSystemLinks(@PathVariable String luId, @PathVariable String lsId) {
        ResponseEntity<Links> response;
        Links luLink = null;
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((luLink = Registry.linkLU.get(luId)) != null) {
                response = new ResponseEntity<>(luLink, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Lubrication Unit
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}/info", method = RequestMethod.GET)
    public ResponseEntity<LubricationUnit> getLubricationUnitinLubricationSystemInfo(@PathVariable String luId, @PathVariable String lsId) {
        ResponseEntity<LubricationUnit> response;
        lu = null;
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if ((lu = Registry.lubricationUnit.get(luId)) != null) {
                response = new ResponseEntity<>(lu, HttpStatus.OK);
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
     * Deleting particular Lubrication Unit and its components
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/lu/{luId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteLubricationUnit(@PathVariable String luId) {
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            if (lu.deleteChildren()) {
                Registry.lubricationUnit.remove(luId);
                RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Lubrication Unit " + luId), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Deletion of Lubrication Unit Children " + luId + " failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication Unit does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting particular Lubrication Unit and its components
     *
     * @param luId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/{luId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteLubricationUnitInLubricationSystem(@PathVariable String luId) {
        lu = null;
        if ((lu = Registry.lubricationUnit.get(luId)) != null) {
            ls = null;
            if ((ls = Registry.lubricationSystem.get(lu.getParentId())) != null) {
                if (ls.deleteChild(luId)) {
                    RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Lubrication Unit " + luId), HttpStatus.ACCEPTED);
                } else {
                    RESPONSE = new ResponseEntity<>(new Response("Deletion of Lubrication Unit" + luId + " failed"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Lubrication System does not exist"), HttpStatus.NOT_FOUND);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication Unit does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting all Lubrication Units in particular Lubrication System.
     *
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/lu/all", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteAllLubricationUnitInLubricationSystem(@PathVariable String lsId) {
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if (ls.deleteChildren()) {
                RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted all Lubrication units in Lubrication System " + lsId), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Deletion of Lubrication units in Lubrication System " + lsId + " failed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication System doesn not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

}
