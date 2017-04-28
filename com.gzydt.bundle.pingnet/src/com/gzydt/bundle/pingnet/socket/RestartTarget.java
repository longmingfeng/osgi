/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：宇阳数码 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016-10-27 10:53:27
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import net.sf.json.JSONObject;

/**
 * 修改target的Conf配置文件后，再重启target
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
public class RestartTarget extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");

        String serverURL = req.getParameter("serverURL") != null ? req.getParameter("serverURL") : "http://192.168.10.59:8082";
        String targetid = req.getParameter("targetid") != null ? req.getParameter("targetid") : "target-1";
        
        //修改target启动配置文件
        /*File file = new File(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "launcher.properties");
        Properties properties = new Properties();
        FileInputStream fis;
        FileOutputStream oFile;
        
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
            
            properties.replace("agent.discovery.serverurls", serverURL);
            
            // 保存属性到b.properties文件
            oFile = new FileOutputStream(file.getPath(), false);// true表示追加打开
            properties.store(oFile, " target online ipList ,author : yxlmf@126.com");
            oFile.close();
            fis.close();
            
        }catch(Exception e){
            e.printStackTrace();
        }*/
        
        //修改target运行的bat文件，便于下次启动时，用这次修改过的信息
        /*File file = new File(System.getProperty("user.dir") + File.separator + "target.bat");
        
        PrintStream ps = new PrintStream(file.getPath()) ;
        
        ps.println("title " + targetid + " \r\n"
            + "java -Dagent.identification.agentid=" + targetid + " "
            + "-Dagent.discovery.serverurls=" + serverURL + " "
            + "-Dagent.controller.syncinterval=30  "
            + "-Dagent.controller.streaming=true "
            + "-Dagent.controller.syncdelay=5 "
            + "-jar target.jar");

        System.setOut(ps);
        
        ps.close();*/
        
        
        //将相应属性设置为系统变量   
        System.setProperty("agent.identification.agentid",targetid);
        System.setProperty("agent.discovery.serverurls", serverURL);//agent启动时，会从系统属性中去读取
        System.setProperty("agent.controller.syncinterval","30");
        System.setProperty("agent.controller.streaming","true");
        System.setProperty("agent.controller.syncdelay","5");
        
        
        //重启agent这个组件，达到重启target的目的
        BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
        for (Bundle bundle : m_bundleContext.getBundles()) {
            if("org.apache.ace.agent".equals(bundle.getSymbolicName()))
            /*if(!"System Bundle".equals(bundle.getHeaders().get(Constants.BUNDLE_NAME))
                && !"Apache Felix Gogo Runtime".equals(bundle.getHeaders().get(Constants.BUNDLE_NAME))
                && !"Apache Felix Gogo Shell".equals(bundle.getHeaders().get(Constants.BUNDLE_NAME))
                && !"Apache Felix Gogo Command".equals(bundle.getHeaders().get(Constants.BUNDLE_NAME)))*/
                try {
                    
                    bundle.stop((int) bundle.getBundleId());
                    bundle.start((int) bundle.getBundleId());
                    
                    //bundle.uninstall();
                } catch (BundleException e) {
                    e.printStackTrace();
                }
        }
        
        //Runtime.getRuntime().exec("cmd.exe /c start "+System.getProperty("user.dir") + File.separator + "target.bat");
       
        //System.exit(0);

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doGet(req, resp);
    }
}
