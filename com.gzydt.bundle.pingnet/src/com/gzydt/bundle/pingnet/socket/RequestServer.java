/**
 *   @author longmingfeng    2016-10-27 10:53:27
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
public class RequestServer extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
    	String server_url=req.getParameter("server_url")!=null&&!"".equals(req.getParameter("server_url"))? req.getParameter("server_url") : "http://192.168.15.25:8080/loadServer";
    	String lines="";
        String ss="";
        
        BufferedReader ins = null ;
        HttpURLConnection conn = null ;
    	try {
            URL m_host = new URL(server_url);
            URL urls = new URL(m_host, "");
            
            conn = (HttpURLConnection) urls.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            
            //POST提交
            /*conn.setRequestMethod("POST");
            DataOutputStream dataout = new DataOutputStream(conn.getOutputStream());
            String parm = "storeId=" + URLEncoder.encode("32", "utf-8"); //URLEncoder.encode()方法  为字符串进行编码
            // 将参数输出到连接
            dataout.writeBytes(parm);
            // 输出完成后刷新并关闭流
            dataout.flush();
            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!) 
*/            
            InputStream in = conn.getInputStream();
            ins = new BufferedReader(new InputStreamReader(in,"utf-8"));
            
            while((lines = ins.readLine()) != null){
                ss += lines;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	if(ins != null)ins.close();
            if(conn != null)conn.disconnect();
		}
    	
    	resp.getWriter().write(ss);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doGet(req, resp);
    }
    
}
