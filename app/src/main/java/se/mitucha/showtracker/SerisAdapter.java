package se.mitucha.showtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by 11011_000 on 2014-07-31.
 */
public class SerisAdapter extends ArrayAdapter<ShowInfo> {

    public final static int ID_TAG = 0;

    public SerisAdapter(Context context, ShowInfo[] list) {
        super(context, R.layout.seris_row, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.seris_row,parent,false);

        ShowInfo tvShow = getItem(position);
        DBTools dbTools = new DBTools(getContext());
        TextView theTextView = (TextView) theView.findViewById(R.id.serisName);
        TextView statusTextView = (TextView) theView.findViewById(R.id.statusText);
        TextProgressBar bar = (TextProgressBar) theView.findViewById(R.id.serieViewProgressbar);
        Calendar calendar = Calendar.getInstance();
        calendar = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        String id = Integer.toString(tvShow.getId());
        int aired = dbTools.countEpisodesBy(id,calendar);
        int unaired = dbTools.countEpisodesFrom(id,calendar);
        int seen = dbTools.countSeen(id, true);
        bar.setVisibility(View.VISIBLE);
        bar.setAired(aired);
        bar.setSeen(seen);
        bar.setUnaired(unaired);
        bar.setMax(aired+unaired);
        bar.setSecondaryProgress(aired);
        bar.setProgress(seen);
        statusTextView.setText(tvShow.getStatus());
        theTextView.setText(tvShow.getTitle());


        theView.setTag(tvShow.getId());

        return theView;
    }
}
