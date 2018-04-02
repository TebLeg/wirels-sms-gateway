/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.log4j.Logger;

/**
 *
 * @author Tebogo Legoabe
 */
public class SMPPProducerHandler implements Processor{
    
    private static final Logger LOGGER = Logger.getLogger(SMPPProducerHandler.class);
    public static List<Endpoint> smppProducerEndpointList = new ArrayList<>();
    public static ConcurrentHashMap<String,Long> deliveryReceiptMap = new ConcurrentHashMap<>(16, 0.9f, 2);
    private static boolean processing = false;
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void process(Exchange exchng) throws Exception {

        try {
            if (smppProducerEndpointList.isEmpty()) {
                for(Route route : exchng.getContext().getRoutes()) {
                    if (route.toString().contains("smpp") && route.toString().contains("systemType=producer")) {
                        LOGGER.debug("route=" + route.getEndpoint());
                        smppProducerEndpointList.add(route.getEndpoint());
                    }   
                }
                LOGGER.debug(smppProducerEndpointList);
                LOGGER.info(smppProducerEndpointList.size() + " SMPP endpoint(s) to send SM notifications to.");
                if (smppProducerEndpointList.isEmpty()) {
                    throw new Exception("No SMPP outbound endpoint.");
                }
            } 
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
        }
    }

    public synchronized static void setProcessing(boolean processing) {
        SMPPProducerHandler.processing = processing;
    }

    public synchronized static boolean isProcessing() {
        return processing;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }
        
}
