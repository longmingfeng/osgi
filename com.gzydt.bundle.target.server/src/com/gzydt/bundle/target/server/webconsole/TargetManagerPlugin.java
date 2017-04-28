package com.gzydt.bundle.target.server.webconsole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;

public class TargetManagerPlugin extends SimpleWebConsolePlugin {

    private static final long serialVersionUID = 906594146351934747L;
    // 请求名称
    private static final String LABEL = "targetmanager";
    // 菜单名称和标题名
    private static final String TITLE = "节点容器管理";
    // 分类菜单名称
    private static final String CATEGORY = "parameterMng";
    // 读取的HTML文件路径,target连接服务
    // private static final String TEMPLATE = "/templates/target.html";
    private static final String TEMPLATE = "/templates/target_manager.html";

    private String template;

    public TargetManagerPlugin() {
        super(LABEL, TITLE, CATEGORY, null);
        template = readTemplateFile(TEMPLATE);
    }

    /**
     * 生成模块的内
     * 
     * @param req
     *            请求
     * @param rsp
     *            响应
     */
    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse rsp)
        throws ServletException, IOException {
        rsp.getWriter().print(template);
    }

}
