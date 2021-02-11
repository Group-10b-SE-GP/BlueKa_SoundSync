package com.group10b.blueka_sync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button buttonSync;
    final SntpClient sntpClient = new SntpClient();
    long currentSystemTime;
    long currentNetworkTime;
    long currentOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSync = (Button) findViewById(R.id.sync_button);

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
                                            setOffsetView(System.currentTimeMillis(),at,offsetView);
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

        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Once number of connections is reached the following values
                //shall be retrieved
                currentSystemTime = System.currentTimeMillis();
                currentNetworkTime = sntpClient.getNtpTime();
                currentOffset = getOffsetValue(currentSystemTime,currentNetworkTime);

                System.out.println("System time: "+currentSystemTime);
                System.out.println("AtomicTime: "+currentNetworkTime);
                System.out.println("Offset:"+currentOffset);

            }
        });

    }

    /**
     * This method uses the timestamp and to calculate the sleep time.
     * Sleep time is obtained by subtracting current system time from future timestamp.
     * Once sleep time is over, the music shall be played.
     * @param timestamp The time to play the music
     * @throws InterruptedException
     */
    public void playMusic(long timestamp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(timestamp-System.currentTimeMillis());
                            startService(new Intent(MainActivity.this, SoundService.class));
                            Toast.makeText(getApplicationContext(),"PLAYED",Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }

    /**
     * This method will return the time at which the snippet shall be played on the server phone
     * This is done by adding a value to the current system time.
     * The value added is a constant value of 5000 ms.
     * @param currentSystemTime
     * @return time to play music on server phone
     */
    public long getServerMusicTime(long currentSystemTime){
        //This is the time at which the server phone will play music
        return (currentSystemTime + 5000);
    }
    public long getServerTimestamp(long currentSystemTime, long offsetValue){
        return getServerMusicTime(currentSystemTime) - offsetValue;
    }



    ///This method shall be called in client phone only
    public long getClientMusicTime(long timestamp, long clientOffset){
        //timestamp is the time received from the server
        //It is used to calculate the time at which the client phone will play music
        return timestamp + clientOffset;
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

}

