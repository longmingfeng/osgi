package com.gzydt.bundle.target.client.info;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * 客户端向服务端注册target的请求servlet
 * 
 * @author longmingfeng 2016年12月19日 上午10:26:52
 */
public class RegisterTarget extends HttpServlet {
    private static final long serialVersionUID = 3907989675241802696L;
    private String serverIp;
    private String targetId;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if ("conn".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getConn());
        } else if ("getServerURL".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getServerURL());
        } else if ("isExistTargetId".equals(pathInfo.substring(1))) {
            // 判断指定targetId是否存在
            String targetId = req.getParameter("targetId");
            String serverURL = req.getParameter("serverURL");
            String reqURL = serverURL + "/registerTarget/isExistTargetId?targetId=" + targetId;
            String data = TargetUtil.changeHttp(reqURL);
            resp.getWriter().print(data);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");

        Map<String, String> map = new HashMap<>();

        map.put("targetId", req.getParameter("targetId"));
        map.put("targetName", req.getParameter("targetName"));
        map.put("targetOrg", req.getParameter("targetOrg"));
        map.put("principal", req.getParameter("principal"));
        map.put("phone", req.getParameter("phone"));
        map.put("email", req.getParameter("email"));
        map.put("address", req.getParameter("address"));
        map.put("descript", req.getParameter("descript"));
        map.put("version", "0.0.0");
        map.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new Date()));

        // 重写target.bat文件
        PrintStream ps = null;
        try {
            TargetUtil.writeProp("targetInfo.properties", map);
            File file = new File(System.getProperty("user.dir")
                + File.separator + "target.bat");
            ps = new PrintStream(file.getPath());
            ps.println("title no_jvm_target  longmf");
            ps.println("start " + req.getParameter("targetAddress") + "/system/console/target_fnMarket");
            ps.println("chcp 936");// 设置请求编码为中文
            ps.println("java -Dagent.identification.agentid="
                + req.getParameter("targetId") + " "
                + "-Dagent.discovery.serverurls="
                + req.getParameter("address") + " "
                + "-Dfile.encoding=utf-8  "
                + "-jar bin/felix.jar");
        } catch (Exception e) {
            resp.getWriter().print("false");
        } finally {
            TargetUtil.closeSilently(ps);
        }

        // 将相应属性设置为系统变量
        System.setProperty("agent.identification.agentid",
            req.getParameter("targetId"));
        System.setProperty("agent.discovery.serverurls",
            req.getParameter("address"));// agent启动时，会从系统属性中去读取
        System.setProperty("agent.controller.syncinterval", "30");
        System.setProperty("agent.controller.streaming", "true");
        System.setProperty("agent.controller.syncdelay", "5");

        // 重启agent这个组件，达到重启target的目的
        BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass())
            .getBundleContext();
        for (Bundle bundle : m_bundleContext.getBundles()) {
            if ("org.apache.ace.agent".equals(bundle.getSymbolicName())) {
                try {
                    bundle.stop((int) bundle.getBundleId());
                    bundle.start((int) bundle.getBundleId());
                } catch (BundleException e) {
                    e.printStackTrace();
                }
                break;// 找到相应的agent这个组件，重启后，就停止这个循环
            }
        }
        resp.getWriter().print("true");
    }

    /**
     * 读取配置文件的serverIP和targetId
     * 
     * @author longmingfeng 2016年12月19日 上午10:29:22
     */
    private void getTargetProp() {
        Properties props = TargetUtil.readProp("config.properties");
        if (props == null || props.size() == 0) {
            props = System.getProperties();
        }
        if (props != null && props.size() != 0) {
            serverIp = System.getProperty("agent.discovery.serverurls");
            targetId = System.getProperty("agent.identification.agentid");
        }
    }

    /**
     * 获取注册服务器是的连接成功
     * 
     * @return
     * @author longmingfeng 2016年12月19日 上午10:29:37
     */
    private String getConn() {
        getTargetProp();
        if (targetId == null || targetId.equals("")) {
            return "节点容器未注册";
        } else {
            // 连接指定接口是否连通
            String state = TargetUtil.getConnectionState(serverIp
                + "/servlet/appstore", "");
            if ("连接成功".equals(state)) {
                return "连接成功";
            } else {
                return "连接失败";
            }

        }
    }

    /**
     * 获取默认服务器地址
     * 
     * @return
     * @author longmingfeng 2016年12月19日 上午10:29:47
     */
    private String getServerURL() {
        getTargetProp();
        return serverIp;
    }
}
