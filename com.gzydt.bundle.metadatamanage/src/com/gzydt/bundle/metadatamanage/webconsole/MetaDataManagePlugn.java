package com.gzydt.bundle.metadatamanage.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;

/**
 * 元数据管理展示类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class MetaDataManagePlugn extends SimpleWebConsolePlugin {
    private static final long serialVersionUID = -676721093425079207L;
    // 请求名称
    private static final String LABEL = "metadata";
    // 菜单名称和标题名称
    private static final String TITLE = "资源类型管理";
    // 分类菜单名称
    private static final String CATEGORY = "metadataMng";
    // 读取的HTML文件路径
    private static final String TEMPLATE = "/templates/metadata.html";

    private String template;

    public MetaDataManagePlugn() {
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
        // 根据请求的路径，切换不同的模板
        template = readTemplateFile(templateHtml);
        rsp.getWriter().print(template);
    }
}
