package se.mitucha.showtracker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SeasonListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<List<EpisodeInfo>> seasonLists;
    private DBTools dbTools;
    public SeasonListAdapter(Activity context, List<List<EpisodeInfo>> seasonLists) {
        this.context = context;
        this.seasonLists = seasonLists;
        dbTools = new DBTools(context);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return seasonLists.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        EpisodeInfo episodeInfo = (EpisodeInfo) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.season_episode_row, null);
        }
        TextView episodeNumberText = (TextView) convertView.findViewById(R.id.episodeNumberText);
        TextView episodeTitleText = (TextView) convertView.findViewById(R.id.episodeTitleText);
        TextView episodeDateText = (TextView) convertView.findViewById(R.id.episodeDateText);
        ImageView seen = (ImageView) convertView.findViewById(R.id.seen);

        episodeNumberText.setText(String.format("%dS%02dE",episodeInfo.getSeason(),episodeInfo.getSeasonNum()));
        episodeTitleText.setText(episodeInfo.getTitle());
        episodeDateText.setText(String.format("%1$tY-%1$tm-%1$td", episodeInfo.getAirDate()));
        if(episodeInfo.isSeen())
            seen.setVisibility(View.VISIBLE);
        else
            seen.setVisibility(View.INVISIBLE);
        convertView.setTag(Integer.toString(episodeInfo.getShowId())+"-"+Integer.toString(episodeInfo.getEpNum()));
        convertView.setOnLongClickListener(longClickListener);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return seasonLists.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        return seasonLists.get(groupPosition);
    }

    public int getGroupCount() {
        return seasonLists.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.season_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.seasonLableText);
        item.setText( "Season " + (groupPosition +1));
        return convertView;
    }


    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            ImageView seen = (ImageView) view.findViewById(R.id.seen);
            String[] epId = ((String) view.getTag()).trim().split("-");
            EpisodeInfo episodeInfo = dbTools.getEpisode(Integer.parseInt(epId[0]),Integer.parseInt(epId[1]));
            episodeInfo.setSeen(!episodeInfo.isSeen());
            if(episodeInfo.isSeen())
                seen.setVisibility(View.VISIBLE);
            else
                seen.setVisibility(View.INVISIBLE);
            dbTools.updateEpisode(episodeInfo,true);
            return true;
        }

    };

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}