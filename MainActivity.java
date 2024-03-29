package com.example.sose_main;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

//detect noise

public class MainActivity extends AppCompatActivity{
    /* constants */
    private static final int POLL_INTERVAL = 1000;
    public static int noise_sum = 0;
    public static int cnt = -1;

    /** running state **/
    private boolean mRunning = false;

    /** config state **/
    private int mThreshold;

    int RECORD_AUDIO = 0;
    private PowerManager.WakeLock mWakeLock;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView,tv_noice,noise_avg;

    /* sound data source */
    private DetectNoise mSensor;
    ProgressBar bar;
    /****************** Define runnable thread again and again detect noise *********/

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Monitoring Voice...", amp);

            if ((amp > mThreshold)) {
                callForHelp(amp);
                //Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Defined SoundLevelView in main.xml file
        setContentView(R.layout.activity_main);

        Button btn_aid;
        btn_aid=(Button)findViewById(R.id.aid);
        btn_aid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_aid = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.응급센터.com/?n_media=27758&n_query=%EC%9D%91%EA%B8%89%EC%B2%98%EC%B9%98%EB%B2%95&n_rank=1&n_ad_group=grp-a001-01-000000011711579&n_ad=nad-a001-01-000000067963587&n_keyword_id=nkw-a001-01-000002152731754&n_keyword=%EC%9D%91%EA%B8%89%EC%B2%98%EC%B9%98%EB%B2%95&n_campaign_type=1"));
                startActivity(intent_aid);

            }});

        Button set;
        set=(Button)findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_set = new Intent(MainActivity.this, setting.class);
                startActivity(intent_set);

            }
        });


        mStatusView = (TextView) findViewById(R.id.status);
        tv_noice=(TextView)findViewById(R.id.tv_noice);
        bar=(ProgressBar)findViewById(R.id.progressBar1);
        // Used to record voice
        mSensor = new DetectNoise();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert:tag");

        noise_avg=(TextView)findViewById(R.id.noise_avg);

    }


    /////////////////////////
    @Override
    public void onResume() {
        super.onResume();
        //Log.i("Noise", "==== onResume ===");

        initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");
        //Stop noise monitoring
        stop();
    }
    private void start() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        }

        //Log.i("Noise", "==== start ===");
        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    private void stop() {
        Log.d("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        bar.setProgress(0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;
    }

    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 8;

    }

    public void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        bar.setProgress((int)signalEMA);
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText((int)signalEMA+"dB");

        MainActivity.cnt ++;
        if(signalEMA <= -2147483648) noise_sum -= (-2147483648); // why is that value detected? T^T

        noise_sum+=(int)signalEMA;
        double avg = noise_sum/(double)cnt;
        noise_avg.setText(noise_sum+"dB, "+avg+"dB");

        if (signalEMA>avg){
            Toast.makeText(getApplicationContext(),"Over",Toast.LENGTH_SHORT).show();
        }
    }

    private void callForHelp(double signalEMA) {

        //stop();
        // Show alert when noise thersold crossed
        Toast.makeText(getApplicationContext(), "Noise Thersold Crossed, do here your stuff.",
                Toast.LENGTH_LONG).show();
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }


};




