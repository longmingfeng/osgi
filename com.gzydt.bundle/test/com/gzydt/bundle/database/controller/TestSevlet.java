/**
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database.controller;

import java.io.IOException;
import java.sql.Driver;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import com.gzydt.bundle.database.dao.PersistenceDao;
import com.gzydt.bundle.database.vo.JpaBridgeManager;

/**
 * 测试类
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
@SuppressWarnings("restriction")
public class TestSevlet extends HttpServlet {

    private LogService log;

    private static final long serialVersionUID = 1L;

    private EntityManager em;

    private PersistenceDao dao;

    private volatile BundleContext context;

    @SuppressWarnings({ "unchecked" })
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("GBK");

        log.log(LogService.LOG_INFO, "persistence.xml中的属性值：" + em.getEntityManagerFactory().getProperties());

        JSONObject json = new JSONObject();
        // 获取注册的数据源名称
        /*List datasourceBundleList = new ArrayList();
        try {
            ServiceReference<DataSource>[] refs = (ServiceReference<DataSource>[]) context.getServiceReferences(DataSource.class.getName(), "(name=*)");
            if (refs != null && refs.length > 0) {
                for (ServiceReference<DataSource> serviceReference : refs) {
                    log.log(LogService.LOG_INFO, "数据源名称:" + serviceReference.getProperty("name"));
        
                    if (!datasourceBundleList.contains(serviceReference.getBundle().getSymbolicName())) {
                        datasourceBundleList.add(serviceReference.getBundle().getSymbolicName());
                    }
                    // 判断配置的连接，是否正确，即是否连接成功
                    DataSource d = context.getService(serviceReference);
                    try {
                        log.debug("连接对象信息：" + d.getConnection());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            json.put("数据源所在组件datasource_bundle", datasourceBundleList);
        
        } catch (InvalidSyntaxException | JSONException e1) {
            e1.printStackTrace();
        }*/

        // 获取persistence.xml文件中的persistence-unit的name
        /*try {
            ServiceReference<?>[] refs = context.getServiceReferences(EntityManager.class.getName(), "(osgi.unit.name=*)");
            if (refs != null && refs.length > 0) {
                for (ServiceReference<?> serviceReference : refs) {
                    log.log(LogService.LOG_INFO, "persistence-unit的name为：" + serviceReference.getProperty("osgi.unit.name"));
                }
            }
        } catch (InvalidSyntaxException e1) {
            e1.printStackTrace();
        }*/

        // 获取当前项目中的实体对象
        // Map map = new HashMap();//每个组件中对同样的服务多次注册，进行过滤，key为组件名，value为实体对象名--map
        try {
            ServiceReference<EntityManager>[] refs = (ServiceReference<EntityManager>[]) context.getServiceReferences(EntityManager.class.getName(), "(osgi.unit.name=*)");
            if (refs != null && refs.length > 0) {
                for (ServiceReference<EntityManager> serviceReference : refs) {
                    EntityManager e = context.getService(serviceReference);

                    // List entityList = new ArrayList();
                    JSONObject entityList = new JSONObject();

                    Set<EntityType<?>> set = e.getMetamodel().getEntities();
                    for (EntityType<?> entityType : set) {
                        // Map cloumnMap = new HashMap();//key为实体属性名，value为实体类型
                        JSONObject cloumnMap = new JSONObject();

                        for (Attribute<?, ?> a : entityType.getAttributes()) {
                            cloumnMap.put("属性名" + a.getName(), "属性类型" + a.getJavaType().getName());// 实体属性名：实体属性的类型

                        }
                        // entityList.add(entityType.getJavaType().getName()+"--"+cloumnMap);
                        String tableName = entityType.toString().split("DatabaseTable\\(")[1].split("\\)")[0];
                        entityList.put("实休名" + entityType.getJavaType().getName() + ",表名" + tableName, cloumnMap);
                    }
                    // map.put(serviceReference.getBundle().getSymbolicName(), entityList);
                    json.put("组件名" + serviceReference.getBundle().getSymbolicName(), entityList);

                }
            }
        } catch (InvalidSyntaxException | JSONException e1) {
            e1.printStackTrace();
        }
        // log.info("实体对象信息：" + map);

        // em对象对数据库的操作测试
        Query q = em.createNativeQuery("select 1");
        log.log(LogService.LOG_INFO, "测试数据有： " + q.getResultList().size() + "条");

        // jpa的持久化测试
        try {
            JpaBridgeManager j = new JpaBridgeManager();
            j.setRuleName("自动");
            j.setCreateTime(new Date());
            dao.save(j);

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<JpaBridgeManager> list = dao.getList(JpaBridgeManager.class, 1, 10);
        log.log(LogService.LOG_INFO, "数据条数：" + list.size());

        // 获取系统中支持的数据库类型及组件信息
        try {
            ServiceReference<DataSourceFactory>[] refs = (ServiceReference<DataSourceFactory>[]) context.getServiceReferences(DataSourceFactory.class.getName(),
                "(&(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=*"
                    + ")(" + DataSourceFactory.OSGI_JDBC_DRIVER_NAME + "=*))");

            if (refs != null && refs.length > 0) {

                for (ServiceReference<DataSourceFactory> ref : refs) {
                    DataSourceFactory df = context.getService(ref);
                    Driver driver = df.createDriver(null);
                    log.log(LogService.LOG_INFO, "系统已存在的数据库驱动名为： " + driver.getClass().getName());

                    // h2数据库测试
                    /*if ("org.h2.Driver".equals(driver.getClass().getName())) {
                        Properties props = new Properties();
                        props.put(DataSourceFactory.JDBC_URL, "jdbc:h2:mem:test");
                        props.put(DataSourceFactory.JDBC_USER, "sa");
                        props.put(DataSourceFactory.JDBC_PASSWORD, "sa");
                        DataSource ds = df.createDataSource(props);
                        Connection connection = ds.getConnection();
                        PreparedStatement stmt = connection
                            .prepareStatement("CREATE TABLE cc (GUID varchar(40) PRIMARY KEY,name varchar(40))");
                        stmt.execute();
                    
                        // 就用这个计时吧
                        long start = System.currentTimeMillis();
                        stmt = connection.prepareStatement("INSERT INTO cc VALUES (?, ?)");
                        for (int i = 0; i < 100; ++i) {
                            stmt.setString(1, i + "a");
                            stmt.setString(2, i + "b");
                            stmt.execute();
                        }
                    
                        long duration = System.currentTimeMillis() - start;
                        ResultSet rs = connection.prepareStatement("select count(*) num from cc").executeQuery();
                        if (rs.next()) {
                            String s = rs.getString("num");
                            System.out.println("Finished in " + duration + " ms........,总记录数：" + s);
                            connection.close();
                        }
                    }*/

                }

                // 测试oracle数据库获取数据
                /*DataSourceFactory df = context.getService(refs[0]);
                
                Properties props = new Properties();
                props.put(DataSourceFactory.JDBC_DATABASE_NAME, "ORCL");
                props.put(DataSourceFactory.JDBC_URL, "jdbc:oracle:thin:@192.168.10.171:1521:ORCL");
                props.put(DataSourceFactory.JDBC_USER, "quality");
                props.put(DataSourceFactory.JDBC_PASSWORD, "quality");
                
                DataSource ds = df.createDataSource(props);
                Connection connection = ds.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("select * from CANCLEINFO");
                while (rs.next()) {
                    log.log(LogService.LOG_INFO, "ENTITYNO值为：" + rs.getString("ENTITYNO"));
                }*/

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.log(LogService.LOG_INFO, json.toString());
        resp.getWriter().println(json);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
