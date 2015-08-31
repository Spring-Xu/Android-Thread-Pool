package cjstar.com.android_thread_pool.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xuchun on 15/8/31.
 */
public class CustomThreadPoolExecutor implements CustomExecutor {

    private BlockingQueue<Runnable> mQueue;

    /**
     * max pool size
     */
    private int maxPoolSize;
    /**
     * max execute threads count at the same time
     */
    private int maxExecutingSize;
    /**
     * the thread max interval time at each of them (mills)
     */
    private int maxInterval;

    public CustomThreadPoolExecutor() {
        this.maxExecutingSize = DefaultConfig.DEFAULT_MAX_EXECUTINGSIZE;
        this.maxPoolSize = DefaultConfig.DEFAULT_MAX_POOL_SIZE;
        this.maxInterval = DefaultConfig.DEFAULT_MAX_INTERVAL;
        init();
    }

    /**
     * Create a pool with the pool size and interval time
     *
     * @param maxPoolSize      max pool size
     * @param maxExecutingSize max execute threads count at the same time
     * @param maxInterval      the thread max interval time at each of them
     */
    private CustomThreadPoolExecutor(int maxPoolSize, int maxExecutingSize, int maxInterval) {
        this.maxExecutingSize = maxExecutingSize;
        this.maxInterval = maxInterval;
        this.maxPoolSize = maxPoolSize;
        init();
    }

    private void init(){
        if(this.maxPoolSize<=0){
            throw new IllegalArgumentException("CustomThreadPoolExecutor maxPoolSize is illegal:"+this.maxPoolSize);
        }

        if(this.maxInterval<=0){
            throw new IllegalArgumentException("CustomThreadPoolExecutor maxInterval is illegal:"+this.maxInterval);
        }

        if(this.maxExecutingSize<=0){
            throw new IllegalArgumentException("CustomThreadPoolExecutor maxExecutingSize is illegal:"+this.maxExecutingSize);
        }

        mQueue = new LinkedBlockingQueue<Runnable>(maxPoolSize);
    }


    /**
     * Execute the customAsyncTask in a background thread and the {@link CustomAsyncTask#onPreExecute()}</br>
     * will be called at main thread before {@link CustomAsyncTask#doInBackground(Object[])}, </br>
     * and the {@link CustomAsyncTask#onPostExecute(Object)} will be called after<br/>
     * {@link CustomAsyncTask#doInBackground(Object[])}
     *
     * @param customAsyncTask
     */
    @Override
    public void execute(CustomAsyncTask customAsyncTask) {

    }

    /**
     * Execute the runnable
     *
     * @param runnable this task content
     */
    @Override
    public void execute(Runnable runnable) {

    }

    /**
     * Build ths Execute pool by set the pool size , executing size and max interval
     */
    public static class Builder {
        private int maxPoolSize;
        private int maxExecutingSize;
        private int maxInterval;

        public Builder() {
        }

        /**
         * Set thread pool size
         * @param maxPoolSize
         * @return
         */
        public Builder setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        /**
         * set the executing size at the same time
         * @param maxExecutingSize
         * @return
         */
        public Builder setMaxExecutingSize(int maxExecutingSize) {
            this.maxExecutingSize = maxExecutingSize;
            return this;
        }

        /**
         * max interval time
         * @param maxInterval mills
         * @return
         */
        public Builder setMaxInterval(int maxInterval) {
            this.maxInterval = maxInterval;
            return this;
        }

        public CustomThreadPoolExecutor build() {
            return new CustomThreadPoolExecutor(maxPoolSize, maxExecutingSize, maxInterval);
        }
    }

    /**
     * worker for runnable or task
     */
    class TaskWorker implements Worker {

        @Override
        public void afterWork() {

        }

        @Override
        public void work() {

        }

        @Override
        public void preWork() {

        }

        @Override
        public boolean hasNext() {
            return false;
        }
    }
}
