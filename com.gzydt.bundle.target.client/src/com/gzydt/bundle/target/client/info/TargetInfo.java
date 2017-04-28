package com.gzydt.bundle.target.client.info;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TargetInfo extends HttpServlet {

    private static final long serialVersionUID = 8759609263949040036L;
    private String serverIp;
    private String targetId;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if ("prop".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getTargetProp());
        } else if ("info".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getTargetInfo());
        } else if ("serverGetInfo".equals(pathInfo.substring(1))) {
            String id = req.getParameter("targetId");
            String serverAddress = req.getParameter("serverAddress");
            resp.getWriter().print(getServerGetInfo(serverAddress, id));
        } else if ("getLog".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getLog());
        } else if ("getLogNum".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getLogNum());
        } else if ("getApp".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getApp());
        } else if ("getTargetIdServerIp".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getTargetIdServerIp());
        } else if ("getSystemInfo".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getSystemInfo());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        Map<String, String> map = new HashMap<>();
        String pathInfo = req.getPathInfo();
        if ("prop".equals(pathInfo.substring(1))) {
            JSONObject json = getTargetProp();
            String targetId = json.getString("agent.identification.agentid");
            String serverIp = json.getString("agent.discovery.serverurls");

            System.setProperty("agent.identification.agentid", targetId);
            System.setProperty("agent.discovery.serverurls", serverIp);
            System.setProperty("agent.controller.syncinterval", req.getParameter("syncinterval"));
            System.setProperty("agent.controller.streaming", req.getParameter("streaming"));
            System.setProperty("agent.controller.syncdelay", req.getParameter("syncdelay"));
            System.setProperty("agent.logging.level", req.getParameter("log_level"));
            System.setProperty("agent.controller.fixpackages", req.getParameter("fixpackages"));
            System.setProperty("agent.controller.retries", req.getParameter("retries"));

            // 重启agent这个组件，达到重启target的目的
            BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass())
                .getBundleContext();
            for (Bundle bundle : m_bundleContext.getBundles()) {
                if ("org.apache.ace.agent".equals(bundle.getSymbolicName()))
                    try {

                        bundle.stop((int) bundle.getBundleId());
                        bundle.start((int) bundle.getBundleId());

                    } catch (BundleException e) {
                        e.printStackTrace();
                    }
            }
        } else {
            Properties props = TargetUtil.readProp("targetInfo.properties");
            for (Object key : props.keySet()) {
                map.put(key.toString(), props.getProperty(key.toString()));
            }
            map.put("targetName", req.getParameter("targetName"));
            map.put("targetOrg", req.getParameter("targetOrg"));
            map.put("principal", req.getParameter("principal"));
            map.put("phone", req.getParameter("phone"));
            map.put("email", req.getParameter("email"));
            map.put("descript", req.getParameter("descript"));
            try {
                TargetUtil.writeProp("targetInfo.properties", map);
            } catch (Exception e) {
                resp.getWriter().print("false");
            }
        }
        resp.getWriter().print("true");

    }

    /**
     * 获取错误日志内容
     * 
     * @return 错误日志内容
     */
    private JSONObject getLog() {
        JSONObject json = null;
        Properties props = TargetUtil.readProp("targetInfo.properties");
        String server_url = props.getProperty("address") + "/serverInfo/prop";
        String serverInfo = TargetUtil.changeHttp(server_url);
        if (serverInfo != null && !"".equals(serverInfo)) {
            json = JSONObject.fromObject(serverInfo);
        } else {
            json = new JSONObject();
            json.put("state", "连接失败");
            json.put("name", "获取失败");
            json.put("principal", "获取失败");
            json.put("descript", "获取失败");
            json.put("phone", "获取失败");
            json.put("email", "获取失败");
        }
        json.put("address", props.getProperty("address"));
        List<String> logs = TargetUtil.readLog();
        json.put("logs", logs);
        TargetUtil.clearLog();
        return json;
    }

    /**
     * 获取日志数目
     * 
     * @return 日志数目
     */
    private JSONObject getLogNum() {
        JSONObject json = new JSONObject();
        List<String> logs = TargetUtil.readLog();
        Properties props = TargetUtil.readProp("targetInfo.properties");
        String server_url = props.getProperty("address") + "/serverInfo/prop";
        String serverInfo = TargetUtil.changeHttp(server_url);
        if (serverInfo == null || "".equals(serverInfo)) {
            json.put("state", "连接失败");
        }
        json.put("logs", logs);
        return json;
    }

    /**
     * 获取target与服务器的相关信息
     * 
     * @param serverAddress
     *            服务器URL
     * @param id
     *            targetID
     * @return
     * @author longmingfeng 2016年12月19日 上午10:33:05
     */
    private JSONObject getServerGetInfo(String serverAddress, String id) {
        JSONObject json = new JSONObject();
        JSONObject json1 = getTargetProp();
        serverIp = json1.getString("agent.discovery.serverurls");
        targetId = json1.getString("agent.identification.agentid");
        if (serverAddress.equals(serverIp) && id.equals(targetId)) {
            json.put("state", "连接成功");
            Properties props = TargetUtil.readProp("targetInfo.properties");
            if (props != null && props.size() != 0) {
                json.put("version", props.get("version"));
                json.put("updateTime", props.get("updateTime"));
            }
        } else {
            json.put("state", "连接失败");
            json.put("version", "获取失败");
            json.put("updateTime", "获取失败");
            json.put("maxVersion", "获取失败");
        }
        return json;

    }

    /**
     * 获取配置信息
     * 
     * @return 配置信息
     */
    private JSONObject getTargetProp() {
        JSONObject json = new JSONObject();
        Properties props = null;
        if (props == null || props.size() == 0) {
            props = System.getProperties();
        }
        for (Object key : props.keySet()) {
            json.put(key.toString(), props.get(key));
        }
        return json;
    }

    /**
     * 获取target基本信息
     * 
     * @return target基本信息
     */
    private JSONObject getTargetInfo() {
        JSONObject json = new JSONObject();
        Properties props = TargetUtil.readProp("targetInfo.properties");
        for (Object key : props.keySet()) {
            json.put(key.toString(), props.get(key));
        }
        return json;
    }

    /**
     * 获取服务端组装的系统
     * 
     * @return
     * @author longmingfeng 2016年12月19日 上午10:34:13
     */
    private JSONObject getApp() {
        JSONObject json = new JSONObject();
        JSONObject json1 = getTargetProp();
        serverIp = json1.getString("agent.discovery.serverurls");
        String serverInfo = TargetUtil.changeHttp(serverIp + "/servlet/appstore");
        if (serverInfo != null && !"".equals(serverInfo)) {
            json = JSONObject.fromObject(serverInfo);
        }
        return json;
    }

    /**
     * 读取文件，获取targetid,serverip
     * 
     * @return
     * @author longmingfeng 2016年12月19日 上午10:35:00
     */
    private JSONObject getTargetIdServerIp() {
        JSONObject json = getTargetProp();
        String targetId = json.getString("agent.identification.agentid");
        String serverIp = json.getString("agent.discovery.serverurls");
        json = new JSONObject();
        json.put("targetId", targetId);
        json.put("serverIp", serverIp);
        return json;
    }

    /**
     * 获取系统名称和Id
     * 
     * @return 系统信息
     */
    private JSONArray getSystemInfo() {
        JSONArray ja = new JSONArray();
        Properties props = TargetUtil.readProp("installInfo.properties");
        for (Object key : props.keySet()) {
            if (key.toString().contains("_name")) {
                JSONObject json = new JSONObject();
                String id = key.toString().substring(0, key.toString().lastIndexOf("_"));
                json.put("id", id);
                json.put("name", props.getProperty(key.toString()));
                json.put("version", props.getProperty(id + "_version"));
                ja.add(json);
            }
        }
        return ja;
    }

}
