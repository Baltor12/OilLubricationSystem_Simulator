/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.simulation;

import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import fi.tut.escop.ols.elements.Registry;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class SimValue {

    public double rangeLO = 0.0;
    public double rangeHI = 0.0;
    public double nomValue = 0.0;
    public double minValue = 0.0;
    public double maxValue = 0.0;
    public double value = 0.0;
    public boolean filterClog = false;
    public double filterValue = 0.0;
    public boolean senLUFlow_Max_Alarm = false;
    public boolean senLULevel_Max_Alarm = false;
    public boolean senLUTemp_Max_Alarm = false;
    public boolean senLUFlow_Min_Alarm = false;
    public boolean senLULevel_Min_Alarm = false;
    public boolean senLUTemp_Min_Alarm = false;
    public boolean senMSFlow_Max_Alarm = false;
    public boolean senMSPressure_Max_Alarm = false;
    public boolean senMSTemp_Max_Alarm = false;
    public boolean senMSFlow_Min_Alarm = false;
    public boolean senMSPressure_Min_Alarm = false;
    public boolean senMSTemp_Min_Alarm = false;
    public boolean senFMFlow_Min_Alarm = false;
    public boolean senFMFlow_Max_Alarm = false;
    private static final Logger LOG = Logger.getLogger("SimValue");

    public enum Mode {

        SV_MODE_RUNNING, SV_MODE_DISTURBANCE, SV_MODE_SHUTDOWN, SV_MODE_RESET
    }

    public enum SensorType {

        senLUFlow, senLULevel, senLUTemp, senLUFilterClog, senLUWaterContent, senLUParticleCount, senMSFlow, senMSTemp, senMSPressure, senFMFlow, senFMParticleCount;
    }

    public Mode mode;
    public SensorType sensorType;

    public SimValue() {
    }

    public SimValue(SensorType sensorType, Mode mode) {
        this.sensorType = sensorType;
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public double getRangeLO() {
        return rangeLO;
    }

    public void setRangeLO(double rangeLO) {
        this.rangeLO = rangeLO;
    }

    public double getRangeHI() {
        return rangeHI;
    }

    public void setRangeHI(double rangeHI) {
        this.rangeHI = rangeHI;
    }

    public double getNomValue() {
        return nomValue;
    }

    public void setNomValue(double nomValue) {
        this.nomValue = nomValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isFilterClog() {
        return filterClog;
    }

    public void setFilterClog(boolean filterClog) {
        this.filterClog = filterClog;
    }

    public double getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(double filterValue) {
        this.filterValue = filterValue;
    }

    public boolean isSenFMFlow_Max_Alarm() {
        return senFMFlow_Max_Alarm;
    }

    public void setSenFMFlow_Max_Alarm(boolean senFMFlow_Max_Alarm) {
        this.senFMFlow_Max_Alarm = senFMFlow_Max_Alarm;
    }

    public boolean isSenMSPressure_Max_Alarm() {
        return senMSPressure_Max_Alarm;
    }

    public void setSenMSPressure_Max_Alarm(boolean senMSPressure_Max_Alarm) {
        this.senMSPressure_Max_Alarm = senMSPressure_Max_Alarm;
    }

    public boolean isSenMSTemp_Max_Alarm() {
        return senMSTemp_Max_Alarm;
    }

    public void setSenMSTemp_Max_Alarm(boolean senMSTemp_Max_Alarm) {
        this.senMSTemp_Max_Alarm = senMSTemp_Max_Alarm;
    }

    public boolean isSenLUTemp_Max_Alarm() {
        return senLUTemp_Max_Alarm;
    }

    public void setSenLUTemp_Max_Alarm(boolean senLUTemp_Max_Alarm) {
        this.senLUTemp_Max_Alarm = senLUTemp_Max_Alarm;
    }

    public boolean isSenLUTemp_Min_Alarm() {
        return senLUTemp_Min_Alarm;
    }

    public void setSenLUTemp_Min_Alarm(boolean senLUTemp_Min_Alarm) {
        this.senLUTemp_Min_Alarm = senLUTemp_Min_Alarm;
    }

    public boolean isSenLUFlow_Max_Alarm() {
        return senLUFlow_Max_Alarm;
    }

    public void setSenLUFlow_Max_Alarm(boolean senLUFlow_Max_Alarm) {
        this.senLUFlow_Max_Alarm = senLUFlow_Max_Alarm;
    }

    public boolean isSenLULevel_Max_Alarm() {
        return senLULevel_Max_Alarm;
    }

    public void setSenLULevel_Max_Alarm(boolean senLULevel_Max_Alarm) {
        this.senLULevel_Max_Alarm = senLULevel_Max_Alarm;
    }

    public boolean isSenMSFlow_Max_Alarm() {
        return senMSFlow_Max_Alarm;
    }

    public void setSenMSFlow_Max_Alarm(boolean senMSFlow_Max_Alarm) {
        this.senMSFlow_Max_Alarm = senMSFlow_Max_Alarm;
    }

    public boolean isSenMSFlow_Min_Alarm() {
        return senMSFlow_Min_Alarm;
    }

    public void setSenMSFlow_Min_Alarm(boolean senMSFlow_Min_Alarm) {
        this.senMSFlow_Min_Alarm = senMSFlow_Min_Alarm;
    }

    public boolean isSenFMFlow_Min_Alarm() {
        return senFMFlow_Min_Alarm;
    }

    public void setSenFMFlow_Min_Alarm(boolean senFMFlow_Min_Alarm) {
        this.senFMFlow_Min_Alarm = senFMFlow_Min_Alarm;
    }

    public boolean isSenMSPressure_Min_Alarm() {
        return senMSPressure_Min_Alarm;
    }

    public void setSenMSPressure_Min_Alarm(boolean senMSPressure_Min_Alarm) {
        this.senMSPressure_Min_Alarm = senMSPressure_Min_Alarm;
    }

    public boolean isSenMSTemp_Min_Alarm() {
        return senMSTemp_Min_Alarm;
    }

    public void setSenMSTemp_Min_Alarm(boolean senMSTemp_Min_Alarm) {
        this.senMSTemp_Min_Alarm = senMSTemp_Min_Alarm;
    }

    public boolean isSenLUFlow_Min_Alarm() {
        return senLUFlow_Min_Alarm;
    }

    public void setSenLUFlow_Min_Alarm(boolean senLUFlow_Min_Alarm) {
        this.senLUFlow_Min_Alarm = senLUFlow_Min_Alarm;
    }

    public boolean isSenLULevel_Min_Alarm() {
        return senLULevel_Min_Alarm;
    }

    public void setSenLULevel_Min_Alarm(boolean senLULevel_Min_Alarm) {
        this.senLULevel_Min_Alarm = senLULevel_Min_Alarm;
    }

    public void setValues() {
        //SensorDetail sensor = null;
        switch (sensorType) {
            case senLUFlow:
                rangeLO = 0;
                maxValue = 0.90 * rangeHI;
                nomValue = 0.75 * rangeHI;
                minValue = 0.30 * rangeHI;
                value = rangeLO;
                break;
            case senLULevel:
                rangeLO = 0;
                maxValue = 0.95 * rangeHI;
                nomValue = 0.75 * rangeHI;
                minValue = 0.50 * rangeHI;
                value = rangeLO;
                break;
            case senLUTemp:
                rangeHI = 90; // hardcoded
                rangeLO = 0;
                maxValue = 0.72 * rangeHI;
                nomValue = 0.50 * rangeHI;
                minValue = 0.33 * rangeHI;
                value = rangeLO;
                break;
            case senLUWaterContent:
                rangeHI = 80.00;
                rangeLO = 0;
                maxValue = 80.00;
                nomValue = 10.00;
                minValue = 0.00;
                value = rangeLO;
                break;
            case senMSTemp:
                rangeHI = 90; // hardcoded
                rangeLO = 0;
                maxValue = 0.72 * rangeHI;
                nomValue = 0.50 * rangeHI;
                minValue = 0.33 * rangeHI;
                break;
            case senMSPressure:
                rangeHI = 16; // hardcoded
                rangeLO = 0;
                maxValue = 0.625 * rangeHI;
                nomValue = 0.50 * rangeHI;
                minValue = 0.25 * rangeHI;
                break;
            case senFMFlow:
                rangeLO = 0;
                maxValue = 0.90 * rangeHI;
                nomValue = 0.50 * rangeHI;
                minValue = 0.10 * rangeHI;
                break;
        }
    }

    public double eval() {
        setValues();
        double intrValue = 0.0;
        switch (mode) {
            case SV_MODE_RUNNING:
                switch (sensorType) {
                    case senLUTemp:
                        intrValue = nomValue * 0.05;
                        nomValue = nomValue - intrValue;
                        value = nomValue + (Math.random() * 2 * intrValue);
                        if (value >= (maxValue)) {
                            senLUTemp_Max_Alarm = true;
                            senLUTemp_Min_Alarm = false;
                        } else if (value <= (minValue)) {
                            senLUTemp_Min_Alarm = true;
                            senLUTemp_Max_Alarm = false;
                        } else {
                            senLUTemp_Max_Alarm = false;
                            senLUTemp_Min_Alarm = false;
                        }
                        break;
                    case senLUWaterContent:
                        intrValue = nomValue * 0.05;
                        nomValue = nomValue - intrValue;
                        value = nomValue + (Math.random() * 2 * intrValue);
                        break;
                    case senMSTemp:
                        intrValue = nomValue * 0.05;
                        nomValue = nomValue - intrValue;
                        value = nomValue + (Math.random() * 2 * intrValue);
                        if (value >= (maxValue)) {
                            senMSTemp_Max_Alarm = true;
                            senMSTemp_Min_Alarm = false;
                        } else if (value <= (minValue)) {
                            senMSTemp_Min_Alarm = true;
                            senMSTemp_Max_Alarm = false;
                        } else {
                            senMSTemp_Max_Alarm = false;
                            senMSTemp_Min_Alarm = false;
                        }
                        break;
                    case senMSPressure:
                        if (!filterClog) {
                            intrValue = nomValue * 0.05;
                            nomValue = nomValue - intrValue;
                            value = nomValue + (Math.random() * 2 * intrValue);
                        } else {
                            value = Math.random() * (rangeHI * (filterValue / 100.0));
                        }
                        if (value >= (maxValue)) {
                            senMSPressure_Max_Alarm = true;
                            senMSPressure_Min_Alarm = false;
                        } else if (value <= (minValue)) {
                            senMSPressure_Min_Alarm = true;
                            senMSPressure_Max_Alarm = false;
                        } else {
                            senMSPressure_Max_Alarm = false;
                            senMSPressure_Min_Alarm = false;
                        }
                        break;
                }
                break;
            case SV_MODE_DISTURBANCE:
                switch (sensorType) {
                    case senLUTemp:
                        value = Math.random() * rangeHI;
                        if (value >= (maxValue)) {
                            senLUTemp_Max_Alarm = true;
                            senLUTemp_Min_Alarm = false;
                        } else if (value <= (minValue)) {
                            senLUTemp_Min_Alarm = true;
                            senLUTemp_Max_Alarm = false;
                        } else {
                            senLUTemp_Max_Alarm = false;
                            senLUTemp_Min_Alarm = false;
                        }
                        break;
                    case senLUWaterContent:
                        value = Math.random() * rangeHI;
                        break;
                    case senMSTemp:
                        value = Math.random() * rangeHI;
                        if (value >= (maxValue)) {
                            senMSTemp_Max_Alarm = true;
                            senMSTemp_Min_Alarm = false;
                        } else if (value <= (minValue)) {
                            senMSTemp_Min_Alarm = true;
                            senMSTemp_Max_Alarm = false;
                        } else {
                            senMSTemp_Max_Alarm = false;
                            senMSTemp_Min_Alarm = false;
                        }
                        break;
                    case senMSPressure:
                        if (!filterClog) {
                            value = Math.random() * rangeHI;
                        } else {
                            value = Math.random() * (rangeHI * (filterValue / 100.0));
                        }
                        if (value >= (maxValue)) {
                            senMSPressure_Max_Alarm = true;
                            senMSPressure_Min_Alarm = false;
                        } else if (value <= (minValue)) {
                            senMSPressure_Min_Alarm = true;
                            senMSPressure_Max_Alarm = false;
                        } else {
                            senMSPressure_Max_Alarm = false;
                            senMSPressure_Min_Alarm = false;
                        }
                        break;
                }
                break;
            case SV_MODE_SHUTDOWN:
                switch (sensorType) {
                    case senLUWaterContent:
                        value = 0.0;
                        break;
                    case senLUTemp:
                        value = 0.0;
                        senLUTemp_Max_Alarm = false;
                        senLUTemp_Min_Alarm = false;
                        break;
                    case senMSTemp:
                        value = 0.0;
                        senMSTemp_Max_Alarm = false;
                        senMSTemp_Min_Alarm = false;
                        break;
                    case senMSPressure:
                        value = 0.0;
                        senMSPressure_Max_Alarm = false;
                        senMSPressure_Min_Alarm = false;
                        break;
                }
                break;
        }
        return value;
    }

    public double lubricationUnitLevel(double level, HashMap<String, MeasuringStation> msList, double inFlow, double outFlow, int simulationMultiplier, int stepForwardMultiplier, int noEntry) {
        setValues();
        if ((inFlow != 0.0) && (noEntry > 2) && (outFlow > inFlow)) {
            value = level + ((inFlow - outFlow) * (simulationMultiplier + stepForwardMultiplier));
        } else if (noEntry == 1) {
            value = level - outFlow;
        } else {
            value = level;
        }
        if (value <= 0.0) {
            value = 0.0;
        }
        if ((level + outFlow) >= (maxValue)) {
            senLULevel_Max_Alarm = true;
            senLULevel_Min_Alarm = false;
        } else if ((level + outFlow) <= (minValue)) {
            senLULevel_Min_Alarm = true;
            senLULevel_Max_Alarm = false;
        } else {
            senLULevel_Max_Alarm = false;
            senLULevel_Min_Alarm = false;
        }
        return value;
    }

    public double lubricationUnitInFlow(HashMap<String, MeasuringStation> msList) {
        double inFlow = 0.0;
        for (String msKey : msList.keySet()) {
            MeasuringStation measuringStation = null;
            if ((measuringStation = Registry.measuringStation.get(msKey)) != null) {
                for (String fmKey : measuringStation.getFmList().keySet()) {
                    FlowMeter flowMeter = null;
                    if ((flowMeter = Registry.flowMeter.get(fmKey)) != null) {
                        inFlow += flowMeter.getOutFlow();
                    }
                }
            }
        }
        return inFlow;
    }

    public double lubricationUnitOutFlow(double oilAllocation, double level) {
        setValues();
        double intrValue = 0.0;
        double workValue = 0.0; //value to work with
        if (level < oilAllocation) {
            workValue = level;
        } else if (level == 0.0) {
            value = 0.0;
        } else {
            workValue = oilAllocation;
        }
        switch (mode) {
            case SV_MODE_RUNNING:
                intrValue = workValue * 0.000001;
                value = workValue - (Math.random() * intrValue);
                break;
            case SV_MODE_DISTURBANCE:
                value = Math.random() * workValue;
                break;
            case SV_MODE_SHUTDOWN:
                value = 0.0;
                break;
        }
        if (value >= (maxValue)) {
            senLUFlow_Max_Alarm = true;
            senLUFlow_Min_Alarm = false;
        } else if (value <= (minValue)) {
            senLUFlow_Min_Alarm = true;
            senLUFlow_Max_Alarm = false;
        } else {
            senLUFlow_Max_Alarm = false;
            senLUFlow_Min_Alarm = false;
        }
        return value;
    }

    public double lubricationUnitFilterEfficiency(double capacity, double litresFilterClog) {
        double filterEfficiency = 0.0;
        filterEfficiency = (litresFilterClog / capacity) * 0.95; // hardcoding Filter Efficiency starting from 95%
        return filterEfficiency;
    }

    public double averageParticle(String particle, HashMap<String, MeasuringStation> msList) {
        double cumParticleValue = 0.0;
        switch (particle) {
            case "particles4um":
                for (String msKey : msList.keySet()) {
                    MeasuringStation measuringStation = null;
                    if ((measuringStation = Registry.measuringStation.get(msKey)) != null) {
                        for (String fmKey : measuringStation.getFmList().keySet()) {
                            FlowMeter flowMeter = null;
                            if ((flowMeter = Registry.flowMeter.get(fmKey)) != null) {
                                cumParticleValue += flowMeter.getParticles4um();
                            }
                        }
                    }
                }
                //cumParticleValue = cumParticleValue / msList.size();
                break;
            case "particles6um":
                for (String msKey : msList.keySet()) {
                    MeasuringStation measuringStation = null;
                    if ((measuringStation = Registry.measuringStation.get(msKey)) != null) {
                        for (String fmKey : measuringStation.getFmList().keySet()) {
                            FlowMeter flowMeter = null;
                            if ((flowMeter = Registry.flowMeter.get(fmKey)) != null) {
                                cumParticleValue += flowMeter.getParticles6um();
                            }
                        }
                    }
                }
                //cumParticleValue = cumParticleValue / msList.size();
                break;
            case "particles14um":
                for (String msKey : msList.keySet()) {
                    MeasuringStation measuringStation = null;
                    if ((measuringStation = Registry.measuringStation.get(msKey)) != null) {
                        for (String fmKey : measuringStation.getFmList().keySet()) {
                            FlowMeter flowMeter = null;
                            if ((flowMeter = Registry.flowMeter.get(fmKey)) != null) {
                                cumParticleValue += flowMeter.getParticles14um();
                            }
                        }
                    }
                }
                //cumParticleValue = cumParticleValue / msList.size();
                break;
        }
        return cumParticleValue;
    }

    public double timeFilterPartice(String particle, double particleValue, double avgParticleValue, double filterEfficiency, int stepForwardMultiplier, double inFlow) {
        switch (particle) {
            case "particles4um":
                avgParticleValue = avgParticleValue / inFlow;
                particleValue += (double) (avgParticleValue * (1 - (filterEfficiency * 0.40))) * stepForwardMultiplier; // 30 % of filter efficiency of Particle 4 micron
                break;
            case "particles6um":
                avgParticleValue = avgParticleValue / inFlow;
                particleValue += (double) (avgParticleValue * (1 - filterEfficiency) * stepForwardMultiplier);
                break;
            case "particles14um":
                avgParticleValue = avgParticleValue / inFlow;
                particleValue += (double) (avgParticleValue * (1 - filterEfficiency) * stepForwardMultiplier);
                break;
        }
        return particleValue;
    }

    public double filterChangeTimeParticleCancel(String particle, double particleValue, double filterEfficiency) {
        switch (particle) {
            case "particles4um":
                particleValue = (double) (particleValue * (1 - (filterEfficiency * 0.40)));
                break;
            case "particles6um":
                particleValue = (double) (particleValue * (1 - (filterEfficiency)));
                break;
            case "particles14um":
                particleValue = (double) (particleValue * (1 - (filterEfficiency)));
                break;
        }
        return particleValue;
    }

    public double filterParticle(String particle, double particleValue, double avgParticleValue, double timeParticlesValue, double filterEfficiency, double inFlow) {
        switch (particle) {
            case "particles4um":
                avgParticleValue = avgParticleValue / inFlow;
                particleValue += avgParticleValue;
                particleValue = (particleValue * (1 - (filterEfficiency * 0.40))) + timeParticlesValue; // 40 % of filter efficiency of Particle 4 micron
                break;
            case "particles6um":
                avgParticleValue = avgParticleValue / inFlow;
                particleValue += avgParticleValue;
                particleValue = particleValue * (1 - filterEfficiency) + timeParticlesValue;
                break;
            case "particles14um":
                avgParticleValue = avgParticleValue / inFlow;
                particleValue += avgParticleValue;
                particleValue = particleValue * (1 - filterEfficiency) + timeParticlesValue;
                break;
        }
        return particleValue;
    }

    public double filterClog(double particles4um, double particles6um, double particles14um, double filterEfficiency, int noChildFlowMeters, double litresFilterClog, int simulationMultiplier, int stepForwardMultiplier) {
        double totalParticle = 0.0;

        totalParticle = (filterEfficiency * noChildFlowMeters * ((particles6um * (Math.pow(6, 3))) + (particles14um * (Math.pow(14, 3))) + (particles4um * (Math.pow(4, 3)) * 0.40))) / 100000000000000l; // 40 % of filter efficiency of Particle 4 micron
        // TODO: Delete
//            System.out.println("inFlow : " + inFlow);
//        System.out.println("filterEfficiency : " + filterEfficiency);
        value = litresFilterClog - totalParticle;
        if (filterEfficiency <= 0.10) {
            filterClog = true;
            if (value <= 1.0) {
                value = 0.0;
            }
        } else {
            filterClog = false;
        }
        return value;
    }

    public String particleCountISO(double particles4um, double particles6um, double particles14um) {
        int particles4umISO = 0;
        int particles6umISO = 0;
        int particles14umISO = 0;
        String particleCount = "";
        Double previousISOValue = 0.0;
        for (int isoKey : Registry.isoCodes.keySet()) {
            if (previousISOValue != 0.0) {
                if ((particles4um < Registry.isoCodes.get(isoKey)) && (particles4um > previousISOValue)) {
                    particles4umISO = isoKey;
                } else if ((particles4um > Registry.isoCodes.get(isoKey)) && (isoKey == 28)) {
                    particles4umISO = isoKey;
                } else if ((particles4um < Registry.isoCodes.get(isoKey)) && (isoKey == 8)) {
                    particles4umISO = isoKey;
                }

                if ((particles6um < Registry.isoCodes.get(isoKey)) && (particles6um > previousISOValue)) {
                    particles6umISO = isoKey;
                } else if ((particles6um > Registry.isoCodes.get(isoKey)) && (isoKey == 28)) {
                    particles6umISO = isoKey;
                } else if ((particles6um < Registry.isoCodes.get(isoKey)) && (isoKey == 8)) {
                    particles6umISO = isoKey;
                }

                if ((particles14um < Registry.isoCodes.get(isoKey)) && (particles14um > previousISOValue)) {
                    particles14umISO = isoKey;
                } else if ((particles14um > Registry.isoCodes.get(isoKey)) && (isoKey == 28)) {
                    particles14umISO = isoKey;
                } else if ((particles14um < Registry.isoCodes.get(isoKey)) && (isoKey == 8)) {
                    particles14umISO = isoKey;
                }
                particleCount = particles4umISO + " / " + particles6umISO + " / " + particles14umISO;
            }
            previousISOValue = Registry.isoCodes.get(isoKey);
        }
        return particleCount;
    }

    public double measuringStationInFlow(String parentId, double oilAllocation, double leakageLevel) {
        double percent = 0.0;
        double inFlow = 0.0;
        LubricationUnit lubricationUnit = null;
        if ((lubricationUnit = Registry.lubricationUnit.get(parentId)) != null) {
            percent = lubricationUnit.getOutFlow() / lubricationUnit.getOilAllocation();
            inFlow = oilAllocation * percent;
            inFlow = inFlow * (1 - leakageLevel);
        }
        return inFlow;
    }

    public double measuringStationOutFlow(double inFlow, double leakageLevel) {
        setValues();
        maxValue = inFlow * 0.90; //hardcoded for time being
        minValue = inFlow * 0.25; //hardcoded for time being
        double intrValue = 0.0;
        switch (mode) {
            case SV_MODE_RUNNING:
                intrValue = inFlow * 0.000001;
                value = inFlow - (Math.random() * intrValue);
                value = value * (1 - leakageLevel);
                break;
            case SV_MODE_DISTURBANCE:
                value = Math.random() * inFlow;
                break;
            case SV_MODE_SHUTDOWN:
                value = 0.0;
                break;
        }
        if (value >= (maxValue)) {
            senMSFlow_Max_Alarm = true;
            senMSFlow_Min_Alarm = false;
        } else if (value <= (minValue)) {
            senMSFlow_Min_Alarm = true;
            senMSFlow_Max_Alarm = false;
        } else {
            senMSFlow_Max_Alarm = false;
            senMSFlow_Min_Alarm = false;
        }
        return value;
    }

    public double flowMeterInFlow(String parentId, double leakageLevel) {
        double percent = 0.0;
        double inFlow = 0.0;
        MeasuringStation measuringStation = null;
        setValues();
        if ((measuringStation = Registry.measuringStation.get(parentId)) != null) {
            percent = measuringStation.getOutFlow() / measuringStation.getOilAllocation();
            inFlow = nomValue * percent;
            inFlow = inFlow * (1 - leakageLevel);
        }
        return inFlow;
    }

    public double flowMeterOutFlow(double inFlow, double leakageLevel) {
        setValues();
        double intrValue = 0.0;
        switch (mode) {
            case SV_MODE_RUNNING:
                intrValue = inFlow * 0.000001;
                value = inFlow - (Math.random() * intrValue);
                value = value * (1 - leakageLevel);
                break;
            case SV_MODE_DISTURBANCE:
                value = Math.random() * inFlow;
                break;
            case SV_MODE_SHUTDOWN:
                value = 0.0;
                break;
        }
        if (value >= (maxValue)) {
            senFMFlow_Max_Alarm = true;
            senFMFlow_Min_Alarm = false;
        } else if (value <= (minValue)) {
            senFMFlow_Min_Alarm = true;
            senFMFlow_Max_Alarm = false;
        } else {
            senFMFlow_Max_Alarm = false;
            senFMFlow_Min_Alarm = false;
        }
        return value;
    }

    public double particleIncrementFlowMeter(double flow, double particleValue, String particle, int simulationMultiplier, double outFlow) {
        switch (particle) {
            case "particles4um":
                particleValue = (double) Math.round(outFlow * (Math.random() * 50) * (simulationMultiplier)); //The value will be outflow * randomly in the limt (0-50)
                break;
            case "particles6um":
                particleValue = (double) Math.round(outFlow * (Math.random() * 10) * (simulationMultiplier)); //The value will be outflow * randomly in the limt (0-5)
                break;
            case "particles14um":
                particleValue = (double) Math.round(outFlow * (Math.random() * 5) * (simulationMultiplier)); //The value will be outflow * randomly in the limt (0-1)
                break;
        }
        return particleValue;
    }

    public double randomLeakageAmount() {
        double leak = 0.0;
        leak = 0.20 + (0.3 * Math.random());
        return leak;
    }
}
