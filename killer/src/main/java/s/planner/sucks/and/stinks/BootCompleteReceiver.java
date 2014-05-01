package s.planner.sucks.and.stinks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pelya on 4/25/14.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, NotificationListener.class);
        context.startService(startServiceIntent);
    }
}
