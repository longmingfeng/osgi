/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:12:12
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.gzydt.bundle.pingnet.socket.util.StreamUtil;

/**
 * 服务器的线程类
 * 
 * @author longmingfeng 2016年10月28日 上午10:12:12
 */
public class ThreadServer extends Thread {
    // 定义一个在线人数的变量
    public static int personCount;
    // 定义一个集合用来装连接进来的Socket对象
    private List<Socket> sockets;
    // 定义一个客户端的Socket对象
    private Socket socket;
    // 定义一个用来群发消息的PrintWriter
    private PrintWriter pw;
    // 接收消息
    private BufferedReader readData;
    // 定义一个线程安全的对象
    private Object o = new Object();

    Properties properties = new Properties();

    FileInputStream fis = null;
    
    FileOutputStream oFile;
    
    public ThreadServer(Socket socket, String comePerson, List<Socket> sockets) {

        String ip = null;

        try {
            File f = new File(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "ip.properties");
            // InputStream inStream = getClass().getResourceAsStream("ip.property");
            fis = new FileInputStream(f);

            properties.load(fis);
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        synchronized (o) {
            try {
                this.socket = socket;
                pw = StreamUtil.getPrint(socket);
                readData = StreamUtil.getBuffer(socket);
                this.sockets = sockets;
                // 将客户端连接进来的信息打印出来
                comePerson = socket.getRemoteSocketAddress().toString();
                ip = comePerson.split("/")[1].split(":")[0];
                System.out.println("target所在机器：" + ip + "已经连接进来！");
                /*for (int i = 0; i < sockets.size(); i++) {
                    // 群发消息
                    pw = StreamUtil.getPrint(sockets.get(i));
                    pw.println("target所在机器：" + ip + "已经连接进来！");
                    pw.flush();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 将当前进来的用户添加到集合中
                sockets.add(socket);
                // 将当前上线的人数 + 1；
                personCount++;

                StreamUtil.ipList.add(ip);

            }
        }
    }

    public void run() {
        // 接收消息
        try {
            while (true) {
                String message = readData.readLine();
                if (message == null) {
                    synchronized (o) {
                        // 客户端断开，那就从集合中将断开的Socket清除掉
                        sockets.remove(socket);
                        break;
                    }
                } else if (message.trim().equals("close")) {
                    synchronized (o) {
                        // 客户端断开，那就从集合中将断开的Socket清除掉
                        sockets.remove(socket);
                        break;
                    }

                } else {
                    synchronized (o) {
                        String ip = socket.getRemoteSocketAddress()
                            .toString().split("/")[1].split(":")[0];
                        pw.println("target所在IP:" + ip  + " 说：" + message);
                        
                        //将IP及targetID写进去
                        properties.put(ip, message);

                        // 保存属性到b.properties文件
                        try {
                            oFile = new FileOutputStream(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "ip.properties", false);// true表示追加打开
                            properties.store(oFile, " target online ipList ,author : yxlmf@126.com");
                            oFile.close();
                            fis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        // 服务器接收到客户端的正常消息,群发
                        /*for (int i = 0; i < sockets.size(); i++) {
                            pw = StreamUtil.getPrint(sockets.get(i));
                            pw.println("target所在IP:" + socket.getRemoteSocketAddress()
                                .toString().split("/")[1].split(":")[0] + "说：" + message);
                            pw.flush();
                        }*/
                    }

                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            synchronized (o) {
                String offPerson = null;
                try {
                    // 客户端断开，要将客户端断开的消息在服务器打印
                    offPerson = socket.getRemoteSocketAddress()
                        .toString().split("/")[1].split(":")[0];
                    System.out.println(offPerson + "已经下线了！");
                    // 将该用户下线的信息进行群发
                    /*for (int i = 0; i < sockets.size(); i++) {
                        pw = StreamUtil.getPrint(sockets.get(i));
                        pw.println("target所在的机器" + offPerson + "已经下线了！");
                        pw.flush();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    personCount--;

                    StreamUtil.ipList.remove(offPerson);

                    properties.remove(offPerson);

                    try {
                        oFile = new FileOutputStream(System.getProperty("user.dir") + File.separator + "conf" + File.separator + "ip.properties", false);// true表示追加打开
                        properties.store(oFile, " target online ipList ,author : yxlmf@126.com");
                        oFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
