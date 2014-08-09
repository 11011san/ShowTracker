package se.mitucha.showtracker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import se.mitucha.showtracker.R;
import se.mitucha.showtracker.adapter.SearchAdapter;
import se.mitucha.showtracker.get.DoSearch;
import se.mitucha.showtracker.get.GetEpisodeInfo;
import se.mitucha.showtracker.info.ShowInfo;
import se.mitucha.showtracker.util.DBTools;
import se.mitucha.showtracker.util.NetworkUtil;


/**
 * Created by 11011_000 on 2014-07-31.
 */
public class SearchActivity extends Activity {

    int parserArrayIncrement = 0;

    private EditText searchField;
    private ListView searchResult;
    private List<ShowInfo> searchEnteries = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        searchResult = (ListView) findViewById(R.id.resultList);
        searchField = (EditText) findViewById(R.id.searchField);
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
    }

    public void search(View view) {
        if(! new NetworkUtil(this).alowedToConect()){
            Toast.makeText(this,"No allowed connection.",Toast.LENGTH_LONG).show();
            return;
        }
        dialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setInverseBackgroundForced(true);
        dialog.setIndeterminate(true);
        dialog.setMessage("Searching...");

        dialog.show();


        final AsyncTask search = (new DoSearch(this)).execute(searchField.getText().toString());

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("Show Tracker", "Canceled search");
                search.cancel(true);
            }
        });

    }

    public void updateResult(List<ShowInfo> searchEnteries) {

        this.searchEnteries = searchEnteries;
        boolean found = true;
        Log.d("Show Tracker", "Ended Search");
        if (searchEnteries.size() == 0) { // TODO beter mesege method if no result
            Log.d("Show Tracker", "No result");
            ShowInfo show = new ShowInfo();
            show.setTitle("No Shows Found, Sorry.");
            searchEnteries.add(show);
            found = false;
        }
        ShowInfo[] list = new ShowInfo[searchEnteries.size()];
        list = searchEnteries.toArray(list);
        ListAdapter theAdapter = new SearchAdapter(searchResult.getContext(), list);
        searchResult.setAdapter(theAdapter);
        if (found)
            searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Integer showId = (Integer) view.getTag();
                    ShowInfo showInfo = getShow(showId);
                    DBTools db = new DBTools(view.getContext());
                    if (showInfo != null) {
                        if (db.haveShowID(Integer.toString(showInfo.getId()))) {
                            db.updateShow(showInfo);
                            Toast.makeText(getApplicationContext(), getString(R.string.search_entery_exixts) + " : " + showInfo.getTitle(), Toast.LENGTH_LONG).show();
                        } else {
                            db.insertShow(showInfo);
                            (new GetEpisodeInfo(SearchActivity.this, null)).execute(Integer.toString(showInfo.getId()));
                            Toast.makeText(getApplicationContext(), getString(R.string.search_entery_added) + " " + showInfo.getTitle(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        else
            searchResult.setOnItemClickListener(null);
        if (dialog.isShowing())
            dialog.dismiss();
    }

    private ShowInfo getShow(int id) {
        for (ShowInfo show : searchEnteries)
            if (show.getId() == id)
                return show;
        return null;
    }

}
