package com.gzydt.bundle.metadatamanage.info.rest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osgi.service.log.LogService;

import com.gzydt.bundle.metadatamanage.info.api.BaseMetaData;
import com.gzydt.bundle.metadatamanage.info.api.BaseMetaDataField;
import com.gzydt.bundle.metadatamanage.info.api.MetaDataService;
import com.gzydt.bundle.metadatamanage.info.api.PersistentService;
import com.gzydt.bundle.metadatamanage.info.api.Relation;
import com.gzydt.bundle.metadatamanage.info.api.ResourceMetaData;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaData;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaDataField;
import com.gzydt.bundle.metadatamanage.info.util.MetaDataUtil;

/**
 * 元数据Http接口类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class MetaDataResource extends HttpServlet {
    private LogService log;

    private static final long serialVersionUID = 1L;
    // 元数据管理接口
    private MetaDataService dao;
    // 持久化接口
    private PersistentService persistentService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();
        try {
            // 保存或者修改的基础元数据方法
            if ("saveOrUpdate".equals(pathInfo.substring(1))) {
                BaseMetaData metaData = new BaseMetaData();
                metaData.setId(req.getParameter("id"));
                metaData.setName(req.getParameter("name"));
                metaData.setType(req.getParameter("type"));
                metaData.setAuthor(req.getParameter("author"));
                metaData.setState(Integer.parseInt(req.getParameter("state")));
                metaData.setContent(req.getParameter("desc"));
                BaseMetaData oldMetaData = dao.getById(BaseMetaData.class, metaData.getId());
                if (oldMetaData == null) {
                    dao.save(metaData);
                } else {
                    dao.update(metaData);
                    List<BaseMetaDataField> metaDataFields = dao
                        .getByMetaDataId(metaData.getId());
                    for (BaseMetaDataField baseMetaDataField : metaDataFields) {
                        dao.remove(BaseMetaDataField.class,
                            baseMetaDataField.getId());
                    }
                }
                String acdValue = "[" + req.getParameter("acdValue") + "]";
                JSONArray array = JSONArray.fromObject(acdValue);
                for (Object object : array) {
                    JSONObject json = JSONObject.fromObject(object);
                    BaseMetaDataField metaDataField = new BaseMetaDataField();
                    metaDataField.setId(UUID.randomUUID().toString());
                    metaDataField.setFieldId(json.getString("id"));
                    metaDataField.setName(json.getString("name"));
                    metaDataField.setMetaDataId(metaData.getId());
                    metaDataField.setContent(json.getString("desc"));
                    metaDataField.setDefaultValue(json.getString("default"));
                    metaDataField.setIndexs(Integer.parseInt(json
                        .getString("index")));
                    metaDataField.setIsNull(Integer.parseInt(json
                        .getString("req")));
                    metaDataField.setIsPrimary(Integer.parseInt(json
                        .getString("key")));
                    metaDataField.setRowNum(Integer.parseInt(json
                        .getString("rows")));
                    metaDataField.setTip(json.getString("tips"));
                    metaDataField.setType(json.getString("type"));
                    dao.save(metaDataField);
                }
                resp.getWriter().print("保存成功");
                // 获取全部基础元数据
            } else if ("getAll".equals(pathInfo.substring(1))) {
                JSONObject json = new JSONObject();
                int pageNum = Integer.parseInt(req.getParameter("pageNum"));
                int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                int isStop = Integer.parseInt(req.getParameter("isStop"));
                String keyword = req.getParameter("keyword");
                pageSize = pageSize > 0 ? pageSize : 10;
                int total = 0;
                if (isStop == -1 && keyword.equals("")) {
                    total = dao.count(BaseMetaData.class);
                } else {
                    total = dao.count(BaseMetaData.class, isStop, keyword);
                }
                int pageCount = total % pageSize == 0 ? total / pageSize
                    : total / pageSize + 1;
                if (pageNum <= 0) {
                    pageNum = 1;
                } else if (pageNum >= pageCount && pageCount != 0) {
                    pageNum = pageCount;
                } else if (pageCount == 0) {
                    pageNum = 1;
                }
                json.put("total", total);
                json.put("pageCount", pageCount);
                List<BaseMetaData> list = new ArrayList<>();
                if (isStop == -1 && keyword.equals("")) {
                    list = dao.getAll(BaseMetaData.class,
                        pageNum, pageSize);
                } else {
                    list = dao.getAll(BaseMetaData.class,
                        pageNum, pageSize, isStop, keyword);
                }
                JSONArray jsonArray = JSONArray.fromObject(list);
                json.put("data", jsonArray);
                resp.getWriter().print(json);
                // 移除基础元数据
            } else if ("remove".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                dao.remove(BaseMetaData.class, id);
                List<BaseMetaDataField> metaDataFields = dao.getByMetaDataId(id);
                for (BaseMetaDataField baseMetaDataField : metaDataFields) {
                    dao.remove(BaseMetaDataField.class, baseMetaDataField.getId());
                }
                resp.getWriter().print("删除成功");
                // 修改基础元数据状态
            } else if ("changeState".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                int state = Integer.parseInt(req.getParameter("state"));
                BaseMetaData metaData = dao.getById(BaseMetaData.class, id);
                metaData.setState(state);
                dao.update(metaData);
                resp.getWriter().print("修改成功");
                // 根据Id获取基础元数据
            } else if ("getById".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                BaseMetaData metaData = dao.getById(BaseMetaData.class, id);
                JSONObject jsonObject = JSONObject.fromObject(metaData);
                List<BaseMetaDataField> metaDataFields = dao.getByMetaDataId(id);
                JSONArray jsonArray = JSONArray.fromObject(metaDataFields);
                JSONObject json = new JSONObject();
                json.put("OCD", jsonObject);
                json.put("AD", jsonArray);
                resp.getWriter().print(json);
                // 上传文件操作
            } else if ("fileOperate".equals(pathInfo.substring(1))) {
                File uploadedFile = null;
                try {
                    File directory = new File(System.getProperty("user.dir") + File.separator + "upload");
                    if (!directory.exists())
                        directory.mkdir();
                    DiskFileItemFactory factory = new DiskFileItemFactory();
                    ServletFileUpload upload = new ServletFileUpload(factory);
                    upload.setHeaderEncoding("utf8");
                    @SuppressWarnings("unchecked")
                    List<FileItem> items = upload.parseRequest(req);
                    Iterator<FileItem> iter = items.iterator();
                    while (iter.hasNext()) {
                        FileItem item = iter.next();
                        if (!item.isFormField()) {
                            uploadedFile = new File(directory + File.separator + item.getName());
                            item.write(uploadedFile);
                        }
                    }
                } catch (FileUploadException e) {
                    log.log(LogService.LOG_ERROR, "上传文件操作" + e);
                    e.printStackTrace();
                } catch (Exception e) {
                    log.log(LogService.LOG_ERROR, "上传文件操作" + e);
                    e.printStackTrace();
                }
                JSONObject json = new JSONObject();
                json.put("fileName", uploadedFile.getName());
                resp.getWriter().print(json.toString());
                // 提取基础元数据
            } else if ("pick".equals(pathInfo.substring(1))) {
                String fileName = req.getParameter("fileName");
                String type = req.getParameter("type");
                String author = req.getParameter("author");
                String state = req.getParameter("state");
                if (fileName.endsWith("xml")) {
                    List<Map<String, Object>> metaDatas = MetaDataUtil.readXml(fileName);
                    for (Map<String, Object> map : metaDatas) {
                        BaseMetaData metaData = (BaseMetaData) map.get("OCD");
                        metaData.setAuthor(author);
                        metaData.setState(Integer.parseInt(state));
                        metaData.setType(type);
                        BaseMetaData oldMetaData = dao.getById(BaseMetaData.class, metaData.getId());
                        if (oldMetaData == null) {
                            dao.save(metaData);
                        } else {
                            dao.update(metaData);
                            List<BaseMetaDataField> metaDataFields = dao
                                .getByMetaDataId(metaData.getId());
                            for (BaseMetaDataField baseMetaDataField : metaDataFields) {
                                dao.remove(BaseMetaDataField.class,
                                    baseMetaDataField.getId());
                            }
                        }
                        @SuppressWarnings("unchecked")
                        List<BaseMetaDataField> metaDataFields = (List<BaseMetaDataField>) map.get("AD");
                        for (BaseMetaDataField baseMetaDataField : metaDataFields) {
                            dao.save(baseMetaDataField);
                        }
                    }
                    resp.getWriter().print("提取成功");
                } else {
                    resp.getWriter().print("请上传正确的XML文件");
                }
                // 导出基础元数据
            } else if ("output".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                BaseMetaData metaData = dao.getById(BaseMetaData.class, id);
                List<BaseMetaDataField> metaDataFields = dao.getByMetaDataId(id);
                File directory = new File(System.getProperty("user.dir") + File.separator + "download");
                if (!directory.exists())
                    directory.mkdir();
                File file = new File(directory, metaData.getId() + ".xml");
                if (!file.exists()) {
                    MetaDataUtil.writeXml(metaData, metaDataFields, file.getPath());
                }
                MetaDataUtil.downloadFileByDir(file.getPath(), resp);
                // 修改或新增资源元数据
            } else if ("saveOrUpdateR".equals(pathInfo.substring(1))) {
                ResourceMetaData metaData = new ResourceMetaData();
                metaData.setId(req.getParameter("id"));
                metaData.setName(req.getParameter("name"));
                metaData.setType(req.getParameter("type"));
                metaData.setAuthor(req.getParameter("author"));
                metaData.setState(Integer.parseInt(req.getParameter("state")));
                metaData.setContent(req.getParameter("desc"));
                metaData.setPath(req.getParameter("path"));
                metaData.setResourceType(Integer.parseInt(req.getParameter("rType")));
                ResourceMetaData oldMetaData = dao.getById(ResourceMetaData.class, metaData.getId());
                if (oldMetaData == null) {
                    dao.save(metaData);
                } else {
                    dao.update(metaData);
                }
                resp.getWriter().print("保存成功");
                // 获取全部资源元数据
            } else if ("getRMAll".equals(pathInfo.substring(1))) {
                JSONObject json = new JSONObject();
                int pageNum = Integer.parseInt(req.getParameter("pageNum"));
                int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                int isStop = Integer.parseInt(req.getParameter("isStop"));
                String keyword = req.getParameter("keyword");
                pageSize = pageSize > 0 ? pageSize : 10;
                int total = 0;
                if (isStop == -1 && keyword.equals("")) {
                    total = dao.count(ResourceMetaData.class);
                } else {
                    total = dao.count(ResourceMetaData.class, isStop, keyword);
                }
                int pageCount = total % pageSize == 0 ? total / pageSize
                    : total / pageSize + 1;
                if (pageNum <= 0) {
                    pageNum = 1;
                } else if (pageNum >= pageCount && pageCount != 0) {
                    pageNum = pageCount;
                } else if (pageCount == 0) {
                    pageNum = 1;
                }
                json.put("total", total);
                json.put("pageCount", pageCount);
                List<ResourceMetaData> list = new ArrayList<>();
                if (isStop == -1 && keyword.equals("")) {
                    list = dao.getAll(ResourceMetaData.class,
                        pageNum, pageSize);
                } else {
                    list = dao.getAll(ResourceMetaData.class,
                        pageNum, pageSize, isStop, keyword);
                }
                JSONArray jsonArray = JSONArray.fromObject(list);
                json.put("data", jsonArray);
                resp.getWriter().print(json);
                // 移除资源元数据
            } else if ("removeRM".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                dao.remove(ResourceMetaData.class, id);
                resp.getWriter().print("删除成功");
                // 修改资源元数据状态
            } else if ("changeStateRM".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                int state = Integer.parseInt(req.getParameter("state"));
                ResourceMetaData metaData = dao.getById(ResourceMetaData.class, id);
                metaData.setState(state);
                dao.update(metaData);
                resp.getWriter().print("修改成功");
                // 根据Id查询资源元数据
            } else if ("getRMById".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                ResourceMetaData metaData = dao.getById(ResourceMetaData.class, id);
                JSONObject jsonObject = JSONObject.fromObject(metaData);
                resp.getWriter().print(jsonObject);
                // 获取全部资源元数据
            } else if ("getAllResource".equals(pathInfo.substring(1))) {
                List<ResourceMetaData> list = dao.getAllResource();
                JSONArray jsonArray = JSONArray.fromObject(list);
                resp.getWriter().print(jsonArray);
                // 获取所有表格
            } else if ("getTables".equals(pathInfo.substring(1))) {
                JSONObject json = new JSONObject();
                String resource = req.getParameter("resource");
                ResourceMetaData rm = dao.getById(ResourceMetaData.class, resource);
                Properties props = MetaDataUtil.readProp("com.gzydt.database.datasource-" + rm.getPath() + ".cfg");
                List<String> tables = persistentService.getTables(props.getProperty("jdbcUrl"), props.getProperty("username"),
                    props.getProperty("password"), props.getProperty("driverClassName"));
                int pageNum = Integer.parseInt(req.getParameter("pageNum"));
                int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                pageSize = pageSize > 0 ? pageSize : 10;
                int total = tables.size();
                int pageCount = total % pageSize == 0 ? total / pageSize
                    : total / pageSize + 1;
                if (pageNum <= 0) {
                    pageNum = 1;
                } else if (pageNum >= pageCount && pageCount != 0) {
                    pageNum = pageCount;
                } else if (pageCount == 0) {
                    pageNum = 1;
                }
                json.put("total", total);
                json.put("pageCount", pageCount);
                int maxResult = 0;
                if (pageNum == pageCount) {
                    maxResult = total;
                } else {
                    maxResult = pageNum * pageSize;
                }
                List<String> list = new ArrayList<>();
                if (total != 0) {
                    for (int i = (pageNum - 1) * pageSize; i < maxResult; i++) {
                        list.add(tables.get(i));
                    }
                }
                json.put("tables", list);
                resp.getWriter().print(json);
                // 保存和修改类型元数据
            } else if ("saveOrUpdateTM".equals(pathInfo.substring(1))) {
                TypeMetaData metaData = new TypeMetaData();
                metaData.setId(req.getParameter("id"));
                metaData.setName(req.getParameter("name"));
                metaData.setTableName(req.getParameter("tableName"));
                metaData.setType(req.getParameter("type"));
                metaData.setAuthor(req.getParameter("author"));
                metaData.setState(Integer.parseInt(req.getParameter("state")));
                metaData.setContent(req.getParameter("desc"));
                metaData.setOrg(req.getParameter("org"));
                metaData.setResourceMetaDataId(req.getParameter("resource"));
                metaData.setCustomize("[" + req.getParameter("customize") + "]");
                TypeMetaData oldMetaData = dao.getById(TypeMetaData.class, metaData.getId());
                if (oldMetaData == null) {
                    dao.save(metaData);
                } else {
                    dao.update(metaData);
                    List<TypeMetaDataField> metaDataFields = dao
                        .getByTypeMetaDataId(metaData.getId());
                    for (TypeMetaDataField typeMetaDataField : metaDataFields) {
                        dao.remove(TypeMetaDataField.class,
                            typeMetaDataField.getId());
                    }
                }
                String fields = "[" + req.getParameter("fields") + "]";
                JSONArray array = JSONArray.fromObject(fields);
                for (Object object : array) {
                    JSONObject json = JSONObject.fromObject(object);
                    TypeMetaDataField metaDataField = new TypeMetaDataField();
                    metaDataField.setId(UUID.randomUUID().toString());
                    metaDataField.setFieldId(json.getString("id"));
                    metaDataField.setName(json.getString("name"));
                    metaDataField.setMetaDataId(metaData.getId());
                    metaDataField.setContent(json.getString("desc"));
                    metaDataField.setDefaultValue(json.getString("default"));
                    metaDataField.setIndexs(Integer.parseInt(json
                        .getString("index")));
                    metaDataField.setIsNull(Integer.parseInt(json
                        .getString("req")));
                    metaDataField.setIsPrimary(Integer.parseInt(json
                        .getString("key")));
                    metaDataField.setType(json.getString("type"));
                    metaDataField.setLength(Integer.parseInt(json.getString("length")));
                    dao.save(metaDataField);
                }
                resp.getWriter().print("保存成功");
                // 获取全部类型元数据
            } else if ("getAllTM".equals(pathInfo.substring(1))) {
                JSONObject json = new JSONObject();
                int pageNum = Integer.parseInt(req.getParameter("pageNum"));
                int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                int isStop = Integer.parseInt(req.getParameter("isStop"));
                String keyword = req.getParameter("keyword");
                pageSize = pageSize > 0 ? pageSize : 10;
                int total = 0;
                if (isStop == -1 && keyword.equals("")) {
                    total = dao.count(TypeMetaData.class);
                } else {
                    total = dao.count(TypeMetaData.class, isStop, keyword);
                }
                int pageCount = total % pageSize == 0 ? total / pageSize
                    : total / pageSize + 1;
                if (pageNum <= 0) {
                    pageNum = 1;
                } else if (pageNum >= pageCount && pageCount != 0) {
                    pageNum = pageCount;
                } else if (pageCount == 0) {
                    pageNum = 1;
                }
                json.put("total", total);
                json.put("pageCount", pageCount);
                List<ResourceMetaData> list = new ArrayList<>();
                if (isStop == -1 && keyword.equals("")) {
                    list = dao.getAll(TypeMetaData.class,
                        pageNum, pageSize);
                } else {
                    list = dao.getAll(TypeMetaData.class,
                        pageNum, pageSize, isStop, keyword);
                }
                JSONArray jsonArray = JSONArray.fromObject(list);
                json.put("data", jsonArray);
                resp.getWriter().print(json);
                // 移除类型元数据
            } else if ("removeTM".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                dao.remove(TypeMetaData.class, id);
                List<TypeMetaDataField> metaDataFields = dao.getByTypeMetaDataId(id);
                for (TypeMetaDataField typeMetaDataField : metaDataFields) {
                    dao.remove(TypeMetaDataField.class, typeMetaDataField.getId());
                }
                resp.getWriter().print("删除成功");
                // 修改类型元数据状态
            } else if ("changeStateTM".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                int state = Integer.parseInt(req.getParameter("state"));
                TypeMetaData metaData = dao.getById(TypeMetaData.class, id);
                metaData.setState(state);
                dao.update(metaData);
                resp.getWriter().print("修改成功");
                // 根据Id获取类型元数据
            } else if ("getTMById".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                TypeMetaData metaData = dao.getById(TypeMetaData.class, id);
                JSONObject jsonObject = JSONObject.fromObject(metaData);
                List<TypeMetaDataField> metaDataFields = dao.getByTypeMetaDataId(id);
                JSONArray jsonArray = JSONArray.fromObject(metaDataFields);
                JSONObject json = new JSONObject();
                json.put("TABLE", jsonObject);
                json.put("FIELDS", jsonArray);
                resp.getWriter().print(json);
                // 同步类型元数据
            } else if ("synchronous".equals(pathInfo.substring(1))) {
                String id = req.getParameter("id");
                TypeMetaData metaData = dao.getById(TypeMetaData.class, id);
                String resourceId = metaData.getResourceMetaDataId();
                ResourceMetaData rm = dao.getById(ResourceMetaData.class, resourceId);
                List<TypeMetaDataField> metaDataFields = dao.getByTypeMetaDataId(id);
                Properties props = MetaDataUtil.readProp("com.gzydt.database.datasource-" + rm.getPath() + ".cfg");
                boolean flag = persistentService.synchronous(props.getProperty("jdbcUrl"), props.getProperty("username"),
                    props.getProperty("password"), props.getProperty("driverClassName"),
                    metaData, metaDataFields);
                if (flag) {
                    resp.getWriter().print("同步成功");
                } else {
                    resp.getWriter().print("同步失败");
                }
                // 提取类型元数据
            } else if ("save".equals(pathInfo.substring(1))) {
                String resourceId = req.getParameter("resource");
                String tables = req.getParameter("tables");
                String[] all_table = tables.split(",");
                for (String tableName : all_table) {
                    TypeMetaData metaData = new TypeMetaData();
                    metaData.setId(UUID.randomUUID().toString());
                    metaData.setTableName(tableName);
                    metaData.setResourceMetaDataId(resourceId);
                    metaData.setState(1);
                    TypeMetaData oldMetaData = dao.getById(TypeMetaData.class, metaData.getId());
                    if (oldMetaData == null) {
                        dao.save(metaData);
                    } else {
                        dao.update(metaData);
                        List<TypeMetaDataField> metaDataFields = dao
                            .getByTypeMetaDataId(metaData.getId());
                        for (TypeMetaDataField typeMetaDataField : metaDataFields) {
                            dao.remove(TypeMetaDataField.class,
                                typeMetaDataField.getId());
                        }
                    }
                    ResourceMetaData rm = dao.getById(ResourceMetaData.class, metaData.getResourceMetaDataId());
                    Properties props = MetaDataUtil.readProp("com.gzydt.database.datasource-" + rm.getPath() + ".cfg");
                    List<TypeMetaDataField> metaDataFields = persistentService.extract(props.getProperty("jdbcUrl"), props.getProperty("username"),
                        props.getProperty("password"), props.getProperty("driverClassName"),
                        metaData.getTableName(), metaData.getId());
                    for (TypeMetaDataField typeMetaDataField : metaDataFields) {
                        dao.save(typeMetaDataField);
                    }
                }
                resp.getWriter().print("提取成功");
                // 预览类型元数据
            } else if ("preview".equals(pathInfo.substring(1))) {
                String resourceId = req.getParameter("resource");
                String tableName = req.getParameter("tableName");
                String hideFields = req.getParameter("hideFields");
                int pageNum = Integer.parseInt(req.getParameter("pageNum"));
                int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                ResourceMetaData rm = dao.getById(ResourceMetaData.class, resourceId);
                Properties props = MetaDataUtil.readProp("com.gzydt.database.datasource-" + rm.getPath() + ".cfg");
                JSONObject json = new JSONObject();
                List<String> fields = persistentService.getTableFiled(props.getProperty("jdbcUrl"), props.getProperty("username"),
                    props.getProperty("password"), props.getProperty("driverClassName"), tableName, UUID.randomUUID().toString());
                List<List<String>> tableValue = persistentService.getTableValue(props.getProperty("jdbcUrl"), props.getProperty("username"),
                    props.getProperty("password"), props.getProperty("driverClassName"), tableName, fields);
                pageSize = pageSize > 0 ? pageSize : 10;
                int total = tableValue.size();
                int pageCount = total % pageSize == 0 ? total / pageSize
                    : total / pageSize + 1;
                if (pageNum <= 0) {
                    pageNum = 1;
                } else if (pageNum >= pageCount && pageCount != 0) {
                    pageNum = pageCount;
                } else if (pageCount == 0) {
                    pageNum = 1;
                }
                json.put("total", total);
                json.put("pageCount", pageCount);
                List<String> lastfields = new ArrayList<>();
                String[] all_hideField = hideFields.split(",");
                for (String field : fields) {
                    boolean flag = true;
                    for (String hideField : all_hideField) {
                        if (field.equals(hideField)) {
                            flag = false;
                        }
                    }
                    if (flag)
                        lastfields.add(field);
                }
                json.put("lastfields", lastfields);
                tableValue = persistentService.getTableValueByPage(props.getProperty("jdbcUrl"), props.getProperty("username"),
                    props.getProperty("password"), props.getProperty("driverClassName"), tableName, lastfields, pageSize, pageNum);
                json.put("tableValue", tableValue);
                resp.getWriter().print(json);
                // 预览元数据字段
            } else if ("previewField".equals(pathInfo.substring(1))) {
                String resourceId = req.getParameter("resource");
                String tableName = req.getParameter("tableName");
                int pageNum = Integer.parseInt(req.getParameter("pageNum"));
                int pageSize = Integer.parseInt(req.getParameter("pageSize"));
                ResourceMetaData rm = dao.getById(ResourceMetaData.class, resourceId);
                Properties props = MetaDataUtil.readProp("com.gzydt.database.datasource-" + rm.getPath() + ".cfg");
                JSONObject json = new JSONObject();
                List<String> fields = persistentService.getTableFiled(props.getProperty("jdbcUrl"), props.getProperty("username"),
                    props.getProperty("password"), props.getProperty("driverClassName"), tableName, UUID.randomUUID().toString());
                pageSize = pageSize > 0 ? pageSize : 10;
                int total = fields.size();
                int pageCount = total % pageSize == 0 ? total / pageSize
                    : total / pageSize + 1;
                if (pageNum <= 0) {
                    pageNum = 1;
                } else if (pageNum >= pageCount && pageCount != 0) {
                    pageNum = pageCount;
                } else if (pageCount == 0) {
                    pageNum = 1;
                }
                json.put("total", total);
                json.put("pageCount", pageCount);
                List<String> lastfields = new ArrayList<>();
                int lastField = pageNum * pageSize >= total ? total : pageNum * pageSize;
                for (int i = (pageNum - 1) * pageSize; i < lastField; i++) {
                    lastfields.add(fields.get(i));
                }
                json.put("lastfields", lastfields);
                resp.getWriter().print(json);
                // 获取类型元数据B
            } else if ("getMetaDataB".equals(pathInfo.substring(1))) {
                List<TypeMetaData> list = new ArrayList<>();
                list = dao.getAllNoPage(TypeMetaData.class);
                JSONArray jsonArray = JSONArray.fromObject(list);
                resp.getWriter().print(jsonArray);
                // 保存关联关系
            } else if ("saveRelation".equals(pathInfo.substring(1))) {
                Relation relation = new Relation();
                relation.setId(UUID.randomUUID().toString());
                relation.setMetadataA(req.getParameter("metadataA"));
                relation.setMetadataB(req.getParameter("metadataB"));
                relation.setFieldA(req.getParameter("fieldA"));
                relation.setFieldB(req.getParameter("fieldB"));
                relation.setType(Integer.parseInt(req.getParameter("type")));
                relation.setContent(req.getParameter("content"));
                Relation oldRelation = dao.getRelationByMetaDataId(relation.getMetadataA());
                if (oldRelation != null) {
                    dao.remove(Relation.class, oldRelation.getId());
                }
                dao.save(relation);
                resp.getWriter().print("操作成功");
                // 根据Id获取关联关系
            } else if ("getRelationById".equals(pathInfo.substring(1))) {
                String metadataId = req.getParameter("metaDataid");
                Relation relation = dao.getRelationByMetaDataId(metadataId);
                JSONObject json = JSONObject.fromObject(relation);
                resp.getWriter().print(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.log(LogService.LOG_ERROR, "获取类型元数据" + e);
            resp.getWriter().print("操作失败");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doGet(req, resp);
    }

}
