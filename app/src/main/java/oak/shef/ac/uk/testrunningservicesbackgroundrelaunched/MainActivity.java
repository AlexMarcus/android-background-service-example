/**
 * created by Fabio Ciravegna, The University of Sheffield, f.ciravegna@shef.ac.uk
 * LIcence: MIT
 * Copyright (c) 2016 (c) Fabio Ciravegna

 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;


import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private SensorService mSensorService;

    public static final String CURRENT_PROGRESS = "CURRENT_PROGRESS";
    public static final String STOP_SERVICE = "STOP_SERVICE";

    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public boolean shouldStopService = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("OnNewIntent", "new Intent");

        shouldStopService = getIntent().getBooleanExtra(STOP_SERVICE, false);
        runService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getCtx(), mSensorService.getClass());
            intent.putExtra(STOP_SERVICE, true);
            startService(intent);
            stopService(intent);
            shouldStopService = false;
            runService();
        });

        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass())
            .putExtra(CURRENT_PROGRESS, 0);

        shouldStopService = getIntent().getBooleanExtra(STOP_SERVICE, false);

        Log.i("Stopping service", "here222 = " + shouldStopService);

        runService();

    }

    private void runService(){
        if(shouldStopService) {
            Log.i("Stopping service", "here");
            stopService(mServiceIntent);
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.cancel(1001);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (!isMyServiceRunning(mSensorService.getClass()) && !shouldStopService) {
                startForegroundService(mServiceIntent);
            }

            CharSequence name = "App Notifications";
            String description = "THE CHANNEL";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("10101", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}


