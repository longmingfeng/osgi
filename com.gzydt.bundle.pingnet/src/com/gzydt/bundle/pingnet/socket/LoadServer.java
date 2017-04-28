package com.gzydt.bundle.pingnet.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class LoadServer extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println(getServer());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		String address = req.getParameter("address");
		changeCurrentServer(address);
	}

	/**
	 * 获取系统当前目录下的serverInfo.properties的配置信息
	 * 
	 * @return 配置信息
	 */
	private Properties getPropByFile() {
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "conf" + File.separator + "serverInfo.properties");
		Properties properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	/**
	 * 将配置信息写进配置文件
	 * 
	 * @param properties
	 *            配置信息
	 */
	private void setPropByFile(Properties properties) {
		File file = new File(System.getProperty("user.dir") + File.separator
				+ "conf" + File.separator + "serverInfo.properties");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			properties.store(fos, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检测该ip地址的连接状态
	 * 
	 * @param url
	 *            地址
	 * @param key
	 *            当前配置文件的key
	 * 
	 * @return JSON对象
	 */
	private JSONObject getServer() {
		JSONObject json = new JSONObject();
		Properties properties = getPropByFile();
		for (Object key : properties.keySet()) {
			String str_key = key.toString();
			if (str_key.contains("address")) {
				String address = properties.get(key).toString();
				String stateValue = ConnctionUtil.getConnectionState(address,
						"/ace/");
				String stateKey = "";
				if ("cur_server_address".equals(key)) {
					stateKey = "cur_server_state";
				} else {
					String endStr = str_key.substring(str_key.length() - 1,
							str_key.length());
					stateKey = "cu_server_state" + endStr;
				}
				json.put(stateKey, stateValue);
			}
			json.put(key, properties.get(key));
		}
		return json;
	}

	/**
	 * 改变当前服务器信息
	 * 
	 * @param address
	 *            当前地址
	 */
	private void changeCurrentServer(String address) {
		String n = "";
		Properties properties = getPropByFile();
		for (Object key : properties.keySet()) {
			String str_key = key.toString();
			if (str_key.contains("address")) {
				if (properties.get(str_key).equals(address)) {
					n = key.toString().substring(str_key.length() - 1,
							str_key.length());
				}
			}
		}
		swapServer(properties, "cur_server_name", "cu_server_name" + n);
		swapServer(properties, "cur_server_address", "cu_server_address" + n);
		swapServer(properties, "cur_server_principal", "cu_server_principal"
				+ n);
		swapServer(properties, "cur_server_dsc", "cu_server_dsc" + n);
		setPropByFile(properties);
	}

	/**
	 * 交换配置文件的服务器内容
	 * 
	 * @param properties
	 *            配置文件
	 * @param key1
	 *            第一个key
	 * @param key2
	 *            第二个key
	 */
	private void swapServer(Properties properties, String key1, String key2) {
		String value1 = properties.getProperty(key1);
		String value2 = properties.getProperty(key2);
		properties.setProperty(key1, value2);
		properties.setProperty(key2, value1);
	}
}
