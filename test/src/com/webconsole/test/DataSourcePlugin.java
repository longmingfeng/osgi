package com.webconsole.test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;

public class DataSourcePlugin extends SimpleWebConsolePlugin {

	private static final long serialVersionUID = -676721093425079207L;
	// 请求名称
    private static final String LABEL = "testwebconsole";
    // 菜单名称和标题名称
    private static final String TITLE = "test";
    // 分类菜单名称
    private static final String CATEGORY = "Web Console";
    // 读取的HTML文件路径
    private static final String TEMPLATE = "/templates/test1.jsp";

    private String template;

    public DataSourcePlugin() {
        super(LABEL, TITLE, CATEGORY, null);
        template = readTemplateFile(TEMPLATE);
    }

    /**
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
    	String pathInfo=req.getPathInfo();
    	String templateHtml = "/templates/" + pathInfo.substring(pathInfo.lastIndexOf("/") + 1) + ".jsp";
        template = readTemplateFile(templateHtml);
        rsp.getWriter().print(template);
    }

}
