/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.processors;

import wirels.sms.gateway.dao.DlrDeliveryReportDAO;
import wirels.sms.gateway.dao.MoMessageOutDAO;
import wirels.sms.gateway.service.SMPPProducerHandler;
import java.util.List;
import java.util.Map;
import org.apache.camel.*;
import org.apache.camel.component.smpp.SmppConstants;
import org.apache.camel.component.smpp.SmppMessageType;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Tebogo Legoabe
 */
public class ReceiveSMProcessor implements Processor {

    private static final Logger LOGGER = Logger.getLogger(ReceiveSMProcessor.class);

    private MoMessageOutDAO moMessageOutDAO;

    public void setMoMessageOutDAO(MoMessageOutDAO moMessageOutDAO) {
        this.moMessageOutDAO = moMessageOutDAO;
    }
    
    private DlrDeliveryReportDAO dlrDeliveryReportDAO;

    public void setMoMessageOutDAO(DlrDeliveryReportDAO dlrDeliveryReportDAO) {
        this.dlrDeliveryReportDAO = dlrDeliveryReportDAO;
    }

    @Override
    public void process(Exchange exchng) throws Exception {
        try {

            if (exchng.getIn().getHeader(SmppConstants.MESSAGE_TYPE) == SmppMessageType.DeliveryReceipt.toString()) {
                String smscDeliveryReceiptText = exchng.getIn().getBody().toString();
                LOGGER.debug(smscDeliveryReceiptText);
                String deliveryStatus = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("stat:"), smscDeliveryReceiptText.indexOf(" err:")).replace("stat:", "").toLowerCase().trim();
                String msgId = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("id:"), smscDeliveryReceiptText.indexOf(" sub:")).replace("id:", "").trim();
                String sub = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("sub:"), smscDeliveryReceiptText.indexOf(" dlvrd:")).replace("sub:", "").trim();
                String dlvrl = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("dlvrd:"), smscDeliveryReceiptText.indexOf(" submit date:")).replace("dlvrd:", "").trim();
                String subDate = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("submit date:"), smscDeliveryReceiptText.indexOf(" done date:")).replace("submit date:", "").trim();
                String doneDate = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("done date:"), smscDeliveryReceiptText.indexOf(" stat:")).replace("done date:", "").trim();
                String err = smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("err:"), smscDeliveryReceiptText.indexOf(" text:")).replace("err:", "").trim();
                String text = StringEscapeUtils.escapeJava(smscDeliveryReceiptText.substring(smscDeliveryReceiptText.indexOf("text:")).replace("text:", "")).trim();
                //use injected DAO object to invoke the createDLr method
                int dlrID = dlrDeliveryReportDAO.createDlr(deliveryStatus, sub, dlvrl, subDate, doneDate, err, text);
                //int dlrID = new DlrDeliveryReportDAO().createDlr(deliveryStatus, sub, dlvrl, subDate, doneDate, err, text);
                Map<String, String> optionalParameters = (Map<String, String>) exchng.getIn().getHeader(SmppConstants.OPTIONAL_PARAMETERS);

                for (String key : optionalParameters.keySet()) {
                    if (key.equals("RECEIPTED_MESSAGE_ID")) {
                        String messageId = optionalParameters.get(key).toString().split("/")[1];
                        LOGGER.debug(key + "=" + messageId);
                        if (SMPPProducerHandler.deliveryReceiptMap.containsKey(messageId)) {
                            long sequenceNumber = SMPPProducerHandler.deliveryReceiptMap.get(messageId).longValue();
                            SMPPProducerHandler.deliveryReceiptMap.remove(messageId);
                            LOGGER.info("Delivery receipt received for SMS notification with the mo_uid:" + sequenceNumber + "; message id=" + messageId + "; delivery status: " + deliveryStatus);
                            String status = "";
                            if (deliveryStatus.contains("cannot be delivered - abandoned") || deliveryStatus.toLowerCase().contains("undeliverable")) {
                                status = "unsuccessful";
                                moMessageOutDAO.updateShortMessage(sequenceNumber, status, dlrID);
                            } else if (deliveryStatus.contains("expired")) {
                                status = "expired";
                                moMessageOutDAO.updateShortMessage(sequenceNumber, status, dlrID);
                            } else if (deliveryStatus.contains("delivered")) {
                                status = "successful";
                                moMessageOutDAO.updateShortMessage(sequenceNumber, status, dlrID);
                            } else {
                                status = "unsuccessful";
                                moMessageOutDAO.updateShortMessage(sequenceNumber, status, dlrID);
                            }

                        } else {
                            LOGGER.warn("Message id=" + messageId + " doesn't exist in the delivery receipt map.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
        }
    }

}
