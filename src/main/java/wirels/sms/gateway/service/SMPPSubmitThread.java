/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.service;

import wirels.sms.gateway.dao.MOSMSBean;
import wirels.sms.gateway.dao.MoMessageOutDAO;
import wirels.sms.gateway.main.GatewayStarter;
import java.io.IOException;
import java.util.List;
import org.apache.camel.*;
import org.apache.camel.component.smpp.SmppConstants;
import org.apache.camel.component.smpp.SmppException;
import org.apache.log4j.Logger;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUStringException;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;

/**
 *
 * @author Tebogo Legoabe
 */
public class SMPPSubmitThread extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(SMPPSubmitThread.class);
    private Endpoint endpoint; 
    private Exchange exchange;
    private List<MOSMSBean> mOSMSBeanList;
    public final static int THREADPASS = 0;
    public final static int THREADFAIL = 1;
    int status;
    
    private MoMessageOutDAO moMessageOutDAO;
    
    public SMPPSubmitThread(Endpoint endpoint, List<MOSMSBean> mOSMSBeanList, MoMessageOutDAO moMessageOutDAO) throws Exception {
        this.endpoint = endpoint;
        this.mOSMSBeanList = mOSMSBeanList;
        this.moMessageOutDAO = moMessageOutDAO;
        status = THREADFAIL;
    }
    
    

    public int getStatus() {
        return status;
    }
    
    
    @Override
    public synchronized void run() {
        try {
            send();
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage()); 
        } 
    }
    
    public void send() throws PDUStringException,
            ResponseTimeoutException, InvalidResponseException,
            NegativeResponseException, IOException, Exception {
        try {    
            
            exchange = endpoint.createExchange(ExchangePattern.InOnly);
            
            exchange.getIn().setHeader(SmppConstants.SERVICE_TYPE, null);
            exchange.getIn().setHeader(SmppConstants.COMMAND, "SubmitSm");

            exchange.getIn().setHeader(SmppConstants.DEST_ADDR_TON, 1);
            exchange.getIn().setHeader(SmppConstants.DEST_ADDR_NPI, 1);
            
            String msisdn = "";
            int count = 1;
            long moUID;
            ProducerTemplate template;
            Producer producer;
            
            for (MOSMSBean mOSMSBean: mOSMSBeanList) {
                LOGGER.debug(mOSMSBean.toString());
                moUID = mOSMSBean.getMoUID();
                msisdn = mOSMSBean.getMoRecipient();
                
                exchange.getIn().setHeader(SmppConstants.DEST_ADDR, msisdn);
                exchange.getIn().setBody(mOSMSBean.getMoSMSMessage());
                exchange.getIn().setHeader(SmppConstants.SEQUENCE_NUMBER, moUID);
                exchange.getIn().setHeader(SmppConstants.ID, moUID);
                // Produce 
                producer = endpoint.createProducer();
                
                moMessageOutDAO.updateShortMessage(moUID,"pending",0);
                producer.process(exchange);
                
                //LOGGER.info("SM sent to:" + msisdn + "; sequence number=" +moUID+ "; count="+count);

                count++;
                //We control the rate at which the SMS notifications are sent.
                Thread.sleep(Long.valueOf(GatewayStarter.getProperties().getString("sleep.timer")));
 
            }

            status = THREADPASS;
        } catch (SmppException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }
    
    
}
