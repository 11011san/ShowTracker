package se.mitucha.showtracker.get;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by mr11011 on 2014-08-06.
 */
public enum SearchTag {
    RESULTS, SHOW, SHOW_ID, NAME, LINK, COUNTRY, STARTED, ENDED, SEASONS, STATUS, CLASSIFICATION, GENRES, GENRE, NETWORK, AIR_TIME, AIR_DAY, AKAS, AKA, RUNTIME;
    private static HashMap<String, SearchTag> map = null;

    private static HashMap<String, SearchTag> getMap() {
        if (map == null) {
            map = new HashMap<String, SearchTag>();
            for (SearchTag tag : SearchTag.values())
                map.put(tag.toString(), tag);
        }
        return map;
    }

    public static SearchTag getTag(String otherTag) {
        SearchTag tag = getMap().get(otherTag.trim());
        if(tag == null)
            Log.e("Show Tracker", "cant find SearchTag : " + otherTag);
        return tag;
    }

    public boolean equals(String otherTag) {
        return (otherTag != null) && this.toString().equals(otherTag);
    }

    @Override
    public String toString() {
        switch (this) {
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
}

