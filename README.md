# Android-Thread-Pool
A simple thread pool for Android, this pool is base on Handler and Runable

#Contact me
* Email:hquspring@gmail.com
* Blog:http://write.blog.csdn.net/postlist

# How to use?

* Create instance
```Java
CustomExecutor executor = new CustomThreadPoolExecutor.Builder()
                .setMaxExecutingSize(5)
                .setMaxInterval(1000)
                .setMaxPoolSize(100)
                .build();
```
OR
```Java
//get default Executor with default config
CustomExecutor DEFAULT_THREAD_POOL_EXECUTOR = new CustomThreadPoolExecutor();
```
* Use pool
```Java
Runnable run = new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.d("Runnable run", "Thread id = "+Looper.myLooper().getThread().getId());
                        Thread.sleep(2000);
                    }catch (InterruptedException i){
                    }
                }
            };
 executor.execute(run);
```
* Use CustomAsyncTask
```Java
CustomAsyncTask<Void,Void,String> task = new CustomAsyncTask<Void, Void, String>() {
                    /**
                     * Runs on the UI thread before {@link #doInBackground}.
                     * But this method will not be executed when we call the {@link #execute()} or {@link #execute(Object...)}
                     * or {@link #executeOnExecutor(CustomExecutor)} or {@link #executeOnExecutor(CustomExecutor, Object...).
                     * It will be call when UI thread handled out. So, this method will not been called as soon as the execute called
                     *
                     * @see #onPostExecute
                     * @see #doInBackground
                     */
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Log.d("","onPreExecute:"+Looper.myLooper().getThread().getId());
                    }

                    /**
                     * it will be called after the doInBackground executed, and this method will
                     * be called in UI thread.
                     *
                     * @param s
                     */
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Log.d("", "onPostExecute:" + Looper.myLooper().getThread().getId());
                        Log.d("", "onPostExecute result:" + s);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        Log.d("", "doInBackground:" + Looper.myLooper().getThread().getId());
                        return "id="+Looper.myLooper().getThread().getId();
                    }
                };

task.executeOnExecutor(executor);
```
