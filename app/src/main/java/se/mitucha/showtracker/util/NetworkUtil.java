package se.mitucha.showtracker.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mr11011 on 2014-08-09.
 */
public class NetworkUtil {

    private Context context;

    public NetworkUtil(Context context) {
        this.context = context;
    }

    public boolean onMobile() {
        return onStat( ConnectivityManager.TYPE_MOBILE );
    }

    public boolean isRoaming() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getNetworkInfo( ConnectivityManager.TYPE_MOBILE );
        if (netInfo != null && netInfo.isRoaming()) {
            return true;
        }
        return false;
    }

    public boolean onWiFi() {
        return onStat( ConnectivityManager.TYPE_WIFI );
    }

    public boolean onEthernet() {
        return onStat( ConnectivityManager.TYPE_ETHERNET );
    }

    private boolean onStat(int type){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getNetworkInfo( type );
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private static final int MOBILE = 1;
    private static final int ROMING = 2;
    private static final int WIFI = 4;
    private static final int ETHERNET = 8;

    public boolean alowedToConect(){
        int method = Settings.getSettings().getConnectionMethod();
        boolean conection = false;
        if(onMobile())
            if(isRoaming())
                conection = (method & (ROMING|MOBILE))!=0;
            else
                conection = (method & MOBILE)!=0;
        else if(onWiFi())
            conection = (method & WIFI)!=0;
        else if(onEthernet())
            conection = (method & ETHERNET)!=0;
        return conection;
    }

}
