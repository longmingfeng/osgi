package com.gzydt.bundle.datasource.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;

/**
 * 数据源展示类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class DataSourcePlugin extends SimpleWebConsolePlugin {

    private static final long serialVersionUID = -676721093425079207L;
    // 请求名称
    private static final String LABEL = "datasource";
    // 菜单名称和标题名称
    private static final String TITLE = "数据源管理";
    // 分类菜单名称
    private static final String CATEGORY = "metadataMng";
    // 读取的HTML文件路径
    private static final String TEMPLATE = "/templates/datasource.html";

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
        String pathInfo = req.getPathInfo();
        String templateHtml = "/templates/" + pathInfo.substring(pathInfo.lastIndexOf("/") + 1) + ".html";
        template = readTemplateFile(templateHtml);
        rsp.getWriter().print(template);
    }

}
