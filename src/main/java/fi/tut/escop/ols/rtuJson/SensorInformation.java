/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.rtuJson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

/**
 *
 * @author Balaji Gopalakrishnan
 */
public class SensorInformation {
    String id;
    Object links;
    @JsonProperty("class")
    String group;
    String type;
    HashMap<String,Object> value;

    public SensorInformation() {
    }

    public SensorInformation(String id, Object links, String group, String type, HashMap<String, Object> value) {
        this.id = id;
        this.links = links;
        this.group = group;
        this.type = type;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getLinks() {
        return links;
    }

    public void setLinks(Object links) {
        this.links = links;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getValue() {
        return value;
    }

    public void setValue(HashMap<String, Object> value) {
        this.value = value;
    }
    
    
}
