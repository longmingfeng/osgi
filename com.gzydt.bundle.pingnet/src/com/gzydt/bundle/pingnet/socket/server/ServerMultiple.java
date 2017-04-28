/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:11:25
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器的类
 * 
 * @author longmingfeng 2016年10月28日 上午10:11:25
 */
public class ServerMultiple {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            // 统计在线人数的线程
            ThreadTongJi tongji = new ThreadTongJi();
            tongji.start();
            List<Socket> sockets = new ArrayList<Socket>();
            while (true) {
                Socket socket = serverSocket.accept();
                String person = socket.getRemoteSocketAddress().toString();
                // 启动服务器的线程类
                ThreadServer server = new ThreadServer(socket, person, sockets);
                server.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
