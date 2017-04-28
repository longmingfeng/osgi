package com.gzydt.bundle.target.client.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;

public class TargetPlugin extends SimpleWebConsolePlugin {

    private static final long serialVersionUID = -6488703106193406214L;
    // 请求名称
    private static final String LABEL = "target_fnMarket";
    // 菜单名称和标题名称
    private static final String TITLE = "容器管理";
    // 分类菜单名称
    private static final String CATEGORY = "parameterMng";
    // 读取的HTML文件路径,target连接服务器
    // private static final String TEMPLATE = "/templates/target.html";
    private static final String TEMPLATE = "/templates/target_fnMarket.html";

    private String template;

    public TargetPlugin() {
        super(LABEL, TITLE, CATEGORY, null);
        template = readTemplateFile(TEMPLATE);
    }

    /**
     * 
     * 生成模块的内容
     * 
     * @param req
     *            请求
     * @param rsp
     *            响应
     */
    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse rsp)
        throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String templateHtml = "/templates/" + pathInfo.substring(pathInfo.lastIndexOf("/") + 1) + ".html";
        template = readTemplateFile(templateHtml);
        rsp.getWriter().print(template);
    }

}
