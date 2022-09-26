package server;

import pojo.Message;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 19:39
 * @Version 1.0
 * @PACKAGE server
 */
public interface RMICenter {
    /**
     * 发送文件
     * @param msg
     */
    void sendMSG(Message msg);
}
