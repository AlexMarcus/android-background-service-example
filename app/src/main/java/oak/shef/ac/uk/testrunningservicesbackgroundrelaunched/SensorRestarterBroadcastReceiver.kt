package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by fabio on 24/01/2016.
 */
class SensorRestarterBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(SensorRestarterBroadcastReceiver::class.java.simpleName, "Service Stops! Oooooooooooooppppssssss!!!!")
        context.startService(Intent(context, SensorService::class.java))
    }

}
