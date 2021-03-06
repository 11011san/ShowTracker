package se.mitucha.showtracker.get;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by mr11011 on 2014-08-06.
 */
public enum EpisodeTag {
    SHOW, NAME, TOTAL_SEASONS, EPISODE_LIST, SEASON, EPISODE, EP_NUM, SEASON_NUM, PROD_NUM, AIR_DATE, LINK, TITLE, NO, SPECIAL, SPECIAL_SEASON;

    private static HashMap<String, EpisodeTag> map = null;

    private static HashMap<String, EpisodeTag> getMap() {
        if (map == null) {
            map = new HashMap<String, EpisodeTag>();
            for (EpisodeTag tag : EpisodeTag.values())
                map.put(tag.toString(), tag);
        }
        return map;
    }

    public static EpisodeTag getTag(String otherTag) {
        EpisodeTag tag = getMap().get(otherTag.trim());
        if(tag == null)
            Log.e("Show Tracker", "cant find EpisodeTag : " + otherTag);
        return tag;
    }

    public boolean equals(String otherTag) {
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
            case SPECIAL:
                return "Special";
            case SPECIAL_SEASON:
                return "season";
        }
        return null;
    }
}

