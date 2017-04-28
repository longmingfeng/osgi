/**
 *   @author longmingfeng    2016-10-27 10:53:27
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.target.client.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * 系统安装详情请求的servlet
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
public class SystemInstall extends HttpServlet {

    private static final long serialVersionUID = 1254039194177789605L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && "info".equals(pathInfo.substring(1))) {
            resp.getWriter().println(readInstallInfo());
        } else if (pathInfo != null && "readAppOperateLog".equals(pathInfo.substring(1))) {
            // 读取应用安装日志
            resp.getWriter().println(readAppOperateLog());
        } else if (pathInfo != null && "uninstall".equals(pathInfo.substring(1))) {
            // 卸载应用系统
            String systemId = req.getParameter("systemId");
            String systemName = req.getParameter("name");
            String bundleCount = req.getParameter("bundleCount");
            String b_num = System.getProperty("target.default.sum") == null ? "12"
                : System.getProperty("target.default.sum");
            int n = bundleCount == null || "".equals(bundleCount) ? 0 : Integer.parseInt(bundleCount);
            int b = b_num == null || "".equals(b_num) ? 0 : Integer.parseInt(b_num);
            BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
            while (true) {
                int bundle_num = m_bundleContext.getBundles().length;
                if (b - n == bundle_num) {
                    System.setProperty("target.default.sum", (b - n) + "");
                    removeInstallInfo(systemId);
                    writeAppOperateLog(systemName, "卸载成功");
                    break;
                }
            }
        } else {
            String num = req.getParameter("bundle_num");// 所要安装系统里的组件数
            String systemId = req.getParameter("systemId");
            String version = req.getParameter("version");
            String name = req.getParameter("name");
            String bundleName = req.getParameter("bundleName");
            int n = num == null || "".equals(num) ? 0 : Integer.parseInt(num);

            // 开发测试环境下12个
            String b_num = System.getProperty("target.default.sum") == null ? "12"
                : System.getProperty("target.default.sum");
            int b = b_num == null || "".equals(b_num) ? 0 : Integer.parseInt(b_num);

            BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();

            String install_state = "";
            TargetUtil.clearLog();
            while (true) {
                int bundle_num = m_bundleContext.getBundles().length;

                if (TargetUtil.readLog().size() > 0) {
                    install_state = "安装";
                    writeInstallInfo(systemId, version, install_state, null, null);
                    writeAppOperateLog(name, "安装失败");
                    break;
                }

                if (b + n > bundle_num) {
                    install_state = "安装中";
                    writeInstallInfo(systemId, version, install_state, null, null);

                } else if (b + n == bundle_num) {
                    install_state = "已安装";
                    writeInstallInfo(systemId, version, install_state, name, bundleName);
                    writeAppOperateLog(name, "安装成功");
                    System.setProperty("target.default.sum", b + n + "");
                    break;
                }
            }
            resp.getWriter().print(install_state);

        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 读取系统是否安装
     * 
     * @return
     * @author longmingfeng 2016年12月1日 下午4:20:33
     */
    private JSONObject readInstallInfo() {
        JSONObject json = new JSONObject();
        Properties props = TargetUtil.readProp("installInfo.properties");
        if (props != null) {
            for (Object key : props.keySet()) {
                json.put(key.toString(), props.getProperty(key.toString()));
            }
        }
        return json;
    }

    /**
     * 向文件中写入安装信息
     * 
     * @param systemId
     *            系统ID
     * @param version
     *            系统版本
     * @param state
     *            系统状态
     * @param name
     *            系统名称
     * @param bundleName
     *            系统包含所有bundle的名称
     * @author longmingfeng 2016年12月1日 下午4:20:54
     */
    private void writeInstallInfo(String systemId, String version, String state, String name, String bundleName) {
        Properties props = TargetUtil.readProp("installInfo.properties");

        Map<String, String> map = new HashMap<>();
        for (Object key : props.keySet()) {
            map.put(key.toString(), props.getProperty(key.toString()));
        }

        map.put(systemId, systemId);
        map.put(systemId + "_version", version);
        map.put(systemId + "_state", state);
        if (name != null) {
            map.put(systemId + "_name", name);
        }
        if (bundleName != null) {
            map.put(systemId + "_bundleName", bundleName);
        }

        TargetUtil.writeProp("installInfo.properties", map);
    }

    /**
     * 将应用的安装操作日志写入文件
     * 
     * @param systemName
     * @param operate
     */
    private void writeAppOperateLog(String systemName, String operate) {
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String content = createTime + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + systemName + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + operate;
        TargetUtil.writeToLog("appOperate.log", content);
    }

    /**
     * 读取应用的安装操作日志
     * 
     * @return 日志内容
     */
    private String readAppOperateLog() {
        File file = null;
        BufferedReader br = null;
        StringBuffer buffer = new StringBuffer();
        try {

            file = new File(System.getProperty("user.dir") + File.separator + "log" + File.separator + "appOperate.log");
            if (!file.exists())
                return "";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String s = null;
            while ((s = br.readLine()) != null) {
                buffer.append("<div style='text-align:left'>" + s + "</div><br/>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TargetUtil.closeSilently(br);
        }
        return buffer.toString();
    }

    /**
     * 移除配置文件中包含指定systemId的项
     * 
     * @param systemId
     *            系统id
     */
    private void removeInstallInfo(String systemId) {
        Properties props = TargetUtil.readProp("installInfo.properties");
        Map<String, String> map = new HashMap<>();
        for (Object key : props.keySet()) {
            map.put(key.toString(), props.getProperty(key.toString()));
        }
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.contains(systemId)) {
                iterator.remove();
                map.remove(key);
            }
        }
        TargetUtil.writeProp("installInfo.properties", map);
    }
}
