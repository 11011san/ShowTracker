package se.mitucha.showtracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import se.mitucha.showtracker.util.DBTools;
import se.mitucha.showtracker.adapter.EpisodeAdapter;
import se.mitucha.showtracker.info.EpisodeInfo;
import se.mitucha.showtracker.R;

/**
 * Created by mr11011 on 2014-08-04.
 */
public class EpisodeWeekActivity extends Activity {

    DBTools db;
    ListView mon;
    ListView tus;
    ListView wed;
    ListView thu;
    ListView fri;
    ListView sat;
    ListView sun;
    TextView monText;
    TextView tusText;
    TextView wedText;
    TextView thuText;
    TextView friText;
    TextView satText;
    TextView sunText;
    TextView weekText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_week);
        mon = (ListView) findViewById(R.id.dayListMonday);
        tus = (ListView) findViewById(R.id.dayListTuesday);
        wed = (ListView) findViewById(R.id.dayListWednesday);
        thu = (ListView) findViewById(R.id.dayListThursday);
        fri = (ListView) findViewById(R.id.dayListFriday);
        sat = (ListView) findViewById(R.id.dayListSaturday);
        sun = (ListView) findViewById(R.id.dayListSunday);
        monText = (TextView) findViewById(R.id.dayMondayText);
        tusText = (TextView) findViewById(R.id.dayTuesdayText);
        wedText = (TextView) findViewById(R.id.dayWednesdayText);
        thuText = (TextView) findViewById(R.id.dayThursdayText);
        friText = (TextView) findViewById(R.id.dayFridayText);
        satText = (TextView) findViewById(R.id.daySaturdayText);
        sunText = (TextView) findViewById(R.id.daySundayText);
        weekText = (TextView) findViewById(R.id.weekText);
//        ListView listVew = (ListView) findViewById(R.id.dayList);
        db = new DBTools(this);

        Calendar cal = GregorianCalendar.getInstance();
        curentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        posishenWeek = curentWeek;

        updateLists(curentWeek);
    }



    private void updateLists(int week){
        List<EpisodeInfo> list;
        posishenWeek = week;
        if(week == curentWeek)
            weekText.setText("This Week " + week);
        else if (week == curentWeek+1)
            weekText.setText("Next Week " + week);
        else if (week == curentWeek-1)
            weekText.setText("Last Week " + week);
        else
            weekText.setText("Week " + week);
        Calendar calendar = getDate(week,MONDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(mon,list);
        monText.setText("Monday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH) );
        calendar = getDate(week,TUESDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(tus,list);
        tusText.setText("Tuesday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH) );
        calendar = getDate(week,WEDNESDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(wed,list);
        wedText.setText("Wednesday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH) );
        calendar = getDate(week,THURSDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(thu, list);
        thuText.setText("Thursday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH) );
        calendar = getDate(week,FRIDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(fri,list);
        friText.setText("Friday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH) );
        calendar = getDate(week,SATURDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(sat,list);
        satText.setText("Saturday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH));
        calendar = getDate(week,SUNDAY);
        list = db.getEpisodeInRange(calendar,calendar);
        setList(sun,list);
        sunText.setText("Sunday - " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + calendar.get(Calendar.DAY_OF_MONTH));

    }



    private static final int MONDAY = Calendar.MONDAY;
    private static final int TUESDAY = Calendar.TUESDAY;
    private static final int WEDNESDAY = Calendar.WEDNESDAY;
    private static final int THURSDAY = Calendar.THURSDAY;
    private static final int FRIDAY = Calendar.FRIDAY;
    private static final int SATURDAY = Calendar.SATURDAY;
    private static final int SUNDAY = Calendar.SUNDAY;
    private int curentWeek;
    private int posishenWeek;
    private int day;
    private int month;
    private int year;



    private Calendar getDate(int week, int day){
        Calendar calendar = new GregorianCalendar(year,month,day);
        calendar.set(Calendar.WEEK_OF_YEAR,week);
        calendar.set(Calendar.DAY_OF_WEEK,day);
        return calendar;
    }


    private void setList(ListView day, List<EpisodeInfo> list){
        EpisodeInfo[] eps = new EpisodeInfo[list.size()];
        eps = list.toArray(eps);
        day.setAdapter( new EpisodeAdapter(this,eps));
        day.setOnItemLongClickListener(longClickListener);
        setListViewHeightBasedOnChildren(day);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView seen = (ImageView) view.findViewById(R.id.seen);
            String[] epId = ((String) view.getTag()).trim().split("-");
            EpisodeInfo episodeInfo = db.getEpisode(Integer.parseInt(epId[0]),Integer.parseInt(epId[1]));
            episodeInfo.setSeen(!episodeInfo.isSeen());
            if(episodeInfo.isSeen())
                seen.setVisibility(View.VISIBLE);
            else
                seen.setVisibility(View.INVISIBLE);
            db.updateEpisode(episodeInfo,true);
            return true;
        }
    };

    public void backWeek(View view) {
        posishenWeek--;
        if(posishenWeek<1){
            year--;
            posishenWeek=52;
        }
        updateLists(posishenWeek);
    }

    public void forwardWeek(View view) {
        posishenWeek++;
        if(posishenWeek>52) {
            year++;
            posishenWeek=1;
        }
        updateLists(posishenWeek);
    }
}
