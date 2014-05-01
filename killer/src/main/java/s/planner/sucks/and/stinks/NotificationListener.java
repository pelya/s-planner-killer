package s.planner.sucks.and.stinks;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pelya on 4/25/14.
 */
public class NotificationListener extends NotificationListenerService {
    @Override
    public void onCreate() {
        //Log.d(TAG, "onCreate()");
        super.onCreate();
        instance = this;
        ntfManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public synchronized void refreshNotifications() {
        //Log.d(TAG, "Refreshing notifications");
        try {
            for (StatusBarNotification n: getActiveNotifications ()) {
                onNotificationPosted (n);
            }
            refreshFailed = false;
        } catch (Exception e) {
            Log.e(TAG, "Refreshing notifications error - this app shall be allowed notification access in Android security settings");
            refreshFailed = true;
        }
        //Log.d(TAG, "Refreshing notifications done");
    }
    @Override
    public void onNotificationPosted(final StatusBarNotification n) {
        //Log.d(TAG, "New notification: " + n.getPackageName() + " " + n.getTag() + " " + n.getId() + " " + n.getNotification().toString());
        if (n.getPackageName().equals("com.android.calendar")) {
            //Log.i(TAG, "Killing notification of a built-in calendar");
            new Thread(new Runnable () {
                public void run () {
                    synchronized (instance) {
                        // Sleep a bit to allow Google Calendar to also show it's notification
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) { }
                        synchronized (ntfsRemoved) {
                            ntfsRemoved.clear ();
                            monitorRemoved = true;
                        }
                        StatusBarNotification oldNtfs[] = getActiveNotifications ();
                        cancelNotification(n.getPackageName(), n.getTag(), n.getId());
                        // Google Calendar for some reason also hides it's own notification,
                        // when we delete S Planner ntf, so we kind of restoring it here
                        try {
                            Thread.sleep (1000);
                        } catch (Exception e) { }
                        synchronized (ntfsRemoved) {
                            try {
                                for (StatusBarNotification rm: ntfsRemoved) {
                                    if (!rm.getPackageName().equals(n.getPackageName())) {
                                        for(StatusBarNotification oldNtf: oldNtfs) {
                                            if (oldNtf != null && rm != null &&
                                                oldNtf.getPackageName() != null && rm.getPackageName() != null &&
                                                oldNtf.getTag() != null && rm.getTag() != null &&
                                                oldNtf.getPackageName().equals(rm.getPackageName()) &&
                                                oldNtf.getTag().equals(rm.getTag()) &&
                                                oldNtf.getId() == rm.getId()) {
                                                    ntfManager.notify(oldNtf.getTag(), oldNtf.getId(), oldNtf.getNotification());
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Cannot add notification: " + e.toString());
                            }
                            ntfsRemoved.clear ();
                            monitorRemoved = false;
                        }
                    }
                }
            }).start ();
            //for (Notification.Action a: n.getNotification().actions) {
            //    Log.i(TAG, "Action " + a.title + " " + a.toString());
            //}
        }
    }

    @Override
    public void onNotificationRemoved (StatusBarNotification n) {
        //Log.d(TAG, "Notification disappeared: " + n.getPackageName() + " " + n.getTag() + " " + n.getId() + " " + n.getNotification().toString());
        synchronized (ntfsRemoved) {
            if (monitorRemoved)
                ntfsRemoved.add (n);
        }
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand()");
        refreshNotifications ();
        return START_STICKY;
    }

    public static NotificationListener instance = null;
    public static String TAG = "S Planner Killer";
    public static boolean refreshFailed = true;
    static boolean monitorRemoved = false;
    static ArrayList<StatusBarNotification> ntfsRemoved = new ArrayList<StatusBarNotification> ();
    static NotificationManager ntfManager = null;

}
