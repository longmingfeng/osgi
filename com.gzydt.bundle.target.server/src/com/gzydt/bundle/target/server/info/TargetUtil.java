package com.gzydt.bundle.target.server.info;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import com.gzydt.bundle.target.server.info.bean.Target;
import com.gzydt.bundle.target.server.info.bean.Targets;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 操作xml文件的相关工具类
 * 
 * @author longmingfeng 2016年12月19日 上午10:41:42
 */
public class TargetUtil {
    /**
     * 将节点容器列表写进xml
     * 
     * @param 节点容器列表
     */
    public static void writeXML(Targets targets) {
        File dir = new File(System.getProperty("user.dir") + File.separator
            + "conf" + File.separator + "nodelist.xml");
        FileOutputStream writer = null;
        PrintWriter pw = null;
        try {
            writer = new FileOutputStream(dir);
            pw = new PrintWriter(new OutputStreamWriter(writer, "UTF-8"));
            pw.println(toXML(targets));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeSilently(pw);
            closeSilently(writer);
        }
    }

    /**
     * 将节点容器列表转换成xml
     * 
     * @param 节点容器列表
     * @return
     */
    private static String toXML(Targets targets) {
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.processAnnotations(new Class[] { Targets.class, Target.class });
        String xml = xstream.toXML(targets);
        return xml;
    }

    /**
     * 读取xml文件，获取节点容器列表
     * 
     * @return 节点容器列表
     */
    public static Targets readXML() {
        Targets targets = null;
        String content = readFile();
        if (content != null && !content.trim().equals("") && !"<targets/>".equals(content)) {
            XStream xstream = new XStream(new DomDriver());
            xstream.setMode(XStream.NO_REFERENCES);
            xstream.processAnnotations(new Class[] { Targets.class,
                Target.class });
            targets = (Targets) xstream.fromXML(content);
        }
        return targets;
    }

    /**
     * 用流读取xml文件
     * 
     * @return xml的流字符串
     */
    private static String readFile() {
        StringBuffer buffer = new StringBuffer();
        File dir = new File(System.getProperty("user.dir") + File.separator
            + "conf" + File.separator + "nodelist.xml");
        if (!dir.exists())
            return "";

        BufferedReader read = null;
        try {
            read = new BufferedReader(new InputStreamReader(
                new FileInputStream(dir), "UTF-8"));
            String line = null;
            while ((line = read.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(read);
        }

        return buffer.toString();
    }

    /**
     * 关闭资源
     * 
     * @param 资源
     * @return null
     */
    public static Closeable closeSilently(Closeable closeable) {
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
     * 下载指定位置的文件
     * 
     * @param 文件路径
     * @param 浏览器响应输出
     */
    public static void downloadFileByDir(String realPath, HttpServletResponse resp) {
        InputStream in = null;
        OutputStream out = null;
        try {
            // 获取要下载的文件名
            String fileName = URLEncoder.encode(realPath.substring(realPath.lastIndexOf("\\") + 1), "UTF-8");
            // 设置文件ContentType类型，这样设置，会自动判断下载
            resp.setContentType("application/x-download");
            //resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);//这一句代码即可解决火狐浏览器中下载文件的中文乱码问题。真奇怪，UTF-8后直接加个空的单引号.  
            in = new FileInputStream(realPath);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = resp.getOutputStream();
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(in);
            closeSilently(out);
        }
    }

    /**
     * 读取conf文件夹下的properties文件
     * 
     * @param 文件名
     * @param 数据信息
     */
    public static Properties readProp(String file) {
        File dir = new File(System.getProperty("user.dir") + File.separator
            + "conf" + File.separator + file);
        FileInputStream input = null;
        Properties props = null;
        try {
            input = new FileInputStream(dir);
            props = new Properties();
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

}
