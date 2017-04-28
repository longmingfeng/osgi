package com.gzydt.bundle.target.server.webconsole;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    private TargetManagerPlugin targetManagerPlugin = null;

    @Override
    public void start(BundleContext context) throws Exception {
        targetManagerPlugin = new TargetManagerPlugin();
        targetManagerPlugin.register(context);
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        if (targetManagerPlugin != null) {
            targetManagerPlugin.unregister();
        }

    }
}
