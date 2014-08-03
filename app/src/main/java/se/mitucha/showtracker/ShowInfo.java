package se.mitucha.showtracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 11011_000 on 2014-07-31.
 */
public class ShowInfo {

    private String title;
    private int id;
    private int started;
    private Calendar startdate;
    private String classification;
    private int runtime;
    private String status;
    private String link;
    private String country;
    private int seasons;
    private ArrayList<String> genres;
    private String network;
    private String networkCountry;
    private String airTime;
    private String airDay;
    private HashMap<String,String> akas;
    private Calendar ended;

    public ShowInfo() {
        akas = new HashMap<String, String>();
        genres = new ArrayList<String>();

    }

    public ShowInfo(String title, int id, int started, Calendar startdate, String classification, int runtime, String status, String link, String country, int seasons, ArrayList<String> genres, String network, String networkCountry, String airTime, String airDay, HashMap<String, String> akas,Calendar ended) {
        this.title = title;
        this.id = id;
        this.started = started;
        this.startdate = startdate;
        this.classification = classification;
        this.runtime = runtime;
        this.status = status;
        this.link = link;
        this.country = country;
        this.seasons = seasons;
        this.genres = genres;
        this.network = network;
        this.networkCountry = networkCountry;
        this.airTime = airTime;
        this.airDay = airDay;
        this.akas = akas;
        this.ended = ended;
    }

    public Calendar getEnded() {
        return ended;
    }

    public void setEnded(Calendar ended) {
        this.ended = ended;
    }

    public void addGenre(String genre){
        genres.add(genre);
    }

    public void addAka(String country, String name) {
        akas.put(country,name);
    }

    public String getAka(String country){
        return akas.get(country);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getSeasons() {
        return seasons;
    }

    public void setSeasons(int seasons) {
        this.seasons = seasons;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getNetworkCountry() {
        return networkCountry;
    }

    public void setNetworkCountry(String networkCountry) {
        this.networkCountry = networkCountry;
    }

    public String getAirTime() {
        return airTime;
    }

    public void setAirTime(String airTime) {
        this.airTime = airTime;
    }

    public String getAirDay() {
        return airDay;
    }

    public void setAirDay(String airDay) {
        this.airDay = airDay;
    }

    public HashMap<String, String> getAkas() {
        return akas;
    }

    public void setAkas(HashMap<String, String> akas) {
        this.akas = akas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }

    public Calendar getStartdate() {
        return startdate;
    }

    public void setStartdate(Calendar startdate) {
        this.startdate = startdate;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
