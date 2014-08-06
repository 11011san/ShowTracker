package se.mitucha.showtracker.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import se.mitucha.showtracker.util.DBTools;
import se.mitucha.showtracker.R;
import se.mitucha.showtracker.info.EpisodeInfo;
import se.mitucha.showtracker.info.ShowInfo;

/**
 * Created by mr11011 on 2014-08-06.
 */
public class SeasonActivity extends Activity {

    private ExpandableListView seasonList;
    public final static String SHOW_TAG = "se.mitucha.showtracker.adapter.SeasonActivity.SHOW";
    public final static String SEASON_TAG = "se.mitucha.showtracker.adapter.SeasonActivity.SEASON";
    private DBTools dbTools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.season_list);
        seasonList = (ExpandableListView) findViewById(R.id.seasonList);
        dbTools = new DBTools(this);
        Bundle bundle = getIntent().getExtras();
        ShowInfo showInfo = (ShowInfo) bundle.getSerializable(SHOW_TAG);
        List<List<EpisodeInfo>> data = new ArrayList<List<EpisodeInfo>>(showInfo.getSeasons());
        for(int i = 1; i<= showInfo.getSeasons();i++){
            data.add(dbTools.getEpisodeByShowSeason(Integer.toString(showInfo.getId()),i));
        }
        seasonList.setAdapter(new SeasonListAdapter(this, data));
        setTitle(showInfo.getTitle());

    }

}
