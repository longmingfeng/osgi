package com.gzydt.bundle.target.server.info;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.gzydt.bundle.target.server.info.bean.Target;
import com.gzydt.bundle.target.server.info.bean.TargetInfo;
import com.gzydt.bundle.target.server.info.bean.Targets;

/**
 * 服务端，对target相关信息的操作servlet
 * 
 * @author longmingfeng 2016年12月19日 上午10:38:43
 */
public class RegisterTarget extends HttpServlet implements TargetInfo {

    private static final long serialVersionUID = 8910871808140358991L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        if ("load".equals(pathInfo.substring(1))) {
            resp.getWriter().print(load());
        } else if ("isExistTargetId".equals(pathInfo.substring(1))) {
            // 判断指定targetId是否存在
            String targetId = req.getParameter("targetId");
            String str = findTargetById(targetId) == null ? "false" : "true";
            resp.getWriter().print(str);
        } else if ("delete".equals(pathInfo.substring(1))) {
            String targetId = req.getParameter("targetId");
            removeTargetById(targetId);
            resp.getWriter().print("删除成功");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        Target target = new Target();
        target.setTargetId(req.getParameter("targetId"));
        target.setTargetName(req.getParameter("targetName"));
        target.setTargetOrg(req.getParameter("targetOrg"));
        target.setPrincipal(req.getParameter("principal"));
        target.setPhone(req.getParameter("phone"));
        target.setEmail(req.getParameter("email"));
        target.setDescript(req.getParameter("descript"));
        target.setServerAddress(req.getParameter("address"));
        target.setVersion(req.getParameter("version"));
        target.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd hh:mm-ss").format(new Date()));
        register(target);
        resp.getWriter().print("true");
    }

    /**
     * 获取节点容器列表
     * 
     * @return 节点容器列表
     */
    private JSONArray load() {
        JSONArray ja = new JSONArray();
        if (getAll() != null) {
            ja.add(getAll());
        }
        return ja;
    }

    @Override
    public void register(Target target) {
        Targets targets = TargetUtil.readXML();
        if (targets != null) {
            targets.getTargets().add(target);
            TargetUtil.writeXML(targets);
        } else {
            targets = new Targets();
            List<Target> list = new ArrayList<>();
            list.add(target);
            targets.setTargets(list);
            TargetUtil.writeXML(targets);
        }
    }

    @Override
    public Targets getAll() {
        Targets targets = new Targets();
        targets = TargetUtil.readXML();
        return targets;
    }

    @Override
    public Target findTargetById(String targetId) {
        Targets targets = TargetUtil.readXML();
        if (targets != null) {
            for (Target target : targets.getTargets()) {
                if (targetId.equals(target.getTargetId())) { return target; }
            }
        }
        return null;
    }

    @Override
    public void removeTargetById(String targetId) {
        Targets newTargets = new Targets();
        Targets targets = TargetUtil.readXML();
        if (targets != null) {
            newTargets.setTargets(new ArrayList<Target>());
            for (Target target : targets.getTargets()) {
                if (!targetId.equals(target.getTargetId())) {
                    newTargets.getTargets().add(target);
                }
            }
            TargetUtil.writeXML(newTargets);
        }
    }
}
