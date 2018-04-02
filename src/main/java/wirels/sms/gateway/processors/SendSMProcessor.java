/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.processors;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 *
 * @author Tebogo Legoabe
 */
public class SendSMProcessor implements Processor{

    private static final Logger LOGGER = Logger.getLogger(SendSMProcessor.class);
        
    @EndpointInject(uri = "direct:sendSM")
    private Endpoint sendSM;

    public SendSMProcessor() throws Exception {

    }
    
    
    
    @Override
    public void process(Exchange exchng) throws Exception {
        try {
            //System.out.println("EnquireLink sent... " + exchng.getIn().getBody().toString().trim());
            //System.out.println("EnquireLink sent... " + exchng.getOut().getBody().toString().trim());
            LOGGER.debug("EnquireLink sent... ");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * @return the sendSM
     */
    public Endpoint getSendSM() {
        return sendSM;
    }

    /**
     * @param sendSM the sendSM to set
     */
    public void setSendSM(Endpoint sendSM) {
        this.sendSM = sendSM;
    }
}
