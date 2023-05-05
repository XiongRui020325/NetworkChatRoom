//边界布局，分为上中下三个面板
package Experiment5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;

import static java.lang.System.exit;

public class chatRoom extends JFrame implements ActionListener, WindowListener, Tool {
    private boolean connectState = false;//连接状态
    private boolean requireSend = false;//申请群发
    private boolean requireSendPrivately = false;//申请私发
    private boolean sendPrivatelyRunnable = false;//可以私发了
    private String nickname;//本类用户名
    private String designatedReceiver;//指定的接收者
    private Socket s;//本类套接字
    private String onlineUser = "";//在线用户
    JPanel up, middle, low, panelForButton;//上中下三个面板和最下面面板里用来放按钮的面板
    JScrollPane scrollPane1, scrollPane2;
    JLabel ipLabel, portLabel, nicknameLabel;
    JTextField ipTextField, portTextField, nicknameTextField;
    JButton enter, quit, send, privateSend;
    JTextArea messageBox, inputBox;//消息框和输入框


    public chatRoom() throws HeadlessException {
        setTitle("全民大讨论聊天室客户端----北林熊睿开发");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //以下是up面板及其组件
        up = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        ipLabel = new JLabel("IP:");
        up.add(ipLabel);
        ipTextField = new JTextField(8);
        up.add(ipTextField);
        portLabel = new JLabel("端口:");
        up.add(portLabel);
        portTextField = new JTextField(8);
        up.add(portTextField);
        nicknameLabel = new JLabel("昵称:");
        up.add(nicknameLabel);
        nicknameTextField = new JTextField(8);
        up.add(nicknameTextField);
        enter = new JButton("进入聊天室");
        enter.addActionListener(this);
        up.add(enter);
        quit = new JButton("退出聊天室");
        quit.setEnabled(false);//初始禁用退出按钮
        quit.addActionListener(this);
        up.add(quit);
        //以上是up面板及其组件
        //以下是middle面板
        middle = new JPanel(new BorderLayout());
        middle.setBackground(Color.WHITE);//设置背景色为白色
        middle.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));//给面板添加边框
        messageBox = new JTextArea();
        messageBox.setFont(new Font("宋体", Font.PLAIN, 15));//设置字体
        messageBox.setLineWrap(true);//设置自动换行
        messageBox.setEditable(false);//设置不可编辑
        middle.add(messageBox, BorderLayout.CENTER);
        scrollPane1 = new JScrollPane(middle);
        //以上是middle面板
        //以下是low面板及其组件
        //注释部分是没有私发功能时的布局
        /*low = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        low.setBackground(Color.WHITE);
        inputBox = new JTextArea(4, 50);
        inputBox.setLineWrap(true);//设置自动换行
        scrollPane2 = new JScrollPane(inputBox);//加滚动条
        low.add(scrollPane2);
        send = new JButton("发送");
        send.setPreferredSize(new Dimension(60, 30));//设置按钮大小
        send.setForeground(Color.WHITE);//按钮字体颜色
        send.setBackground(Color.BLUE);//按钮背景色
        send.addActionListener(this);
        low.add(send);*/
        low = new JPanel(new BorderLayout());
        low.setBackground(Color.WHITE);
        inputBox = new JTextArea(4, 50);
        inputBox.setFont(new Font("宋体", Font.PLAIN, 12));//设置字体
        inputBox.setLineWrap(true);//设置自动换行
        scrollPane2 = new JScrollPane(inputBox);//加滚动条
        low.add(scrollPane2, BorderLayout.CENTER);
        panelForButton = new JPanel(new GridLayout(2, 1, 10, 5));//采用网格布局放按钮，2行1列
        send = new JButton("发送");
        send.setForeground(Color.WHITE);//按钮字体颜色
        send.setBackground(Color.BLUE);//按钮背景色
        send.addActionListener(this);
        panelForButton.add(send);//放在第一行
        privateSend = new JButton("私发");
        privateSend.setForeground(Color.WHITE);//按钮字体颜色
        privateSend.setBackground(Color.BLUE);//按钮背景色
        privateSend.addActionListener(this);
        panelForButton.add(privateSend);//放在第二行
        low.add(panelForButton, BorderLayout.EAST);

        //以上是low面板及其组件
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(up, BorderLayout.NORTH);
        c.add(middle, BorderLayout.CENTER);
        c.add(low, BorderLayout.SOUTH);
        c.validate();//刷新容器
        setVisible(true);
    }

    public String logOn(String ip, int port, String nickname) throws Exception {//用于登录
        try {
            s = new Socket(ip, port);//创建连接
        } catch (Exception e) {
            return LOG_ON_FAILED;//连接失败
        }
        //把昵称发送到服务器
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        Message message = new Message(nickname, REQUEST_LOG_ON);//发送请求
        oos.writeObject(message);
        //获取服务器反馈
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        Message callback = (Message) ois.readObject();
        String state = callback.getMessageType();
        System.out.println("客户端连接状态: " + state);
        return state;
    }

    public void enterChatRoom() throws Exception {//进入聊天室
        String ip = ipTextField.getText();
        String port = portTextField.getText();
        String nickname = nicknameTextField.getText();
        //判断输入是否出错
        if (ip.equals("")) {
            JOptionPane.showMessageDialog(null, "请输入IP地址！", "错误", JOptionPane.WARNING_MESSAGE);
        } else if (port.equals("")) {
            JOptionPane.showMessageDialog(null, "请输入端口号！", "错误", JOptionPane.WARNING_MESSAGE);
        } else if (nickname.equals("")) {
            JOptionPane.showMessageDialog(null, "请输入昵称！", "错误", JOptionPane.WARNING_MESSAGE);
        } else {//登录
            String callback = logOn(ip, Integer.parseInt(port), nickname);//登录
            if (callback.equals(LOG_ON_SUCCESSFULLY)) {//如果连接成功
                this.connectState = true;//设置连接状态
                this.nickname = nickname;//设置本类的用户名属性
                ipTextField.setEditable(false);//不允许修改IP地址
                portTextField.setEditable(false);//不允许修改端口号
                nicknameTextField.setEditable(false);//不允许修改昵称
                enter.setEnabled(false);//禁用进入按钮
                quit.setEnabled(true);//退出按钮可用
                new Thread(new ClientSend()).start();//启动发送信息的线程
                new Thread(new ClientReceive()).start();//启动接收信息的线程
            } else if (callback.equals(USERNAME_REPEATED)) {//如果重名
                s.close();//关闭套接字
                JOptionPane.showMessageDialog(null, "该昵称已存在！");
            } else {//连接失败
                JOptionPane.showMessageDialog(null, "连接失败，请检查IP地址和端口号是否有误！");
            }
        }
    }

    public void sendPrivately() throws IOException, InterruptedException {//实现私聊功能
        requireSendPrivately = true;
        while (true) {//循环等待服务器发来在线用户列表
            if (!this.onlineUser.equals("")) {
                System.out.println("收到服务器的对于自己私发请求的反馈");
                System.out.println(onlineUser);
                String[] options = onlineUser.split(",");//把在线用户列表按","分割
                System.out.println(options.length);
                for (String option : options) {
                    System.out.println(option);
                }
                designatedReceiver = (String) JOptionPane.showInputDialog(null, "请选择要私发的用户:", "私聊", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                sendPrivatelyRunnable = true;//可以私发了
                onlineUser = "";//处理完恢复
                break;
            }
            Thread.sleep(50L);
        }
    }

    public void exitChatRoom() throws IOException, ClassNotFoundException {//退出聊天室
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        Message message = new Message(nickname, REQUEST_EXIT);//发出退出申请
        oos.writeObject(message);
        this.connectState = false;
        //enter.setEnabled(true);//进入按钮可用
        //quit.setEnabled(false);//禁用退出按钮
        exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton temp = (JButton) e.getSource();
        if (temp == enter) {//进入聊天室
            try {
                enterChatRoom();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (temp == quit) {//退出聊天室
            try {
                exitChatRoom();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else if (temp == send) {//群发
            requireSend = true;
            System.out.println("接收到发送消息申请");
        } else if (temp == privateSend) {//私发
            System.out.println("我要申请私发");
            try {
                sendPrivately();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    //以下是用户关闭窗口时的操作
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            exitChatRoom();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
    //以上是用户关闭窗口时的操作

    private class ClientSend implements Runnable {//客户端发送信息线程
        private ObjectOutputStream oos;

        @Override
        public void run() {
            while (true) {
                boolean state = connectState;
                //boolean require = requireSend;
                if (state) {//如果在运行
                    if (requireSend) {//如果请求群发
                        System.out.println("用户申请发消息！");
                        //把要发送的消息封装成一个对象
                        Message message = new Message(nickname, "All", inputBox.getText(), DateUtils.getCurrentTime(), SIMPLE_MESSAGE);
                        try {
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            requireSend = false;
                            inputBox.setText("");//清空输入框
                        }
                        continue;
                    }
                    if (requireSendPrivately) {//如果申请私发
                        System.out.println("用户申请私发消息");
                        Message requireSendPrivateMsg = new Message(nickname, REQUEST_SEND_PRIVATELY);
                        try {
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(requireSendPrivateMsg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            requireSendPrivately = false;
                        }
                        continue;
                    }
                    if (sendPrivatelyRunnable) {//如果可以私发了
                        System.out.println("用户申请私发消息！");
                        //把要发送的消息封装成一个对象
                        Message message = new Message(nickname, designatedReceiver, inputBox.getText(), DateUtils.getCurrentTime(), PRIVATE_MESSAGE);
                        try {
                            oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            sendPrivatelyRunnable = false;//处理完恢复状态
                            inputBox.setText("");//清空输入框
                        }
                        continue;
                    }
                } else {//如果断开，线程结束
                    System.out.println("发送的线程侦测到用户断开连接");
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

    private class ClientReceive implements Runnable {//客户端接收信息线程
        private ObjectInputStream ois;

        @Override
        public void run() {
            while (true) {
                if (connectState) {//如果自己还处于连接状态
                    try {
                        ois = new ObjectInputStream(s.getInputStream());
                        //读取Message对象
                        Message message = (Message) ois.readObject();
                        String messageType = message.getMessageType();//获取消息类型
                        if (messageType.equals(SIMPLE_MESSAGE) && message.getReceiverName().equals("All")) {//如果是群发消息
                            messageBox.append("\n" + message.getSendTime() + message.getSenderName() + ":\n");//信息的发送时间和发送者
                            messageBox.append(message.getMessageContent());//消息内容
                        } else if (messageType.equals(ANNOUNCEMENT)) {//如果是服务器发来的群公告
                            //messageBox.setForeground(Color.RED);
                            messageBox.append("\n" + message.getSendTime() + "群公告: " + message.getMessageContent() + "\n");
                        } else if (messageType.equals(RESPONSE_OF_PRIVATE)) {//如果是服务器对于私发请求的回复
                            System.out.println("接收到服务器对于自己私发请求的回复");
                            onlineUser = message.getMessageContent();//获取在线用户
                            System.out.println("在线用户：" + onlineUser);
                        } else if (messageType.equals(PRIVATE_MESSAGE)) {//如果接收到私发消息
                            messageBox.append("\n" + message.getSendTime() + message.getSenderName() + "悄悄对你说:\n");//信息的发送时间和发送者
                            messageBox.append(message.getMessageContent());//消息内容
                        } else if (messageType.equals(PERSONAL_MESSAGE)) {//是一条要显示在自己界面的消息
                            messageBox.append("\n" + message.getSendTime() + "你悄悄地对" + message.getReceiverName() + "说:\n");//信息的发送时间和接收者
                            messageBox.append(message.getMessageContent());//消息内容
                        } else if (messageType.equals(FORCE_QUIT)) {//如果是服务器踢出用户的消息
                            if (message.getReceiverName().equals(nickname)) {//判断是不是自己被踢出
                                JOptionPane.showMessageDialog(null, "你已被服务器强制退出！", "连接中断！", JOptionPane.WARNING_MESSAGE);
                                exit(0);
                            } else {
                                messageBox.append("\n" + message.getSendTime() + "群公告: " + message.getMessageContent() + "\n");
                            }
                        } else if (messageType.equals(SERVER_CLOSED)) {//如果是服务器即将关闭的信息
                            messageBox.append("\n" + message.getSendTime() + "群公告: " + message.getMessageContent() + "\n");
                            JOptionPane.showMessageDialog(null, "群公告:\n" + message.getMessageContent());
                            exit(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//如果断开连接，则线程结束
                    System.out.println("接收的线程侦测到用户断开连接");
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
}
