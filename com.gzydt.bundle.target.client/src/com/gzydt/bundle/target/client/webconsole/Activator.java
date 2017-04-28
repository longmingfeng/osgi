package com.gzydt.bundle.target.client.webconsole;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private TargetPlugin targetPlugin = null;

    @Override
    public void start(BundleContext context) throws Exception {
        targetPlugin = new TargetPlugin();
        targetPlugin.register(context);
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        if (targetPlugin != null) {
            targetPlugin.unregister();
        }

    }

}
