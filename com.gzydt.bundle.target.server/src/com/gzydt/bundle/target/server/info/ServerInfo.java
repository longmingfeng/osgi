package com.gzydt.bundle.target.server.info;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * target容器下载接口servlet
 * 
 * @author longmingfeng 2016年12月19日 上午10:40:00
 */
public class ServerInfo extends HttpServlet {

    private static final long serialVersionUID = 544651750978605284L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if ("prop".equals(pathInfo.substring(1))) {
            resp.getWriter().print(getProp());
        } else if ("download1".equals(pathInfo.substring(1))) {
            jvmDownload(resp);
            resp.getWriter().print("true");
        } else if ("download".equals(pathInfo.substring(1))) {
            download(resp);
            resp.getWriter().print("true");
        }
    }

    /**
     * 带JVM的容器下载(zip包)
     * 
     * @param resp
     * @author longmingfeng 2016年12月19日 上午10:41:24
     */
    private void jvmDownload(HttpServletResponse resp) {
        /*TargetUtil.downloadFileByDir(System.getProperty("user.dir")
            + File.separator + "zip" + File.separator + "jvm_target.zip", resp);*/
        downloadEXE(resp);
    }

    /**
     * 不带JVM的容器下载（双击可执行jar包）
     * 
     * @param resp
     * @author longmingfeng 2016年12月19日 上午10:41:16
     */
    private void download(HttpServletResponse resp) {
        /*TargetUtil.downloadFileByDir(System.getProperty("user.dir")
            + File.separator + "zip" + File.separator + "target.jar", resp);*/
        downloadEXE(resp);
    }

    /**
     * 下载EXE安装包
     * 
     * @param resp
     * @author longmingfeng 2017年4月11日 下午2:16:33
     */
    private void downloadEXE(HttpServletResponse resp) {
        TargetUtil.downloadFileByDir(System.getProperty("user.dir")
            + File.separator + "zip" + File.separator + "target客户端_windows_1_0_0.exe", resp);
    }

    /**
     * 获取服务器基本信息
     * 
     * @return
     * @author longmingfeng 2016年12月19日 上午10:41:07
     */
    private JSONObject getProp() {
        JSONObject json = new JSONObject();
        Properties props = TargetUtil.readProp("serverInfo.properties");
        if (props.size() != 0) {
            json.put("address", props.getProperty("address"));
            json.put("name", props.getProperty("name"));
            json.put("principal", props.getProperty("principal"));
            json.put("descript", props.getProperty("descript"));
            json.put("phone", props.getProperty("phone"));
            json.put("email", props.getProperty("email"));
            json.put("state", "连接成功");
        }
        return json;
    }

}
