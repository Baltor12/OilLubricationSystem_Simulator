/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson;

/**
 * Class to have the JSON representation of the payload sent by Service
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class ServicePayload {
    String count;
    String lastRun;

    public ServicePayload() {
    }

    public ServicePayload(String count, String lastRun) {
        this.count = count;
        this.lastRun = lastRun;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLastRun() {
        return lastRun;
    }

    public void setLastRun(String lastRun) {
        this.lastRun = lastRun;
    }
    
}
