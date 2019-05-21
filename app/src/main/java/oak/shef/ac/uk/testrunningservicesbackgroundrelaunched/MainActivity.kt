/**
 * created by Fabio Ciravegna, The University of Sheffield, f.ciravegna@shef.ac.uk
 * LIcence: MIT
 * Copyright (c) 2016 (c) Fabio Ciravegna
 *
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
package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var mServiceIntent: Intent
    private lateinit var mSensorService: SensorService

    lateinit var ctx: Context
        internal set

    private var shouldStopService = false

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("OnNewIntent", "new Intent")

        shouldStopService = getIntent().getBooleanExtra(STOP_SERVICE, false)
        runService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shouldStopService = false

        ctx = this
        mSensorService = SensorService()
        mServiceIntent = Intent(ctx, mSensorService.javaClass).putExtra(CURRENT_PROGRESS, 0)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener { v ->
            val intent = Intent(ctx, mSensorService.javaClass)
            intent.putExtra(RESTART_SERVICE, true)
            startService(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App Notifications"
            val description = "THE CHANNEL"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("10101", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        shouldStopService = intent.getBooleanExtra(STOP_SERVICE, false)

        Log.i("Stopping service", "here222 = $shouldStopService")

        runService()

    }

    private fun runService() {
        if (shouldStopService) {
            Log.i("Stopping service", "from main activity")
            stopService(mServiceIntent)
            val manager = NotificationManagerCompat.from(this)
            manager.cancel(1001)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isMyServiceRunning()) {
                startForegroundService(mServiceIntent)
            }
        }
    }

    private fun isMyServiceRunning(): Boolean {
        Log.i("Is My Service Running?", ServiceManager.isTimerServiceRunning.toString())
        return ServiceManager.isTimerServiceRunning
    }


    override fun onDestroy() {
        //stopService(mServiceIntent)
        Log.i("Main Activity", "onDestroy!")
        super.onDestroy()

    }

    companion object {
        const val RESTART_SERVICE = "RESTART_SERVICE"
        const val CURRENT_PROGRESS = "CURRENT_PROGRESS"
        const val STOP_SERVICE = "STOP_SERVICE"
    }
}


