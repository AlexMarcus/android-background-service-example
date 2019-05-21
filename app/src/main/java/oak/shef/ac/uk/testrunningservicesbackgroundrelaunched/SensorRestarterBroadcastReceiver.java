package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by fabio on 24/01/2016.
 */
public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        int val = intent.getIntExtra(MainActivity.CURRENT_PROGRESS, 0);
        context.startService(new Intent(context, SensorService.class).putExtra(MainActivity.CURRENT_PROGRESS, val));
    }

}
