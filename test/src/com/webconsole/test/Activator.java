package com.webconsole.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    
	private DataSourcePlugin dataSourcePlugin;
	
	@Override
	public void start(BundleContext context) throws Exception {
		dataSourcePlugin=new DataSourcePlugin();
		dataSourcePlugin.register(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
        if(dataSourcePlugin!=null){
        	dataSourcePlugin.unregister();
        }
	}

}
