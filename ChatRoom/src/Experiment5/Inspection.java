//该类用于客户端巡检
package Experiment5;

import java.net.ServerSocket;
import java.util.Scanner;
import java.util.Set;

public class Inspection implements Runnable, Tool{
    private boolean serverRunningFlag = true;//服务器运行标志
    private ServerSocket ss;
    private String choice = "";
    private Scanner sc = new Scanner(System.in);

    public Inspection(ServerSocket ss) {
        this.ss = ss;
    }

    @Override
    public void run() {
        while (true){
            Set<String> userName = Server.userTable.keySet();//获取在线用户
            choice = sc.nextLine();
            switch (choice) {
                case "show": //如果是显示在线用户列表
                    if (!Server.userTable.isEmpty()) {
                        int i = 1;
                        for (String name : userName) {
                            System.out.println(i + ". " + name);
                            i++;
                        }
                    } else {
                        System.out.println("当前无在线用户");
                    }
                    break;
                case "remove": //踢出用户
                    System.out.println("请输入要踢出的用户昵称：");
                    String nickname = sc.nextLine();
                    if (userName.contains(nickname)) {//判断输入是否正确
                        System.out.println("真的要踢出" + nickname + "吗？请输入\"yes\"或\"no\"");
                        String temp = sc.nextLine();
                        if(temp.equals("yes")){
                            Message severMsg = new Message("服务器", nickname, nickname + "已被踢出群聊", DateUtils.getCurrentTime(), FORCE_QUIT);
                            Server.messages.add(severMsg);
                            //应该在发送完这条信息后在踢出，挪到发送线程里
                            //Server.userTable.remove(nickname);
                            System.out.println("已踢出！");
                        }else if(temp.equals("no")){
                            System.out.println("已取消操作！");
                        }else{
                            System.out.println("非法输入，已取消操作！");
                        }
                    }else{
                        System.out.println("该用户不存在！");
                    }
                    break;
                case "shut down": //关闭服务器
                    System.out.println("真的要关闭服务器吗？请输入\"yes\"或\"no\"");
                    String temp = sc.nextLine();
                    if(temp.equals("yes")){
                        Message severMsg = new Message("服务器", "All", "各位，服务器即将关闭，江湖路远，我们日后再见！", DateUtils.getCurrentTime(), SERVER_CLOSED);
                        Server.messages.add(severMsg);
                        //同样的，这行代码也要放在发送信息的线程里
                        //Server.userTable.clear();//清空所有的用户和套接字
                        serverRunningFlag = false;
                        System.out.println("服务器已成功关闭！");
                    }else if(temp.equals("no")){
                        System.out.println("已取消操作！");
                    }else{
                        System.out.println("非法输入，已取消操作！");
                    }
                    break;
                default:
                    System.out.println("非法输入，已取消操作！");
            }
            if(!serverRunningFlag) break;//如果服务器关闭，线程结束
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
