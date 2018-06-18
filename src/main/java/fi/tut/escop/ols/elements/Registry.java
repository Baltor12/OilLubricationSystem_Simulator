/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.elements;

import fi.tut.escop.ols.discovery.Probing;
import fi.tut.escop.ols.jsonLinks.Links;
import fi.tut.escop.ols.rtuJson.ServiceDetails;
import fi.tut.escop.ols.rtuJson.EventSubscriberInputs;
import fi.tut.escop.ols.rtuJson.ServiceSubscriberInputs;
import java.util.HashMap;

/**
 * Registry used to store all the components of Simulator.
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Registry {
    
    //------------------------------Declarations--------------------------------
    public static HashMap<String, LubricationSystem> lubricationSystem = new HashMap<>();
    public static HashMap<String, LubricationUnit> lubricationUnit = new HashMap<>();
    public static HashMap<String, MeasuringStation> measuringStation = new HashMap<>();
    public static HashMap<String, FlowMeter> flowMeter = new HashMap<>();
    public static HashMap<String, HMI> hmi = new HashMap<>();

    public static HashMap<String, Links> linkLS = new HashMap<>();
    public static HashMap<String, Links> linkLU = new HashMap<>();
    public static HashMap<String, Links> linkMS = new HashMap<>();
    public static HashMap<String, Links> linkFM = new HashMap<>();

    public static HashMap<Integer, Double> isoCodes = new HashMap<>();
    public static HashMap<String, EventSubscriberInputs> eventSubscribers = new HashMap<>();
    public static HashMap<String, ServiceSubscriberInputs> serviceSubscribers = new HashMap<>();

    public static HashMap<String, ServiceDetails> filterChange = new HashMap<>();
    public static HashMap<String, ServiceDetails> oilChange = new HashMap<>();
    public static HashMap<String, ServiceDetails> message = new HashMap<>();
    public static HashMap<String, ServiceDetails> start = new HashMap<>();
    public static HashMap<String, ServiceDetails> stop = new HashMap<>();
    public static HashMap<Integer, Probing> probing = new HashMap<>();

    public static int lubricationUnitID = 0;
    public static int measuringStationID = 0;
    public static int flowMeterID = 0;
    public static long subscriberID = 0;

    //------------------------------Constructors--------------------------------
    public Registry() {
    }

    public static String generateUnassignedLubricationUnitID() {
        return "LU_" + lubricationUnitID++;
    }

    public static String generateUnassignedMeasuringStationID() {
        return "MS_" + measuringStationID++;
    }

    public static String generateUnassignedFlowMeterID() {
        return "FM_" + flowMeterID++;
    }

    public static String generateSubscriberID() {
        return "ols" + subscriberID++;
    }

}
