package com.gzydt.bundle.target.client.info;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public final class Activator implements BundleActivator {

    @SuppressWarnings("rawtypes")
    private ServiceTracker httpTracker;

    // felix环境，就取系统属性org.osgi.framework.storage的值，如果是eclipse本机运行，就取*.bndrun文件中的launch.storage.dir值
    public static final String ROOT = System.getProperty("org.osgi.framework.storage") == null ? "bundle-cache"
        : System.getProperty("org.osgi.framework.storage");// 值默认为bundle-cache;

    public static final String SYS_FILE_NAME = "sys_bundle_install_time.txt";// bundle安装时间保存文件名

    public static final String FILE_NAME = "bundle_install_time.txt";// bundle安装时间保存文件名

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void start(BundleContext context) throws Exception {

        /**
         * 单独处理System Bundle的安装时间 为了避免每次安装新的组件时，系统组件（org.apache.felix.framework）System Bundle的安装时间自动变动， 故将System
         * Bundle组件的安装时间写到bundle-cache目录下的sys_bundle_install_time.txt文件中
         */
        File f = new File(System.getProperty("user.dir") + File.separator + ROOT + File.separator + SYS_FILE_NAME);

        if (!f.exists()) {
            f.createNewFile();
            writeTime(f);
        }

        // 监听其它bundle安装事件，安装时，记录下安装时间
        context.addBundleListener(new BundleListener() {

            @Override
            public void bundleChanged(BundleEvent event) {
                Bundle bundle = event.getBundle();

                if (bundle.getState() > BundleEvent.INSTALLED) {
                    String bundle_path = bundle.getDataFile("").getPath();

                    File f = new File(bundle_path + File.separator + FILE_NAME);
                    if (f.exists()) { return; }

                    writeTime(f);

                }

            }
        });

        // 注册服务
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {

            public Object addingService(ServiceReference reference) {

                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    httpService.registerServlet("/findTarget", new FindTarget(), null, null);// target客户端，运行组件详细信息
                    httpService.registerServlet("/systemInstall", new SystemInstall(), null, null);// 系统是否安装成功，即系统安装状态
                    httpService.registerServlet("/targetInfo", new TargetInfo(), null, null);
                    httpService.registerServlet("/registerInfo", new RegisterTarget(), null, null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return httpService;
            }

            public void removedService(ServiceReference reference, Object service) {
                try {
                    ((HttpService) service).unregister("/findTarget");
                    ((HttpService) service).unregister("/systemInstall");
                    ((HttpService) service).unregister("/targetInfo");
                    ((HttpService) service).unregister("/registerInfo");
                } catch (IllegalArgumentException exception) {

                }
            }
        };

        httpTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        httpTracker.close();
    }

    /**
     * 向文件里写入组件安装时间
     * 
     * @param f
     * @author longmingfeng 2016年12月29日 上午9:20:15
     */
    public void writeTime(File f) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            bw.write(sdf.format(new Date()));
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
