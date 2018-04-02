/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
/**
 *
 * @author Tebogo Legoabe
 */
public class MOSMSMapper implements RowMapper<MOSMSBean>{

    @Override
    public MOSMSBean mapRow(ResultSet rs, int i) throws SQLException {
        MOSMSBean mOSMSBean = new MOSMSBean();
        mOSMSBean.setMoUID(rs.getLong("MO_UID"));
        mOSMSBean.setMoSender(rs.getString("MO_SENDER"));
        mOSMSBean.setMoRecipient(rs.getString("MO_RECIPIENT"));
        mOSMSBean.setMoSMSMessage(rs.getString("MO_SMS_MESSAGE"));
        return mOSMSBean;
    }
    
}
