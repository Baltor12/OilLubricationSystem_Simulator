/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.jsonLinks;

/**
 *
 * @author Balaji Gopalakrishnan
 */
public class SensorLinks {
    String self;
    String info;

    public SensorLinks() {
    }

    public SensorLinks(String self, String info) {
        this.self = self;
        this.info = info;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
          
}
