/**
 *   @author longmingfeng 2017年4月5日 下午4:36:43
 *   Email: yxlmf@126.com 
 */
package org.ops4j.pax.jdbc.cassandra;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.dbschema.CassandraJdbcDriver;

/**
 * 
 * @author longmingfeng 2017年4月5日 下午4:36:43
 */
@SuppressWarnings("restriction")
public class CassandraDataSource implements XADataSource, DataSource, 
    ConnectionPoolDataSource, Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    private transient PrintWriter logWriter;
    private String username = "";
    private String password = "";
    private String url = "";
    private int portNumber;
    private int loginTimeout;
    
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = seconds;

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        CassandraJdbcDriver driver = new CassandraJdbcDriver();
        
        Properties paramProperties = new Properties();
        paramProperties.put("user", this.getUsername());
        paramProperties.put("password", this.getPassword());
        
        Connection con = driver.connect(url, paramProperties);
        return con;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        CassandraJdbcDriver driver = new CassandraJdbcDriver();
        
        Properties paramProperties = new Properties();
        paramProperties.put("user", username);
        paramProperties.put("password", password);
        
        Connection con = driver.connect(url, paramProperties);
        return con;
    }

    @Override
    public XAConnection getXAConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
