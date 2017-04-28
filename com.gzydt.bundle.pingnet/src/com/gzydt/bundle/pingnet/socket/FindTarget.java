/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：宇阳数码 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016-10-27 10:53:27
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import net.sf.json.JSONObject;

/**
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
public class FindTarget extends HttpServlet {

	private JSONObject json = new JSONObject();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		/*String serverURL = req.getParameter("serverURL") != null ? req.getParameter("serverURL")
				: "http://192.168.10.59:8082";*/
		
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");

		//resp.setHeader("Access-Control-Allow-Origin", "*");

		json = getBundle();
		
		/*try {
			String state = ConnctionUtil.getConnectionState(serverURL, "/ace/");
			json.put("state", state);

		} catch (Exception e) {
			
			json.put("state", "连接失败");
		}*/
		
		resp.getWriter().println(json);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	public JSONObject getBundle() {
		BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
		Map<String, Object> bundlemap;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		/*
		 * JarInputStream jarInputStream; 
		 * Manifest bundleManifest = null;
		 * Attributes mainAttributes ; 
		 * String descrip;//bundle描述 
		 * String bundlename;//取Bundle-Name值，非Bundle-SymbolicName值
		 */

		for (Bundle bundle : m_bundleContext.getBundles()) {
			bundlemap = new HashMap<String, Object>();

			bundlemap.put("bundlename", bundle.getHeaders().get(Constants.BUNDLE_NAME));
			bundlemap.put("bundlestate", getSate(bundle.getState()));
			bundlemap.put("bundleversion", bundle.getVersion().toString());
			bundlemap.put("bundleModifiedTime", sdf.format(new Date(bundle.getLastModified())));
			bundlemap.put("descrip", bundle.getHeaders().get(Constants.BUNDLE_DESCRIPTION));
			bundlemap.put("is_systemBundle", bundle.getHeaders().get("Is_SystemBundle"));
			
			String bundle_path = bundle.getDataFile("").getPath();
			try {
				if("System Bundle".equals(bundle.getLocation()))
					bundlemap.put("bundle_installTime", Activator.system_bundle_installTime);
				else{
					BufferedReader br = new BufferedReader(new FileReader(bundle_path + File.separator + Activator.FILE_NAME));
					String install_time = br.readLine();
					bundlemap.put("bundle_installTime", install_time);
					
					br.close();
				}
			} catch (Exception e) {
				bundlemap.put("bundle_installTime", "");
				//e.printStackTrace();
			}
			
			/*if(bundle.getBundleId() != 0){
	            try {
	                jarInputStream = new JarInputStream(new FileInputStream(bundle.getLocation().replace("file:/", "")));
	                bundleManifest = jarInputStream.getManifest();
	                mainAttributes = bundleManifest.getMainAttributes();
	                
	                bundlename = mainAttributes.getValue(Constants.BUNDLE_NAME);
	                descrip = mainAttributes.getValue(Constants.BUNDLE_DESCRIPTION);
	                bundlemap.put("bundlename", bundlename);
	                bundlemap.put("descrip", descrip);
	                
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }else{
	            bundlemap.put("descrip", "系统组件");
	        }*/
			
			list.add(bundlemap);
		}
			
		json.put("bundleinfo", list);
			

		/*File file = new File(
				System.getProperty("user.dir") + File.separator + "conf" + File.separator + "ip.properties");

		if (!file.exists())
			json.put("targetIP_list", "");
		else {
			Properties properties = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				properties.load(fis);
				Map<String, Object> map = new HashMap<String, Object>();
				for (Object ip : properties.keySet()) {
					map.put(ip.toString(), properties.getProperty((String) ip));
				}
				json.put("targetIP_list", map);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}*/

		return json;
	}

	public String getSate(int state) {
		String sta;
		switch (state) {
		case Bundle.INSTALLED:
			sta = "已安装";
			break;
		case Bundle.RESOLVED:
			sta = "已编译";
			break;
		case Bundle.UNINSTALLED:
			sta = "未安装";
			break;
		case Bundle.STARTING:
			sta = "启动中";
			break;
		case Bundle.ACTIVE:
			sta = "激活";
			break;
		case Bundle.STOPPING:
			sta = "停止中";
			break;
		default:
			sta = "已编译";
		}
		return sta;
	}
}
