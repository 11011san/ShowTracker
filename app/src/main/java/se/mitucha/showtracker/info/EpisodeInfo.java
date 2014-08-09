package se.mitucha.showtracker.info;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by mr11011 on 2014-08-03.
 */
public class EpisodeInfo implements Serializable {

    private int showId;
    private int epNum;
    private int seasonNum;
    private String prodNum;
    private Calendar airDate;
    private String link;
    private String title;
    private int season;
    private boolean seen;
    private ShowInfo show;

    public EpisodeInfo() {
        seen = false;
    }

    public EpisodeInfo(int showId, int epNum, int seasonNum, String prodNum, Calendar airDate, String link, String title, int season, boolean seen, ShowInfo show) {
        this.showId = showId;
        this.epNum = epNum;
        this.seasonNum = seasonNum;
        this.prodNum = prodNum;
        this.airDate = airDate;
        this.link = link;
        this.title = title;
        this.season = season;
        this.seen = seen;
        this.show = show;
    }

    public ShowInfo getShow() {
        return show;
    }

    public void setShow(ShowInfo show) {
        this.show = show;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getEpNum() {
        return epNum;
    }

    public void setEpNum(int epNum) {
        this.epNum = epNum;
    }

    public int getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(int seasonNum) {
        this.seasonNum = seasonNum;
    }

    public String getProdNum() {
        return prodNum;
    }

    public void setProdNum(String prodNum) {
        this.prodNum = prodNum;
    }

    public Calendar getAirDate() {
        return airDate;
    }

    public void setAirDate(Calendar airDate) {
        this.airDate = airDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
