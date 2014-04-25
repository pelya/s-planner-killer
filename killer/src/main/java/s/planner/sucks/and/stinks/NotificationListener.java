package s.planner.sucks.and.stinks;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by pelya on 4/25/14.
 */
public class NotificationListener extends NotificationListenerService {
    @Override
    public void onCreate() {
        //Log.d(TAG, "onCreate()");
        super.onCreate();
        instance = this;
    }

    public synchronized void refreshNotifications() {
        //Log.d(TAG, "Refreshing notifications");
        try {
            for (StatusBarNotification n: getActiveNotifications()) {
                onNotificationPosted(n);
            }
            refreshFailed = false;
        } catch (Exception e) {
            Log.e(TAG, "Refreshing notifications error - this app shall be allowed notification access in Android security settings");
            refreshFailed = true;
        }
        //Log.d(TAG, "Refreshing notifications done");
    }
    @Override
    public void onNotificationPosted(StatusBarNotification n) {
        //Log.d(TAG, "New notification: " + n.getPackageName() + " " + n.getTag() + " " + n.getId() + " " + n.getNotification().toString());
        if (n.getPackageName().equals("com.android.calendar")) {
            //Log.i(TAG, "Killing notification of a built-in calendar");
            cancelNotification(n.getPackageName(), n.getTag(), n.getId());
            //for (Notification.Action a: n.getNotification().actions) {
            //    Log.i(TAG, "Action " + a.title + " " + a.toString());
            //}
        }
    }

    @Override
    public void  onNotificationRemoved(StatusBarNotification n) {
        //Log.d(TAG, "Notification disappeared: " + n.getPackageName() + " " + n.getTag() + " " + n.getId() + " " + n.getNotification().toString());
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand()");
        refreshNotifications();
        return START_STICKY;
    }

    public static NotificationListener instance = null;
    public static String TAG = "S Planner Killer";
    public static boolean refreshFailed = true;
}
