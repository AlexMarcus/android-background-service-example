package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    public int counter=0;

    public static final int MAX = 20;

    public NotificationCompat.Builder builder;
    public NotificationManagerCompat manager;

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("Start Service", "Some Messgae");
        counter = intent.getIntExtra(MainActivity.CURRENT_PROGRESS, 0);

        boolean terminate = intent.getBooleanExtra(MainActivity.STOP_SERVICE, false);
        if(terminate){
            counter = MAX;
            return START_STICKY;
        }


        if(counter <= MAX - 1) {

            Log.i("HERE", "AGAINS");
            manager = NotificationManagerCompat.from(this);

            builder = new NotificationCompat.Builder(this, "10101")
                    .setContentTitle("Loading")
                    .setContentText("Be Patient")
                    .setSmallIcon(R.drawable.ic_action_name)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(1001, builder.build());

            builder.setProgress(MAX, counter, false);

            manager.notify(1001, builder.build());

            startTimer();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class)
                .putExtra(MainActivity.CURRENT_PROGRESS, counter);
        if(counter <= MAX - 1) {
            sendBroadcast(broadcastIntent);
        }
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
                builder.setProgress(MAX, counter, false);
                manager.notify(1001, builder.build());
                if(counter >= MAX){

                    Intent intent = new Intent(SensorService.this, MainActivity.class)
                            .putExtra(MainActivity.STOP_SERVICE, true)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(SensorService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    stoptimertask();
                    builder.setProgress(MAX, MAX, false)
                        .setContentText("Download Complete")
                        .setContentIntent(pendingIntent)
                        .setOngoing(false)
                        .setAutoCancel(true);
                    manager.notify(1001, builder.build());
                }

            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i("Stop Service", "Called");

        counter = MAX;
        return super.stopService(name);
    }

}