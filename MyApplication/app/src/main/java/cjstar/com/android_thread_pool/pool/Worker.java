package cjstar.com.android_thread_pool.pool;

/**
 * Created by xuchun on 15/8/31.
 */
public interface Worker {
    public void work();

    public boolean hasNext();

    public void preWork();

    public void afterWork();
}
