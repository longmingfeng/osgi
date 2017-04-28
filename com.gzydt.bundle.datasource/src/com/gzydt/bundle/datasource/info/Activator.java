package com.gzydt.bundle.datasource.info;

import java.util.Hashtable;

import javax.servlet.Servlet;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * 数据源管理组件，启动类
 * 
 * @author longmingfeng 2017年3月30日 下午2:06:18
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Activator extends DependencyActivatorBase {

    @Override
    public void destroy(BundleContext context, DependencyManager manager)
        throws Exception {
    }

    @Override
    public void init(BundleContext context, DependencyManager arg1)
        throws Exception {
        Hashtable props = new Hashtable();
        // 发布HTTP服务
        props.put("osgi.http.whiteboard.servlet.pattern", "/getDriverInfo/*");
        arg1.add(createComponent().setInterface(Servlet.class.getName(), props)
            .setImplementation(DriverInfo.class));
    }

}
