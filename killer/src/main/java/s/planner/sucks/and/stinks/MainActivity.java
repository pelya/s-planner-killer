package s.planner.sucks.and.stinks;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        if (NotificationListener.instance == null) {
            startService(new Intent(this, NotificationListener.class));
        }
        checkServiceRunning();
    }

    public void checkServiceRunning() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) { }
                NotificationListener.instance.refreshNotifications();
                Log.i(NotificationListener.TAG, "NotificationListener.refreshFailed: " + NotificationListener.refreshFailed);
                if (NotificationListener.refreshFailed) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle(R.string.enable_security);
                    alert.setMessage(R.string.enable_security_text);
                    alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
                            TextView text = (TextView)findViewById(R.id.text);
                            text.setText(R.string.enable_security_text);
                            alertShown = false;
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(NotificationListener.TAG, "alertShown " + alertShown);
                            if (!alertShown) {
                                try {
                                    alert.create().show();
                                } catch (Exception e) {
                                    Log.i(NotificationListener.TAG, "Activity destroyed, cannot show alert dialog");
                                    return;
                                }
                            }
                            alertShown = true;
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView text = (TextView)findViewById(R.id.text);
                            text.setText(R.string.service_started);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        Log.i(NotificationListener.TAG, "onResume");
        super.onResume();
        checkServiceRunning();
    }

    @Override
    public void onPause() {
        Log.i(NotificationListener.TAG, "onPause");
        super.onPause();
        alertShown = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    static boolean alertShown = false;
}
