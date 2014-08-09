package se.mitucha.showtracker.get;

import android.app.Notification;
import android.app.NotificationManager;
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

import se.mitucha.showtracker.info.EpisodeInfo;
import se.mitucha.showtracker.info.ShowInfo;
import se.mitucha.showtracker.service.UpdateService;
import se.mitucha.showtracker.util.DBTools;

/**
 * Created by mr11011 on 2014-08-06.
 */
public class GetShowInfo extends Get<String, String, List<ShowInfo>> {


    private static final String TVRAGE = "http://services.tvrage.com/";
    private static final String SHOW_INFO = "feeds/showinfo.php?sid=";
    private static final String EPISODE_INFO = "feeds/episode_list.php?sid=" ;
    private Context activity;
    private PowerManager.WakeLock mWakeLock;
    private Notification.Builder notification;
    private NotificationManager notificationManager;
    private int notifiID;
    private boolean updateEpisods;
    private int total;
    private int curently;
    private DBTools dbTools;

    public GetShowInfo(Context activity, Notification.Builder notification, NotificationManager notificationManager , int notifiID) {
        this.activity = activity;
        this.notification = notification;
        updateEpisods = false;
        this.notificationManager = notificationManager;
        this.notifiID = notifiID;
    }

    public GetShowInfo setUpdateEpisodes(boolean updateEpisods){
        this.updateEpisods = updateEpisods;
        return this;
    }

    protected void onPreExecute() {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        DBTools.resetEpisodeUpatedCounter();
        dbTools = new DBTools(activity);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (notification != null) {
            Log.d("ShowInfo Tracker","updating notification");
            notification.setContentText("Updating : " + values[0]);
            notification.setProgress(total,curently,false);
            notificationManager.notify(notifiID,notification.build());
        }
    }

