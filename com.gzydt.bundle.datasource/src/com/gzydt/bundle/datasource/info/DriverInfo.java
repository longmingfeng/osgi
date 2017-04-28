package com.gzydt.bundle.datasource.info;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;

/**
 * 数据源信息类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
@SuppressWarnings({ "unchecked", "serial" })
public class DriverInfo extends HttpServlet {

    private volatile BundleContext m_context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if ("getInfo".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getDriverInfo());
        } else if ("getConn".equals(pathInfo.substring(1))) {
            String url = req.getParameter("url");
            String user = req.getParameter("user");
            String psw = req.getParameter("password");
            String type = req.getParameter("type");
            resp.getWriter().print(testConn(url, user, psw, type));
        } else if ("getDS".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getDataSource());
        } else if ("findJpa".equals(pathInfo.substring(1))) {
            String name = req.getParameter("name");
            resp.getWriter().print(getJpa(name));
        } else if ("getBundleInfo".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getBundleInfo());
        } else if ("findColumn".equals(pathInfo.substring(1))) {
            String beanName = req.getParameter("name");
            resp.getWriter().print(getColumn(beanName));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if ("save".equals(pathInfo.substring(1))) {
            Map<String, String> map = new HashMap<>();
            map.put("jdbcUrl", req.getParameter("url"));
            map.put("username", req.getParameter("user"));
            map.put("password", req.getParameter("password"));
            map.put("driverClassName", req.getParameter("type"));
            map.put("name", req.getParameter("dsname"));
            map.put("managed", req.getParameter("jta"));
            map.put("discription", req.getParameter("context"));
            map.put("initialSize", req.getParameter("initNum"));
            map.put("maxTotal", req.getParameter("maxNum"));
            map.put("maximumPoolSize", req.getParameter("maxNum"));
            map.put("maxIdle", req.getParameter("maxIdle"));
            map.put("minIdle", req.getParameter("minIdle"));
            map.put("minimumIdle", req.getParameter("minIdle"));
            map.put("pool", req.getParameter("pooltype").equals("default") ? "" : req.getParameter("pooltype"));
            map.put("maxActive", req.getParameter("maxActive"));
            map.put("maxWait", req.getParameter("maxWait"));
            map.put("timeBetweenEvictionRunsMillis", req.getParameter("timeBetweenEvictionRunsMillis"));
            map.put("minEvictableIdleTimeMillis", req.getParameter("minEvictableIdleTimeMillis"));
            // map.put("validationQuery", "SELECT 1");
            map.put("testWhileIdle", "true");
            map.put("testOnBorrow", "false");
            map.put("testOnReturn", "false");
            map.put("poolPreparedStatements", "false");
            map.put("maxPoolPreparedStatementPerConnectionSize", "20");
            map.put("filters", "start");
            map.put("connectionTestQuery", "SELECT 1");
            map.put("connectionTimeout", req.getParameter("connectionTimeout"));
            map.put("idleTimeout", req.getParameter("idleTimeout"));
            map.put("maxLifetime", req.getParameter("maxLifetime"));
            resp.getWriter().print(save(map, req.getParameter("dsname")));
        }
    }

    /**
     * 获取驱动信息
     * 
     * @return json信息
     */
    private String getDriverInfo() {
        JSONArray jsonArray = new JSONArray();
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        if (null != refs) {
            for (ServiceReference<DataSourceFactory> serviceReference : refs) {
                JSONObject json = new JSONObject();
                Bundle bundle = serviceReference.getBundle();
                try {
                    json.put("bundleName", bundle.getSymbolicName());
                    json.put("context",
                        bundle.getHeaders().get("Bundle-Description"));
                    json.put("provide", bundle.getHeaders()
                        .get("Bundle-Vendor"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                DataSourceFactory dsf = m_context.getService(serviceReference);
                try {
                    Driver driver = dsf.createDriver(null);
                    json.put("driverName", driver.getClass().getName());
                    json.put("version", driver.getMajorVersion() + ".0."
                        + driver.getMinorVersion());
                    json.put("driverType",
                        bundle.getHeaders().get("osgi.jdbc.driver.name"));
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(json);
            }
        }
        return jsonArray.toString();
    }

    /**
     * 测试连接是否成功
     * 
     * @param url
     *            驱动URL
     * @param user
     *            用户名
     * @param password
     *            密码
     * @param type
     *            数据库类型
     * @return 是否成功
     */
    private boolean testConn(String url, String user, String password,
        String type) {
        ServiceReference<DataSourceFactory>[] refs = getDataSourceFactoryRef();
        boolean flag = true;
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
                    Connection conn = ds.getConnection();
                    if (conn != null) {
                        conn.close();
                        return flag = true;
                    } else {
                        flag = false;
                    }
                } catch (Exception e) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 查找数据源工厂
     * 
     * @return 数据源工厂的ServiceReference
     */
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

    /**
     * 查找数据源
     * 
     * @return 数据源的ServiceReference
     */
    private ServiceReference<DataSource>[] getDataSourceRef() {
        ServiceReference<DataSource>[] refs = null;
        try {
            refs = (ServiceReference<DataSource>[]) m_context
                .getServiceReferences(DataSource.class.getName(),
                    "(name=*)");
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        return refs;
    }

    /**
     * 保存
     * 
     * @param map
     *            数据源信息
     * @param dsname
     *            数据源名称
     * @return 是否保存成功
     */
    private boolean save(Map<String, String> map, String dsname) {
        try {
            String file = "com.gzydt.database.datasource-" + dsname + ".cfg";
            DataSourceUtil.writeProp(file, map);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取数据源
     * 
     * @return json格式数据源信息
     */
    private String getDataSource() {
        JSONArray jsonArray = new JSONArray();
        ServiceReference<DataSource>[] refs = getDataSourceRef();
        File f = new File(System.getProperty("user.dir") + File.separator
            + "load");
        if (!f.exists()) { return null; }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                continue;
            } else {
                if (fs.getName().contains("com.gzydt.database.datasource-")) {// 只读取pid为com.gzydt.database.datasource的文件
                    Properties props = DataSourceUtil.readProp(fs.getName());
                    JSONObject json = new JSONObject();
                    try {
                        json = new JSONObject();
                        for (Object key : props.keySet()) {
                            json.put(key.toString(), props.get(key));
                        }
                        if (refs != null) {
                            boolean flag = false;
                            for (ServiceReference<DataSource> ref : refs) {
                                String name = (String) ref.getProperty("name");
                                if (name.equals(json.get("name"))) {
                                    DataSource ds = m_context.getService(ref);
                                    Connection conn = ds.getConnection();
                                    if (conn != null) {
                                        flag = true;
                                        conn.close();
                                    } else {
                                        flag = false;
                                    }
                                }
                            }
                            if (flag) {
                                json.put("state", "注册成功");
                                json.put("message", "无");
                            } else {
                                json.put("state", "注册失败");
                                json.put("message", "没有检测到指定驱动");
                            }
                        } else {
                            json.put("state", "注册失败");
                            json.put("message", "数据库工厂找不到");
                        }
                        jsonArray.put(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            json.put("state", "注册失败");
                            json.put("message", "连接超时");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        jsonArray.put(json);
                    }
                } else {
                    continue;
                }
            }
        }
        return jsonArray.toString();
    }

    /**
     * 获取组件信息
     * 
     * @return json组件信息
     */
    private String getBundleInfo() {
        JSONArray array = new JSONArray();
        /*Bundle[] bundles = m_context.getBundles();
        try {
            for (Bundle bundle : bundles) {
                JSONObject json_bundle = new JSONObject();
                if ("true".equals(bundle.getHeaders().get(
                    "Bundle-ISPersistence"))) {
                    json_bundle.put("name", bundle.getSymbolicName());
                    json_bundle.put("version", bundle.getVersion());
                    array.put(json_bundle);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        // 2017-4-13 longmingfeng修改
        try {
            ServiceReference<EntityManager>[] refs = (ServiceReference<EntityManager>[]) m_context
                .getServiceReferences(EntityManager.class.getName(), "(osgi.unit.name=*)");
            JSONObject json_bundle = null;
            for (ServiceReference<EntityManager> serviceReference : refs) {
                json_bundle = new JSONObject();
                json_bundle.put("name", serviceReference.getBundle().getSymbolicName());
                json_bundle.put("version", serviceReference.getBundle().getVersion());
                json_bundle.put("description", serviceReference.getBundle().getHeaders().get("Bundle-Description"));
                json_bundle.put("provide",serviceReference.getBundle().getHeaders().get("Bundle-Vendor"));
                if (!isRepeat(array, serviceReference.getBundle().getSymbolicName(), "name"))
                    array.put(json_bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array.toString();
    }

    /**
     * 获取JPA
     * 
     * @param name
     *            组件名称
     * @return json的JPA信息
     */
    private String getJpa(String name) {
        JSONArray array = new JSONArray();
        try {
            ServiceReference<EntityManager>[] refs = (ServiceReference<EntityManager>[]) m_context
                .getServiceReferences(EntityManager.class.getName(),
                    "(osgi.unit.name=*)");
            if (refs != null && refs.length > 0) {
                for (ServiceReference<EntityManager> serviceReference : refs) {
                    if (name.equals(serviceReference.getBundle()
                        .getSymbolicName())) {
                        EntityManager e = m_context
                            .getService(serviceReference);
                        Set<EntityType<?>> set = e.getMetamodel().getEntities();
                        /*if (set.size() == 0) {
                            JSONObject json_entity = new JSONObject();
                            json_entity.put("entityName", "无");
                            json_entity.put("tableName", "无");
                            json_entity.put("state", "fail");
                            json_entity.put("bundleName", serviceReference
                                .getBundle().getSymbolicName());
                            json_entity.put("message", "持久化xml文件有误或者持久化注释有误");
                            json_entity.put("provide", "无");
                            json_entity.put("context", "无");
                            if (!isRepeat(array, serviceReference.getBundle()
                                .getSymbolicName(), "bundleName"))
                                array.put(json_entity);
                        }*/
                        for (EntityType<?> entityType : set) {
                            JSONObject json_entity = new JSONObject();
                            String tableName = entityType.toString().split(
                                "DatabaseTable\\(")[1].split("\\)")[0];
                            json_entity.put("entityName", entityType
                                .getJavaType().getName());
                            json_entity.put("tableName", tableName);
                            json_entity.put("bundleName", serviceReference
                                .getBundle().getSymbolicName());
                            json_entity.put("state", "success");
                            /*json_entity.put(
                                "provide",
                                serviceReference.getBundle()
                                    .getHeaders()
                                    .get("Bundle-Vendor"));
                            json_entity.put("message", "无");
                            json_entity.put(
                                "description",
                                serviceReference.getBundle()
                                    .getHeaders()
                                    .get("Bundle-Description"));*/
                            if (!isRepeat(array, entityType.getJavaType()
                                .getName(), "entityName"))
                                array.put(json_entity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    /**
     * 是否重复
     * 
     * @param array
     *            josnArray对象
     * @param entityName
     *            实体名称
     * @param key
     *            键
     * @return 是否重复
     */
    private boolean isRepeat(JSONArray array, String entityName, String key) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (entityName.equals(json.getString(key))) { return true; }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取行
     * 
     * @param beanName
     *            组件名称
     * @return json行的数据
     */
    private String getColumn(String beanName) {
        JSONArray array = new JSONArray();
        try {
            ServiceReference<EntityManager>[] refs = (ServiceReference<EntityManager>[]) m_context
                .getServiceReferences(EntityManager.class.getName(),
                    "(osgi.unit.name=*)");
            if (refs != null && refs.length > 0) {
                for (ServiceReference<EntityManager> serviceReference : refs) {
                    EntityManager e = m_context.getService(serviceReference);
                    Set<EntityType<?>> set = e.getMetamodel().getEntities();
                    for (EntityType<?> entityType : set) {
                        if (beanName.equals(entityType.getJavaType().getName())) {
                            for (Attribute<?, ?> attribute : entityType.getAttributes()) {
                                JSONObject json_column = new JSONObject();
                                json_column.put("name", attribute.getName());
                                json_column.put("type", attribute.getJavaType().getName());
                                json_column.put("member", attribute.getJavaMember());
                                if (!isRepeat(array, attribute.getName(), "name"))
                                    array.put(json_column);
                            }
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return array.toString();
    }
}
