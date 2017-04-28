/**
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database;

import java.util.Hashtable;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.servlet.Servlet;

import org.amdatu.jta.ManagedTransactional;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import com.gzydt.bundle.database.controller.PersistenceServlet;
import com.gzydt.bundle.database.dao.PersistenceDao;
import com.gzydt.bundle.database.dao.impl.PersistenceDaoImpl;

/**
 * 
 * @author longmingfeng 2016年12月16日 下午2:00:34
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void destroy(BundleContext arg0, DependencyManager arg1) throws Exception {

    }

    @Override
    public void init(BundleContext context, DependencyManager dm) throws Exception {

        Hashtable<String, String> p1 = new Hashtable<String, String>();
        p1.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/getPersistenceTest");

        dm.add(createComponent()
            .setInterface(Servlet.class.getName(), p1)
            .setImplementation(PersistenceServlet.class)
            .add(createServiceDependency()
                .setService(PersistenceDao.class).setRequired(true))
            .add(createServiceDependency()
                .setService(EntityManager.class, "(osgi.unit.name=testPersistent)").setRequired(true))
            .add(createServiceDependency()
                .setService(LogService.class).setRequired(false)));

        // 加入事物管理
        Properties p2 = new Properties();
        p2.put(ManagedTransactional.SERVICE_PROPERTY, PersistenceDao.class.getName());

        dm.add(createComponent()
            .setInterface(Object.class.getName(), p2)
            .setImplementation(PersistenceDaoImpl.class)
            .add(createServiceDependency()
                .setService(EntityManager.class, "(osgi.unit.name=testPersistent)").setRequired(true))
            .add(createServiceDependency()
                .setService(LogService.class).setRequired(false)));

    }

}
