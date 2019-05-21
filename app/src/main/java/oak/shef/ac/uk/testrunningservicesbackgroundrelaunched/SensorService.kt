package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log

import java.util.Timer
import java.util.TimerTask

/**
 * Created by fabio on 30/01/2016.
 */
class SensorService : Service() {
    var counter = 0

    lateinit var builder: NotificationCompat.Builder
    lateinit var manager: NotificationManagerCompat

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    var shouldRestart = false


    override fun onCreate() {
        super.onCreate()
        ServiceManager.isTimerServiceRunning = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("Start Service", "Some Messgae")
        counter = intent.getIntExtra(MainActivity.CURRENT_PROGRESS, 0)

        shouldRestart = intent.getBooleanExtra(MainActivity.RESTART_SERVICE, false)
        if (shouldRestart) {
            stopSelf()
            return START_NOT_STICKY
        }


        if (counter <= MAX - 1) {

            Log.i("HERE", "AGAINS")
            manager = NotificationManagerCompat.from(this)

            builder = NotificationCompat.Builder(this, "10101")
                    .setContentTitle("Loading")
                    .setContentText("Be Patient")
                    .setSmallIcon(R.drawable.ic_action_name)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            startForeground(1001, builder.build())

            builder.setProgress(MAX, counter, false)

            manager.notify(1001, builder.build())

            startTimer()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Service", "On Destroy!")
        ServiceManager.isTimerServiceRunning = false
        stopTimerTask()

        if(shouldRestart) {
            val broadcastIntent = Intent(this, SensorRestarterBroadcastReceiver::class.java)

            sendBroadcast(broadcastIntent)
        }


    }

    private fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    private fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {

                counter++
                Log.i("in timer", "in timer ++++ $counter")
                builder.setProgress(MAX, counter, false)
                manager.notify(1001, builder.build())

                if (counter >= MAX) {

                    val intent = Intent(this@SensorService, MainActivity::class.java)
                            .putExtra(MainActivity.STOP_SERVICE, true)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

                    val pendingIntent = PendingIntent.getActivity(this@SensorService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    builder.setProgress(MAX, MAX, false)
                            .setContentText("Download Complete")
                            .setContentIntent(pendingIntent)
                            .setOngoing(false)
                            .setAutoCancel(true)
                    manager.notify(1001, builder.build())

                    stopTimerTask()
                }
            }
        }
    }

    /**
     * not needed
     */
    private fun stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        const val MAX = 7
    }

}