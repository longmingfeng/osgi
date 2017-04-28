/**
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.metadatamanage.info.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import com.gzydt.bundle.database.dao.CommonUtilDao;
import com.gzydt.bundle.database.dao.impl.CommonUtilDaoImpl;
import com.gzydt.bundle.metadatamanage.info.util.MetaDataUtil;

/**
 * 视图管理请求的通用增删改查方法，统一在这里处理<br>
 * 
 * 增删改查方法参数说明:dataSourceName,tableName这两个参数必不可少，其它参数名要与数据库表的列名一致<br>
 * 
 * 新增(primaryKeyName主键名，primaryKeyType主键类型有int，Integer，num,String四种,primaryKeyValue主键值，主键有可能让前端输入)<br>
 * http://localhost:8080/viewdata_util/add?dataSourceName=?&tableName=?&primaryKeyName=?&primaryKeyType=?&
 * primaryKeyValue=?<br>
 * 
 * 删除（primaryKeyName主键名，idArray所要删除的记录ID集合,ID与ID之间用逗号隔开）<br>
 * http://localhost:8080/viewdata_util/delete?dataSourceName=?&tableName=?&primaryKeyName=?&idArray=?,?,?<br>
 * 
 * 删除主表数据（primaryKeyName主表主键名，idArray所要删除的主表记录ID集合,ID与ID之间用逗号隔开,sonTableName子表名，sonFkName子表外键名）<br>
 * http://localhost:8080/viewdata_util/deleteParent?dataSourceName=?&tableName=?&primaryKeyName=?&idArray=?,?,?&
 * sonTableName=?&sonFkName=?<br>
 * 
 * 删除子表数据（primaryKeyName主表主键名，idArray所要删除的子表记录ID集合,ID与ID之间用逗号隔开,primaryKeyName主表主键名,sonTableName子表名，
 * sonPrimaryKeyName子表主键名，sonFkName子表外键名，sonFkValue子表外键值）<br>
 * http://localhost:8080/viewdata_util/deleteSon?dataSourceName=?&tableName=?&primaryKeyName=?&idArray=?,?,?&
 * sonTableName=?&sonFkName=?&sonFkValue=?<br>
 * 
 * 修改（primaryKeyName主键名，primaryKeyValue主键值）<br>
 * http://localhost:8080/viewdata_util/update?dataSourceName=?&tableName=?&primaryKeyName=?&primaryKeyValue=?<br>
 * 
 * 查询 （dateParam参数对应的数据库字段为时间类型,字段名::开始时间::结束时间,可能有多个,多个之间用逗号隔开，格式timename::stime::etime,timename::stime::etime，
 * pageStart要显示第几页数据，pageNum每页显示多少条）<br>
 * likeParamNameValue模糊字段，字段名：：模糊字段值，可能有多个,多个之间用逗号隔开，格式key::value,key::value，key为字段名，value为模糊字段值<br>
 * http://localhost:8080/viewdata_util/select?dataSourceName=?&tableName=?&dateParam=?::?::?,?::?::?&
 * likeParamNameValue=?::?,?::?&pageStart=?&pageNum=?<br>
 * 
 * @author longmingfeng 2017年2月28日 上午9:44:23
 */
public class ViewdataUtilServlet extends HttpServlet {

    private LogService log;

    private static final long serialVersionUID = 1L;

    private volatile BundleContext context;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/javascript;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        log.log(LogService.LOG_INFO, "组件" + context.getBundle().getSymbolicName() + "请求路径为：" + req.getRequestURL());

        Map<String, String[]> map = req.getParameterMap();

        // 数据源名
        String dataSourceName = (map.get("dataSourceName") == null || map.get("dataSourceName").length == 0) ? "" : map.get("dataSourceName")[0];

        // 没有主从表时，tableName就是当前操作的表名，有主从表时，tableName表示主表的表名
        String tableName = (map.get("tableName") == null || map.get("tableName").length == 0) ? "" : map.get("tableName")[0];
        // 没有主从表时，primaryKeyName就是当前操作的表的主键名，有主从表时，primaryKeyName表示主表的主键名
        String primaryKeyName = (map.get("primaryKeyName") == null || map.get("primaryKeyName").length == 0) ? "" : map.get("primaryKeyName")[0];
        // 主键值
        String primaryKeyValue = (map.get("primaryKeyValue") == null || map.get("primaryKeyValue").length == 0) ? "" : map.get("primaryKeyValue")[0];
        // 主键类型int，Integer，num,String四种
        String primaryKeyType = (map.get("primaryKeyType") == null || map.get("primaryKeyType").length == 0) ? "" : map.get("primaryKeyType")[0];

