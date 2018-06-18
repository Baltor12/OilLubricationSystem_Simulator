package fi.tut.escop.ols;

import fi.tut.escop.ols.discovery.MulticastListener;
import fi.tut.escop.ols.constants.HostPortandConfig;
import fi.tut.escop.ols.elements.Registry;
import fi.tut.escop.ols.ontology.RegisterSavedSystem;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Simulator {

    private static final Logger LOG = Logger.getLogger(Simulator.class.getName());

    public static void main(String[] args) throws ParseException, IOException, URISyntaxException, org.json.simple.parser.ParseException, InterruptedException {

        String property = "server.host";
        String value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.ROOT_ADDRESS = value;
            HostPortandConfig.updateRoot();
        }

        property = "server.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.ROOT_PORT = value;
            HostPortandConfig.updateRoot();
        }

        property = "rpl.host";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.RPL_ROOT_ADDRESS = value;
            HostPortandConfig.updateRPLRoot();
        }

        property = "rpl.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.RPL_ROOT_PORT = value;
            HostPortandConfig.updateRPLRoot();
        }

        property = "fuseki.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.FUSEKI_ENDPOINT_PORT = value;
        }

        property = "probing.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.PROBING_PORT = Integer.valueOf(value);
        }

        property = "probing.group";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.PROBING_GROUP = value;
        }

        property = "hello.port";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.HELLO_PORT = Integer.valueOf(value);
        }

        property = "hello.group";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.HELLO_GROUP = value;
        }

        property = "discovery";
        value = System.getProperty(property);
        if (value != null) {
            HostPortandConfig.DISCOVERY = value.equalsIgnoreCase("true");
        }

        SpringApplication.run(Simulator.class, args);

        RegisterSavedSystem.registerLubricationSystems(); // while Starting the Simulator reading all the saved sytem from ontology and saving it to Simulator Registry

        Double startValue = 1.3;
        Integer startCode = 7;
        Registry.isoCodes.clear();
        while (startCode < 29) {
            if (startCode > 6) {
                startValue = (double) Math.round(startValue * 100) / 100;
                Registry.isoCodes.put(startCode, startValue);
            }
            startCode = startCode + 1;
            startValue = startValue + startValue;

        }
        LOG.log(Level.INFO,
                "\n*************************************************************\n"
                + "***********************CONFIGURATION*************************\n"
                + "*************************************************************\n"
                + "1. Self: {0}\n"
                + "2. Discovery {5}\n"
                + "3. Probing config: PORT - {1}; GROUP - {2}\n"
                + "4. Hello config: PORT - {3}; GROUP - {4}\n"
                + "*************************************************************",
                new Object[]{
                    HostPortandConfig.ROOT_URL,
                    HostPortandConfig.PROBING_PORT,
                    HostPortandConfig.PROBING_GROUP,
                    HostPortandConfig.HELLO_PORT,
                    HostPortandConfig.HELLO_GROUP,
                    HostPortandConfig.status[(HostPortandConfig.DISCOVERY) ? 1 : 0]
                }
        );

        if (HostPortandConfig.DISCOVERY) {
            Thread t = new Thread(new MulticastListener());
            t.start();
        }

    }
}
