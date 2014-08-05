package se.mitucha.showtracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 11011_000 on 2014-08-01.
 */
public class GetData {

    private enum SearchTag{ RESULTS, SHOW, SHOW_ID, NAME, LINK, COUNTRY, STARTED, ENDED, SEASONS, STATUS, CLASSIFICATION, GENRES, GENRE, NETWORK, AIR_TIME, AIR_DAY, AKAS, AKA, RUNTIME;
        public boolean equals(String otherTag){
            return (otherTag != null) && this.toString().equals(otherTag);
        }
        private static HashMap<String,SearchTag> map = null;
        @Override
        public String toString(){
            switch(this) {
                case RESULTS:
                    return "Results";
                case SHOW:
                    return "show";
                case SHOW_ID:
                    return "showid";
                case NAME:
                    return "name";
                case LINK:
                    return "link";
                case COUNTRY:
                    return "country";
                case STARTED:
                    return "started";
                case ENDED:
                    return "ended";
                case SEASONS:
                    return "seasons";
                case STATUS:
                    return "status";
                case CLASSIFICATION:
                    return "classification";
                case GENRES:
                    return "genres";
                case GENRE:
                    return "genre";
                case NETWORK:
                    return "network";
                case AIR_TIME:
                    return "airtime";
                case AIR_DAY:
                    return "airday";
                case AKAS:
                    return "akas";
                case AKA:
                    return "aka";
                case RUNTIME:
                    return "runtime";
            }
            return null;
        }
        private static HashMap<String, SearchTag> getMap(){
            if(map==null) {
                map = new HashMap<String, SearchTag>();
                for (SearchTag tag : SearchTag.values())
                    map.put(tag.toString(), tag);
            }
            return map;
        }

        public static SearchTag getTag(String otherTag){
            return getMap().get(otherTag.trim());
        }
    }

    private enum EpisodeTag { SHOW, NAME, TOTAL_SEASONS, EPISODE_LIST, SEASON, EPISODE, EP_NUM, SEASON_NUM, PROD_NUM, AIR_DATE, LINK, TITLE, NO;

        private static HashMap<String, EpisodeTag> map = null;

        public boolean equals(String otherTag){
            return (otherTag != null) && this.toString().equals(otherTag);
        }
        @Override
        public String toString() {
            switch (this) {
                case SHOW:
                    return "Show";
                case NAME:
                    return "name";
                case TOTAL_SEASONS:
                    return "totalseasons";
                case EPISODE_LIST:
                    return "Episodelist";
                case SEASON:
                    return "Season";
                case EPISODE:
                    return "episode";
                case EP_NUM:
                    return "epnum";
                case SEASON_NUM:
                    return "seasonnum";
                case PROD_NUM:
                    return "prodnum";
                case AIR_DATE:
                    return "airdate";
                case LINK:
                    return "link";
                case TITLE:
                    return "title";
                case NO:
                    return "no";
            }
            return null;
        }

        private static HashMap<String, EpisodeTag> getMap(){
            if(map==null) {
                map = new HashMap<String, EpisodeTag>();
                for (EpisodeTag tag : EpisodeTag.values())
                    map.put(tag.toString(), tag);
            }
            return map;
        }

        public static EpisodeTag getTag(String otherTag){
            return getMap().get(otherTag.trim());
        }
    }

    public static class DoSearch extends AsyncTask<String, String, List<ShowInfo>> {

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

    public static class GetEpisodeInfo extends AsyncTask<String, String, List<EpisodeInfo> >{

        public GetEpisodeInfo(Context activity, ProgressDialog dialog) {
            this.activity = activity;
            this.dialog = dialog;
        }

        private ProgressDialog dialog;
        private Context activity;
        private PowerManager.WakeLock mWakeLock;
        private static final String tvrage = "http://services.tvrage.com/feeds/episode_list.php?sid=";

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if(dialog != null)
                dialog.setMessage(values[0]);
        }

        protected void onPreExecute() {
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected List<EpisodeInfo> doInBackground(String... params) {


            ArrayList<EpisodeInfo> list = new ArrayList<EpisodeInfo>();
            for (String id : params){
                try {
                    String yqlURL = tvrage + URLEncoder.encode(id.trim(), "UTF-8");
                    Log.d("Show Tracker", "Starting ep get");
                    XmlPullParser parser = getParser(yqlURL);
                    int eventType = parser.getEventType();
                    EpisodeInfo entery = null;
                    EpisodeTag tag = null;
                    int season = 0;
                    do {
                        if (isCancelled())
                            return list;
                        if (eventType == XmlPullParser.TEXT) {
                            if (tag != null && !parser.getText().equals("\n"))
                                switch (tag) {
                                    case NAME:
                                        publishProgress(parser.getText());
                                        break;
                                    case EP_NUM:
                                        entery.setEpNum(Integer.parseInt(parser.getText()));
                                        break;
                                    case SEASON_NUM:
                                        entery.setSeasonNum(Integer.parseInt(parser.getText()));
                                        break;
                                    case PROD_NUM:
                                        entery.setProdNum(parser.getText());
                                        break;
                                    case AIR_DATE:
                                        Calendar date = new GregorianCalendar();
                                        date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(parser.getText()));
                                        entery.setAirDate(date);
                                        break;
                                    case LINK:
                                        entery.setLink(parser.getText());
                                        break;
                                    case TITLE:
                                        entery.setTitle(parser.getText());
                                        break;
                                    default:
                                        break;
                                }
                        } else if (eventType == XmlPullParser.START_TAG) {
                            tag = EpisodeTag.getTag(parser.getName());
                            if(tag!=null)
                                switch (tag) {
                                    case SEASON:
                                        season = Integer.parseInt(parser.getAttributeValue(null,EpisodeTag.NO.toString()));
                                        break;
                                    case EPISODE:
                                        entery = new EpisodeInfo();
                                        entery.setSeason(season);
                                        entery.setShowId(Integer.parseInt(id));
                                        break;
                                    default:
                                        break;
                                }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            tag = EpisodeTag.getTag(parser.getName());
                            if(tag!=null)
                                switch (tag) {
                                    case EPISODE:
                                        list.add(entery);
                                        Log.d("Show Tracker", "Adding now ep");
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
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<EpisodeInfo> episodeList) {
            mWakeLock.release();
            DBTools db = new DBTools(activity);
            db.updateEpisode(episodeList,false);
        }
    }

    private static final String TAG = "Show Tracker Output";


    private static XmlPullParser getParser(String url) throws URISyntaxException, IOException, XmlPullParserException {
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
