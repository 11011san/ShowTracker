package se.mitucha.showtracker.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import se.mitucha.showtracker.service.UpdateService;

/**
 * Created by mr11011 on 2014-08-09.
 */
public class BroadcastSync extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UpdateService.class));
    }
}
