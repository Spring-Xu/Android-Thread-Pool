package cjstar.com.android_thread_pool.pool;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * custom AsyncTask , the same as AsyncTask, but it can be created in other
 * Thread which is not main Thread or use this in UI thread.</br>
 * The reason for doing this is to use this CustomAsyncTask to instead
 * AsyncTask, because AsyncTask can not be used in other thread except UI thread
 * see {@link AsyncTask}
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 * @author xuchun 2014-11-6
 */
public abstract class CustomAsyncTask<Params, Progress, Result> {

    private static String TAG = "CustomAsyncTask";

    /**
     * the thread run status
     */
    private Status mStatus = Status.PENDING;

    /**
     * if true , the thread had been canceled,else it is run normal
     */
    private AtomicBoolean mCancelled = new AtomicBoolean(false);

    /**
     * do the background task
     */
    private TaskThread mFuture;
    private Params[] mParams;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "CustomAsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    /**
     * the task pool manager,the same as {@link AsyncTask#THREAD_POOL_EXECUTOR}
     */
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    /**
     * it will be called in a thread which is not UI thread
     *
     * @param params
     * @return the result will return to {@link #onPostExecute(Object)}
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     * But this method will not be executed when we call the {@link #execute()} or {@link #execute(Object...)}
     * or {@link #executeOnExecutor(Executor)} or {@link #executeOnExecutor(Executor, Object...).
     * It will be call when UI thread handled out. So, this method will not been called as soon as the execute called
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
        mStatus = Status.PENDING;
    }

    /**
     * execute the task , the doInBackground will be call in a thread which is
     * not UI thread; and all the task will be managed in a thread pool, this
     * pool has a max size of 128 {@link #THREAD_POOL_EXECUTOR} normally. you
     * can provide the executor at the same time.
     *
     * @param executor
     */
    public void executeOnExecutor(final Executor executor) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                mFuture = new TaskThread();
                try {
                    executor.execute(mFuture);
                    mStatus = Status.RUNNING;
                } catch (Throwable t) {
                    mStatus = Status.FINISHED;
                    t.printStackTrace();
                    Log.e(TAG, t.getMessage());
                }
            }
        };

        mainHandler.post(myRunnable);
    }

    /**
     * execute the task , the doInBackground will be call in a thread which is
     * not UI thread; and all the task will be managed in a thread pool, this
     * pool has a max size of 128 {@link #THREAD_POOL_EXECUTOR} normally. you
     * can provide the executor at the same time.
     *
     * @param
     * @param executor
     */
    public void executeOnExecutor(final Executor executor, Params... params) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                mFuture = new TaskThread();
                try {
                    executor.execute(mFuture);
                    mStatus = Status.RUNNING;
                } catch (Throwable t) {
                    mStatus = Status.FINISHED;
                    t.printStackTrace();
                    Log.e(TAG, t.getMessage());
                }
            }
        };

        this.mParams = params;

        mainHandler.post(myRunnable);
    }

    /**
     * execute the task with the follow params
     *
     * @param params
     */
    public void execute(Params... params) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                mFuture = new TaskThread();
                mStatus = Status.RUNNING;
                mFuture.start();
            }
        };

        this.mParams = params;

        mainHandler.post(myRunnable);

    }

    /**
     * execute the task without params
     */
    public void execute() {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                mFuture = new TaskThread();
                mStatus = Status.RUNNING;
                mFuture.start();
            }
        };

        mainHandler.post(myRunnable);

    }

    /**
     * after the task executed, call the {@link #onPostExecute(Object)}.
     *
     * @param result the result got in background
     */
    private void doPostExecute(final Result result) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPostExecute(result);
            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * it will be called after the doInBackground executed, and this method will
     * be called in UI thread.
     *
     * @param result
     */
    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * if the task have been canceled ,will call this method.see
     * {@link #cancel(boolean)}
     *
     * @param result
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }

    /**
     * whether the task is canceled,see {@link #getStatus()}
     *
     * @return
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * whether the task is running,see {@link #getStatus()}
     *
     * @return
     */
    public final boolean isRunning() {
        return mStatus == Status.RUNNING;
    }

    /**
     * get the finish status,see {@link #getStatus()}
     *
     * @return
     */
    public final boolean isFinished() {
        return mStatus == Status.FINISHED;
    }

    /**
     * get the status whether the task have been executed
     *
     * @return if the task have been execute return true, else return false. see
     * {@link #getStatus()}
     */
    public final boolean isExecuted() {
        return mStatus != Status.PENDING;
    }

    /**
     * get the task status at now
     *
     * @return an enum value will be got
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * if the Task is run , call this method to cancel the doInBackground
     * method, finish the task without any result for this task
     *
     * @param mayInterruptIfRunning if true the task will be finished, else do nothing
     * @return if the task had been executed, and not been interrupted ,it will
     * be canceled and call onCancelled method ,finally return true,
     * else do nothing and return false
     */
    @SuppressWarnings("static-access")
    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning) {
            mCancelled.set(true);
            onCancelled();

            if (mFuture != null && !mFuture.isInterrupted()) {
                return mFuture.interrupted();
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * the background thread handler</br>
     * use this handler to handler the result for the doBackground method
     *
     * @author xuchun
     */
    private class TaskhHandler extends Handler {
        public TaskhHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mStatus == Status.FINISHED) {
                @SuppressWarnings("unchecked")
                Result mResult = (Result) msg.obj;
                doPostExecute(mResult);
            }
        }
    }

    /**
     * TaskThread extends Thread and we can use the thread to execute the
     * doBackground method
     *
     * @author xuchun
     */
    private class TaskThread extends Thread {
        @Override
        public void run() {
            super.run();
            Result mResult = doInBackground(mParams);
            mStatus = Status.FINISHED;
            Message msg = new Message();
            msg.obj = mResult;
            mainHandler.handleMessage(msg);
        }
    }

    /**
     * Indicates the current status of the task. Each status will be set only
     * once
     * during the lifetime of a task.
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link CustomAsyncTask#onPostExecute} has finished.
         */
        FINISHED,
    }

}