package com.group10b.blueka_sync;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button buttonSync;
    final SntpClient sntpClient = new SntpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSync = (Button) findViewById(R.id.sync_button);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.merdeka);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10);
                        runOnUiThread(new Runnable() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void run() {

                                /**
                                 * Obtaining system time and displaying on screen
                                 */
                                TextView systemTimeView = (TextView) findViewById(R.id.system);
                                long st = System.currentTimeMillis();
                                systemTimeView.setText(getFormattedTime(st));

                                /**
                                 * Obtaining atomic time from internet via NTP and displaying on screen
                                 * Calculating offset with sign
                                 */

                                TextView atomicTimeView = (TextView) findViewById(R.id.atomic);
                                TextView offsetView = (TextView) findViewById(R.id.offsetId);

                                new AsyncTask<Void, Integer, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        return sntpClient.requestTime("pool.ntp.org", 3000);
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        if (result) {

                                            long at = sntpClient.getNtpTime();
                                            atomicTimeView.setText(getFormattedTime(at));

                                            setOffsetView(System.currentTimeMillis(),sntpClient.getNtpTime(),offsetView);
                                        }
                                    }
                                }.execute();


                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

    }

    public String getFormattedTime(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
        String formattedTime = sdf.format(time);
        return formattedTime;
    }

    public boolean getOffsetSign(long systemTime, long atomicTime){
        if (systemTime >= atomicTime){
            return true;
        } else {
            return false;
        }
    }

    public long getOffsetValue(long systemTime, long atomicTime){
        long offsetValue;
        if (systemTime >= atomicTime){
            offsetValue = systemTime - atomicTime;
        } else {
            offsetValue = atomicTime - systemTime;
        }
        //Obtain offset value in milliseconds
        return offsetValue;
    }

    public void setOffsetView(long systemTime, long atomicTime, TextView view){
        long value = getOffsetValue(systemTime,atomicTime);
        SimpleDateFormat offsetFormat = new SimpleDateFormat("ss.S");
        String offsetString = offsetFormat.format(value);
        if (getOffsetSign(systemTime,atomicTime) == true){
            view.setText("+" +offsetString);
        } else {
            view.setText("-"+offsetString);
        }
    }

    /*public void setSystemTime(long atomicTime){
        /*Calendar c = Calendar.getInstance();
        c.setTimeInMillis(atomicTime);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.setTime(c.getTimeInMillis());*/

        //SystemClock.setCurrentTimeMillis(atomicTime);
        /*if (ShellInterface.isSuAvailable()) {
            ShellInterface.runCommand("chmod 666 /dev/alarm");
            SystemClock.setCurrentTimeMillis(atomicTime);
            ShellInterface.runCommand("chmod 664 /dev/alarm");
        }

        System.out.println("System Time: " + System.currentTimeMillis());
        System.out.println("Atomic Time: " + atomicTime);

    }*/

    public long getCurrentInstance(){
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return  currentTime;
    }

   /* buttonSync.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }
    });*/



}

