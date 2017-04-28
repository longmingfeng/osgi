/**
 *   Email: yxlmf@126.com 
 */
package com.test;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import connectionpoll.HikariConnectionManager;

/**
 * 
 * @author longmingfeng
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void destroy(BundleContext arg0, DependencyManager arg1) throws Exception {

    }

    @Override
    public void init(BundleContext context, DependencyManager dm) throws Exception {

        HikariConnectionManager h = new HikariConnectionManager();
        dm.add(createComponent()
            .setInterface(Object.class.getName(), null)
            .setImplementation(Te.class));

    }

}
