package cjstar.com.android_thread_pool.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cjstar.com.android_thread_pool.R;
import cjstar.com.customthreadpoollibrary.CustomAsyncTask;
import cjstar.com.customthreadpoollibrary.CustomExecutor;
import cjstar.com.customthreadpoollibrary.CustomThreadPoolExecutor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button mBtnCreate;
    Button mBtnShoutDown;
    TextView mTvInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnCreate = (Button)findViewById(R.id.create_pool);
        mBtnCreate.setOnClickListener(this);
        mBtnShoutDown = (Button)findViewById(R.id.shout_down_pool);
        mBtnShoutDown.setOnClickListener(this);
        findViewById(R.id.add_runnable).setOnClickListener(this);
        findViewById(R.id.add_asynctask).setOnClickListener(this);
        mTvInfo = (TextView)findViewById(R.id.info);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_pool:
                createPool();
                break;

            case R.id.shout_down_pool:
                shoutDown();
                break;

            case R.id.add_runnable:
                new Thread(){
                    public void run(){
                        for(int i=0; i<100; i++){
                            handler.post(newRun(i));
                        }
                    }
                }.start();
                break;

            case R.id.add_asynctask:
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

                task.execute();
//                task.executeOnExecutor(executor);
                break;

            default:break;
        }
    }

    CustomExecutor executor;
    private void createPool(){
        executor = new CustomThreadPoolExecutor.Builder()
                .setMaxExecutingSize(10)
                .setMaxInterval(1000)
                .setMaxPoolSize(100)
                .build();
    }

    private Runnable newRun(final int i){
        return new Runnable() {
            @Override
            public void run() {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Log.d("Runnable run", "Thread id = "+Looper.myLooper().getThread().getId());
                            Thread.sleep(i*10);
                        }catch (InterruptedException i){
                        }
                    }
                };
                executor.execute(run);
            }
        };
    }

    Runnable handlerRun = new Runnable() {
        @Override
        public void run() {
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
        }
    };

    Handler handler = new Handler();

    private void shoutDown(){
        handler.removeCallbacks(handlerRun);
        executor.shoutDown();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
