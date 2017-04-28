/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:08:30
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.gzydt.bundle.pingnet.socket.util.StreamUtil;

/**
 * 客户端发送消息的线程类
 * @author longmingfeng   2016年10月28日  上午10:08:30
 */
public class ThreadPrintMessage extends Thread{
  //键盘输入的key
    private BufferedReader key;
    //发送消息pw
    private PrintWriter pw;
    
    public ThreadPrintMessage(Socket socket) throws IOException{
        key = StreamUtil.getkey();
        pw = StreamUtil.getPrint(socket);
    }
    public void run(){
        System.out.println("请输入本机targetID：");
        try {
            //int i = 0;
            while(true){//只让其输入两次，第一次为tartgetID,第二次为关闭close
                //循环的输入消息
                String message = key.readLine();
                if(message.equals("") || message.length() == 0){//输入为空，将i置为0,重新输入循环
                    //输入消息是空的
                    //i = 0;
                    System.out.println("请不要输入空的targetID，1S后请重新输入！");
                    Thread.sleep(1000);
                    System.out.println("可以输入了！");
                    continue;
                }else if(message == null){
                    //代表客户端已经断开:关闭窗口
                    break;
                }else if(message.trim().equals("close")){
                    pw.println(message);
                    pw.flush();
                    System.out.println("客户端已经断开了！");
                    System.exit(1);
                }else{
                    //正常消息，发送过去服务器
                    pw.println(message);
                    pw.flush();
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }finally{
            //客户端断开
            //System.out.println("ThreadPrintMessage客户端即将断开！");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("ThreadPrintMessage客户端已经断开了！");
            System.exit(1);
        }
    }
}
