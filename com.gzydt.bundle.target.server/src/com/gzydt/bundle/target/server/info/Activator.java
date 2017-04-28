package com.gzydt.bundle.target.server.info;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public final class Activator implements BundleActivator {

    @SuppressWarnings("rawtypes")
    private ServiceTracker httpTracker;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void start(BundleContext context) throws Exception {
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {

            public Object addingService(ServiceReference reference) {
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    httpService.registerServlet("/registerTarget", new RegisterTarget(), null, null);
                    httpService.registerServlet("/serverInfo", new ServerInfo(), null, null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return httpService;
            }

            public void removedService(ServiceReference reference, Object service) {
                try {
                    ((HttpService) service).unregister("/registerTarget");
                    ((HttpService) service).unregister("/serverInfo");
                } catch (IllegalArgumentException exception) {
                }
            }
        };

        httpTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        httpTracker.close();
    }
}
