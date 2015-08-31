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
     * Execute the customAsyncTask in a background thread and the {@link CustomAsyncTask#onPreExecute()}</br>
     * will be called at main thread before {@link CustomAsyncTask#doInBackground(Object[])}, </br>
     * and the {@link CustomAsyncTask#onPostExecute(Object)} will be called after {@link CustomAsyncTask#doInBackground(Object[])}
     * @param customAsyncTask
     */
    public void execute(CustomAsyncTask customAsyncTask);
}
