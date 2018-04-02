/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.service;

import wirels.sms.gateway.dao.MOSMSBean;
import wirels.sms.gateway.dao.MoMessageOutDAO;
import wirels.sms.gateway.main.GatewayStarter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.*;
import org.apache.camel.component.smpp.SmppConstants;
import org.apache.log4j.Logger;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUStringException;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;

/**
 *
 * @author Tebogo Legoabe
 */
public class SMSRetriever implements Processor{
    
    private static final Logger LOGGER = Logger.getLogger(SMSRetriever.class);

    private MoMessageOutDAO moMessageOutDAO;
    
    public void setMoMessageOutDAO(MoMessageOutDAO moMessageOutDAO) {
        this.moMessageOutDAO = moMessageOutDAO;
    } 

    private long moUID;
    private Exchange exchange;
    private ProducerTemplate template;
    private Endpoint endpoint;
    private int count;

    @Override
    public void process(Exchange exchng) throws Exception {
        
        try {
            retrieveSMSRecords();          
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
        }        
    }
    
    private void retrieveSMSRecords() throws Exception {
        LOGGER.info("Going to retrieve SM records.");
        try {
            if (SMPPProducerHandler.isProcessing()) {
                LOGGER.warn("Still processing the current batch. This has to complete first.");
            } else {
                List<MOSMSBean> mOSMSBeanList = moMessageOutDAO.getShortMessages();
                if (mOSMSBeanList.isEmpty()) {
                    LOGGER.info("No short messages to send.");
                } else {
                    SMPPProducerHandler.setProcessing(true);
                    LOGGER.info(mOSMSBeanList.size() + " total SMS notification(s) retrieved and to be sent.");
                    int smppProducerCount = 0;
                    count = 1;
            
                    for (MOSMSBean moSMSBean : mOSMSBeanList) {
                        endpoint = SMPPProducerHandler.smppProducerEndpointList.get(smppProducerCount);
                        send(moSMSBean);
                        count++;
                        if (smppProducerCount == (SMPPProducerHandler.smppProducerEndpointList.size() - 1)) {
                            smppProducerCount = 0;
                        } else {
                            smppProducerCount++;
                        }  
                        //We control the rate at which the SMS notifications are sent.
                        Thread.sleep(Long.valueOf(GatewayStarter.getProperties().getString("sleep.timer")));
                    }
                    SMPPProducerHandler.setProcessing(false);
                }
            }
        } catch (Exception e) {
            LOGGER.info("Exception: " + e.getMessage());
            e.printStackTrace();
            SMPPProducerHandler.setProcessing(false);
            throw e;
        }
    }

    private void send(MOSMSBean mOSMSBean) throws PDUStringException,
            ResponseTimeoutException, InvalidResponseException,
            NegativeResponseException, IOException, Exception {
        try {    

            LOGGER.debug(mOSMSBean.toString());
            moUID = mOSMSBean.getMoUID();

            exchange = endpoint.createExchange(ExchangePattern.InOnly);          
            exchange.getIn().setHeader(SmppConstants.SERVICE_TYPE, null);
            exchange.getIn().setHeader(SmppConstants.COMMAND, "SubmitSm");
            exchange.getIn().setHeader(SmppConstants.DEST_ADDR_TON, 1);
            exchange.getIn().setHeader(SmppConstants.DEST_ADDR_NPI, 1);
            exchange.getIn().setHeader(SmppConstants.DEST_ADDR, mOSMSBean.getMoRecipient());
            exchange.getIn().setBody(mOSMSBean.getMoSMSMessage());
            exchange.getIn().setHeader(SmppConstants.SEQUENCE_NUMBER, moUID);
            exchange.getIn().setHeader(SmppConstants.ID, moUID);
            // Produce 

            template = exchange.getContext().createProducerTemplate();

            moMessageOutDAO.updateShortMessage(moUID,"pending",0);
            
            SMPPProducerHandler.getExecutorService().execute(new Runnable() {
                public void run() {
                    try {
                        template.send(endpoint, exchange);
                        
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage());
                    }
                }
            });

            LOGGER.info("SM sent to:" + mOSMSBean.getMoRecipient() + "; sequence number=" +moUID+ "; count="+count);
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }
}
