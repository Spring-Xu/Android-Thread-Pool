package cjstar.com.android_thread_pool.pool;

/**
 * Custom executor for Threads, it content executors for {@link Runnable} and {@link CustomAsyncTask}
 */
public interface CustomExecutor {
    /**
     * Execute the runnable
     * @param runnable this task content
     */
    public void execute(Runnable runnable);

    /**
     * shout the thread pool
     */
    public void shoutDown();
}
