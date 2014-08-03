package se.mitucha.showtracker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by 11011_000 on 2014-08-01.
 */
public class GetData {

    private enum SearchTag{ RESULTS, SHOW, SHOW_ID, NAME, LINK, COUNTRY, STARTED, ENDED, SEASONS, STATUS, CLASSIFICATION, GENRES, GENRE, NETWORK, AIR_TIME, AIR_DAY, AKAS, AKA, RUNTIME;
        public boolean equals(String otherTag){
            return (otherTag != null) && this.toString().equals(otherTag);
        }
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
        public static SearchTag getTag(String otherTag){
            if(otherTag == null)
                return null;
            for(SearchTag tag : SearchTag.values()){
                if(tag.equals(otherTag))
                    return tag;
            }
            return null;
        }
    }
/*
    private enum ShowInfoTag { RESULTS, SHOW_INFO, SHOW_ID, SHOW_NAME, SHOW_LINK, SEASONS, STARTED, START_DATE, ENDED, ORIGIN_COUNTRY, STATUS, CLASSIFICATION, GENRES, GENRE, RUN_TIME, NETWORK, COUNTRY, AIR_TIME, AIR_DAY, TIMEZONE, AKAS, AKA;

        public boolean equals(String otherTag){
            return (otherTag != null) && this.toString().equals(otherTag);
        }
        @Override
        public String toString(){
            switch(this){
                case RESULTS :
                    return "Results";
                case SHOW_INFO:
                    return "Showinfo";
                case SHOW_ID :
                    return "showid";
                case  SHOW_NAME :
                    return "showname";
                case  SHOW_LINK :
                    return "showlink";
                case  SEASONS :
                    return "seasons";
                case  STARTED:
                    return "started";
                case  START_DATE :
                    return "startdate";
                case  ENDED :
                    return "ended";
                case  ORIGIN_COUNTRY :
                    return "origin_country";
                case STATUS :
                    return "status";
                case CLASSIFICATION :
                    return "classification";
                case GENRES :
                    return "genres";
                case GENRE :
                    return "genre";
                case RUN_TIME :
                    return "runtime";
                case COUNTRY :
                    return "country";
                case NETWORK:
                    return "network";
                case AIR_TIME :
                    return "airtime";
                case AIR_DAY :
                    return "airday";
                case TIMEZONE :
                    return "timezone";
                case AKAS :
                    return "akas";
                case AKA :
                    return "aka";
            }
            return null;
        }
        public static ShowInfoTag getTag(String otherTag){
            if(otherTag == null)
                return null;
            for(ShowInfoTag tag : ShowInfoTag.values()){
                if(tag.equals(otherTag))
                    return tag;
            }
            return null;
        }
    }
*/
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
            String yqlURL = tvrage + params[0];

            ArrayList<ShowInfo> list = new ArrayList<ShowInfo>();
            try {

                Log.d("Show Tracker", "Starting Search");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                //InputStreamReader is = new InputStreamReader(getUrlData(yqlURL));
                InputStreamReader is = new InputStreamReader(downloadUrl(yqlURL));
                parser.setInput(is);
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
                                if (entery != null)
                                    list.add(entery);
                                Log.d("Show Tracker", "Adding now show");
                                entery = new ShowInfo();
                                break;
                            case NETWORK:
                                entery.setNetworkCountry(parser.getAttributeValue(0));
                                break;
                            case AKA:
                                akaCountry = parser.getText();
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
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<ShowInfo> searchEnteries) {
            mWakeLock.release();
            activity.updateResult(searchEnteries);
        }
    }
/*
    public class GetShowInfo extends AsyncTask<String, String, ShowInfo >{

        private DBTools db;

        private static final String tvrage = "http://services.tvrage.com/feeds/showinfo.php?sid=";

        public GetShowInfo setDB(DBTools db) {
            this.db = db;
            return this;
        }

        @Override
        protected ShowInfo doInBackground(String... params) {
            String yqlURL = tvrage + params[0];

            ArrayList<ShowInfo> list = new ArrayList<ShowInfo>();
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(getUrlData(yqlURL)));
                beginDocument(parser,"Results");
                int eventType = parser.getEventType();
                ShowInfo entery= null;
                do {
                    nextElement(parser);
                    parser.next();
                    ShowInfoTag tag = ShowInfoTag.getTag(parser.getName());
                    if(eventType == XmlPullParser.TEXT){
                        switch (tag){
                            case SHOW_ID :
                                entery.setId(Integer.parseInt(parser.getText()));
                                break;
                            case SHOW_NAME :
                                entery.setTitle(parser.getText());
                                break;
                            case STARTED :
                                entery.setStarted(Integer.parseInt(parser.getText()));
                                break;
                            case STATUS :
                                entery.setStatus(parser.getText());
                                break;
                            default:
                                break;
                        }
                    }else if(eventType == XmlPullParser.START_TAG){
                        switch (tag){
                            case SHOW_INFO:
                                if(entery !=null)
                                    list.add(entery);
                                entery = new ShowInfo();
                                break;
                            default:
                                break;
                        }
                    }
                }while( eventType != XmlPullParser.END_DOCUMENT);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return list.get(0);
        }

        @Override
        protected void onPostExecute(ShowInfo showInfo) {
            if(db.haveShowID(Integer.toString(showInfo.getId())))
                db.updateShow(showInfo);
            else
                db.insertShow(showInfo);

        }

    }
*/
    private static String getTextValue(Element entry, String tagName){

        String tagvalueToReturn = null;

        NodeList nl = entry.getElementsByTagName(tagName);

        if(nl != null && nl.getLength() > 0){

            Element element = (Element) nl.item(0);

            tagvalueToReturn = element.getFirstChild().getNodeValue();

        }

        return tagvalueToReturn;

    }

    private static InputStream downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Show Tracker", "The response is: " + response);
            return  conn.getInputStream();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static InputStream getUrlData(String url) throws URISyntaxException, ClientProtocolException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet method = new HttpGet(new URI(url));

        HttpResponse res = client.execute(method);
        return res.getEntity().getContent();
    }

    private static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException{
        int type;
        while((type=parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT){
            ;
        }
        if(type != parser.START_TAG) {
            throw new XmlPullParserException("No Start Tag Found");
        }
        if(!parser.getName().equals(firstElementName)){
            throw new XmlPullParserException("UnExpected Start Tag Found " + parser.getName() + ", expexted " + firstElementName);
        }
    }

    private static final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        while((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT ){
            ;
        }
    }



}
