package se.mitucha.showtracker.get;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by mr11011 on 2014-08-06.
 */
public enum ShowInfoTag {
    SHOW_INFO, SHOW_ID, NAME, LINK, COUNTRY, STARTED, ENDED, SEASONS, STATUS, CLASSIFICATION, GENRES, GENRE, NETWORK, AIR_TIME, AIR_DAY, AKAS, AKA, RUNTIME, ORIGIN_COUNTRY, TIMEZONE, START_DATE;
    private static HashMap<String, ShowInfoTag> map = null;

    private static HashMap<String, ShowInfoTag> getMap() {
        if (map == null) {
            map = new HashMap<String, ShowInfoTag>();
            for (ShowInfoTag tag : ShowInfoTag.values())
                map.put(tag.toString(), tag);
        }
        return map;
    }

    public static ShowInfoTag getTag(String otherTag)
    {
        ShowInfoTag tag = getMap().get(otherTag.trim());
        if(tag == null)
            Log.e("Show Tracker" , "cant find ShowInfoTag : " + otherTag);
        return tag;
    }

    public boolean equals(String otherTag) {
        return (otherTag != null) && this.toString().equals(otherTag);
    }

    @Override
    public String toString() {
        switch (this) {
            case SHOW_INFO:
                return "Showinfo";
            case SHOW_ID:
                return "showid";
            case NAME:
                return "showname";
            case LINK:
                return "showlink";
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
            case ORIGIN_COUNTRY:
                return "origin_country";
            case TIMEZONE:
                return "timezone";
            case START_DATE:
                return "startdate";
        }
        return null;
    }
}

