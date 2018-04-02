package wirels.sms.gateway.utils;

import java.util.ArrayList;
import java.util.List;

public class ApplicationConstants {
	
    public static List<String> ignoredRecords;
    public static final String POST = "post.transaction"; 
    public static final String PROPERTIES_FILE = "application.properties";
    public static final String ORACLE_POOL = "oraclePool";
    public static final String MYSQL_POOL = "mysqlPool";
    public static final String POOLING_DRIVER = "jdbc:apache:commons:dbcp:";
    public static final String POOLING_DRIVER_MANAGER = "org.apache.commons.dbcp.PoolingDriver";
    public static final String RESTART_ME = "PLEASERESTARTME!";
	
	public static void setup(){
		ignoredRecords = new ArrayList<String>();
		ignoredRecords.add("HDR");
		ignoredRecords.add("FTR");
	}
	
	public static String getPathOfApp(){
        return System.getProperty("user.dir");
    }

}
