/**
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import org.amdatu.jta.Transactional;

import com.gzydt.bundle.database.dao.PersistenceDao;

/**
 * 持久化接口具体实现
 * 
 * @author longmingfeng 2016年7月13日 上午10:32:53
 * @param <T>
 */
@Transactional
public class PersistenceDaoImpl implements PersistenceDao {

    private volatile EntityManager entityManager;

    @Override
    public <T> T save(T vo) {
        entityManager.persist(vo);
        return vo;
    }

    @Override
    public <T> void update(T vo) {
        entityManager.merge(vo);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T getVOById(Class c, Object id) {
        return (T) entityManager.find(c, id);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> List<T> getList(Class c, int pageStart, int num) {
        // setFirstResult(startRow)//起始数据编号，从零开始编号。与数据库中的数据的id没有关系。
        // setMaxResults(maxResult);//所要读取的数据条数

        if (pageStart == 0 && num == 0) {// 查询全部
            return entityManager.createQuery("select j from " + c.getName() + " j")
                .getResultList();
        } else {// 分页查询
            return entityManager.createQuery("select j from " + c.getName() + " j")
                .setFirstResult((pageStart - 1) * num).setMaxResults(num)
                .getResultList();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getDataList(Class c, int pageStart, int num,
        List<String> column, List<String> dateParam, List<String> likeParam) {

        String sql = "select 1";

        // 对时间类型的条件的拼接
        StringBuilder dateSql = new StringBuilder();
        if (dateParam != null && dateParam.size() > 0) {

            String[] dateColumn = new String[3];
            String dateColumnName = "";
            String sDate = "";
            String eDate = "";

            for (int i = 0, length = dateParam.size(); i < length; i++) {

                dateColumn = dateParam.get(i).split("::");

                dateColumnName = dateColumn[0];
                sDate = dateColumn[1];
                eDate = dateColumn[2];

                if (i < length - 1) {
                    dateSql.append("(j." + dateColumnName + " between '" + sDate +
                        "' and '" + eDate + "') and ");
                } else {
                    dateSql.append("(j." + dateColumnName + " between '" + sDate +
                        "' and '" + eDate + "') ");
                }
            }

        } else {// 没有时间类型

        }

        // 其它查询字段的条件的拼接
        String key = "";
        String value = "";
        StringBuilder wheresql = new StringBuilder();

        if (column != null && column.size() > 0) {
            for (int i = 0, length = column.size(); i < length; i++) {

                key = column.get(i).split("::")[0];
                value = column.get(i).split("::")[1];

                if (i < length - 1) {
                    wheresql.append("j." + key + " = '" + value + "' and ");
                } else {
                    wheresql.append("j." + key + " = '" + value + "'");
                }
            }
        }

        // 模糊字段的拼接
        if (!"".equals(wheresql.toString())) {
            if (likeParam != null && likeParam.size() > 0) {
                wheresql.append(" and ");
            }
        }
        if (likeParam != null && likeParam.size() > 0) {
            for (int i = 0, length = likeParam.size(); i < length; i++) {
                key = likeParam.get(i).split("::")[0];
                value = likeParam.get(i).split("::")[1];

                if (i < (length - 1)) {
                    wheresql.append("j." + key + " like '%" + value + "%' and ");
                } else {
                    wheresql.append("j." + key + " like '%" + value + "%'");
                }
            }
        }

        // 将基本的SQL条件与时间字段SQL拼接成最终SQL
        if (!"".equals(wheresql.toString())) {
            if (!"".equals(dateSql.toString())) {
                sql = "select j from " + c.getName() + " j where " + wheresql.toString() + " and " + dateSql.toString();
            } else {
                sql = "select j from " + c.getName() + " j where " + wheresql.toString();
            }
        } else {
            if (!"".equals(dateSql.toString())) {
                sql = "select j from " + c.getName() + " j where " + dateSql.toString();
            } else {
                sql = "select j from " + c.getName() + " j";
            }
        }

        if (pageStart == 0 && num == 0) {// 查询全部
            return entityManager.createQuery(sql)
                .getResultList();
        } else {// 分页查询
            return entityManager.createQuery(sql)
                .setFirstResult((pageStart - 1) * num).setMaxResults(num)
                .getResultList();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void delete(Class c, Object[] ids) {
        for (int i = 0, length = ids.length; i < length; i++) {
            entityManager.remove(this.getVOById(c, ids[i]));
        }
    }

}
