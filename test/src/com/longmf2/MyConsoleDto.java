/**
 *   @author longmingfeng    2016年12月8日  下午2:03:11
 *   Email: yxlmf@126.com 
 */
package com.longmf2;

/**
 * 数据传输对象
 * 
 * @author longmingfeng 2016年12月8日 下午2:03:30
 */
public class MyConsoleDto {

    /**
     * 使用单例模式设计
     */
    private static MyConsoleDto myConsoleDto;

    /**
     * 保存当前consoleTextArea的所有文本
     */
    private String strConsoleText;

    /**
     * 记录当前的根目录
     */
    private String nowRootDir;

    /**
     * 记录当前命令行打开的目录
     */
    private String nowPath;

    /**
     * 记录前一个目录
     */
    private String prePath;

    /**
     * 通过静态方法返回唯一实例
     * 
     * @return
     */
    public static MyConsoleDto getMyConsoleDto() {
        if (myConsoleDto == null) {
            myConsoleDto = new MyConsoleDto();
        }
        return myConsoleDto;
    }

    /**
     * 其余所有值的getter和setter方法
     */
    public String getStrConsoleText() {
        return strConsoleText;
    }

    public void setStrConsoleText(String strConsoleText) {
        this.strConsoleText = strConsoleText;
    }

    public String getNowPath() {
        return nowPath;
    }

    public void setNowPath(String nowPath) {
        this.nowPath = nowPath;
    }

    public String getPrePath() {
        return prePath;
    }

    public void setPrePath(String prePath) {
        this.prePath = prePath;
    }

    public String getNowRootDir() {
        this.setNowRootDir(this.nowPath.substring(0, 2));
        return nowRootDir;
    }

    public void setNowRootDir(String nowRootDir) {
        this.nowRootDir = nowRootDir;
    }
}
