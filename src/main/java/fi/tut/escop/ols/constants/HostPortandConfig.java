/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.tut.escop.ols.constants;

/**
 * Class consists of the main URLs which works with the Fluid House Simulator
 *
 * @author Balaji Gopalakrishnantor
 */
public class HostPortandConfig {

    //Device Host Port
    public static String ROOT_SCHEME = "http://";
    public static String ROOT_ADDRESS = "localhost";
    public static String ROOT_PORT = "5012";
    public static String ROOT_URL = "http://localhost:5012";
    
    //Fuseki Host Port
    public static String FUSEKI_ENDPOINT_PORT = "5050"; // Fuseki host will always be the device host
    
    public static String RPL_ROOT_SCHEME = "http://";
    public static String RPL_ROOT_ADDRESS = "localhost";
    public static String RPL_ROOT_PORT = "8300";
    public static String RPL_ROOT_URL = "http://localhost:8300";
    
    //Host port for Hello Bye messages
    public static Integer HELLO_PORT = 55555;
    public static String HELLO_GROUP = "239.0.1.1";
    
    //Host port for Probing messages
    public static Integer PROBING_PORT = 55556;
    public static String PROBING_GROUP = "239.0.0.1";
    
    //RPL Available
    public static boolean RPL_AVAILABLE = false; 
    
    public static boolean DISCOVERY = false;
    public static Integer BYE_STATUS = 0;
    
    public static final String[] status = {"disabled", "enabled"};
    
    public static void updateRoot() {
        ROOT_URL = ROOT_SCHEME + ROOT_ADDRESS + ":" + ROOT_PORT;
    }
    
    public static void updateRPLRoot() {
        RPL_ROOT_URL = RPL_ROOT_SCHEME + RPL_ROOT_ADDRESS + ":" + RPL_ROOT_PORT;
    }
}
