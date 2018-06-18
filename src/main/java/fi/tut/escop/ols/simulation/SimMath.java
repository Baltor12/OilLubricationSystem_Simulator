/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.simulation;

import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import java.util.HashMap;

/**
 * This Class contains all the mathematics performed in the simulator.
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class SimMath {

    /**
     * Function to perform the Calculations for the Flow of Lubrication System
     * determining the oil flow allocated and the Oil Flow Remaining
     *
     * @param lubricationUnit
     * @return
     */
    public static HashMap<String, Double> lsAnalyze(HashMap<String, LubricationUnit> lubricationUnit) {
        HashMap<String, Double> values = new HashMap<>();

        Double oilAllocation = 0.0;
        Double oilRemaining = 0.0;

        HashMap<String, LubricationUnit> luValues = new HashMap<>(lubricationUnit);
        for (LubricationUnit lu : luValues.values()) {
            oilAllocation += lu.getOilAllocation();
            oilRemaining += lu.getOilRemaining();
        }

        values.put("oilAllocation", oilAllocation);
        values.put("oilRemaining", oilRemaining);
        return values;
    }

    /**
     * Function to perform the Calculations for the Flow of Lubrication Unit
     * determining the oil flow allocated and the Oil Flow Remaining
     *
     * @param maxOilFlow
     * @param measuringStations
     * @return
     */
    public static HashMap<String, Double> luAnalyze(Double maxOilFlow, HashMap<String, MeasuringStation> measuringStations) {
        HashMap<String, Double> values = new HashMap<>();
        Double totalOilFlowReq = 0.0;
        Double oilAllocation = 0.0;
        Double oilRemaining = 0.0;

        if (!measuringStations.isEmpty()) {
        HashMap<String, MeasuringStation> msValues = new HashMap<>(measuringStations);
            for (MeasuringStation ms : msValues.values()) {
                totalOilFlowReq += ms.measuringStationAnalyze();
            }
            oilAllocation = (double) Math.round(totalOilFlowReq);
            oilRemaining = (double) Math.round((maxOilFlow) - totalOilFlowReq);
        } else {
            oilRemaining = (double) Math.round(maxOilFlow);
        }
        values.put("oilAllocation", oilAllocation);
        values.put("oilRemaining", oilRemaining);
        return values;
    }

    /**
     * Function to perform the Calculations for the total flow in Measuring
     * Station by adding individual flow of each Flow Meter
     *
     * @param flowMeters
     * @return
     */
    public static Double msAnalyze(HashMap<String, FlowMeter> flowMeters) {
        Double totalNomFlow = 0.0;
        HashMap<String, FlowMeter> fmValues = new HashMap<>(flowMeters);
        for (FlowMeter fm : fmValues.values()) {
            totalNomFlow += fm.getNomFlow();
        }
        return totalNomFlow;
    }
}
