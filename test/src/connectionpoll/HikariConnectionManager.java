/**
 *   @author longmingfeng    2016年12月27日  下午5:34:04
 *   Email: yxlmf@126.com 
 */
package connectionpoll;

import java.sql.Connection;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author longmingfeng 2016年12月27日 下午5:34:04
 */
public class HikariConnectionManager {

    public static void main(String[] args) throws Exception {
        
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(5);
        config.setDataSourceClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("port", "3306");
        config.addDataSourceProperty("databaseName", "infoshare2");
        config.addDataSourceProperty("user", "root");
        config.addDataSourceProperty("password", "root");

        HikariDataSource ds = new HikariDataSource(config);

        Connection conn = ds.getConnection();

        Properties p = conn.getClientInfo();
        //conn.close();
      
        Thread.sleep(600000);

    }
}
