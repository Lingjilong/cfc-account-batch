package cn.com.njcb.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import cn.com.njcb.annotation.TypeAnnotation;

/**
 * 多线程工具类
 * @author Uncle chang
 * @time 2020年2月28日下午8:39:01
 * @copyright NJCB
 */
@TypeAnnotation("@description:多线程工具类 @author:Uncle chang @time:2020年2月28日下午8:39:01")
public class ThreadUtils {

    /**
     * 多线程并发数控制
     * @param threadPoolTaskExecutor 线程池
     * @param runnable 实现Runnable接口的多线程类
     * @param activeCountControl 并发数控制
     * @param sleepTime 线程休眠时间单位s
     * @throws Exception
     */
    public static void concurrencyControl(ThreadPoolTaskExecutor threadPoolTaskExecutor, Runnable runnable, int activeCountControl, int sleepTime) throws Exception {
        while (true) {
            if (threadPoolTaskExecutor.getActiveCount() < (activeCountControl == 0 ? 100 : activeCountControl)) {
                threadPoolTaskExecutor.execute(runnable);
                break;
            } else {
                Thread.sleep(sleepTime == 0 ? 1 * 1000 : sleepTime * 1000);
            }
        }
    }
}
