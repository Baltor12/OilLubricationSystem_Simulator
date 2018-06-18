/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

/**
 * Class to generate the Json for sensor Informations
 *
 * @author Balaji Gopalakrishnan
 */
public class SensorInfo {
    String type;
    String dataType;

    public SensorInfo() {
    }

    public SensorInfo(String type, String dataType) {
        this.type = type;
        this.dataType = dataType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
}
