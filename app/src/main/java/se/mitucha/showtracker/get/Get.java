package se.mitucha.showtracker.get;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mr11011 on 2014-08-06.
 */
public abstract class Get<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private static final String TAG = "Show Tracker Output";

    protected XmlPullParser getParser(String url) throws URISyntaxException, IOException, XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        DefaultHttpClient client = new DefaultHttpClient();
        Log.d("Show Tracker", "url = " + url);
        HttpGet method = new HttpGet(new URI(url));

        HttpResponse res = client.execute(method);
        InputStreamReader is = new InputStreamReader(res.getEntity().getContent());
        if(false) {
            HttpResponse res1 = client.execute(method);
            InputStream in = res1.getEntity().getContent();
            Log.e(TAG, ">>>>>PRINTING<<<<<");
            Log.e(TAG, in.toString());
            Log.e(TAG, convertStreamToString(in));
        }
        parser.setInput(is);
        return parser;
    }
    private static String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

}
