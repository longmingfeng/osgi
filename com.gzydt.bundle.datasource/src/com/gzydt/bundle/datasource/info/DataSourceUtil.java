package com.gzydt.bundle.datasource.info;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * 数据源工具类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class DataSourceUtil {

    /**
     * 读取加载文件
     * 
     * @param file
     *            文件
     * @return 加载文件信息
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
     * 写入加载文件
     * 
     * @param file
     *            文件
     * @param data
     *            数据
     */
    public static void writeProp(String file, Map<String, String> data) {
        File directory = new File(System.getProperty("user.dir") + File.separator + "load");
        if (!directory.exists())
            directory.mkdir();

        File dir = new File(directory.getPath() + File.separator + file);

        FileOutputStream output = null;
        Properties props = null;
        try {
            output = new FileOutputStream(dir);
            props = new Properties();
            for (String key : data.keySet()) {
                props.put(key, data.get(key));
            }
            props.store(output, "");
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        } finally {
            closeSilently(output);
        }
    }

    /**
     * 关闭方法
     * 
     * @param closeable
     *            需要关闭的类
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
}
