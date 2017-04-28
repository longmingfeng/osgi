package com.gzydt.bundle.metadatamanage.info.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gzydt.bundle.metadatamanage.info.api.BaseMetaData;
import com.gzydt.bundle.metadatamanage.info.api.BaseMetaDataField;

/**
 * 元数据管理工具类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class MetaDataUtil {

    // MetaDataServlet及SetDataToTableByURLServlet用到
    public static String dataSourceName = "usimDS2";

    /**
     * 读取XML
     * 
     * @param fileName
     *            文件名
     * @return xml内容
     */
    public static List<Map<String, Object>> readXml(String fileName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document;
        List<Map<String, Object>> list = new ArrayList<>();
        File file = new File(System.getProperty("user.dir") + File.separator
            + "upload" + File.separator + fileName);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(file);
            NodeList ocdList = document.getElementsByTagName("OCD");
            for (int i = 0; i < ocdList.getLength(); i++) {
                Map<String, String> ocdMap = new HashMap<>();
                List<Map<String, String>> adList = new ArrayList<>();
                Node ocd = ocdList.item(i);
                NamedNodeMap attrs = ocd.getAttributes();
                for (int j = 0; j < attrs.getLength(); j++) {
                    Node attr = attrs.item(j);
                    ocdMap.put(attr.getNodeName(), attr.getNodeValue());
                }
                NodeList childNodes = ocd.getChildNodes();
                for (int k = 0; k < childNodes.getLength(); k++) {
                    Map<String, String> adMapAttrs = new HashMap<>();
                    if (childNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap attrs_char = childNodes.item(k)
                            .getAttributes();
                        for (int j = 0; j < attrs_char.getLength(); j++) {
                            Node attr = attrs_char.item(j);
                            adMapAttrs.put(attr.getNodeName(), attr.getNodeValue());
                        }
                        adList.add(adMapAttrs);
                    }
                }
                BaseMetaData metaData = new BaseMetaData();
                metaData.setId(ocdMap.get("id"));
                metaData.setName(ocdMap.get("name"));
                metaData.setContent(ocdMap.get("description"));
                List<BaseMetaDataField> metaDataFields = new ArrayList<>();
                for (Map<String, String> map : adList) {
                    BaseMetaDataField metaDataField = new BaseMetaDataField();
                    metaDataField.setId(UUID.randomUUID().toString());
                    metaDataField.setName(map.get("name"));
                    metaDataField.setContent(map.get("description"));
                    metaDataField.setFieldId(map.get("id"));
                    metaDataField.setIsNull(map.get("required").equals("true") ? 1 : 0);
                    metaDataField.setDefaultValue(map.get("default"));
                    metaDataField.setType(map.get("type"));
                    metaDataField.setRowNum(Integer.parseInt(map.get("row")));
                    metaDataField.setIndexs(Integer.parseInt(map.get("index")));
                    metaDataField.setMetaDataId(metaData.getId());
                    metaDataFields.add(metaDataField);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("OCD", metaData);
                map.put("AD", metaDataFields);
                list.add(map);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 写入XML
     * 
     * @param metaData
     *            元数据
     * @param metaDataFields
     *            元数据字段
     * @param fileName
     *            文件名
     */
    public static void writeXml(BaseMetaData metaData, List<BaseMetaDataField> metaDataFields, String fileName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document;
        PrintWriter pw = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.newDocument();
            Element root = document.createElement("metatype:MetaData");
            root.setAttribute("xmlns:metatype",
                "http://www.osgi.org/xmlns/metatype/v1.0.0");
            root.setAttribute("localization", "OSGI-INF/metatype/metatype");
            document.appendChild(root);
            Element ocd = document.createElement("OCD");
            ocd.setAttribute("description", metaData.getContent());
            ocd.setAttribute("name", metaData.getName());
            ocd.setAttribute("id", metaData.getId());
            for (BaseMetaDataField baseMetaDataField : metaDataFields) {
                Element ad = document.createElement("AD");
                ad.setAttribute("name", baseMetaDataField.getName());
                ad.setAttribute("description", baseMetaDataField.getContent());
                ad.setAttribute("id", baseMetaDataField.getFieldId());
                ad.setAttribute("required", baseMetaDataField.getIsNull() == 1 ? "true" : "false");
                ad.setAttribute("default", baseMetaDataField.getDefaultValue());
                ad.setAttribute("type", baseMetaDataField.getType());
                ad.setAttribute("row", baseMetaDataField.getRowNum() + "");
                ad.setAttribute("index", baseMetaDataField.getIndexs() + "");
                ad.setAttribute("showTip", baseMetaDataField.getTip() != null && !baseMetaDataField.getTip().equals("") ? "true" : "false");
                ocd.appendChild(ad);
            }
            root.appendChild(ocd);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            pw = new PrintWriter(new FileOutputStream(fileName));
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(pw);
        }
    }

    /**
     * 下载文件
     * 
     * @param path
     *            路径
     * @param resp
     *            响应
     */
    public static void downloadFileByDir(String path, HttpServletResponse resp) {
        InputStream in = null;
        OutputStream out = null;
        try {
            String fileName = path.substring(path.lastIndexOf("\\") + 1);
            fileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
            resp.setContentType("application/x-msdownload");
            resp.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            in = new FileInputStream(path);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = resp.getOutputStream();
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(out);
            closeSilently(in);
        }
    }

    /**
     * 关闭资源
     * 
     * @param closeable
     *            资源
     * @return null
     */
    private static Closeable closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException exception) {
            // Ignore...
        }
        return null;
    }

    /**
     * 读取配置文件
     * 
     * @param file
     *            文件
     * @return 文件信息
     */
    public static Properties readProp(String file) {
        Properties props = new Properties();
        File directory = new File(System.getProperty("user.dir") + File.separator + "load");
        if (!directory.exists())
            directory.mkdir();
        File dir = new File(directory.getPath() + File.separator + file);
        if (!dir.exists())
            return props;
        FileInputStream input = null;
        try {
            input = new FileInputStream(dir);
            props.load(input);
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        } finally {
            closeSilently(input);
        }
        return props;
    }

    /**
     * 根据数据源名字，获取Connection对象
     * 
     * @param dataSourceName
     * @return
     * @author longmingfeng 2017年4月14日 下午4:50:36
     */
    @SuppressWarnings("unchecked")
    public static Connection getConnectionByDatasourcename(String dataSourceName, BundleContext context, LogService log) {
        Connection con = null;
        try {
            ServiceReference<DataSource>[] refs = (ServiceReference<DataSource>[]) context.getServiceReferences(DataSource.class.getName(), "(name=" + dataSourceName + ")");

            if (refs != null && refs.length > 0) {
                for (ServiceReference<DataSource> serviceReference : refs) {
                    DataSource d = context.getService(serviceReference);
                    con = d.getConnection();
                }
            }
        } catch (InvalidSyntaxException | SQLException e) {
            log.log(LogService.LOG_ERROR, "获取数据源" + dataSourceName + "对应的DataSource服务失败，获取取连接失败...");
            e.printStackTrace();
        }

        return con;

    }

    /**
     * 格式化
     * 
     * @param jsonStr
     * @return
     * @author longmingfeng 2017年4月13日 上午9:03:28
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";

        StringBuilder sb = new StringBuilder();

        char last = '\0';
        char current = '\0';
        int indent = 0;

        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);

            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加制表符
     * 
     * @param sb
     * @param indent
     * @author longmingfeng 2017年4月13日 上午9:02:54
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }
}
