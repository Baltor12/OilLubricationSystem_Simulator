/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.ontology;

import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class RegisterSavedSystem {

    private static final Logger LOG = Logger.getLogger(RegisterSavedSystem.class.getName());

    public static Boolean registerLubricationSystems() {
        Boolean result = false;
        try {
            HashMap<String, HashMap<String, String>> lubricationSystemDetails = new HashMap<>();
            if (registerLubricationUnits()) {
                lubricationSystemDetails = OntologyManager.querySavedLubricationSystem();
                for (String lsDetailKey : lubricationSystemDetails.keySet()) {
                    if (!lubricationSystemDetails.get(lsDetailKey).get("id").equals("")) {
                        LubricationSystem lubricationSystem = null;
                        LubricationUnit lubricationUnit = null;
                        lubricationSystem = Registry.lubricationSystem.get(lubricationSystemDetails.get(lsDetailKey).get("id"));
                        if (lubricationSystem == null) {
                            LubricationSystem ls = new LubricationSystem();
                            ls.setId(lubricationSystemDetails.get(lsDetailKey).get("id"));
                            ls.setLinks();
                            if ((lubricationUnit = Registry.lubricationUnit.get(lubricationSystemDetails.get(lsDetailKey).get("lu"))) != null) {
                                ls.getLuList().put(lubricationUnit.getId(), lubricationUnit);
                            }
                            ls.lubricationSystemGenerate();
                            Registry.lubricationSystem.put(ls.getId(), ls);
                        } else {
                            if ((lubricationUnit = Registry.lubricationUnit.get(lubricationSystemDetails.get(lsDetailKey).get("lu"))) != null) {
                                lubricationSystem.getLuList().put(lubricationUnit.getId(), lubricationUnit);
                            }
                            lubricationSystem.setLUValues();
                            Registry.lubricationSystem.put(lubricationSystem.getId(), lubricationSystem);
                        }
                    }
                }
                result = true;
            }
            for (String luKey : Registry.lubricationUnit.keySet()) {
                LubricationUnit lu = Registry.lubricationUnit.get(luKey);
                lu.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, e + "- Exception Occurred");
            result = false;
        }
        return result;
    }

    public static Boolean registerLubricationUnits() {
        Boolean result = false;
        try {
            HashMap<String, HashMap<String, String>> lubricationUnitDetails = new HashMap<>();
            if (registerMeasuringStations()) {
                lubricationUnitDetails = OntologyManager.querySavedLubricationUnit();
                for (String luDetailKey : lubricationUnitDetails.keySet()) {
                    if (!lubricationUnitDetails.get(luDetailKey).get("id").equals("")) {
                        LubricationUnit lubricationUnit = null;
                        MeasuringStation measuringStation = null;
                        lubricationUnit = Registry.lubricationUnit.get(lubricationUnitDetails.get(luDetailKey).get("id"));
                        if (lubricationUnit == null) {
                            LubricationUnit lu = new LubricationUnit();
                            lu.setId(lubricationUnitDetails.get(luDetailKey).get("id"));
                            lu.setTankCapacity(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("tankCapacity")));
                            lu.setFilterCapacity(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("filterCapacity")));
                            lu.setShutTankCapacity(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("tankCapacity")));
                            lu.setShutFilterCapacity(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("filterCapacity")));
                            lu.setLevel(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("tankCapacity")));
                            lu.setLitresFilterClog(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("filterCapacity")));                            
                            lu.setMaxOilFlow(Double.parseDouble(lubricationUnitDetails.get(luDetailKey).get("maxFlow")));
                            if ((measuringStation = Registry.measuringStation.get(lubricationUnitDetails.get(luDetailKey).get("ms"))) != null) {
                                lu.getMsList().put(measuringStation.getId(), measuringStation);
                            }
                            lu.lubricationUnitGenerate();
                            Registry.lubricationUnit.put(lu.getId(), lu);
                        } else {
                            if ((measuringStation = Registry.measuringStation.get(lubricationUnitDetails.get(luDetailKey).get("ms"))) != null) {
                                lubricationUnit.getMsList().put(measuringStation.getId(), measuringStation);
                            }
                            lubricationUnit.lubricationUnitGenerate();
                            Registry.lubricationUnit.put(lubricationUnit.getId(), lubricationUnit);
                        }
                    }
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, e + "- Exception Occurred");
            result = false;
        }
        return result;
    }

    public static Boolean registerMeasuringStations() {
        Boolean result = false;
        try {
            HashMap<String, HashMap<String, String>> measuringStationDetails = new HashMap<>();
            if (registerFlowMeters()) {
                measuringStationDetails = OntologyManager.querySavedMeasuringStation();
                for (String msDetailKey : measuringStationDetails.keySet()) {
                    if (!measuringStationDetails.get(msDetailKey).get("id").equals("")) {
                        MeasuringStation measuringStation = null;
                        FlowMeter flowMeter = null;
                        measuringStation = Registry.measuringStation.get(measuringStationDetails.get(msDetailKey).get("id"));
                        if (measuringStation == null) {
                            MeasuringStation ms = new MeasuringStation();
                            ms.setId(measuringStationDetails.get(msDetailKey).get("id"));
                            if ((flowMeter = Registry.flowMeter.get(measuringStationDetails.get(msDetailKey).get("fm"))) != null) {
                                ms.getFmList().put(flowMeter.getId(), flowMeter);
                            }
                            ms.measuringStationGenerate();
                            Registry.measuringStation.put(ms.getId(), ms);
                        } else {
                            if ((flowMeter = Registry.flowMeter.get(measuringStationDetails.get(msDetailKey).get("fm"))) != null) {
                                measuringStation.getFmList().put(flowMeter.getId(), flowMeter);
                            }
                            measuringStation.measuringStationGenerate();
                            Registry.measuringStation.put(measuringStation.getId(), measuringStation);
                        }
                    }
                }
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, e + "- Exception Occurred");
            result = false;
        }
        return result;
    }

    public static Boolean registerFlowMeters() {
        Boolean result = false;
        try {
            HashMap<String, HashMap<String, String>> flowMeterDetails = new HashMap<>();
            flowMeterDetails = OntologyManager.querySavedFlowMeter();
            for (String fmDetailKey : flowMeterDetails.keySet()) {
                if (!flowMeterDetails.get(fmDetailKey).get("id").equals("")) {
                    FlowMeter flowMeter = new FlowMeter();
                    flowMeter.setId(flowMeterDetails.get(fmDetailKey).get("id"));
                    flowMeter.setNomFlow(Double.parseDouble(flowMeterDetails.get(fmDetailKey).get("nomFlow")));
                    flowMeter.flowMeterGenerate();
                    Registry.flowMeter.put(flowMeter.getId(), flowMeter);
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, e + "- Exception Occurred");
            result = false;
        }
        return result;
    }
}
