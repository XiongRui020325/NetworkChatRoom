//这是一个工具接口
package Experiment5;

public interface Tool {
    String LOG_ON_SUCCESSFULLY = "1";//登陆成功
    String LOG_ON_FAILED = "-1";//登录失败
    String USERNAME_REPEATED = "2";//重名
    String REQUEST_LOG_ON = "3";//请求登录
    String SIMPLE_MESSAGE = "4";//普通消息
    String PRIVATE_MESSAGE = "9";//私聊消息
    String REQUEST_EXIT = "5";//请求退出
    String ANNOUNCEMENT = "6";//服务器发来的群消息
    String REQUEST_SEND_PRIVATELY = "7";//请求私聊
    String RESPONSE_OF_PRIVATE = "8";//服务器对于请求私聊的回复
    String PERSONAL_MESSAGE = "10";//自己私发出去的要显示在自己界面的消息
    String FORCE_QUIT = "11";//服务器强制退出
    String SERVER_CLOSED = "12";//关闭服务器
}
