package com.gzydt.bundle.pingnet.socket;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

public class ConnctionUtil {
	
    /**
     * http://192.168.11.169:8082/ace/
     * @param parent_url 主url(http://192.168.11.169:8082)
     * @param  son_url 子url(/ace/)
     * @return  连接成功/连接失败
     * @author 2016年11月2日  下午4:02:06
     */
	public static String getConnectionState(String parent_url,String son_url){
	    String state ;
        try {
            URL m_host = new URL(parent_url);
            URL urls = new URL(m_host, son_url);
            
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setAllowUserInteraction(false);
            conn.setDefaultUseCaches(false);
            conn.setUseCaches(false);
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            
            int responseCode = conn.getResponseCode();
            
            state = responseCode == HttpServletResponse.SC_OK ? "连接成功" : "连接失败";
            
        } catch (Exception e) {
            e.printStackTrace();
            state = "连接失败";
        }
        return state;
	}
}
