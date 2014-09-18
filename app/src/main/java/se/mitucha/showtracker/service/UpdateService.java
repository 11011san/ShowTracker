package se.mitucha.showtracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import se.mitucha.showtracker.R;
import se.mitucha.showtracker.get.GetShowInfo;
import se.mitucha.showtracker.util.DBTools;
import se.mitucha.showtracker.util.NetworkUtil;
import se.mitucha.showtracker.util.Settings;

/**
 * Created by mr11011 on 2014-08-07.
 */
public class UpdateService extends Service {

    private DBTools dbTools;
    private static UpdateService running;
    private static boolean updating = false;
    private static Context context;

    public static void setContext(Context context){
        UpdateService.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Show Tracker" , "starting update service");
        super.onStartCommand(intent, flags, startId);
        dbTools = new DBTools(this);
        if(running!=null)
            stopSelf();
        else{
            running = this;
            update();
            running = null;
            stopSelf();

        }
    return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        running = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static UpdateService getInstant(){
        return running;
    }

    public static void setUpdating(boolean updating) {
        UpdateService.updating = updating;
    }

    public static void stop(){
        running.stopSelf();
    }

    public void update(){
        if(!updating) {
            Notification.Builder mNotifyBuilder = null;
            boolean notif = Settings.getSettings().getNotificationUpdate();
            NotificationManager mNotificationManager = null;
            int notifyID = 1;
            if(!new NetworkUtil(getApplicationContext()).alowedToConect()){
                mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Sets an ID for the notification, so it can be updated

                mNotifyBuilder = new Notification.Builder(this)
                        .setContentTitle("Show Tracker Updating Failed")
                        .setContentText("Cant Use current connection.")
                        .setSmallIcon(R.drawable.ic_launcher);
                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());

                return;
            }
            updating = true;
            Log.d("Show Tracker", "starting update proses");
            List<Integer> idList = dbTools.getAllShowId();
            String[] idStrings = new String[idList.size()];
            for (int i = 0; i < idStrings.length; i++)
                idStrings[i] = idList.get(i).toString();


            if (notif) {
                mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Sets an ID for the notification, so it can be updated

                mNotifyBuilder = new Notification.Builder(this)
                        .setContentTitle("Show Tracker Updating")
                        .setContentText("Updating")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setOngoing(true);


                mNotificationManager.notify(
                        notifyID,
                        mNotifyBuilder.build());
            }
            (new GetShowInfo(getApplicationContext(), mNotifyBuilder, mNotificationManager, notifyID)).setUpdateEpisodes(true).execute(idStrings);

        }
    }
/*
    private class TimerTask extends java.util.TimerTask {
        private Context context;
        public TimerTask(Context context){
            this.context = context;
        }
        @Override
        public void run() {
            if(UpdateService.this!=null)
                update();
            else
                startService(new Intent(context,UpdateService.class));

        }
    }
*/
}
