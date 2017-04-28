/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gzydt.bundle.pingnet.socket;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public final class Activator implements BundleActivator {

    private ServiceTracker httpTracker;
    
    public static final String FILE_NAME = "bundle_install_time.txt";//bundle安装时间保存文件名
    
    public static String system_bundle_installTime ;//系统组件（org.apache.felix.framework）名为System Bundle的安装时间

    @SuppressWarnings("unchecked")
	public void start(BundleContext context) throws Exception {
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	system_bundle_installTime = sdf.format(new Date());
    	
    	//监听bundle安装事件，安装时，记录下安装时间
    	context.addBundleListener(new BundleListener() {
			
			@Override
			public void bundleChanged(BundleEvent event) {
				Bundle bundle = event.getBundle();
				
				if(bundle.getState() > event.INSTALLED){
					String bundle_path = bundle.getDataFile("").getPath();
					
						File f = new File(bundle_path + File.separator + FILE_NAME);
						try {
							BufferedWriter bw = new BufferedWriter(new FileWriter(f));
							bw.write(sdf.format(new Date()));
							bw.flush();
							bw.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					
				}
				
			}
		});
        
    	
    	//发布服务接口
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {

            public Object addingService(ServiceReference reference) {
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    httpService.registerServlet("/findTarget", new FindTarget(), null, null);//target客户端，运行组件详细信息
                    //httpService.registerServlet("/restartTarget", new RestartTarget(), null, null);//重启target方法
                    httpService.registerServlet("/loadServer", new LoadServer(), null, null);
                    httpService.registerServlet("/requestServer", new RequestServer(), null, null);//前期为了解决js跨域问题，后期不再用这种方法,改用在被请求端加入resp.setHeader("Access-Control-Allow-Origin", "*");
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                return httpService;
            }

            public void removedService(ServiceReference reference, Object service) {
                try {
                    ((HttpService) service).unregister("/findTarget");
                    //((HttpService) service).unregister("/restartTarget");
                    ((HttpService) service).unregister("/loadServer");
                    ((HttpService) service).unregister("/requestServer");
                }
                catch (IllegalArgumentException exception) {
                }
            }
        };

        httpTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        httpTracker.close();
    }
}
