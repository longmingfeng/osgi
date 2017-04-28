/**
 *   @author longmingfeng    2017年2月23日  下午1:41:40
 *   Email: yxlmf@126.com 
 */
package one_to_many;

import java.util.Hashtable;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.servlet.Servlet;

import org.amdatu.jta.ManagedTransactional;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * 
 * @author longmingfeng 2017年2月23日 下午1:41:40
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext arg0, DependencyManager dm) throws Exception {
        Hashtable<String, String> p1 = new Hashtable<String, String>();
        p1.put("osgi.http.whiteboard.servlet.pattern", "/many_to_many/*");

        dm.add(createComponent()
            .setInterface(Servlet.class.getName(), p1)
            .setImplementation(ManyToManyServlet.class)
            .add(createServiceDependency()
                .setService(TestDao.class).setRequired(true))
            .add(createServiceDependency()
                .setService(LogService.class).setRequired(false)));

        // 加入事物管理
        Properties p2 = new Properties();
        p2.put(ManagedTransactional.SERVICE_PROPERTY, TestDao.class.getName());

        dm.add(createComponent()
            .setInterface(Object.class.getName(), p2)
            .setImplementation(TestDaoImpl.class)
            .add(createServiceDependency()
                .setService(EntityManager.class, "(osgi.unit.name=many_to_many)").setRequired(true))
            .add(createServiceDependency()
                .setService(LogService.class).setRequired(false)));
    }

}
