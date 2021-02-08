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

    public static final String SERVER_NAME = "pool.ntp.org";
    private static final String TAG = "";
    final SntpClient sntpClient = new SntpClient();
    //final NTPUDPClient client = new NTPUDPClient();



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

                               /*
                                new GetNTPAsynctask().execute();
                                if (client.requestTime("pool.ntp.org",10)){
                                    long at = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss.SS");
                                    String atomicString = sdf2.format(at);
                                    atomicTime.setText(atomicString);
                                }*/

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

                                            System.out.println("Atomic Time: " + sntpClient.getNtpTime());
                                            System.out.println("System Time: " + System.currentTimeMillis());
                                            long offset = System.currentTimeMillis() - sntpClient.getNtpTime() ;
                                            System.out.println("Offset: " + offset);
                                            SimpleDateFormat offsetFormat = new SimpleDateFormat("ss.S");
                                            String offsetString = offsetFormat.format(offset);
                                            offsetView.setText(offsetString);

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

