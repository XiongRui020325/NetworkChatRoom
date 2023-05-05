//服务器接收客户端的消息，一对一
package Experiment5;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Set;

public class ServerReceive implements Runnable, Tool {

    private Socket socket;
    private ObjectInputStream ois;

    public ServerReceive(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            Set<String> userSet = Server.userTable.keySet();//获取所有的用户
            Collection<Socket> socketCollection = Server.userTable.values();//获取所有的套接字
            if(!Server.userTable.isEmpty() && socketCollection.contains(this.socket)){//判断自己是否还在聊天室中
                try {
                    ois = new ObjectInputStream(socket.getInputStream());//获取输入流
                    Message message = (Message) ois.readObject();//读取一个message
                    String userName = message.getSenderName();//获取发送者名称
                    String messageType = message.getMessageType();//获取消息类型
                    synchronized (Server.obj){
                        if (messageType.equals(SIMPLE_MESSAGE)) {//如果是普通消息
                            System.out.println("收到来自" + userName + "的消息: " + message.getMessageContent());
                            Server.messages.add(message);//加入到处理队列
                            System.out.println("已加入到待处理队列");
                        }else if(messageType.equals(REQUEST_EXIT)){//如果申请退出
                            System.out.println("收到来自" + userName + "的退出请求");
                            Server.userTable.remove(userName);//删除掉这名用户的套接字
                            System.out.println("已经将" + userName + "从在线用户中移出");
                            Message serverMsg = new Message("服务器", "All", userName + "已退出聊天室", DateUtils.getCurrentTime(), ANNOUNCEMENT);
                            Server.messages.add(serverMsg);//加入到处理队列
                            System.out.println("已处理" + userName + "的退出请求");
                        }else if(messageType.equals(REQUEST_SEND_PRIVATELY)){//如果申请私发消息
                            System.out.println("收到来自" + userName + "的私发请求");
                            StringBuilder sb = new StringBuilder();
                            int i = 1;
                            int size = userSet.size();//获取在线用户个数
                            for(String user : userSet){
                                sb.append(user);
                                if(i != size){//如果不是最后一个
                                    sb.append(",");
                                }
                                i++;
                            }
                            Message serverMsg = new Message("服务器", userName, sb.toString(), DateUtils.getCurrentTime(), RESPONSE_OF_PRIVATE);
                            Server.messages.add(serverMsg);//加入到处理队列
                            System.out.println("已处理" + userName + "的私发请求");
                        }else if(messageType.equals(PRIVATE_MESSAGE)){//如果是私发消息
                            System.out.println("收到来自" + userName + "的私发消息: " + message.getMessageContent());
                            Server.messages.add(message);//加入到处理队列
                            Message personalMsg = new Message(userName, message.getReceiverName(),message.getMessageContent(), message.getSendTime(), PERSONAL_MESSAGE);
                            Server.messages.add(personalMsg);//显示在自己的界面
                            System.out.println("已加入到待处理队列");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{//如果不在，则线程结束
                break;
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
