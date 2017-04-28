/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:13:47
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端和服务器都需要使用到得工具类
 * @author longmingfeng   2016年10月28日  上午10:13:47
 */
public class StreamUtil {
    
    //定义一个当前在线的IP（所有线程公用）
    public static List<String> ipList = new ArrayList<String>();
    
    /**
     * 键盘输入的方法
     * @return
     */
    public static BufferedReader getkey(){
        return new BufferedReader(new InputStreamReader(System.in));
    }
    
    /**
     * 发送消息的方法
     * @param socket
     * @return
     * @throws IOException
     */
    public static PrintWriter getPrint(Socket socket) throws IOException{
        return new PrintWriter(socket.getOutputStream());
    }
    
    /**
     * 接收消息的方法
     * @param socket
     * @return
     * @throws IOException
     */
    public static BufferedReader getBuffer(Socket socket) throws IOException{
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}
