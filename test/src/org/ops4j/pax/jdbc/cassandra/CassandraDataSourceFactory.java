/**
 *   @author longmingfeng 2017年4月5日 下午3:50:30
 *   Email: yxlmf@126.com 
 */
package org.ops4j.pax.jdbc.cassandra;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.service.jdbc.DataSourceFactory;

import com.dbschema.CassandraJdbcDriver;

/**
 * 
 * @author longmingfeng 2017年4月5日 下午3:50:30
 */
@SuppressWarnings("restriction")
public class CassandraDataSourceFactory implements DataSourceFactory {
    public DataSource createDataSource(Properties props)
        throws SQLException {
        CassandraDataSource ds = new CassandraDataSource();
        setProperties(ds, props);
        return ds;
    }

    private void setProperties(CassandraDataSource ds, Properties properties) throws SQLException {
        Properties props = (Properties) properties.clone();
        String url = (String) props.remove("url");
        if (url != null) {
            ds.setUrl(url);
        }

        String password = (String) props.remove("password");
        ds.setPassword(password);

        String portNumber = (String) props.remove("portNumber");
        if (portNumber != null) {
            ds.setPortNumber(Integer.parseInt(portNumber));
        }

        String user = (String) props.remove("userName");
        ds.setUsername(user);

        if (!props.isEmpty())
            throw new SQLException("cannot set properties " + props.keySet());
    }

    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props)
        throws SQLException {
        CassandraDataSource ds = new CassandraDataSource();
        setProperties(ds, props);
        return null;
    }

    public XADataSource createXADataSource(Properties props) throws SQLException {
        CassandraDataSource ds = new CassandraDataSource();
        setProperties(ds, props);
        return ds;
    }

    public java.sql.Driver createDriver(Properties props) throws SQLException {
        CassandraJdbcDriver driver = new CassandraJdbcDriver();
        return driver;
    }
}
