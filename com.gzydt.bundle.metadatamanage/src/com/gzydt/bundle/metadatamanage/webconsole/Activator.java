package com.gzydt.bundle.metadatamanage.webconsole;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 元数据管理组件webconsole前端页面组件启动类
 * 
 * @author longmingfeng 2017年3月30日 下午2:07:13
 */
public class Activator implements BundleActivator {

    private MetaDataManagePlugn metaDataManagePlugn;

    @Override
    public void start(BundleContext context) throws Exception {
        metaDataManagePlugn = new MetaDataManagePlugn();
        metaDataManagePlugn.register(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (metaDataManagePlugn != null) {
            metaDataManagePlugn.unregister();
        }
    }

}
