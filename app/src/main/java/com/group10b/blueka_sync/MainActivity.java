package com.group10b.blueka_sync;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {

    final SntpClient sntpClient = new SntpClient();
    private long offset;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                               TextView systemTime = (TextView) findViewById(R.id.system);
                                long st = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                                String systemString = sdf.format(st);
                                systemTime.setText(systemString);

                                /**
                                 * Obtaining atomic time from internet via NTP and displaying on screen
                                 */

                                TextView atomicTime = (TextView) findViewById(R.id.atomic);
                                TextView offsetView = (TextView) findViewById(R.id.offsetId);
                                new AsyncTask<Void,Integer,Boolean>(){
                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        return sntpClient.requestTime("pool.ntp.org",3000);
                                    }
                                    @Override
                                    protected void onPostExecute(Boolean result) {
                                        if(result){
                                            long at = sntpClient.getNtpTime();
                                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SS");
                                            String atomicString = formatter.format(at);
                                            atomicTime.setText(atomicString);

                                            //System.out.println("Atomic Time: " + sntpClient.getNtpTime());
                                            //System.out.println("System Time: " + System.currentTimeMillis());
                                            if (System.currentTimeMillis() >= sntpClient.getNtpTime()) {
                                                offset = System.currentTimeMillis() - sntpClient.getNtpTime() ;
                                            } else {
                                                offset = sntpClient.getNtpTime() - System.currentTimeMillis()  ;
                                            }
                                            //System.out.println("Offset: " + offset);
                                            SimpleDateFormat offsetFormat = new SimpleDateFormat("ss.S");
                                            String offsetString = offsetFormat.format(offset);

                                            if (System.currentTimeMillis() >= sntpClient.getNtpTime()){
                                                offsetView.setText("+" +offsetString);
                                            } else {
                                                offsetView.setText("-" +offsetString);
                                            }


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

}

