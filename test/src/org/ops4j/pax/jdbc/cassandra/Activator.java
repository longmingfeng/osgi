/**
 *   @author longmingfeng 2017年4月5日 下午2:51:26
 *   Email: yxlmf@126.com 
 */
package org.ops4j.pax.jdbc.cassandra;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.jdbc.DataSourceFactory;

import com.dbschema.CassandraJdbcDriver;

/**
 * 
 * @author longmingfeng 2017年4月5日 下午2:51:26
 */
@SuppressWarnings("restriction")
public class Activator implements BundleActivator {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void start(BundleContext context)
        throws Exception {
        CassandraDataSourceFactory dsf = new CassandraDataSourceFactory();
        Dictionary props = new Hashtable();
        props.put("osgi.jdbc.driver.class", CassandraJdbcDriver.class.getName());
        props.put("osgi.jdbc.driver.name", "cassandra");
        context.registerService(DataSourceFactory.class.getName(), dsf, props);
    }

    public void stop(BundleContext context)
        throws Exception {
    }
}
