/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wirels.sms.gateway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 *
 * @author Tebogo Legoabe
 */
public class MoMessageOutDAO {
    
    private static final Logger LOGGER = Logger.getLogger(MoMessageOutDAO.class);
    private static String getSQL = "Select MO_ID, MO_SOURCE, MO_MSISDN, MO_MSG From MO_MESSAGE_OUT where MO_SS_ID = 1 and MO_CD_ID = 1 and MO_ACTIONED_DATE is null Order By MO_PRIORITY, MO_ID asc";
    private static String updatePendingSQL = "Update MO_MESSAGE_OUT set MO_DATE_ACTIONED = NOW(), MO_SS_ID = 2 Where MO_ID=?";   
    private static String updateSuccessfulDeliverySQL = "Update MO_MESSAGE_OUT set MO_SS_ID = 3, MO_DLR_ID = ? Where MO_ID=?";
    private static String updateUnsuccessfulDeliverySQL = "Update MO_MESSAGE_OUT set MO_SS_ID = 4, MO_DLR_ID = ? Where MO_ID=?";
    private static String updateExpiredDeliverySQL = "Update MO_MESSAGE_OUT set MO_SS_ID = 5, MO_DLR_ID = ? Where MO_ID=?";
    
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
   
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplateObject = new JdbcTemplate(this.dataSource);
    }
    
    public List<MOSMSBean> getShortMessages() throws DataAccessException,Exception{
        
        try {
            List<MOSMSBean> mOSMSBeanList = jdbcTemplateObject.query(getSQL, new MOSMSMapper());
            return mOSMSBeanList;
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
            throw e;
        }
        
    }
    
    public void updateShortMessage(long moID, String status, int dlrID) throws DataAccessException,Exception {
        try {
            switch(status) {
                case "pending":
                    LOGGER.debug(updatePendingSQL);
                    jdbcTemplateObject.update(updatePendingSQL, moID);
                    break;
                case "successful":
                    LOGGER.debug(updateSuccessfulDeliverySQL);
                    jdbcTemplateObject.update(updateSuccessfulDeliverySQL, dlrID, moID);
                    break;
                case "unsuccessful":
                    LOGGER.debug(updateUnsuccessfulDeliverySQL);
                    jdbcTemplateObject.update(updateUnsuccessfulDeliverySQL, dlrID, moID);
                    break;
                case "expired":
                    LOGGER.debug(updateExpiredDeliverySQL);
                    jdbcTemplateObject.update(updateExpiredDeliverySQL, dlrID, moID);
                    break;
                default:
                    throw new Exception("Uknown update status: " + status);
            }           

        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
            throw e;
        }
    }
}
