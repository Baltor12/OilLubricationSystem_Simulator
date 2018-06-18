/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.ontology;

import java.util.ArrayList;

/**
 * Class That Generates SPARQL Query/Update as per the request from Ontology
 * Manager
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class SparqlQueryFactory {

    private static final String PREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
            + "PREFIX OLS:<http://www.escop-project.eu/OLS.owl#>\n";

    /**
     * Function to generate the SPARQL Query for getting List of already saved
     * Lubrication Systems
     *
     * @return
     */
    public static String sparqlQuerySavedLubricationSystemList() {
        String query = PREFIX;
        query += "SELECT ?ls\n"
                + "WHERE{\n"
                + " ?ls a OLS:LubricationSystem.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting details of saved
     * Lubrication System
     *
     * @return
     */
    public static String sparqlQuerySavedLubricationSystem() {
        String query = PREFIX;
        query += "SELECT ?ls ?id ?lu\n"
                + "WHERE{\n"
                + " ?ls a OLS:LubricationSystem.\n"
                + " ?ls OLS:id ?id.\n"
                + " ?ls OLS:hasLubricationUnit ?lu.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting details of saved
     * Lubrication Unit
     *
     * @return
     */
    public static String sparqlQuerySavedLubricationUnit() {
        String query = PREFIX;
        query += "SELECT ?lu ?name ?id ?filterCapacity ?tankCapacity ?maxFlow ?ms\n"
                + "WHERE{\n"
                + " ?lu a OLS:LubricationUnit.\n"
                + " ?lu OLS:id ?id.\n"
                + " ?lu OLS:flow ?maxFlow.\n"
                + " ?lu OLS:filterCapacity ?filterCapacity.\n"
                + " ?lu OLS:tankCapacity ?tankCapacity.\n"
                + " ?lu OLS:hasMeasuringStation ?ms.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting details of saved
     * Measuring Station
     *
     * @return
     */
    public static String sparqlQuerySavedMeasuringStation() {
        String query = PREFIX;
        query += "SELECT ?ms ?id ?fm\n"
                + "WHERE{\n"
                + " ?ms a OLS:MeasuringStation.\n"
                + " ?ms OLS:id ?id.\n"
                + " ?ms OLS:hasFlowMeter ?fm.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to generate the SPARQL Query for getting details of saved Flow
     * Meter
     *
     * @return
     */
    public static String sparqlQuerySavedFlowMeter() {
        String query = PREFIX;
        query += "SELECT ?fm ?id ?nomFlow\n"
                + "WHERE{\n"
                + " ?fm a OLS:FlowMeter.\n"
                + " ?fm OLS:id ?id.\n"
                + " ?fm OLS:flow ?nomFlow.\n"
                + "}\n";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the Lubrication Systems
     * generated to be saved in the ontology
     *
     * @param lsId
     * @return
     */
    public static String sparqlUpdateLubricationSystem(String lsId) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + lsId + " a OLS:LubricationSystem.\n"
                + " OLS:" + lsId + " OLS:id \"" + lsId + "\"^^xsd:string.\n";
        query += "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the Lubrication Systems
     * generated to be saved in the ontology
     *
     * @param lsId
     * @param luId
     * @return
     */
    public static String sparqlUpdateSpecificLubricationSystem(String lsId, String luId) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + lsId + " OLS:hasLubricationUnit OLS:" + luId + ".\n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the Lubrication Units
     * generated to be saved in the ontology
     *
     * @param luId
     * @param tankCapacity
     * @param filterCapacity
     * @param maxFlow
     * @return
     */
    public static String sparqlUpdateLubricationUnit(String luId, double tankCapacity, double filterCapacity, double maxFlow) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + luId + " a OLS:LubricationUnit.\n"
                + " OLS:" + luId + " OLS:id \"" + luId + "\"^^xsd:string.\n"
                + " OLS:" + luId + " OLS:tankCapacity \"" + tankCapacity + "\"^^xsd:float.\n"
                + " OLS:" + luId + " OLS:filterCapacity \"" + filterCapacity + "\"^^xsd:float.\n"
                + " OLS:" + luId + " OLS:flow \"" + maxFlow + "\"^^xsd:float.\n";
        query += "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the Lubrication Units
     * generated to be saved in the ontology
     *
     * @param luId
     * @param msId
     * @return
     */
    public static String sparqlUpdateSpecificLubricationUnit(String luId, String msId) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + luId + " OLS:hasMeasuringStation OLS:" + msId + ".\n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the Measuring Stations
     * generated to be saved in the ontology
     *
     * @param msId
     * @return
     */
    public static String sparqlUpdateMeasuringStation(String msId) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + msId + " a OLS:MeasuringStation.\n"
                + " OLS:" + msId + " OLS:id \"" + msId + "\"^^xsd:string.\n";
        query += "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the Measuring Stations
     * generated to be saved in the ontology
     *
     * @param msId
     * @param fmId
     * @return
     */
    public static String sparqlUpdateSpecificMeasuringStation(String msId, String fmId) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + msId + " OLS:hasFlowMeter OLS:" + fmId + ".\n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for having the FlowMeters generated to
     * be saved in the ontology
     *
     * @param fmId
     * @param nomFlow
     * @return
     */
    public static String sparqlUpdateFlowMeter(String fmId, double nomFlow) {
        String query = PREFIX;
        query += "INSERT DATA {\n"
                + " OLS:" + fmId + " a OLS:FlowMeter.\n"
                + " OLS:" + fmId + " OLS:id \"" + fmId + "\"^^xsd:string.\n"
                + " OLS:" + fmId + " OLS:flow \"" + nomFlow + "\"^^xsd:float.\n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for Deleting the Lubrication Systems in
     * the ontology
     *
     * @param lsId
     * @return
     */
    public static String sparqlDeleteLubricationSystem(String lsId) {
        String query = PREFIX;
        query += "DELETE { ?ls ?p ?v}\n"
                + " WHERE {\n"
                + " ?ls a OLS:LubricationSystem.\n"
                + " ?ls OLS:id \"" + lsId + "\"^^xsd:string.\n"
                + " ?ls ?p ?v \n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for Deleting the Lubrication Units in
     * the ontology
     *
     * @param luId
     * @return
     */
    public static String sparqlDeleteLubricationUnit(String luId) {
        String query = PREFIX;
        query += "DELETE { ?lu ?p ?v}\n"
                + " WHERE {\n"
                + " ?lu a OLS:LubricationUnit.\n"
                + " ?lu OLS:id \"" + luId + "\"^^xsd:string.\n"
                + " ?lu ?p ?v \n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for Deleting the Measuring Stations in
     * the ontology
     *
     * @param msId
     * @return
     */
    public static String sparqlDeleteMeasuringStation(String msId) {
        String query = PREFIX;
        query += "DELETE { ?ms ?p ?v}\n"
                + " WHERE {\n"
                + " ?ms a OLS:MeasuringStation.\n"
                + " ?ms OLS:id \"" + msId + "\"^^xsd:string.\n"
                + " ?ms ?p ?v \n"
                + "}";
        return query;
    }

    /**
     * Function to perform SPARQL Update for Deleting the Flow Meters in the
     * ontology
     *
     * @param fmId
     * @return
     */
    public static String sparqlDeleteFlowMeter(String fmId) {
        String query = PREFIX;
        query += "DELETE { ?fm ?p ?v}\n"
                + " WHERE {\n"
                + " ?fm a OLS:FlowMeter.\n"
                + " ?fm OLS:id \"" + fmId + "\"^^xsd:string.\n"
                + " ?fm ?p ?v \n"
                + "}";
        return query;
    }
}
