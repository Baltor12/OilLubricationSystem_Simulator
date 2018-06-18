/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson;

/**
 * Class to represent payload in JSON format
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class Payload {

    String value;
    String type;
    String state;

    public Payload() {
    }

    public Payload(String value, String type, String state) {
        this.value = value;
        this.type = type;
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
