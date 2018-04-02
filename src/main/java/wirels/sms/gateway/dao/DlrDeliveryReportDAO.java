/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author mokhina
 */
public class DlrDeliveryReportDAO {
    private static final Logger LOGGER = Logger.getLogger(MoMessageOutDAO.class);
    private static String createDLRSQL = "Insert into dlr_delivery_report set dlr_stat = ?, dlr_sub = ?, dlr_dlvrd = ?, dlr_submit_date = ?, dlr_done_date = ?, dlr_err = ?, dlr_text = ?";
    
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
   
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplateObject = new JdbcTemplate(this.dataSource);
    }
    
    public int createDlr(String deliveryStatus, String sub, String dlvrl, String subDate, String doneDate, String err, String text) throws DataAccessException,Exception{
        int id = 0;
        try {
            LOGGER.debug(createDLRSQL);
            id = jdbcTemplateObject.queryForInt(createDLRSQL, deliveryStatus, sub, dlvrl, subDate, doneDate, err, text);
            return id;
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
            throw e;
        }
        
    }
    
}
