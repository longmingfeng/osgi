/**
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.log.LogService;

import com.gzydt.bundle.database.dao.PersistenceDao;
import com.gzydt.bundle.database.vo.JpaBridgeManager;
import com.gzydt.bundle.database.vo.JpaBridgeManager2;

/**
 * 测试持久化
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
public class PersistenceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LogService log;

    private EntityManager em;

    private PersistenceDao dao;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // em对象对数据库的简单操作测试
        Query q = em.createQuery("select 1");
        log.log(LogService.LOG_INFO, "测试数据有： " + q.getResultList().size() + "条");

        // jpa的持久化测试
        try {
            JpaBridgeManager j = new JpaBridgeManager();
            j.setRuleName("自动");
            j.setCreateTime(new Date());
            dao.save(j);

            JpaBridgeManager2 j2 = new JpaBridgeManager2();
            j2.setRuleName("自动");
            j2.setCreateTime(new Date());
            dao.save(j2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<JpaBridgeManager> list = dao.getList(JpaBridgeManager.class, 1, 10);
        log.log(LogService.LOG_INFO, "数据有：" + list.size() + "条");

        resp.getWriter().print("数据有：" + list.size() + "条");

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
