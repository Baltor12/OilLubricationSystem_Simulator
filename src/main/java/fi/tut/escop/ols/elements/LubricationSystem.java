/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.jsonLinks.Links;
import fi.tut.escop.ols.ontology.OntologyManager;
import fi.tut.escop.ols.simulation.SimMath;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LubricationSystem implements Runnable {

    //------------------------------Declarations--------------------------------
    String id = "";
    public boolean isSimulated = false;
    Double oilAllocation = 0.0;
    Double oilRemaining = 0.0;
    String flowUnit = "dm3/min";
    long simTime = 1;
    Integer noLubricationUnits = 0;

    HashMap<String, Links> lubricationUnits = new HashMap<>();

    @JsonIgnore
    HashMap<String, LubricationUnit> luList = new HashMap<>();
    @JsonIgnore
    static final String LU_ID_PREFIX = "_LU_";
    @JsonIgnore
    private Thread thread;
    @JsonIgnore
    int simulationMultiplier = 1;
    @JsonIgnore
    String self = "";
    @JsonIgnore
    String parent = "";
    @JsonIgnore
    String info = "";
    @JsonIgnore
    String myUrl = HostPortandConfig.ROOT_URL;
    @JsonIgnore
    int incIdLU = 0;
    @JsonIgnore
    int stepForwardMultiplier = 0;
    @JsonIgnore
    private static final Logger LOG = Logger.getLogger(LubricationSystem.class.getName());

    //------------------------------Constructors--------------------------------
    public LubricationSystem() {

    }

    public LubricationSystem(String id) {
        this.id = id;
    }

    public LubricationSystem(String id, HashMap<String, LubricationUnit> luList) {
        this.id = id;
        this.luList = luList;
        this.noLubricationUnits = this.luList.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, LubricationUnit> getLuList() {
        return luList;
    }

    public void setLuList(HashMap<String, LubricationUnit> luList) {
        this.luList = luList;
    }

    public boolean getIsAlive() {
        return isSimulated;
    }

    public void setIsSimulated(boolean isSimulated) {
        this.isSimulated = isSimulated;
    }

    public Double getOilAllocation() {
        return oilAllocation;
    }

    public void setOilAllocation(Double oilAllocation) {
        this.oilAllocation = oilAllocation;
    }

    public Double getOilRemaining() {
        return oilRemaining;
    }

    public void setOilRemaining(Double oilRemaining) {
        this.oilRemaining = oilRemaining;
    }

    public Integer getNoLubricationUnits() {
        return noLubricationUnits;
    }

    public void setNoLubricationUnits(Integer noLubricationUnits) {
        this.noLubricationUnits = noLubricationUnits;
    }

    public void updateNoLubricationUnits() {
        this.noLubricationUnits = luList.size();
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

    public HashMap<String, Links> getLubricationUnits() {
        return lubricationUnits;
    }

    public void setLubricationUnits(HashMap<String, Links> lubricationUnits) {
        this.lubricationUnits = lubricationUnits;
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
        for (String luKey : luList.keySet()) {
            LubricationUnit lu = luList.get(luKey);
            lu.setStepForwardMultiplier(stepForwardMultiplier);
        }
    }

    public long getSimTime() {
        return simTime;
    }

    public void setSimTime(long simTime) {
        this.simTime = simTime;
    }

    /**
     * Method to store the registered values in Registry Class
     */
    public void reg() {
        Registry.lubricationSystem.put(id, this);
    }

    public void LubricationSystemAnalyze() {
        HashMap<String, Double> values = new HashMap<>();
        values = SimMath.lsAnalyze(this.luList);
        this.oilAllocation = values.get("oilAllocation");
        this.oilRemaining = values.get("oilRemaining");
    }

    /**
     * Function to Add the unallocated Lubrication Unit to the specified
     * Lubrication System
     *
     * @param luId
     * @return
     */
    public boolean addChild(String luId) {
        boolean response;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            LubricationUnit lu = Registry.lubricationUnit.get(luId);
            if (lu.getIsSimulated()) {
                lu.interrupt();
            }
            if (luId.substring(0, 2).equals("LU")) {
                this.luList.put(this.generateLUId(), lu);
                Registry.lubricationUnit.remove(luId);
                LOG.log(Level.INFO, "Added Lubrication Unit {0}", lu.getId());
            } else {
                this.luList.put(luId, lu);
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
     * Function to Add the unallocated Lubrication Units to the specified
     * Lubrication System
     *
     * @param luIds
     * @return
     */
    public boolean addChildren(ArrayList<String> luIds) {
        boolean response;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            for (String luId : luIds) {
                LubricationUnit lu = Registry.lubricationUnit.get(luId);
                if (lu.getIsSimulated()) {
                    lu.interrupt();
                }
                if (luId.substring(0, 2).equals("LU")) {
                    this.luList.put(this.generateLUId(), lu);
                    Registry.lubricationUnit.remove(luId);
                    LOG.log(Level.INFO, "Added Lubrication Unit {0}", lu.getId());
                } else {
                    this.luList.put(luId, lu);
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
     * Delete particular child Lubrication Unit
     *
     * @param luId
     * @return
     */
    public boolean deleteChild(String luId) {
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            LubricationUnit lu = luList.get(luId);
            if (lu.getIsSimulated()) {
                lu.interrupt();
            }
            lu.deleteChildren();
            //Remove LU Link Details in current LS and Registry of LU
            lubricationUnits.remove(luId);
            Registry.linkLU.remove(luId);
            LOG.log(Level.INFO, "Deleted Lubrication Unit {0}", lu.getId());
            //Remove LU Details in current LS and Registry of LU
            luList.remove(luId);
            Registry.lubricationUnit.remove(luId);
            if (!this.isSimulated) {
                this.start();
            }
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, id);
            return false;
        }
    }

    /**
     * Delete all child Lubrication Unit
     *
     * @return
     */
    public boolean deleteChildren() {
        boolean result = false;
        try {
            if (this.isSimulated) {
                this.interrupt();
            }
            for (String luKey : luList.keySet()) {
                LubricationUnit lu = luList.get(luKey);
                if (lu.getIsSimulated()) {
                    lu.interrupt();
                }
                lu.deleteChildren();
                OntologyManager.deleteLubricationUnit(lu);
                //Remove LU Link Details
                lubricationUnits.remove(luKey);
                Registry.linkLU.remove(luKey);
                LOG.log(Level.INFO, "Deleted Lubrication Unit {0}", lu.getId());
                //Remove LU Details
                Registry.lubricationUnit.remove(luKey);
            }
            luList.clear();

            this.start();

            result = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, id);
            result = false;
        }
        return result;
    }

    /**
     * Function to save the Lubrication System to Ontology
     *
     * @return
     */
    public boolean saveLubricationSystem() {
        boolean result = false;
        try {
            OntologyManager.insertLubricationSystem(this);
            result = true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Exception " + e, id);
            result = false;
        }
        return result;
    }

    /**
     * Function to Interrupt Children which are running
     *
     * @return
     */
    public boolean interruptChildren() {
        boolean result = false;
        for (String luKey : luList.keySet()) {
            LubricationUnit lu = null;
            try {
                if ((lu = luList.get(luKey)) != null) {
                    if (lu.getIsSimulated()) {
                        lu.interrupt();
                        lu.interruptChildren();
                    }
                }
                lu.reg();
                result = true;
            } catch (IllegalThreadStateException ex) {
                result = false;
                Logger
                        .getLogger(LubricationSystem.class
                                .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    /**
     * Commands to be start while executing each Individual Lubrication System
 thread
     */
    @Override
    public void run() {
        LOG.log(Level.INFO, "Starting lubrication system {0}", id);
        while (!Thread.interrupted()) {
            try {
                int flagStep = 0;
                if (this.stepForwardMultiplier != 0) {
                    flagStep = 1;
                }
                this.isSimulated = true;
                this.simTime += 60000 * (this.simulationMultiplier + this.stepForwardMultiplier);
                this.reg();
                if (flagStep == 1) {
                    this.stepForwardMultiplier = 0;
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
        LOG.log(Level.INFO, "Stopping lubrication system {0}", id);
        isSimulated = false;
    }

    /**
     * Set Label,Maximum oil outFlow and register each Lubrication unit which is
     * to -be started.
     *
     */
    public void setLUValues() {
        for (String luKey : luList.keySet()) {
            LubricationUnit lu = luList.get(luKey);
            lu.setId(luKey);
            lu.setParentId(this.id);
            //Sets link Values for the Luberication Unit
            lu.setParent(this.self);
            lu.setSelf(this.self + "/lu/" + lu.getId());
            lu.setInfo(this.self + "/lu/" + lu.getId() + "/info");
            lu.setSimTime(this.simTime);
            //Registers the links by converting it to a class and storing it in the Registry Class 
            lu.setSimulationMultiplier(this.simulationMultiplier);
            lu.setMSValues();
            Links luLink;
            luLink = new Links(lu.getId(), lu.getSelf(), lu.getParent(), lu.getInfo());
            luLink.regLU();
            this.lubricationUnits.put(lu.getId(), luLink);
            //Register the Lubrication Unit Values in the registry Class by accessing the specific LUs function 
            lu.reg();
        }
        this.noLubricationUnits = luList.size();
    }

    /**
     * Method to Generate the IDs for Lubrication Units under this Lubrication
     * -System
     *
     * @return
     */
    public String generateLUId() {
        incIdLU = this.luList.size();
        return id + LU_ID_PREFIX + (incIdLU++);
    }

    /**
     * Function sets the links for Lubrication System. this is used to represent
     * Lubrication System json
     */
    public void setLinks() {
        this.self = myUrl + "/ls/" + this.id;
        this.parent = myUrl + "/ls/" + this.id;
        this.info = myUrl + "/ls/" + this.id + "/info";
        Links lsLink;
        lsLink = new Links(this.id, this.self, this.parent, this.info);
        lsLink.regLS();
    }

    /**
     * Things to start without Thread
     */
    public void lubricationSystemGenerate() {
        setLinks();
        setLUValues();
        LubricationSystemAnalyze();
        if (this.simTime == 1) {
            Date date = new Date();
            this.simTime = date.getTime();
        }
        this.reg();
        for (String luKey : luList.keySet()) {
            LubricationUnit lu = luList.get(luKey);
            lu.lubricationUnitGenerate();
        }
        LOG.log(Level.INFO, "Registered lubrication system {0}", this.id);
    }

    //------------------------------Thread Commands-----------------------------
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            this.isSimulated = true;
            lubricationSystemGenerate();
            try {
                for (String luKey : luList.keySet()) {
                    LubricationUnit lu = null;
                    try {
                        if ((lu = luList.get(luKey)) != null) {
                            if (!lu.getIsSimulated()) {
                                lu.start();
                            }
                        }
                    } catch (IllegalThreadStateException ex) {
                        Logger.getLogger(LubricationSystem.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } catch (ConcurrentModificationException e) {
                Logger.getLogger(LubricationUnit.class
                        .getName()).log(Level.SEVERE, null, e);
            }
            thread.start();
            this.isSimulated = true;
            this.reg();
        }
    }

    public void interrupt() {
        thread.interrupt();
        isSimulated = false;
        thread = null;
    }

}
