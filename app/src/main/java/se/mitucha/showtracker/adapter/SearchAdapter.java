package se.mitucha.showtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.mitucha.showtracker.R;
import se.mitucha.showtracker.info.ShowInfo;

/**
 * Created by 11011_000 on 2014-07-31.
 */
public class SearchAdapter extends ArrayAdapter<ShowInfo> {

    public SearchAdapter(Context context, ShowInfo[] list) {
        super(context, R.layout.search_row, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.search_row, parent, false);

        ShowInfo tvShow = getItem(position);

        TextView title = (TextView) theView.findViewById(R.id.title);
        TextView started = (TextView) theView.findViewById(R.id.started);
        TextView status = (TextView) theView.findViewById(R.id.status);

        title.setText(tvShow.getTitle());
        started.setText("(" + tvShow.getStarted() + ")");
        status.setText(tvShow.getStatus());

        theView.setTag(tvShow.getId());

        return theView;
    }
}