        // 以下几个字段，在有主从表情况下，级联删除时用到
        // 子表名
        String sonTableName = (map.get("sonTableName") == null || map.get("sonTableName").length == 0) ? "" : map.get("sonTableName")[0];
        // 子表主键名
        String sonPrimaryKeyName = (map.get("sonPrimaryKeyName") == null || map.get("sonPrimaryKeyName").length == 0) ? "" : map.get("sonPrimaryKeyName")[0];
        // 子表外键名
        String sonFkName = (map.get("sonFkName") == null || map.get("sonFkName").length == 0) ? "" : map.get("sonFkName")[0];
        // 子表外键值
        String sonFkValue = (map.get("sonFkValue") == null || map.get("sonFkValue").length == 0) ? "" : map.get("sonFkValue")[0];

        // 删除时，所需的主键ID集合(多个ID用逗号隔开)
        String idArray = (map.get("idArray") == null || map.get("idArray").length == 0) ? "" : map.get("idArray")[0];
        String[] ids = idArray.split(",");

        // 第几页
        String pageStart = (map.get("pageStart") == null || map.get("pageStart").length == 0) ? "" : map.get("pageStart")[0];

        // 每面显示几条记录
        String pageNum = (map.get("pageNum") == null || map.get("pageNum").length == 0) ? "" : map.get("pageNum")[0];

        // 查询时，参数对应的数据库字段为时间类型的字段名::开始时间::结束时间,多个字段以逗号隔开（timename::stime::etime,timename::stime::etime）
        String dateParam = (map.get("dateParam") == null || map.get("dateParam").length == 0) ? "" : map.get("dateParam")[0];
        List<String> dateParamList = !"".equals(dateParam) ? Arrays.asList(dateParam.split(",")) : new ArrayList<String>();

        // 模糊字段，可能有多个,多个之间用逗号隔开（以key::value,key::value方式传递）key为字段名，value为模糊字段值
        String likeParamNameValue = (map.get("likeParamNameValue") == null || map.get("likeParamNameValue").length == 0) ? "" : map.get("likeParamNameValue")[0];
        List<String> likeParam = !"".equals(likeParamNameValue) ? Arrays.asList(likeParamNameValue.split(",")) : new ArrayList<String>();

        List<String> paramList = new ArrayList<String>();
        for (String set : map.keySet()) {
            if (!"dataSourceName".equals(set)
                && !"tableName".equals(set)
                && !"primaryKeyName".equals(set)
                && !"primaryKeyValue".equals(set)
                && !"primaryKeyType".equals(set)
                && !"sonTableName".equals(set)
                && !"sonPrimaryKeyName".equals(set)
                && !"sonFkName".equals(set)
                && !"sonFkValue".equals(set)
                && !"idArray".equals(set)
                && !"pageStart".equals(set)
                && !"pageNum".equals(set)
                && !"dateParam".equals(set)
                && !"likeParamNameValue".equals(set)) {
                paramList.add(set + "::" + map.get(set)[0]);// 用双冒号隔开，避免在后面拆分时，与时间类型的冲突
            }
        }

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao dao = new CommonUtilDaoImpl(con, log);

        JSONArray jsonArray = new JSONArray();
        boolean isSuccess = false;
        if ("/add".equals(pathInfo)) {// 新增
            isSuccess = dao.save(tableName, primaryKeyName, primaryKeyType, primaryKeyValue, paramList);
        } else if ("/delete".equals(pathInfo)) {// 删除
            isSuccess = dao.delete(tableName, primaryKeyName, ids);
        } else if ("/deleteParent".equals(pathInfo)) {// 有主从表，删除主表记录
            isSuccess = dao.deleteParent(tableName, primaryKeyName, ids, sonTableName, sonFkName);
        } else if ("/deleteSon".equals(pathInfo)) {// 有主从表，删除子表记录
            isSuccess = dao.deleteSon(sonTableName, sonPrimaryKeyName, ids, tableName, primaryKeyName, sonFkName, sonFkValue);
        } else if ("/update".equals(pathInfo)) {// 修改
            isSuccess = dao.update(tableName, primaryKeyName, primaryKeyValue, paramList);
        } else if ("/select".equals(pathInfo)) {// 查询
            jsonArray = dao.getDataList(pageStart, pageNum, tableName, paramList, dateParamList, likeParam);
        } else {

        }

        PrintWriter out = resp.getWriter();
        if ("/select".equals(pathInfo)) {
            out.print(jsonArray);
        } else {
            out.print(isSuccess);
        }
        out.close();

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
