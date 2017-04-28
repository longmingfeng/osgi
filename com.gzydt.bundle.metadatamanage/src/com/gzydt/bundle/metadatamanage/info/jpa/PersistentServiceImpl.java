package com.gzydt.bundle.metadatamanage.info.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;

import com.gzydt.bundle.metadatamanage.info.api.PersistentService;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaData;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaDataField;
import com.gzydt.bundle.metadatamanage.info.util.JDBCUtil;

/**
 * 提取同步实现类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class PersistentServiceImpl implements PersistentService {
    private volatile BundleContext m_context;

    @Override
    public boolean synchronous(String url, String user, String password,
        String type, TypeMetaData metaData, List<TypeMetaDataField> list) {
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                DataSourceFactory dsf = m_context.getService(serviceReference);
                Properties props = new Properties();
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, type);
                props.put(DataSourceFactory.JDBC_URL, url);
                props.put(DataSourceFactory.JDBC_USER, user);
                props.put(DataSourceFactory.JDBC_PASSWORD, password);
                try {
                    DataSource ds = dsf.createDataSource(props);
                    JDBCUtil.executeQuery(ds, type, metaData, list, true);
                    return true;
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    @Override
    public List<TypeMetaDataField> extract(String url, String user, String password, String type,
        String tableName, String id) {
        List<TypeMetaDataField> metaDataFields = new ArrayList<>();
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                DataSourceFactory dsf = m_context.getService(serviceReference);
                Properties props = new Properties();
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, type);
                props.put(DataSourceFactory.JDBC_URL, url);
                props.put(DataSourceFactory.JDBC_USER, user);
                props.put(DataSourceFactory.JDBC_PASSWORD, password);
                try {
                    DataSource ds = dsf.createDataSource(props);
                    metaDataFields = JDBCUtil.getField(ds, type, tableName, id);
                    return metaDataFields;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return metaDataFields;
    }

    @SuppressWarnings("unchecked")
    private ServiceReference<DataSourceFactory>[] getDataSourceFactoryRef() {
        ServiceReference<DataSourceFactory>[] refs = null;
        try {
            refs = (ServiceReference<DataSourceFactory>[]) m_context
                .getServiceReferences(DataSourceFactory.class.getName(),
                    "(&(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS
                        + "=*" + ")("
                        + DataSourceFactory.OSGI_JDBC_DRIVER_NAME
                        + "=*))");
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        return refs;
    }

    @Override
    public List<String> getTables(String url, String user, String password,
        String type) {
        List<String> list = new ArrayList<>();
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                DataSourceFactory dsf = m_context.getService(serviceReference);
                Properties props = new Properties();
                if (url == null || user == null || password == null || type == null) { return list; }
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, type);
                props.put(DataSourceFactory.JDBC_URL, url);
                props.put(DataSourceFactory.JDBC_USER, user);
                props.put(DataSourceFactory.JDBC_PASSWORD, password);
                try {
                    DataSource ds = dsf.createDataSource(props);
                    list = JDBCUtil.getTables(ds, type, url.substring(url.lastIndexOf("/") + 1));
                    return list;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getTableFiled(String url, String user, String password,
        String type, String tableName, String id) {
        List<String> list = new ArrayList<>();
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                DataSourceFactory dsf = m_context.getService(serviceReference);
                Properties props = new Properties();
                if (url == null || user == null || password == null || type == null || tableName == null) { return list; }
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, type);
                props.put(DataSourceFactory.JDBC_URL, url);
                props.put(DataSourceFactory.JDBC_USER, user);
                props.put(DataSourceFactory.JDBC_PASSWORD, password);
                try {
                    DataSource ds = dsf.createDataSource(props);
                    List<TypeMetaDataField> fields = JDBCUtil.getField(ds, type, tableName, id);
                    for (TypeMetaDataField typeMetaDataField : fields) {
                        list.add(typeMetaDataField.getName());
                    }
                    return list;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return list;
    }

    @Override
    public List<List<String>> getTableValue(String url, String user,
        String password, String type, String tableName, List<String> fields) {
        List<List<String>> list = new ArrayList<>();
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                DataSourceFactory dsf = m_context.getService(serviceReference);
                Properties props = new Properties();
                if (url == null || user == null || password == null || type == null || tableName == null) { return list; }
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, type);
                props.put(DataSourceFactory.JDBC_URL, url);
                props.put(DataSourceFactory.JDBC_USER, user);
                props.put(DataSourceFactory.JDBC_PASSWORD, password);
                try {
                    DataSource ds = dsf.createDataSource(props);
                    list = JDBCUtil.getTableValue(ds, type, tableName, fields);
                    return list;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return list;
    }

    @Override
    public List<List<String>> getTableValueByPage(String url, String user,
        String password, String type, String tableName,
        List<String> fields, int pageSize, int pageNum) {
        List<List<String>> list = new ArrayList<>();
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                DataSourceFactory dsf = m_context.getService(serviceReference);
                Properties props = new Properties();
                if (url == null || user == null || password == null || type == null || tableName == null) { return list; }
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, type);
                props.put(DataSourceFactory.JDBC_URL, url);
                props.put(DataSourceFactory.JDBC_USER, user);
                props.put(DataSourceFactory.JDBC_PASSWORD, password);
                try {
                    DataSource ds = dsf.createDataSource(props);
                    list = JDBCUtil.getTableByPage(ds, type, tableName, fields, pageSize, pageNum);
                    return list;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return list;
    }

}
