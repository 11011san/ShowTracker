package se.mitucha.showtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import se.mitucha.showtracker.R;
import se.mitucha.showtracker.info.ShowInfo;
import se.mitucha.showtracker.util.DBTools;
import se.mitucha.showtracker.view.TextProgressBar;

/**
 * Created by 11011_000 on 2014-07-31.
 */
public class SerisAdapter extends ArrayAdapter<ShowInfo> {

    public final static int ID_TAG = 0;
    private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;

    public SerisAdapter(Context context, ShowInfo[] list) {
        super(context, R.layout.seris_row, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.seris_row, parent, false);

        ShowInfo tvShow = getItem(position);
        DBTools dbTools = new DBTools(getContext());
        TextView theTextView = (TextView) theView.findViewById(R.id.serisName);
        TextView statusTextView = (TextView) theView.findViewById(R.id.statusText);
        TextProgressBar bar = (TextProgressBar) theView.findViewById(R.id.serieViewProgressbar);
        Calendar calendar = Calendar.getInstance();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        String id = Integer.toString(tvShow.getId());
        int aired = dbTools.countEpisodesBy(id, calendar);
        int unaired = dbTools.countEpisodesFrom(id, calendar);
        int seen = dbTools.countSeen(id, true);
        bar.setVisibility(View.VISIBLE);
        bar.setAired(aired);
        bar.setSeen(seen);
        bar.setUnaired(unaired);
        bar.setMax(aired + unaired);
        bar.setSecondaryProgress(aired);
        bar.setProgress(seen);
        String status = "";
        Calendar last = dbTools.latestEpisode(tvShow.getId(), calendar);
        Calendar next = dbTools.nextEpisode(tvShow.getId(), calendar);
        if (last != null)
            status = " Last: " + daysBetween(last, calendar) + " days ago |";
        if (next != null)
            status = status + " Next: " + daysBetween(next, calendar) + " days left";
        else
            status = status + " Status: " + tvShow.getStatus();
        statusTextView.setText(status);
        theTextView.setText(tvShow.getTitle());


        theView.setTag(tvShow.getId());

        return theView;
    }

    private int daysBetween(Calendar start, Calendar end) {
        long sm = start.getTimeInMillis();
        long em = end.getTimeInMillis();
        return (int) (Math.abs(sm - em) / MILLISECS_PER_DAY);
    }
}
