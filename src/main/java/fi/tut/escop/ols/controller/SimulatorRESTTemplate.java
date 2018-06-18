/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.controller;

import fi.tut.escop.ols.discovery.RegisterationMessage;
import fi.tut.escop.ols.extra.JsonConverters;
import fi.tut.escop.ols.rtuJson.EventPayload;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * REST Template which posts the data to respective subscribers
 *
 * @author Balaji Gopalakrishnan (TUT)
 */
public class SimulatorRESTTemplate {

    private static final Logger LOG = Logger.getLogger(SimulatorRESTTemplate.class.getName());

    public SimulatorRESTTemplate() {   }  
    
    public void sensorValuePOST(EventPayload response, String uri) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        String result = restTemplate.postForObject(uri, response, String.class);
    }

    public void probingResponsePOST(RegisterationMessage response, String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterationMessage> message = new HttpEntity<>(response, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        System.out.println(message);
        String result = restTemplate.postForObject(uri, message, String.class);
    }

    /**
     * Function which send the REST GET request to to the RPL to ping it alive
     * status the reply from RPL will be pong
     *
     * @param uri
     * @return
     */
    public static String pingRPLGET(String uri) {
        String result = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            result = restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
        return result;
    }

    /**
     * Function which send the REST GET request to to the RPL to get the events
     * available for a particular Lubrication Unit
     *
     * @param uri
     * @return
     */
    public static String eventFromRPLGET(String uri) {
        String result = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            result = restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Exception " + e);
        }
        return result;
    }

}
