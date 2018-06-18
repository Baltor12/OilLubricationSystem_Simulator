/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.extra;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Class to convert Json to String and vice versa.
 *
 * @author Balaji opalakrishnna(TUT)
 */
public class JsonConverters {

    public String toJson(Object object) throws IOException {
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        om.writeValue(sw, object);
        return sw.toString();
    }

    public Object fromJson(String json, Class c) {
        ObjectMapper om = new ObjectMapper();
        Object result;
        try {
            result = om.readValue(json, c);
        } catch (IOException ex) {
            result = null;
        }

        return result;
    }
}
