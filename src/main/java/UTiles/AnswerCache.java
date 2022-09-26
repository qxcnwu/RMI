package UTiles;

import org.jetbrains.annotations.Contract;
import pojo.Message;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author qxc
 * @version 1.0
 * @date 2022 2022/9/26 11:38
 * @PACKAGE UTiles
 */
public class AnswerCache {
    private static final LinkedBlockingQueue<Message> MSGANSWER = new LinkedBlockingQueue<>();

    @Contract(pure = true)
    public static LinkedBlockingQueue<Message> getAns() {
        return MSGANSWER;
    }

    /**
     * 添加远程主机返回的结果
     * @param e
     */
    public static void add(Message e) {
        MSGANSWER.offer(e);
    }
}
