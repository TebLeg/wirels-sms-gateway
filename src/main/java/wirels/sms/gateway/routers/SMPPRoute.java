/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.routers;

import wirels.sms.gateway.processors.SendSMProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.jsmpp.session.SMPPSession;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tebogo Legoabe
 */
public class SMPPRoute extends RouteBuilder{

    private static final Logger LOGGER = Logger.getLogger(SMPPRoute.class);
    private static int deleteQueueTime = 0;
    private static int clearQueueTime = 0;
    private static int enquireLinkTimer = 0;
    private static String user = "";
    private static String password = "";
    private static String hostName = "";
    private static int port = 0;
    private SMPPSession session = null;
    
    @Autowired
    private SendSMProcessor sendSMProcessor;

    public SendSMProcessor getSendSMProcessor() {
        return sendSMProcessor;
    }

    public void setSendSMProcessor(SendSMProcessor sendSMProcessor) {
        this.sendSMProcessor = sendSMProcessor;
    }
    
    @Override
    public void configure() throws Exception {
        try {
            
            user = "airtime";
            hostName = "10.220.16.132";
            port = 1080;
            password = "air445";
            enquireLinkTimer = 3;
            //new DefaultCamelContext(); //So that CAMEL creates these route
            //from("direct:sendSM").
                    //setHeader("CamelSmppDestAddr", header("deliveryAddress")).
                    //setHeader("CamelSmppAlphabet", constant(4)).
            //to("smpp://"+user+"@"+hostName+":"+port+"?password="+password+"&amp;registeredDelivery=1&amp;enquireLinkTimer="+enquireLinkTimer+"&amp;transactionTimer=5000&amp;systemType=consumer&amp;destAddr=27843002325&amp;sourceAddr=CellC").
                    //to("smpp://"+user+"@"+hostName+":"+port+"?password="+password+"&amp;registeredDelivery=1&amp;enquireLinkTimer="+enquireLinkTimer+"s&amp;transactionTimer=5000&amp;systemType=producer").
            //processRef("sendSMProcessor");
            //System.out.println("SMMP route created.");
            
            LOGGER.info("Starting the SMPP route.");
                from("direct:sendSM").routeId("SMPPRoute").
                    to("smpp://airtime@"+hostName+":"+port+"?password=air445&registeredDelivery=1&enquireLinkTimer=5000&transactionTimer=1000&systemType=producer")
                        .processRef("sendSMProcessor");;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }    
    
}
