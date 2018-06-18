/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.ontology;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.elements.FlowMeter;
import fi.tut.escop.ols.elements.LubricationSystem;
import fi.tut.escop.ols.elements.LubricationUnit;
import fi.tut.escop.ols.elements.MeasuringStation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that operates with Ontology
 *
 * @author Balaji Gopalakrishnan(TUT)
 */
public class OntologyManager {

    private static final String myUrl = HostPortandConfig.ROOT_SCHEME + HostPortandConfig.ROOT_ADDRESS + ":" + HostPortandConfig.FUSEKI_ENDPOINT_PORT;
    private static final String fusekiServerEndpoint = myUrl + "/OLS";
    private static final Logger LOG = Logger.getLogger(OntologyManager.class.getName());

    //Remove !!
    private static final String PREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
            + "PREFIX OLS:<http://www.escop-project.eu/OLS.owl#>\n";

    public static HashMap<String, HashMap<String, String>> querySavedLubricationSystem() {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQuerySavedLubricationSystem();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?ls");
            variables.add("?id");
            variables.add("?lu");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    public static HashMap<String, HashMap<String, String>> querySavedLubricationUnit() {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQuerySavedLubricationUnit();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?lu");
            variables.add("?id");
            variables.add("?tankCapacity");
            variables.add("?filterCapacity");
            variables.add("?maxFlow");
            variables.add("?ms");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    public static HashMap<String, HashMap<String, String>> querySavedMeasuringStation() {
        try {
            ArrayList<String> variables = new ArrayList<>();
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQuerySavedMeasuringStation();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?ms");
            variables.add("?id");
            variables.add("?fm");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    public static HashMap<String, HashMap<String, String>> querySavedFlowMeter() {
        try {
            ArrayList<String> variables = new ArrayList<>();            
            String service = fusekiServerEndpoint + "/query";
            String query = SparqlQueryFactory.sparqlQuerySavedFlowMeter();
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService(service, query);
            variables.add("?fm");
            variables.add("?id");
            variables.add("?nomFlow");
            return sparqlTable(queryExecution, variables);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            return null;
        }
    }

    public static void insertLubricationSystem(LubricationSystem ls) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateLubricationSystem(ls.getId());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }
    }
    
    public static void insertLubricationUnitIntoLubricationSystem(String lsId, String luId) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateSpecificLubricationSystem(lsId, luId);
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }
    }

    public static void insertLubricationUnit(LubricationUnit lu) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateLubricationUnit(lu.getId(), lu.getTankCapacity(), lu.getFilterCapacity(), lu.getMaxOilFlow());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }
    }

    public static void insertMeasuringStationIntoLubricationUnit(String luId, String msId) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateSpecificLubricationUnit(luId, msId);
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }
    }

    public static void insertMeasuringStation(MeasuringStation ms) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateMeasuringStation(ms.getId());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }

    }

    public static void insertFlowMeterIntoMeasuringStation(String msId, String fmId) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateSpecificMeasuringStation(msId, fmId);
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }

    }

    public static void insertFlowMeter(FlowMeter fm) {
        try {
            String update = SparqlQueryFactory.sparqlUpdateFlowMeter(fm.getId(), fm.getNomFlow());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }

    }

    public static Boolean deleteLubricationSystem(LubricationSystem ls) {
        Boolean result = false;
        try {
            for (String lubricationUnit : ls.getLuList().keySet()) {
                deleteLubricationUnit(ls.getLuList().get(lubricationUnit));
            }
            String update = SparqlQueryFactory.sparqlDeleteLubricationSystem(ls.getId());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
            result = false;
        }
        return result;
    }

    public static void deleteLubricationUnit(LubricationUnit lu) {
        try {
            for (String measuringStation : lu.getMsList().keySet()) {
                deleteMeasuringStation(lu.getMsList().get(measuringStation));
            }
            String update = SparqlQueryFactory.sparqlDeleteLubricationUnit(lu.getId());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }
    }

    public static void deleteMeasuringStation(MeasuringStation ms) {
        try {
            for (String flowMeter : ms.getFmList().keySet()) {
                deleteFlowMeter(ms.getFmList().get(flowMeter));
            }
            String update = SparqlQueryFactory.sparqlDeleteMeasuringStation(ms.getId());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }

    }

    public static void deleteFlowMeter(FlowMeter fm) {
        try {
            String update = SparqlQueryFactory.sparqlDeleteFlowMeter(fm.getId());
            UpdateRequest request = UpdateFactory.create(update);
            UpdateExecutionFactory.createRemote(request, fusekiServerEndpoint + "/update").execute();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "connection to fuseki failed. " + e);
        }
    }

    public static void sparqlHelp(QueryExecution queryExecution, String valueLabel, ArrayList resultArray) {
        ResultSet result = queryExecution.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            RDFNode node = soln.get(valueLabel);
            if (node != null) {
                resultArray.add(correctString(node.toString().replace("http://www.escop-project.eu/OLS.owl#", "")));
            }
        }
        queryExecution.close();
    }

    public static void sparqlMultipleOutput(QueryExecution queryExecution, HashMap<String, ArrayList<String>> variables) {
        ResultSet result = queryExecution.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            for (String valueLabel : variables.keySet()) {
                ArrayList<String> value = new ArrayList<>();
                RDFNode node = soln.get(valueLabel);
                if (node != null) {
                    value.add(correctString(node.toString().replace("http://www.escop-project.eu/OLS.owl#", "")));
                }
                variables.put(valueLabel, value);
            }
        }
        queryExecution.close();
    }

    public static HashMap<String, HashMap<String, String>> sparqlTable(QueryExecution queryExecution, ArrayList<String> variables) {
        ResultSet result = queryExecution.execSelect();
        HashMap<String, HashMap<String, String>> reply = new HashMap<>();
        int increment = 1;
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            HashMap<String, String> value = new HashMap<>();
            for (String valueLabel : variables) {
                RDFNode node = soln.get(valueLabel);
                if (node != null) {
                    value.put(valueLabel.replace("?", ""), correctString(node.toString().replace("http://www.escop-project.eu/OLS.owl#", "")));
                }
            }
            reply.put("Key_" + increment, value);
            increment++;
        }
        queryExecution.close();
        return reply;
    }

    public static String correctString(String str) {
        if (str.indexOf('^') != -1) {
            str = str.substring(0, str.indexOf('^'));
        }
        return str;
    }

}
