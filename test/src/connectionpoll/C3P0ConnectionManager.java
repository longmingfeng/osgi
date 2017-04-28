/**
 *   @author longmingfeng    2016年12月27日  下午5:34:04
 *   Email: yxlmf@126.com 
 */
package connectionpoll;

import java.sql.Connection;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
 * @author longmingfeng 2016年12月27日 下午5:34:04
 */
public class C3P0ConnectionManager {

    public static void main(String[] args) throws Exception {
        
        ComboPooledDataSource ds = new ComboPooledDataSource();
        
        ds.setUser("root");
        ds.setPassword("root");
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/infoshare2?autoReconnect=true&useUnicode=true&characterEncoding=GB2312");
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setInitialPoolSize(5);
        ds.setMinPoolSize(2);
        ds.setMaxPoolSize(4);
        ds.setMaxStatements(20);
        ds.setMaxIdleTime(60);

        Connection conn = ds.getConnection();

        //conn.close();
      
        Thread.sleep(600000);
        
        //DataSources.destroy(ds);

    }
}
