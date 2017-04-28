package com.gzydt.bundle.metadatamanage.info;

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

import com.gzydt.bundle.metadatamanage.info.api.MetaDataService;
import com.gzydt.bundle.metadatamanage.info.api.PersistentService;
import com.gzydt.bundle.metadatamanage.info.jpa.MetaDataServiceImpl;
import com.gzydt.bundle.metadatamanage.info.jpa.PersistentServiceImpl;
import com.gzydt.bundle.metadatamanage.info.rest.MetaDataResource;
import com.gzydt.bundle.metadatamanage.info.rest.MetaDataRest;
import com.gzydt.bundle.metadatamanage.info.rest.ViewdataUtilServlet;

/**
 * 元数据管理组件，启动类
 * 
 * @author longmingfeng 2017年3月30日 下午2:06:18
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void destroy(BundleContext arg0, DependencyManager arg1) throws Exception {

    }

    @Override
    public void init(BundleContext context, DependencyManager dm) throws Exception {
        // HTTP白板注入
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("osgi.http.whiteboard.servlet.pattern", "/mateDataManager/*");
        dm.add(createComponent()
            .setInterface(Servlet.class.getName(), map)
            .setImplementation(MetaDataResource.class)
            .add(createServiceDependency()
                .setService(MetaDataService.class).setRequired(true))
            .add(createServiceDependency()
                .setService(PersistentService.class).setRequired(true)));

        // 事务管理注入
        Properties p2 = new Properties();
        p2.put(ManagedTransactional.SERVICE_PROPERTY, MetaDataService.class.getName());
        dm.add(createComponent()
            .setInterface(Object.class.getName(), p2)
            .setImplementation(MetaDataServiceImpl.class)
            .add(createServiceDependency()
                .setService(EntityManager.class, "(osgi.unit.name=metaDataPU)").setRequired(true)));
        dm.add(createComponent()
            .setInterface(PersistentService.class.getName(), null)
            .setImplementation(PersistentServiceImpl.class));

        // 为视图预览提供数据
        Hashtable<String, String> viewdata = new Hashtable<String, String>();
        viewdata.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/viewdata_util/*");

        dm.add(createComponent()
            .setInterface(Servlet.class.getName(), viewdata)
            .setImplementation(ViewdataUtilServlet.class)
            .add(createServiceDependency()
                .setService(LogService.class).setRequired(false)));

        // 元数据接口（根据指定格式的URL，返回对应的元数据）,都是针对类型元数据主表及子表操作的
        Properties resource = new Properties();
        resource.put("osgi.jaxrs.resource.base", "/resource");

        dm.add(createComponent()
            .setInterface(Object.class.getName(), null)
            .setImplementation(MetaDataRest.class).setServiceProperties(resource)
            .add(createServiceDependency()
                .setService(MetaDataService.class).setRequired(true))
            .add(createServiceDependency()
                .setService(LogService.class).setRequired(false)));
    }

}
