//日期工具类，提供两个静态方法：获取时间和获取日期
package Experiment5;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private DateUtils(){}//工具类的构造方法设为private,外界就不会创建对象

    public static String getCurrentTime(){//获取当前系统时间
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(d);
        return "[" + time + "] ";
    }

    public static String getCurrentDate(){//获取日期
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(d);
    }
}
