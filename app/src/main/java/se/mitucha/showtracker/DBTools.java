package se.mitucha.showtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class DBTools extends SQLiteOpenHelper {

    private final static String ID_SHOW = "id_show";
    private final static String SHOW_NAME = "show_name";
    private final static String STARTED = "started";
    private final static String STATUS = "status";
    private final static String STARTDATE = "startdate";
    private final static String CLASSIFICATION = "classification";
    private final static String RUNTIME = "runtime";
    private final static String NETWORK = "network";
    private final static String LINK = "link";
    private final static String COUNTRY = "country";
    private final static String SEASONS = "seasons";
    private final static String NETWORK_COUNTRY = "network_country";
    private final static String AIR_TIME = "air_time";
    private final static String AIR_DAY = "air_day";
    private final static String ENDED = "ended";
    private final static String GENRE = "genre";
    private final static String NAME = "name";
    private final static String SHOW_TABLE = "shows";
    private final static String GENRE_TABLE = "genre";
    private final static String AKA_TABLE = "aka";
    private final static String QUERY_CREATE_SHOW = "CREATE TABLE shows ( id_show INTEGER PRIMARY KEY, show_name TEXT NOT NULL, started INTEGER, startdate INTEGER, status TEXT, classification TEXT, runtime INTEGER, network TEXT, link TEXT, country TEXT, seasons INTEGER, network_country TEXT, air_time TEXT, air_day TEXT, ended INTEGER)";
    private final static String QUERY_CREATE_GENRE = "CREATE TABLE genre ( id_show INTEGER, genre TEXT, FOREIGN KEY(id_show) REFERENCES shows(id_show), PRIMARY KEY (id_show, genre))";
    private final static String QUERY_CREATE_AKA = "CREATE TABLE aka ( id_show INTEGER, country TEXT, name TEXT,  FOREIGN KEY(id_show) REFERENCES shows(id_show), PRIMARY KEY (id_show, country))";
    private final static String QUERY_DROP_SHOW = "DROP TABLE IF EXISTS shows";
    private final static String QUERY_DROP_GENRE = "DROP TABLE IF EXISTS genre";
    private final static String QUERY_DROP_AKA = "DROP TABLE IF EXISTS aka";
    private final static String QUERY_DELETE_SHOW_BY_ID = "DELETE FROM shows WHERE id_show =\'%1$s\'";
    private final static String QUERY_DELETE_GENRE_BY_ID = "DELETE FROM genre WHERE id_show =\'%1$s\'";
    private final static String QUERY_DELETE_AKA_BY_ID = "DELETE FROM aka WHERE id_show =\'%1$s\'";
    private final static String QUERY_ALL_SHOW = "SELECT * FROM shows ORDER BY id_show";
    private final static String QUERY_ALL_AKA = "SELECT * FROM aka ORDER BY id_show";
    private final static String QUERY_ALL_GENRE = "SELECT * FROM genre ORDER BY id_show";
    private final static String QUERY_GET_SHOW_BY_ID = "SELECT * FROM shows WHERE id_show=\'%1$s\'";
    private final static String QUERY_GET_AKA_BY_PRIM = "SELECT * FROM aka WHERE id_show=\'%1$s\' AND country='%2$s'";
    private final static String QUERY_GET_GENRE_BY_PRIM = "SELECT * FROM genre WHERE id_show=\'%1$s\' AND genre='%2$s'";

    public DBTools(Context applicationContext) {

        super(applicationContext, "show.db", null, 1);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(QUERY_CREATE_SHOW);
        db.execSQL(QUERY_CREATE_AKA);
        db.execSQL(QUERY_CREATE_GENRE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(QUERY_DROP_GENRE);
        db.execSQL(QUERY_DROP_AKA);
        db.execSQL(QUERY_DROP_SHOW);

        onCreate(db);

    }

    public boolean haveShowID(String id){


        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = String.format(QUERY_GET_SHOW_BY_ID, id);

        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor.getCount() == 1;
    }

    public void insertShow(ShowInfo show) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = mackContentValuesShow(show);
        db.insert(SHOW_TABLE, null, values);
        ArrayList<ContentValues> list = mackContentValuesAka(show);
        for(ContentValues item : list)
            db.insert(AKA_TABLE, null, item);
        list = mackContentValuesGenres(show);
        for(ContentValues item : list)
            db.insert(GENRE_TABLE, null, item);
        db.close();

    }

    private ContentValues mackContentValuesShow(ShowInfo show) {

        ContentValues values = new ContentValues();

        values.put(ID_SHOW, show.getId());
        values.put(SHOW_NAME, show.getTitle());
        values.put(STARTED, show.getStarted());
        values.put(STARTDATE, (show.getStartdate() != null)? show.getStartdate().getTimeInMillis():-1);
        values.put(CLASSIFICATION, show.getClassification());
        values.put(RUNTIME, show.getRuntime());
        values.put(NETWORK, show.getNetwork());
        values.put(STATUS, show.getStatus());
        values.put(LINK, show.getLink());
        values.put(COUNTRY, show.getCountry());
        values.put(SEASONS, show.getSeasons());
        values.put(NETWORK, show.getNetwork());
        values.put(NETWORK_COUNTRY, show.getNetworkCountry());
        values.put(AIR_TIME, show.getAirTime());
        values.put(AIR_DAY, show.getAirDay());
        values.put(ENDED, (show.getEnded() != null)? show.getEnded().getTimeInMillis():-1);
        return values;
    }

    private ArrayList<ContentValues> mackContentValuesGenres(ShowInfo show) {

        ArrayList<ContentValues> list = new ArrayList<ContentValues>(show.getGenres().size());

        for(String item : show.getGenres()) {
            ContentValues values = new ContentValues();
            values.put(ID_SHOW, show.getId());
            values.put(GENRE, item);
            list.add(values);
        }
        return list;
    }
    private ArrayList<ContentValues> mackContentValuesAka(ShowInfo show) {

        ArrayList<ContentValues> list = new ArrayList<ContentValues>(show.getGenres().size());

        HashMap<String, String> aka = show.getAkas();
        for(String item : aka.keySet()) {
            ContentValues values = new ContentValues();
            values.put(ID_SHOW, show.getId());
            values.put(COUNTRY, item);
            values.put(NAME, aka.get(item));
            list.add(values);
        }
        return list;
    }

    public int updateShow(ShowInfo show) {

        SQLiteDatabase db = this.getWritableDatabase();
        String id = Integer.toString(show.getId());
        int count = db.update(SHOW_TABLE, mackContentValuesShow(show), ID_SHOW + " = ?",new String[] { id });

        ArrayList<ContentValues> list = mackContentValuesAka(show);
        for(ContentValues item : list)
            if( haveAka(id,(String)item.get(COUNTRY)))
                count += db.update(AKA_TABLE, item , ID_SHOW + " = ? AND " + COUNTRY + " = ?",new String[] {id,(String)item.get(COUNTRY) });
            else
                count += db.insert(AKA_TABLE,null,item);

        list = mackContentValuesGenres(show);
        for(ContentValues item : list)
            if( !haveGenre(id,(String)item.get(GENRE)))
                 count += db.insert(GENRE_TABLE,null,item);
//TODO check for removed genre and aka

        return count;
        }

    public boolean haveGenre(String id,String genre){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = String.format(QUERY_GET_GENRE_BY_PRIM, id, genre);
// TODO android.database.sqlite.SQLiteException: no such column: genre (code 1): , while compiling: SELECT * FROM aka WHERE id_show='8511' AND genre='Comedy'
 //at android.database.sqlite.SQLiteConnection.nativePrepareStatement(Native Method);
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor.getCount() == 1;

    }

    public boolean haveAka(String id, String country){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = String.format(QUERY_GET_AKA_BY_PRIM, id, country);

        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor.getCount() == 1;
    }

    public void deleteShow(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = String.format(QUERY_DELETE_GENRE_BY_ID, id);
        db.execSQL(deleteQuery);
        deleteQuery = String.format(QUERY_DELETE_AKA_BY_ID, id);
        db.execSQL(deleteQuery);
        deleteQuery = String.format(QUERY_DELETE_SHOW_BY_ID, id);
        db.execSQL(deleteQuery);


    }

    public ArrayList<ShowInfo> getAllShows() {

        ArrayList<ShowInfo> showArrayList = new ArrayList<ShowInfo>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor showCursor = db.rawQuery(QUERY_ALL_SHOW, null);
        Cursor akaCursor = db.rawQuery(QUERY_ALL_AKA, null);
        Cursor genreCursor = db.rawQuery(QUERY_ALL_GENRE, null);

        akaCursor.moveToFirst();
        genreCursor.moveToFirst();
        int akaCount = akaCursor.getCount();
        int genreCount = genreCursor.getCount();
        if (showCursor.moveToFirst()) {

            do {
                ShowInfo show = makeShowFromCursor(showCursor);
                if(akaCount > 0 && akaCursor.getInt(0) ==show.getId())
                    do{
                        show.addAka(akaCursor.getString(1),akaCursor.getString(2));
                        akaCount--;
                        if(! akaCursor.moveToNext())
                            break;
                    }while (akaCursor.getInt(0) ==show.getId());
                if(genreCount > 0 && genreCursor.getInt(0) ==show.getId())
                    do{
                        show.addGenre(genreCursor.getString(1));
                        genreCount--;
                        if(! genreCursor.moveToNext())
                            break;
                    }while (genreCursor.getInt(0) ==show.getId());

                showArrayList.add(show);

            } while (showCursor.moveToNext());
        }

        return showArrayList;

    }

    public ShowInfo makeShowFromCursor(Cursor cursor){

        ShowInfo show = new ShowInfo();
        show.setId(cursor.getInt(0));
        show.setTitle(cursor.getString(1));
        show.setStarted(cursor.getInt(2));
        if(cursor.getLong(3) == -1)
            show.setStartdate(null);
        else {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(cursor.getLong(3));
            show.setStartdate(cal);
        }
        show.setStatus(cursor.getString(4));
        show.setClassification(cursor.getString(5));
        show.setRuntime(cursor.getInt(6));
        show.setNetwork(cursor.getString(7));
        show.setLink(cursor.getString(8));
        show.setCountry(cursor.getString(9));
        show.setSeasons(cursor.getInt(10));
        show.setNetworkCountry(cursor.getString(11));
        show.setAirTime(cursor.getString(12));
        show.setAirDay(cursor.getString(13));
        if(cursor.getLong(14) == -1)
            show.setEnded(null);
        else {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(cursor.getLong(14));
            show.setEnded(cal);
        }
        return show;
    }

    // TODO Update to new layout
    public ShowInfo getShow(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = String.format(QUERY_GET_SHOW_BY_ID, id);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            return makeShowFromCursor(cursor);

        }

        return null;

    }


}
