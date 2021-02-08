package com.group10b.blueka_sync;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {

    public static final String TIME_SERVER = "ntp.xs4all.nl";
    private static final String TAG = "";


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
                                 * Obtaining atomic time and displaying on screen
                                 */
                                /*TextView atomicTime = (TextView) findViewById(R.id.atomic);
                                long at = 0;
                                try {
                                    at = getCurrentNetworkTime();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SS");
                                String atomicString = sdf2.format(at);
                                atomicTime.setText(atomicString);*/


                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();





    }



  /*public static long getCurrentNetworkTime() throws IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        //long returnTime = timeInfo.getReturnTime();   //local device time
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time

        Date time = new Date(returnTime);
        Log.d(TAG, "Time from " + TIME_SERVER + ": " + time);

        return returnTime;
    }*/



}