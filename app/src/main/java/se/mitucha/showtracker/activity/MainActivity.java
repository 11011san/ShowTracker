package se.mitucha.showtracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.mitucha.showtracker.R;
import se.mitucha.showtracker.adapter.SerisAdapter;
import se.mitucha.showtracker.info.ShowInfo;
import se.mitucha.showtracker.service.UpdateService;
import se.mitucha.showtracker.util.DBTools;
import se.mitucha.showtracker.util.Settings;


public class MainActivity extends Activity {

    private ListView serisListView;
    private ArrayList<ShowInfo> serisList;
    private DBTools db = new DBTools(this);
    private UpdateService updateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Settings.makeSettings(this);
        serisListView = (ListView) findViewById(R.id.serisList);

        updateService = UpdateService.getInstant();
        if(updateService==null) {
            startService(new Intent(this, UpdateService.class));
        }
        UpdateService.setContext(this);

        /*Intent intent = new Intent(this, BroadcastSync.class);
        intent.setAction(Intent.ACTION_SYNC);
        this.registerReceiver(new BroadcastSync(), IntentFilter.create());*/


        updateList();

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        serisList = db.getAllShows();
        if (serisList.size() != 0) {
            Collections.sort(serisList, new Comparator<ShowInfo>() {
                @Override
                public int compare(ShowInfo lhs, ShowInfo rhs) {
                    return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                }
            });
            ShowInfo[] list = new ShowInfo[serisList.size()];
            list = serisList.toArray(list);
            ListAdapter serisAdapter = new SerisAdapter(this, list);
            serisListView.setAdapter(serisAdapter);
            serisListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Integer showId = (Integer) view.getTag();
                    db.deleteShow(showId.toString());
                    updateList();
                    return true;
                }
            });
            serisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Integer showId = (Integer) view.getTag();
                    final ShowInfo showInfo = getShow(showId);
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SeasonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SeasonActivity.SHOW_TAG, showInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }
    }

    private ShowInfo getShow(int id) {
        for (ShowInfo showInfo : serisList)
            if (id == showInfo.getId())
                return showInfo;
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent theIntent = new Intent(getApplication(), SettingActivity.class);
            startActivity(theIntent);
        } else if (id == R.id.action_add_show) {
            Intent theIntent = new Intent(getApplication(), SearchActivity.class);
            startActivity(theIntent);
            return true;
        } else if (id == R.id.action_week_view) {
            Intent theIntent = new Intent(getApplication(), EpisodeWeekActivity.class);
            startActivity(theIntent);
        } else if (id==R.id.action_update){
            if(updateService==null)
                updateService = UpdateService.getInstant();
            updateService.update();
        }
        return super.onOptionsItemSelected(item);
    }


}
