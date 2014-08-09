package se.mitucha.showtracker.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import se.mitucha.showtracker.R;
import se.mitucha.showtracker.adapter.SeasonListAdapter;
import se.mitucha.showtracker.info.EpisodeInfo;
import se.mitucha.showtracker.info.ShowInfo;
import se.mitucha.showtracker.util.DBTools;

/**
 * Created by mr11011 on 2014-08-06.
 */
public class SeasonActivity extends Activity {

    public final static String SHOW_TAG = "se.mitucha.showtracker.activity.SeasonActivity.SHOW";
    public final static String SEASON_TAG = "se.mitucha.showtracker.activity.SeasonActivity.SEASON";
    private ExpandableListView seasonList;
    private DBTools dbTools;
    private static final String[] items = {"Set all seen","Set all unseen"};
    private ShowInfo showInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.season_list);
        seasonList = (ExpandableListView) findViewById(R.id.seasonList);
        dbTools = new DBTools(this);
        Bundle bundle = getIntent().getExtras();
        showInfo = (ShowInfo) bundle.getSerializable(SHOW_TAG);
        updateList(null,-1);
    }

    private void updateList(int[] open,int position){
        final List<List<EpisodeInfo>> data = new ArrayList<List<EpisodeInfo>>(showInfo.getSeasons());
        for (int i = 0; i <= showInfo.getSeasons(); i++) {
            data.add(dbTools.getEpisodeByShowSeason(Integer.toString(showInfo.getId()), i));
        }
        seasonList.setAdapter(new SeasonListAdapter(this, data));
        setTitle(showInfo.getTitle());
        if(open != null)
            for(int i : open)
                seasonList.expandGroup(i,false);
        if(position!=-1) {
            seasonList.setSelectionFromTop(position,0);
        }
        seasonList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    final int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    AlertDialog.Builder builder3 = new AlertDialog.Builder(SeasonActivity.this);
                    builder3.setItems(items, new DialogInterface.OnClickListener() {

                        @Override

                        public void onClick(DialogInterface dialog, int which) {

                            EpisodeInfo episodeInfo = data.get(groupPosition).get(0);
                            if(episodeInfo!=null) {
                                dbTools.setEpisodeSeenStat(episodeInfo.getShowId(), episodeInfo.getSeason(), (which == 0));
                                ArrayList<Integer> list = new ArrayList<Integer>();
                                for(int i=0;i<data.size();i++)
                                    if(seasonList.isGroupExpanded(i))
                                        list.add(i);
                                int[] open = new int[list.size()];
                                for(int i=0;i<list.size();i++)
                                    open[i]=list.get(i);
                                updateList(open,seasonList.getFirstVisiblePosition());
                            }
                        }

                    });

                    builder3.show();
                    return true;
                }
                return false;
            }
        });

    }

}
