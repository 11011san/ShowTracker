package se.mitucha.showtracker.get;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import se.mitucha.showtracker.activity.SearchActivity;
import se.mitucha.showtracker.info.ShowInfo;

/**
 * Created by mr11011 on 2014-08-06.
 */
public class DoSearch extends Get<String, String, List<ShowInfo>>  {
//AsyncTask<String, String, List<ShowInfo>> {

    public DoSearch(SearchActivity activity) {
        this.activity = activity;

    }

    private SearchActivity activity;
    private PowerManager.WakeLock mWakeLock;
    private static final String tvrage = "http://services.tvrage.com/feeds/full_search.php?show=";


    protected void onPreExecute() {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected List<ShowInfo> doInBackground(String... params) {

        ArrayList<ShowInfo> list = new ArrayList<ShowInfo>();
        try {
            String yqlURL = tvrage + URLEncoder.encode(params[0].trim(), "UTF-8");
            Log.d("Show Tracker", "Starting Search");
            XmlPullParser parser = getParser(yqlURL);
            int eventType = parser.getEventType();
            ShowInfo entery = null;
            SearchTag tag = null;
            String akaCountry = null;
            do {
                if (isCancelled())
                    return list;
                if (eventType == XmlPullParser.TEXT) {
                    if (tag != null && !parser.getText().equals("\n"))
                        switch (tag) {
                            case SHOW_ID:
                                entery.setId(Integer.parseInt(parser.getText()));
                                break;
                            case NAME:
                                entery.setTitle(parser.getText());
                                break;
                            case STARTED:
                                String started = parser.getText();
                                try {
                                    if (started.length() > 8) {
                                        Calendar date = new GregorianCalendar();
                                        date.setTime(new SimpleDateFormat("MMM/dd/yyyy").parse(started));
                                        entery.setStartdate(date);
                                        entery.setStarted(date.get(Calendar.YEAR));
                                    } else if (started.length() > 4) {
                                        Calendar date = new GregorianCalendar();
                                        date.setTime(new SimpleDateFormat("MMM/yyyy").parse(started));
                                        entery.setStartdate(date);
                                        entery.setStarted(date.get(Calendar.YEAR));
                                    } else if (started.length() == 4) {
                                        entery.setStarted(Integer.parseInt(started));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case STATUS:
                                entery.setStatus(parser.getText());
                                break;
                            case AKA:
                                entery.addAka(akaCountry, parser.getText());
                                break;
                            case AIR_DAY:
                                entery.setAirDay(parser.getText());
                                break;
                            case AIR_TIME:
                                entery.setAirTime(parser.getText());
                                break;
                            case CLASSIFICATION:
                                entery.setClassification(parser.getText());
                                break;
                            case COUNTRY:
                                entery.setCountry(parser.getText());
                                break;
                            case ENDED:
                                String ended = parser.getText();
                                try {
                                    if (ended == null || ended.length() == 0) {
                                        entery.setEnded(null);
                                    }
                                    if (ended.length() > 8) {
                                        Calendar date = new GregorianCalendar();
                                        date.setTime(new SimpleDateFormat("MMM/dd/yyyy").parse(ended));
                                        entery.setEnded(date);
                                    } else if (ended.length() > 4) {
                                        Calendar date = new GregorianCalendar();
                                        date.setTime(new SimpleDateFormat("MMM/yyyy").parse(ended));
                                        entery.setEnded(date);
                                    } else if (ended.length() == 4) {
                                        Calendar date = new GregorianCalendar();
                                        date.setTime(new SimpleDateFormat("yyyy").parse(ended));
                                        entery.setEnded(date);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case NETWORK:
                                entery.setNetwork(parser.getText());
                                break;
                            case GENRE:
                                entery.addGenre(parser.getText());
                                break;
                            case LINK:
                                entery.setLink(parser.getText());
                                break;
                            case SEASONS:
                                entery.setSeasons(Integer.parseInt(parser.getText()));
                                break;
                            case RUNTIME:
                                entery.setRuntime(Integer.parseInt(parser.getText()));

                            default:
                                break;
                        }
                } else if (eventType == XmlPullParser.START_TAG) {
                    tag = SearchTag.getTag(parser.getName());
                    switch (tag) {
                        case SHOW:
                            entery = new ShowInfo();
                            break;
                        case NETWORK:
                            entery.setNetworkCountry(parser.getAttributeValue(null,SearchTag.COUNTRY.toString()));
                            break;
                        case AKA:
                            akaCountry = parser.getText();
                            break;
                        default:
                            break;
                    }
                } else if (eventType == XmlPullParser.END_TAG){
                    tag = SearchTag.getTag(parser.getName());
                    switch (tag) {
                        case SHOW:
                            list.add(entery);
                            Log.d("Show Tracker", "Adding now show");
                            break;
                        default:
                            break;
                    }
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<ShowInfo> searchEnteries) {
        mWakeLock.release();
        activity.updateResult(searchEnteries);
    }
}

