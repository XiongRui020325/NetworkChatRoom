//这是一个由服务器向所有的套接字发送消息的线程，一对多
package Experiment5;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

public class ServerSend implements Runnable, Tool{
    private boolean serverRunningFlag = true;//服务器正在运行的标志
    private ObjectOutputStream oos;

    @Override
    public void run() {
        while(true){
            if(!Server.messages.isEmpty()){//如果需要处理的消息不空
                System.out.println("有需要处理的消息");
                synchronized (Server.obj){
                    Collection<Socket> collection = Server.userTable.values();//获取所有客户端的套接字
                    for(Message message : Server.messages){//遍历所有的消息
                        String messageType = message.getMessageType();//获取消息类型
                        if(message.getReceiverName().equals("All")){//如果是群发消息
                            for(Socket socket : collection){//遍历所有的套接字
                                try {
                                    oos = new ObjectOutputStream(socket.getOutputStream());//获取这个套接字的输出流
                                    oos.writeObject(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            continue;
                        }
                        if(messageType.equals(RESPONSE_OF_PRIVATE) || messageType.equals(PRIVATE_MESSAGE)){//如果是处理私聊的请求或处理私发的消息
                            Socket socket = Server.userTable.get(message.getReceiverName());//获取接收者
                            try {
                                oos = new ObjectOutputStream(socket.getOutputStream());//获取这个套接字的输出流
                                oos.writeObject(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        if(messageType.equals(PERSONAL_MESSAGE)){//是一条自己私发出去的要显示在自己界面的消息
                            Socket socket = Server.userTable.get(message.getSenderName());//因为是要显示在自己的界面上，所以获取发送者
                            try {
                                oos = new ObjectOutputStream(socket.getOutputStream());//获取这个套接字的输出流
                                oos.writeObject(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        if(messageType.equals(FORCE_QUIT)){//如果是服务器强制踢出
                            for(Socket socket : collection){//遍历所有的套接字
                                try {
                                    oos = new ObjectOutputStream(socket.getOutputStream());//获取这个套接字的输出流
                                    oos.writeObject(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Server.userTable.remove(message.getReceiverName());//发送完信息踢出
                            continue;
                        }
                        if(messageType.equals(SERVER_CLOSED)){//如果是服务器即将关闭
                            for(Socket socket : collection){//遍历所有的套接字
                                try {
                                    oos = new ObjectOutputStream(socket.getOutputStream());//获取这个套接字的输出流
                                    oos.writeObject(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            serverRunningFlag = false;
                            continue;
                        }
                    }
                    Server.messages.clear();//清空待处理消息
                }
                System.out.println("消息已处理");
            }
            if(!serverRunningFlag) {//如果服务器已经关闭
                Server.userTable.clear();//清空所有的用户和套接字
                break;//服务器关闭，线程结束
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
