/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:10:38
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import com.gzydt.bundle.pingnet.socket.util.StreamUtil;

/**
 * 客户端接收消息的线程类
 * @author longmingfeng   2016年10月28日  上午10:10:38
 */
public class ThreadReceiveMessage extends Thread{
    //接收消息的readData
    private BufferedReader readData;
    
    public ThreadReceiveMessage(Socket socket) throws IOException{
        readData = StreamUtil.getBuffer(socket);
    }
    public void run(){
        try {
            while(true){
                String message = readData.readLine();
                if(message == null){
                    //客户端已经断开
                    break;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
        }finally{
            //客户端断开
            System.out.println("ACE服务器已断开，客户端将在2S后断开！");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("客户端已经断开了！");
            System.exit(1);
        }
    }
}
