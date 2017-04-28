package com.gzydt.bundle.datasource.webconsole;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 数据源管理组件webconsole前端页面组件启动类
 * 
 * @author longmingfeng 2017年3月30日 下午2:07:13
 */
public class Activator implements BundleActivator {

    private DataSourcePlugin dataSourcePlugin;

    @Override
    public void start(BundleContext context) throws Exception {
        dataSourcePlugin = new DataSourcePlugin();
        dataSourcePlugin.register(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (dataSourcePlugin != null) {
            dataSourcePlugin.unregister();
        }
    }

}
