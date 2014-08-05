package se.mitucha.showtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mr11011 on 2014-08-04.
 */
public class EpisodeAdapter extends ArrayAdapter<EpisodeInfo> {

    public EpisodeAdapter(Context context, EpisodeInfo[] list) {
        super(context, R.layout.episode_row, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.episode_row,parent,false);

        EpisodeInfo ep = getItem(position);

        TextView title = (TextView) theView.findViewById(R.id.show_name);
        TextView info = (TextView) theView.findViewById(R.id.episodeInfo);
        ImageView seen = (ImageView) theView.findViewById(R.id.seen);
        title.setText(ep.getShow().getTitle());
        String infoText = ep.getSeason() + "S" + String.format("%02d", ep.getSeasonNum()) + "E - " + ep.getTitle();
        info.setText(infoText);
        if(ep.isSeen())
            seen.setVisibility(View.VISIBLE);

        theView.setTag(Integer.toString(ep.getShowId())+"-"+Integer.toString(ep.getEpNum()));

        return theView;
    }
}

