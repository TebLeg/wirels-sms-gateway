/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.dao;

/**
 *
 * @author Tebogo Legoabe
 */
public class MOSMSBean {
    private long moUID;
    private String moSender;
    private String moRecipient;
    private String moSMSMessage;

    public MOSMSBean(long moUID, String moSender, String moRecipient, String moSMSMessage) {
        this.moUID = moUID;
        this.moSender = moSender;
        this.moRecipient = moRecipient;
        this.moSMSMessage = moSMSMessage;
    }

    public MOSMSBean() {
    }

    
    /**
     * @return the moUID
     */
    public long getMoUID() {
        return moUID;
    }

    /**
     * @param moUID the moUID to set
     */
    public void setMoUID(long moUID) {
        this.moUID = moUID;
    }

    /**
     * @return the moSender
     */
    public String getMoSender() {
        return moSender;
    }

    /**
     * @param moSender the moSender to set
     */
    public void setMoSender(String moSender) {
        this.moSender = moSender;
    }

    /**
     * @return the moRecipient
     */
    public String getMoRecipient() {
        return moRecipient;
    }

    /**
     * @param moRecipient the moRecipient to set
     */
    public void setMoRecipient(String moRecipient) {
        this.moRecipient = moRecipient;
    }

    /**
     * @return the moSMSMessage
     */
    public String getMoSMSMessage() {
        return moSMSMessage;
    }

    /**
     * @param moSMSMessage the moSMSMessage to set
     */
    public void setMoSMSMessage(String moSMSMessage) {
        this.moSMSMessage = moSMSMessage;
    }

    @Override
    public String toString() {
        return "MOSMSBean{" + "moUID=" + moUID + ", moSender=" + moSender + ", moRecipient=" + moRecipient + ", moSMSMessage=" + moSMSMessage + '}';
    }
    
    
}
