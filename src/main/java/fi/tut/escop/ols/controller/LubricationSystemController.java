/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import fi.tut.escop.ols.constants.HostPortandConfig;
import static fi.tut.escop.ols.controller.LubricationUnitController.ls;
import static fi.tut.escop.ols.controller.LubricationUnitController.lu;
import static fi.tut.escop.ols.controller.MeasuringStationController.ms;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.jsonLinks.Links;
import fi.tut.escop.ols.extra.NumberAwareStringComparator;
import fi.tut.escop.ols.extra.Response;
import fi.tut.escop.ols.ontology.OntologyManager;
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
 * Restful controller for service with respect to Lubrication System
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@RestController
public class LubricationSystemController {

    String myUrl = HostPortandConfig.ROOT_URL;
    ResponseEntity<Response> RESPONSE;
    Comparator<CharSequence> comparator = new NumberAwareStringComparator();
    int incId = 0;
    LubricationSystem ls;
    LubricationUnit lu;
    MeasuringStation ms;
    FlowMeter fm;
    private static final Logger LOG = Logger.getLogger(LubricationSystemController.class.getName());

    //---------------------------------POST Methods For Json-----------------------------------------
    /**
     * Adding Lubrication System through JSON
     *
     * @param lsRequest
     * @return
     */
    @RequestMapping(value = "/ls", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<HashMap<String, String>> createLubricationSystem(@RequestBody LubricationSystem lsRequest) {
        String url = myUrl + "/ls/";
        HashMap<String, String> lsDetails = new HashMap<>();
        ResponseEntity<HashMap<String, String>> response;
        ArrayList<String> lubricationSystemDetails = new ArrayList<>();
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            lubricationSystemDetails.add(lsKey);
        }
        if (!lubricationSystemDetails.isEmpty()) {
            Comparator<CharSequence> comparator = new NumberAwareStringComparator();
            Collections.sort(lubricationSystemDetails, comparator);
            String tempId = lubricationSystemDetails.get(lubricationSystemDetails.size() - 1);
            incId = Integer.parseInt(tempId.substring(tempId.indexOf("_") + 1)) + 1;
        } else {
            incId = 0;
        }
        String id = "LS_" + (incId);
        try {
            ls = new LubricationSystem(id, lsRequest.getLuList());
            ls.lubricationSystemGenerate();
            ls.saveLubricationSystem();
            lsDetails.put("id", id);
            lsDetails.put("response", url + id);
            response = new ResponseEntity<>(lsDetails, HttpStatus.CREATED);
        } catch (Exception e) {
            lsDetails.put("id", "");
            lsDetails.put("response", "Creation of Lubrication System failed");
            LOG.log(Level.SEVERE, "Exception " + e, id);
            response = new ResponseEntity<>(lsDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    //---------------------------------PUT Methods For Json-----------------------------------------
    /**
     * Action on Lubrication System (save/monitor)
     *
     * @param action
     * @param lsId
     * @return
     */
    @RequestMapping(value = "ls/{lsId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<Response> actionOnLubricationSystem(@RequestParam(value = "action", defaultValue = "noAction") String action, @PathVariable String lsId) {
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            switch (action) {
                case "simulate":
                    ls.start();
                    RESPONSE = new ResponseEntity<>(new Response("Simulating Lubrication System '" + lsId + "'"), HttpStatus.ACCEPTED);
                    break;
                case "stop":
                    if (ls.getIsAlive()) {
                        ls.interrupt();
                        ls.interruptChildren();
                    }
                    ls.reg();
                    RESPONSE = new ResponseEntity<>(new Response("Stopped simulating Lubrication System '" + lsId + "'"), HttpStatus.ACCEPTED);
                    break;
                case "noAction":
                    RESPONSE = new ResponseEntity<>(new Response("No action has been performed. Available actions are 'simulate' and 'stop'"), HttpStatus.ACCEPTED);
                    break;
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication System does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    //---------------------------------GET Methods For Json-----------------------------------------
    /**
     * Getting Json of list of Lubrication Systems
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/ls", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<LubricationSystem>> getLubricationSystemTotal() {
        ArrayList<LubricationSystem> lubricationSystems = new ArrayList<>();
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            ls = null;
            ls = Registry.lubricationSystem.get(lsKey);
            lubricationSystems.add(ls);
        }
        return new ResponseEntity<>(lubricationSystems, HttpStatus.OK);
    }

    /**
     * Getting Json of list of Lubrication Systems
     *
     * @param category
     * @return
     */
    @RequestMapping(value = "/ls/ids", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ArrayList<String>> getLubricationSystemIds() {
        ArrayList<String> lubricationSystemDetails = new ArrayList<>();
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            lubricationSystemDetails.add(lsKey);
        }
        Comparator<CharSequence> comparator = new NumberAwareStringComparator();
        Collections.sort(lubricationSystemDetails, comparator);
        return new ResponseEntity<>(lubricationSystemDetails, HttpStatus.OK);
    }

    /**
     * Getting the JSON representation of a particular Lubrication System
     *
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}", method = RequestMethod.GET)
    public ResponseEntity<Links> getLubricationSystem(@PathVariable String lsId) {
        ResponseEntity<Links> response;
        Links lsLink = null;
        if ((lsLink = Registry.linkLS.get(lsId)) != null) {
            response = new ResponseEntity<>(lsLink, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of a particular Lubrication System
     *
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}/info", method = RequestMethod.GET)
    public ResponseEntity<LubricationSystem> getLubricationSystemInfo(@PathVariable String lsId) {
        ResponseEntity<LubricationSystem> response;
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            response = new ResponseEntity<>(ls, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Getting the JSON representation of all Lubrication System
     *
     * @return
     */
    @RequestMapping(value = "/ls/all", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, Links>> getAllLubricationSystem() {
        return new ResponseEntity<>(Registry.linkLS, HttpStatus.OK);
    }

    //---------------------------------DELETE Methods For Json-----------------------------------------
    /**
     * Deleting particular Lubrication System and its components
     *
     * @param type
     * @param lsId
     * @return
     */
    @RequestMapping(value = "/ls/{lsId}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteLS(@PathVariable String lsId) {
        ls = null;
        if ((ls = Registry.lubricationSystem.get(lsId)) != null) {
            if (ls.deleteChildren()) {
                Registry.lubricationSystem.remove(lsId);
                RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted Lubrication System " + lsId), HttpStatus.ACCEPTED);
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Failuer in deleted Lubrication System " + lsId), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            RESPONSE = new ResponseEntity<>(new Response("Lubrication System does not exist"), HttpStatus.NOT_FOUND);
        }
        return RESPONSE;
    }

    /**
     * Deleting all Lubrication System and its components
     *
     * @return
     */
    @RequestMapping(value = "/ls/all", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteAllLubricationSystem() {
        Boolean dummyCheck = false;
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            ls = Registry.lubricationSystem.get(lsKey);
            if (ls.deleteChildren()) {
                RESPONSE = new ResponseEntity<>(new Response("Successfully Deleted all Lubrication Systems in Simulator"), HttpStatus.ACCEPTED);
                dummyCheck = true;
            } else {
                RESPONSE = new ResponseEntity<>(new Response("Failure in deleting all Lubrication System from Simulator"), HttpStatus.INTERNAL_SERVER_ERROR);
                dummyCheck = false;
            }
            OntologyManager.deleteLubricationSystem(ls);
        }
        if (dummyCheck) {
            Registry.lubricationSystem.clear();
        }
        return RESPONSE;
    }

    @RequestMapping(value = "/ls/mock", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> createLubricationSystemMock(@RequestBody LubricationSystem lsRequest) {
        String url = myUrl + "/ls/";
        HashMap<String, String> lsDetails = new HashMap<>();
        ResponseEntity<String> response;
        ArrayList<String> lubricationSystemDetails = new ArrayList<>();
        for (String lsKey : Registry.lubricationSystem.keySet()) {
            lubricationSystemDetails.add(lsKey);
        }
        if (!lubricationSystemDetails.isEmpty()) {
            Comparator<CharSequence> comparator = new NumberAwareStringComparator();
            Collections.sort(lubricationSystemDetails, comparator);
            String tempId = lubricationSystemDetails.get(lubricationSystemDetails.size() - 1);
            incId = Integer.parseInt(tempId.substring(tempId.indexOf("_") + 1)) + 1;
        } else {
            incId = 0;
        }
        String id = "LS_" + (incId);
        try {
            ls = new LubricationSystem(id);
            ls.lubricationSystemGenerate();
            ls.reg();
            ls.lubricationSystemGenerate();
            ls.saveLubricationSystem();
            String luId = ls.generateLUId();
            lu = new LubricationUnit(luId);
            lu.setTankCapacity(4000.0);
            lu.setFilterCapacity(550.0);
            lu.setMaxOilFlow(400.0);
            lu.setLevel(lu.getTankCapacity());
            lu.setLitresFilterClog(lu.getFilterCapacity());
            lu.reg();
            ls.addChild(lu.getId());
            lu.saveLubricationUnit();
            for (int i = 0; i < 1; i++) {
                String msId = lu.generateMSId();
                ms = new MeasuringStation(msId);
                ms.reg();
                lu.addChild(ms.getId());
                ms.saveMeasuringStation();
                for (int j = 0; j < 10; j++) {
                    String fmId = ms.generateFMId();
                    fm = new FlowMeter(fmId);
                    fm.setNomFlow(15.0);
                    fm.reg();
                    ms.addChild(fm.getId());
                    fm.saveFlowMeter();
                }
            }
            response = new ResponseEntity<>("success", HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, id);
            response = new ResponseEntity<>("failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

}
