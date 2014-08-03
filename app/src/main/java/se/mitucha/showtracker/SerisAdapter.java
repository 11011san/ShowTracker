package se.mitucha.showtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        TextView theTextView = (TextView) theView.findViewById(R.id.serisName);

        theTextView.setText(tvShow.getTitle());

        theView.setTag(tvShow.getId());

        return theView;
    }
}
