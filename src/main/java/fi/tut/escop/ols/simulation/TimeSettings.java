/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.simulation;

import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.Registry;

/**
 * Class to set the timings for the simulation
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class TimeSettings {

    int dayMultiplier = 0;
    int hourMultiplier = 0;
    int minuteMultiplier = 0;
    int pointMultiplier = 0;
    String lubricationSystemId;
    String lubricationUnitId;

    public TimeSettings() {
    }

    public TimeSettings(int dayMultiplier, int hourMultiplier, int minuteMultiplier, String lubricationSystemId , String lubricationUnitId) {
        this.dayMultiplier = dayMultiplier;
        this.hourMultiplier = hourMultiplier;
        this.minuteMultiplier = minuteMultiplier;
        this.lubricationSystemId = lubricationSystemId;
        this.lubricationUnitId = lubricationUnitId;
    }

    public int getDayMultiplier() {
        return dayMultiplier;
    }

    public void setDayMultiplier(int dayMultiplier) {
        this.dayMultiplier = dayMultiplier;
    }

    public int getHourMultiplier() {
        return hourMultiplier;
    }

    public void setHourMultiplier(int hourMultiplier) {
        this.hourMultiplier = hourMultiplier;
    }

    public int getMinuteMultiplier() {
        return minuteMultiplier;
    }

    public void setMinuteMultiplier(int minuteMultiplier) {
        this.minuteMultiplier = minuteMultiplier;
    }

    public int getPointMultiplier() {
        return pointMultiplier;
    }

    public void setPointMultiplier(int pointMultiplier) {
        this.pointMultiplier = pointMultiplier;
    }

    public String getLubricationSystemId() {
        return lubricationSystemId;
    }

    public void setLubricationSystemId(String lubricationSystemId) {
        this.lubricationSystemId = lubricationSystemId;
    }

    public String getLubricationUnitId() {
        return lubricationUnitId;
    }

    public void setLubricationUnitId(String lubricationUnitId) {
        this.lubricationUnitId = lubricationUnitId;
    }

    /**
     * Function to set simulation speed
     *
     * @return
     */
    public boolean setLubricationSystemTime() {
        boolean response = false;
        LubricationSystem ls = null;
        if ((ls = Registry.lubricationSystem.get(this.lubricationSystemId)) != null) {
            if (this.dayMultiplier != 0 || this.hourMultiplier != 0 || this.minuteMultiplier != 0) {
                ls.setSimulationMultiplier((this.dayMultiplier * 1440) + (this.hourMultiplier * 60) + (this.minuteMultiplier));
                ls.setLUValues();
            }
            response = true;
        } else {
            response = false;
        }
        return response;
    }

    /**
     * Function to step forward the simulation for specific time.
     *
     * @param step
     * @param type
     * @return
     */
    public boolean setLubricationSystemStepForward() {
        boolean response = false;
        int stepMultiplier = 1;
        LubricationSystem ls = null;
        if ((ls = Registry.lubricationSystem.get(this.lubricationSystemId)) != null) {
            stepMultiplier = (this.dayMultiplier * 1440) + (this.hourMultiplier * 60) + (this.minuteMultiplier * 1) + (this.pointMultiplier * ls.getSimulationMultiplier());
            ls.setStepForwardMultiplier(stepMultiplier);
            response = true;
        } else {
            response = false;
        }
        return response;
    }
    
    /**
     * Function to step forward the simulation for specific time.
     *
     * @param step
     * @param type
     * @return
     */
    public boolean setLubricationUnitStepForward() {
        boolean response = false;
        int stepMultiplier = 1;
        LubricationUnit lu = null;
        if ((lu = Registry.lubricationUnit.get(this.lubricationUnitId)) != null) {
            stepMultiplier = (this.dayMultiplier * 1440) + (this.hourMultiplier * 60) + (this.minuteMultiplier * 1) + (this.pointMultiplier * lu.getSimulationMultiplier());
            lu.setStepForwardMultiplier(stepMultiplier);
            response = true;
        } else {
            response = false;
        }
        return response;
    }

}
