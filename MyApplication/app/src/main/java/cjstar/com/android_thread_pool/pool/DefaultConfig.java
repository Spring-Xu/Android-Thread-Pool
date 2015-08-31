package cjstar.com.android_thread_pool.pool;

/**
 * Created by xuchun on 15/8/31.
 */
public class DefaultConfig {
    /**
     * max pool size
     */
    public static  int DEFAULT_MAX_POOL_SIZE = 128;
    /**
     * max execute threads count at the same time
     */
    public static  int DEFAULT_MAX_EXECUTINGSIZE = 7;
    /**
     * the thread max interval time at each of them (mills)
     */
    public static  int DEFAULT_MAX_INTERVAL = 1000;
}
