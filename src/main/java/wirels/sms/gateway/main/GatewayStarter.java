/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.main;

import java.util.Date;
import org.apache.camel.spring.Main;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author Tebogo Legoabe
 */
public class GatewayStarter {

    /**
     * @param args the command line arguments
     */
    private static final Logger LOGGER = Logger.getLogger(GatewayStarter.class);
	
    // Spring Camel Main object
    private Main smsGW;
    private static final String LOG4J_FILE = "log4j.xml";
    private static final String CAMEL_CONTEXT_FILE = "camel-context.xml";
    private static final String FILE = "file:///";
    private static String SOFTWARE_DIR;
    private static final String CONF_DIR = "../conf/";
    private static final int EXIT = 1;
    
    private static PropertiesConfiguration properties = new PropertiesConfiguration();

    public static PropertiesConfiguration getProperties() {
        return properties;
    }
    
    public GatewayStarter() {
        try {
            
        } catch (Exception e) {
            System.out.println("Log4j Setup issue : " + e.getMessage());
            LOGGER.info("======== Service exit. =========");
            System.exit(EXIT);
        }
	//ApplicationConstants.setup();
        try {
            properties.setDelimiterParsingDisabled(true);
            properties.load("application.properties");
            properties.setReloadingStrategy(new FileChangedReloadingStrategy());
            DOMConfigurator.configureAndWatch("application.properties", 10000);
        } catch (Exception e) {
            LOGGER.error("Exception :" + e.getMessage());
            LOGGER.error("Service exiting");
            System.exit(0);
        }
    }
    
    public void boot() throws Exception {
        smsGW = new Main();
        try{
            smsGW.setFileApplicationContextUri(CONF_DIR+CAMEL_CONTEXT_FILE);
        } catch (Exception e) {
            LOGGER.error("Cannot bootstrap process : " + e.getMessage());
            LOGGER.info("======== Service exit. =========");
	    System.exit(EXIT);
        }
        smsGW.enableHangupSupport();

        
        try {
            LOGGER.info("======== Service start " + new Date() + " =========");
            LOGGER.info("Operational - CHECKING FOR SMSQUEUE REQUESTS");
            smsGW.run();
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
        }

    }
    
    public static void main(String[] args) {
        try {
            GatewayStarter main = new GatewayStarter();
            main.boot();
        } catch (Exception ex) {
            LOGGER.error("Exception: " + ex.getMessage());
            LOGGER.info("======== Service exit. =========");
            System.exit(EXIT);
        }
    }
}
