/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.processors;

import wirels.sms.gateway.service.SMPPProducerHandler;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.component.smpp.SmppConstants;
import org.apache.log4j.Logger;

/**
 *
 * @author Tebogo Legoabe
 */
public class SubmitSMProcessor implements Processor{

    private static final Logger LOGGER = Logger.getLogger(SubmitSMProcessor.class);
    
    @Override
    public void process(Exchange exchange) throws Exception {
        try {           
            long sequenceNumber = Long.parseLong(exchange.getIn().getHeader(SmppConstants.SEQUENCE_NUMBER).toString());
            List<String> messageIds = (List<String>) exchange.getIn().getHeader(SmppConstants.ID);
            String messageId = messageIds.get(0).split("/")[1];
            SMPPProducerHandler.deliveryReceiptMap.put(messageId, sequenceNumber);
            LOGGER.info("Submit_sm response received with mo_uid=" + sequenceNumber + "; message id=" + messageId);
        } catch (Exception e) {
            LOGGER.debug("Exception: " + e.getMessage());
            throw e;
        }
    }
    
}
