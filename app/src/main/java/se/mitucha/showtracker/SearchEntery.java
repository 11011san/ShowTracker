package se.mitucha.showtracker;

/**
 * Created by 11011_000 on 2014-07-31.
 */
public class SearchEntery {
    private int id;
    private String title;
    private int started;
    private String status;

    public SearchEntery(int id, String title, int started, String status) {
        this.id = id;
        this.title = title;
        this.started = started;
        this.status = status;
    }

    public SearchEntery() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