    @Override
    protected List<ShowInfo> doInBackground(String... params) {

        ArrayList<ShowInfo> list = new ArrayList<ShowInfo>();
        total = params.length;
        curently = 1;
        for (String id : params) {
            try {
                String yqlURL = TVRAGE + SHOW_INFO + URLEncoder.encode(id.trim(), "UTF-8");
                Log.d("Show Tracker", "Starting get Shows");
                XmlPullParser parser = getParser(yqlURL);
                int eventType = parser.getEventType();
                ShowInfo entry = null;
                ShowInfoTag tag = null;
                String akaCountry = null;
                do {
                    if (isCancelled())
                        return list;
                    if (eventType == XmlPullParser.TEXT) {
                        String text = parser.getText().replace("\n","").trim();
                        if (tag != null && !text.equals(""))
                            switch (tag) {
                                case SHOW_ID:
                                    entry.setId(Integer.parseInt(text));
                                    break;
                                case NAME:
                                    entry.setTitle(text);
                                    publishProgress(text);
                                    break;
                                case STARTED:
                                case START_DATE:
                                    String started = text;
                                    try {
                                        if (started.length() > 8) {
                                            Calendar date = new GregorianCalendar();
                                            date.setTime(new SimpleDateFormat("MMM/dd/yyyy").parse(started));
                                            entry.setStartdate(date);
                                            entry.setStarted(date.get(Calendar.YEAR));
                                        } else if (started.length() > 4) {
                                            Calendar date = new GregorianCalendar();
                                            date.setTime(new SimpleDateFormat("MMM/yyyy").parse(started));
                                            entry.setStartdate(date);
                                            entry.setStarted(date.get(Calendar.YEAR));
                                        } else if (started.length() == 4) {
                                            entry.setStarted(Integer.parseInt(started));
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case STATUS:
                                    entry.setStatus(text);
                                    break;
                                case AKA:
                                    entry.addAka(akaCountry, text);
                                    break;
                                case AIR_DAY:
                                    entry.setAirDay(text);
                                    break;
                                case AIR_TIME:
                                    entry.setAirTime(text);
                                    break;
                                case CLASSIFICATION:
                                    entry.setClassification(text);
                                    break;
                                case ORIGIN_COUNTRY:
                                    entry.setCountry(text);
                                    break;
                                case ENDED:
                                    String ended = text;
                                    try {
                                        if (ended == null || ended.length() == 0) {
                                            entry.setEnded(null);
                                        }
                                        if (ended.length() > 8) {
                                            Calendar date = new GregorianCalendar();
                                            date.setTime(new SimpleDateFormat("MMM/dd/yyyy").parse(ended));
                                            entry.setEnded(date);
                                        } else if (ended.length() > 4) {
                                            Calendar date = new GregorianCalendar();
                                            date.setTime(new SimpleDateFormat("MMM/yyyy").parse(ended));
                                            entry.setEnded(date);
                                        } else if (ended.length() == 4) {
                                            Calendar date = new GregorianCalendar();
                                            date.setTime(new SimpleDateFormat("yyyy").parse(ended));
                                            entry.setEnded(date);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case NETWORK:
                                    entry.setNetwork(text);
                                    break;
                                case GENRE:
                                    entry.addGenre(text);
                                    break;
                                case LINK:
                                    entry.setLink(text);
                                    break;
                                case SEASONS:
                                    entry.setSeasons(Integer.parseInt(text));
                                    break;
                                case RUNTIME:
                                    entry.setRuntime(Integer.parseInt(text));

                                default:
                                    break;
                            }
                    } else if (eventType == XmlPullParser.START_TAG) {
                        tag = ShowInfoTag.getTag(parser.getName());
                        if(tag!=null)
                            switch (tag) {
                                case SHOW_INFO:
                                    entry = new ShowInfo();
                                    break;
                                case NETWORK:
                                    entry.setNetworkCountry(parser.getAttributeValue(null, SearchTag.COUNTRY.toString()));
                                    break;
                                case AKA:
                                    akaCountry = parser.getAttributeValue(null, ShowInfoTag.COUNTRY.toString());
                                    break;
                                default:
                                    break;
                            }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        tag = ShowInfoTag.getTag(parser.getName());
                        if(tag!=null)
                            switch (tag) {
                                case SHOW_INFO:
                                    list.add(entry);
                                    curently++;
                                    Log.d("Show Tracker", "Adding now show");
                                    if(updateEpisods)
                                        getEpisodeList(Integer.toString(entry.getId()));
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
        }
        Log.d("ShowInfo Tracker","ending get show");
        return list;
    }

    private void getEpisodeList(String id){

        ArrayList<EpisodeInfo> list = new ArrayList<EpisodeInfo>();
        Log.d("ShowInfo Tracker","starting get episode");
        try {
            String yqlURL = TVRAGE + EPISODE_INFO + URLEncoder.encode(id.trim(), "UTF-8");
            Log.d("Show Tracker", "Starting ep get");
            XmlPullParser parser = getParser(yqlURL);
            int eventType = parser.getEventType();
            EpisodeInfo entry = null;
            EpisodeTag tag = null;
            int season = 0;
            int spNum = -10000;
            do {
                if (isCancelled()) {
                    addEpisodeToDB(list);
                    return;
                }
                if (eventType == XmlPullParser.TEXT) {
                    String text = parser.getText().replace("\n","").trim();
                    if (tag != null && ! text.equals(""))
                        switch (tag) {
                            case NAME:
                                publishProgress(text);
                                break;
                            case EP_NUM:
                                entry.setEpNum(Integer.parseInt(text));
                                break;
                            case SEASON_NUM:
                                entry.setSeasonNum(Integer.parseInt(text));
                                break;
                            case PROD_NUM:
                                entry.setProdNum(text);
                                break;
                            case AIR_DATE:
                                Calendar date = new GregorianCalendar();
                                date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(text));
                                entry.setAirDate(date);
                                break;
                            case LINK:
                                entry.setLink(text);
                                break;
                            case TITLE:
                                entry.setTitle(text);
                                break;
                            case SPECIAL_SEASON:
                                entry.setSeasonNum(Integer.parseInt(text));
                                entry.setEpNum(spNum);
                                spNum++;
                            default:
                                break;
                        }
                } else if (eventType == XmlPullParser.START_TAG) {
                    tag = EpisodeTag.getTag(parser.getName());
                    if (tag != null)
                        switch (tag) {
                            case SEASON:
                                season = Integer.parseInt(parser.getAttributeValue(null, EpisodeTag.NO.toString()));
                                break;
                            case EPISODE:
                                entry = new EpisodeInfo();
                                entry.setSeason(season);
                                entry.setShowId(Integer.parseInt(id));
                                break;
                            case SPECIAL:
                                season = 0;
                                break;
                            default:
                                break;
                        }
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = EpisodeTag.getTag(parser.getName());
                    if (tag != null)
                        switch (tag) {
                            case EPISODE:
                                list.add(entry);
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

        Log.d("ShowInfo Tracker","ending get episode");
        addEpisodeToDB(list);
        return;
    }


    private void addEpisodeToDB(List<EpisodeInfo> episodeList) {
        Log.d("ShowInfo Tracker","post get episode");
        dbTools.updateEpisode(episodeList, false);
    }





    @Override
    protected void onPostExecute(List<ShowInfo> searchEnteries) {
        Log.d("ShowInfo Tracker","post prosesing of get show");
        mWakeLock.release();
        DBTools dbTools = new DBTools(activity);
        dbTools.updateShow(searchEnteries);
        if (notification != null) {
            if(isCancelled())
                notification.setContentText("Update Cancelled : found " + DBTools.getEpisodeUpatedCounter() + " new Episodes");
            else
                notification.setContentText("Update Complete : found " + DBTools.getEpisodeUpatedCounter() + " new Episodes");
            notification.setOngoing(false);
            notification.setProgress(0,0,false);
            notificationManager.notify(notifiID,notification.build());
            UpdateService.setUpdating(false);
        }
    }
}

