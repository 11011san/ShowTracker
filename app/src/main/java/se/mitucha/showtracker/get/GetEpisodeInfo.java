package se.mitucha.showtracker.get;

import android.app.ProgressDialog;
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
import se.mitucha.showtracker.util.DBTools;

/**
 * Created by mr11011 on 2014-08-06.
 */
public class GetEpisodeInfo extends Get<String, String, List<EpisodeInfo>> {

    private static final String tvrage = "http://services.tvrage.com/feeds/episode_list.php?sid=";
    private ProgressDialog dialog;
    private Context activity;
    private PowerManager.WakeLock mWakeLock;
    public GetEpisodeInfo(Context activity, ProgressDialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (dialog != null)
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
        Log.d("ShowInfo Tracker","starting get episode");
        for (String id : params) {
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
                        String text = parser.getText().replace("\n","").trim();
                        if (tag != null && !text.equals(""))
                            switch (tag) {
                                case NAME:
                                    publishProgress(text);
                                    break;
                                case EP_NUM:
                                    entery.setEpNum(Integer.parseInt(text));
                                    break;
                                case SEASON_NUM:
                                    entery.setSeasonNum(Integer.parseInt(text));
                                    break;
                                case PROD_NUM:
                                    entery.setProdNum(text);
                                    break;
                                case AIR_DATE:
                                    Calendar date = new GregorianCalendar();
                                    date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(text));
                                    entery.setAirDate(date);
                                    break;
                                case LINK:
                                    entery.setLink(text);
                                    break;
                                case TITLE:
                                    entery.setTitle(text);
                                    break;
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
                                    entery = new EpisodeInfo();
                                    entery.setSeason(season);
                                    entery.setShowId(Integer.parseInt(id));
                                    break;
                                default:
                                    break;
                            }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        tag = EpisodeTag.getTag(parser.getName());
                        if (tag != null)
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
        Log.d("ShowInfo Tracker","ending get episode");
        return list;
    }

    @Override
    protected void onPostExecute(List<EpisodeInfo> episodeList) {
        Log.d("ShowInfo Tracker","post get episode");
        mWakeLock.release();
        DBTools db = new DBTools(activity);
        db.updateEpisode(episodeList, false);
    }
}

