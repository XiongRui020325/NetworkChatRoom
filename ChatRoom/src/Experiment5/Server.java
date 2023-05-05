//服务器端，端口号2428
package Experiment5;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Tool{
    public static Object obj = new Object();//用于线程同步synchronized参数
    //用于记录在线用户的昵称和socket
    public static ConcurrentHashMap<String, Socket> userTable = new ConcurrentHashMap<>();
    //记录要发送的消息
    public static ArrayList<Message> messages = new ArrayList<>();
    //检查昵称是否重复
    public static boolean CheckLegal(Socket s) throws Exception {
        //服务器端获取客户端发来的昵称
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        Message message = (Message) ois.readObject();
        if(message.getMessageType().equals(REQUEST_LOG_ON)){
            String userName = message.getSenderName();//获取名称
            //服务器给出反馈，确定客户是否登陆成功
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            //获取用户列表
            Set<String> userSet = userTable.keySet();
            //判断昵称是否重复
            if(userSet.contains(userName)){
                Message callback = new Message(USERNAME_REPEATED);
                oos.writeObject(callback);
                return false;
            }else{
                Message callback = new Message(LOG_ON_SUCCESSFULLY);
                oos.writeObject(callback);
                System.out.println(userName + "进入聊天室");
                userTable.put(userName, s);
                //有新成员进入聊天室，发布群公告
                Message serverMsg = new Message("服务器", "All", userName + "已进入聊天室，快来和他打招呼吧！", DateUtils.getCurrentTime(), ANNOUNCEMENT);
                Server.messages.add(serverMsg);//加入到处理队列
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        //创建ServerSocket对象
        ServerSocket ss = new ServerSocket(2428);
        System.out.println("服务器正在运行……");
        System.out.println("输入 show 显示当前在线用户列表，输入 remove 指定踢出用户，输入 shut down 关闭服务器");
        //启动服务器巡检的线程
        new Thread(new Inspection(ss)).start();
        //开启发送新消息的线程
        new Thread(new ServerSend()).start();
        while (true){
            try {
                Socket s = ss.accept();//监听客户端的连接，如果没有连接会阻塞在此处
                if(CheckLegal(s)){//检查昵称合法性
                    new Thread(new ServerReceive(s)).start();//如果昵称合法，启动一个接收这个客户端的信息的线程
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
