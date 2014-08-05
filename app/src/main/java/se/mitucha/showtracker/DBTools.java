package se.mitucha.showtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

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
    private final static String EP_NUM = "ep_num";
    private final static String SEASON_NUM = "season_num";
    private final static String PROD_NUM = "prod_num";
    private final static String AIR_DATE = "air_date";
    private final static String TITLE = "title";
    private final static String SEASON = "season";
    private final static String SEEN = "seen";
    private final static String SHOW_TABLE = "shows";
    private final static String GENRE_TABLE = "genre";
    private final static String AKA_TABLE = "aka";
    private final static String EPISODE_TABLE = "episode";
    private final static String QUERY_CREATE_SHOW = "CREATE TABLE shows ( id_show INTEGER PRIMARY KEY, show_name TEXT NOT NULL, started INTEGER, startdate INTEGER, status TEXT, classification TEXT, runtime INTEGER, network TEXT, link TEXT, country TEXT, seasons INTEGER, network_country TEXT, air_time TEXT, air_day TEXT, ended INTEGER)";
    private final static String QUERY_CREATE_GENRE = "CREATE TABLE genre ( id_show INTEGER, genre TEXT, FOREIGN KEY(id_show) REFERENCES shows(id_show), PRIMARY KEY (id_show, genre))";
    private final static String QUERY_CREATE_AKA = "CREATE TABLE aka ( id_show INTEGER, country TEXT, name TEXT,  FOREIGN KEY(id_show) REFERENCES shows(id_show), PRIMARY KEY (id_show, country))";
    private final static String QUERY_CREATE_EPISODE = "CREATE TABLE episode ( id_show INTEGER, ep_num INTEGER, season_num INTEGER, prod_num TEXT, air_date INTEGER, link TEXT, title TEXT, season INTEGER, seen NUMERIC, FOREIGN KEY(id_show) REFERENCES shows(id_show), PRIMARY KEY (id_show, ep_num))";
    private final static String QUERY_DROP_SHOW = "DROP TABLE IF EXISTS shows";
    private final static String QUERY_DROP_GENRE = "DROP TABLE IF EXISTS genre";
    private final static String QUERY_DROP_AKA = "DROP TABLE IF EXISTS aka";
    private final static String QUERY_DROP_EPISODE = "DROP TABLE IF EXISTS episode";
    private final static String QUERY_DELETE_SHOW_BY_ID = "DELETE FROM shows WHERE id_show =\'%1$s\'";
    private final static String QUERY_DELETE_GENRE_BY_ID = "DELETE FROM genre WHERE id_show =\'%1$s\'";
    private final static String QUERY_DELETE_AKA_BY_ID = "DELETE FROM aka WHERE id_show =\'%1$s\'";
    private final static String QUERY_DELETE_EPISODE_BY_ID = "DELETE FROM episode WHERE id_show =\'%1$s\'";
    private final static String QUERY_ALL_SHOW = "SELECT * FROM shows ORDER BY id_show";
    private final static String QUERY_ALL_AKA = "SELECT * FROM aka ORDER BY id_show";
    private final static String QUERY_ALL_GENRE = "SELECT * FROM genre ORDER BY id_show";
    private final static String QUERY_ALL_EPISODE = "SELECT * FROM episode ORDER BY id_show";
    private final static String QUERY_ALL_EPISODE_BY_ID = "SELECT * FROM episode WHERE id_show=\'%1$s\' ORDER BY ep_num";
    private final static String QUERY_ALL_EPISODE_BY_DATE_RANGE = "SELECT * FROM episode WHERE air_date BETWEEN %1$s AND  %2$s  ORDER BY ep_num";
    private final static String QUERY_GET_SHOW_BY_ID = "SELECT * FROM shows WHERE id_show=\'%1$s\'";
    private final static String QUERY_GET_AKA_BY_PRIM = "SELECT * FROM aka WHERE id_show=\'%1$s\' AND country='%2$s'";
    private final static String QUERY_GET_GENRE_BY_PRIM = "SELECT * FROM genre WHERE id_show=\'%1$s\' AND genre='%2$s'";
    private final static String QUERY_GET_EPISODE_BY_PRIM = "SELECT * FROM episode WHERE id_show=\'%1$s\' AND ep_num='%2$s'";
    private final static String QUERY_GET_AKA_BY_ID = "SELECT * FROM aka WHERE id_show=\'%1$s\'";
    private final static String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genre WHERE id_show=\'%1$s\'";

    public DBTools(Context applicationContext) {

        super(applicationContext, "show.db", null, 2);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(QUERY_CREATE_SHOW);
        db.execSQL(QUERY_CREATE_AKA);
        db.execSQL(QUERY_CREATE_GENRE);
        db.execSQL(QUERY_CREATE_EPISODE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(QUERY_DROP_EPISODE);
        db.execSQL(QUERY_DROP_GENRE);
        db.execSQL(QUERY_DROP_AKA);
        db.execSQL(QUERY_DROP_SHOW);

        onCreate(db);

    }

    public boolean haveShowID(String id){

        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = haveShowID(id, db);
        db.close();
        return result;
    }

    private boolean haveShowID(String id, SQLiteDatabase db ){

        String selectQuery = String.format(QUERY_GET_SHOW_BY_ID, id);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() == 1;
    }

    public void insertShow(ShowInfo show) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(SHOW_TABLE, null, makeContentValuesFromShow(show));
        for(ContentValues item : makeContentValuesFromAka(show))
            db.insert(AKA_TABLE, null, item);
        for(ContentValues item : makeContentValuesFromGenres(show))
            db.insert(GENRE_TABLE, null, item);
        db.close();

    }

    public void insertEpisode(EpisodeInfo ep){

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(EPISODE_TABLE,null,makeContentValuesFromEpisode(ep,true));
        db.close();

    }

    public void insertEpisode(List<EpisodeInfo> eps){

        SQLiteDatabase db = this.getWritableDatabase();
        for (EpisodeInfo ep : eps)
            db.insert(EPISODE_TABLE,null,makeContentValuesFromEpisode(ep,true));
        db.close();

    }

    private ContentValues makeContentValuesFromEpisode(EpisodeInfo ep, boolean updateSeen) {

        ContentValues values = new ContentValues();

        values.put(ID_SHOW, ep.getShowId());
        values.put(EP_NUM, ep.getEpNum());
        values.put(SEASON_NUM, ep.getSeasonNum());
        values.put(PROD_NUM, ep.getProdNum());
        values.put(AIR_DATE, (ep.getAirDate() != null)? ep.getAirDate().getTimeInMillis():-1);
        values.put(LINK, ep.getLink());
        values.put(TITLE, ep.getTitle());
        values.put(SEASON, ep.getSeason());
        if(updateSeen)
            values.put(SEEN, ep.isSeen());
        return values;
    }

    private List<ContentValues> makeContentValuesFromEpisode(List<EpisodeInfo> eps, boolean updateSeen) {
        List<ContentValues> list = new ArrayList<ContentValues>(eps.size());
        for(EpisodeInfo ep : eps)
            list.add(makeContentValuesFromEpisode(ep, updateSeen));
        return list;
    }

    private ContentValues makeContentValuesFromShow(ShowInfo show) {

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

    private ArrayList<ContentValues> makeContentValuesFromGenres(ShowInfo show) {

        ArrayList<ContentValues> list = new ArrayList<ContentValues>(show.getGenres().size());

        for(String item : show.getGenres()) {
            ContentValues values = new ContentValues();
            values.put(ID_SHOW, show.getId());
            values.put(GENRE, item);
            list.add(values);
        }
        return list;
    }
    private ArrayList<ContentValues> makeContentValuesFromAka(ShowInfo show) {

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
        int count = db.update(SHOW_TABLE, makeContentValuesFromShow(show), ID_SHOW + " = ?", new String[]{id});
        for(ContentValues item : makeContentValuesFromAka(show))
            if( haveAka(id,(String)item.get(COUNTRY),db))
                count += db.update(AKA_TABLE, item , ID_SHOW + " = ? AND " + COUNTRY + " = ?",new String[] {id,(String)item.get(COUNTRY) });
            else
                count += db.insert(AKA_TABLE,null,item);
        for(ContentValues item : makeContentValuesFromGenres(show))
            if( !haveGenre(id,(String)item.get(GENRE),db))
                count += db.insert(GENRE_TABLE,null,item);
//TODO check for removed genre and aka
        db.close();
        return count;
    }

    public int updateEpisode(EpisodeInfo ep, boolean updateSeen){
        SQLiteDatabase db = this.getWritableDatabase();
        String id = Integer.toString(ep.getShowId());
        String epNum = Integer.toString(ep.getEpNum());
        int count = 0;
        if(! haveEpisode(id,epNum,db)){
            db.insert(EPISODE_TABLE,null,makeContentValuesFromEpisode(ep,true));
            count++;
        }else {
            count += db.update(EPISODE_TABLE, makeContentValuesFromEpisode(ep, updateSeen), ID_SHOW + " = ? AND " + EP_NUM + " = ?", new String[]{id, epNum});
        }
        db.close();
        return count;
    }

    public int updateEpisode(List<EpisodeInfo> eps,boolean updateSeen){
        SQLiteDatabase db = this.getWritableDatabase();

        int count = 0;
        for( EpisodeInfo ep : eps ) {
            String id = Integer.toString(ep.getShowId());
            String epNum = Integer.toString(ep.getEpNum());
            if (!haveEpisode(id, epNum,db)) {
                db.insert(EPISODE_TABLE, null, makeContentValuesFromEpisode(ep, true));
                count++;
            } else {
                count += db.update(EPISODE_TABLE, makeContentValuesFromEpisode(ep, updateSeen), ID_SHOW + " = ? AND " + PROD_NUM + " = ?", new String[]{id, epNum});
            }
        }
        db.close();
        return count;
    }


    public boolean haveGenre(String id,String genre){
        SQLiteDatabase db = this.getReadableDatabase();

        boolean result = haveGenre(id,genre,db);
        db.close();
        return result;
    }

    private boolean haveGenre(String id,String genre, SQLiteDatabase db){

        String selectQuery = String.format(QUERY_GET_GENRE_BY_PRIM, id, genre);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() == 1;
    }

    public boolean haveAka(String id, String country){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = haveAka(id,country,db);
        db.close();
        return result;
    }

    private boolean haveAka(String id, String country, SQLiteDatabase db){

        String selectQuery = String.format(QUERY_GET_AKA_BY_PRIM, id, country);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() == 1;
    }

    public boolean haveEpisode(String id, String epNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = haveEpisode(id,epNum,db);
        db.close();
        return result;
    }

    private boolean haveEpisode(String id, String epNum, SQLiteDatabase db ) {

        String selectQuery = String.format(QUERY_GET_EPISODE_BY_PRIM, id, epNum);
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount() == 1;
    }


    public void deleteShow(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format(QUERY_DELETE_EPISODE_BY_ID, id));
        db.execSQL(String.format(QUERY_DELETE_GENRE_BY_ID, id));
        db.execSQL(String.format(QUERY_DELETE_AKA_BY_ID, id));
        db.execSQL(String.format(QUERY_DELETE_SHOW_BY_ID, id));
        db.close();

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
        db.close();
        return showArrayList;

    }

    public List<EpisodeInfo> getAllEpisode(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(QUERY_GET_EPISODE_BY_PRIM, null);
        ArrayList<EpisodeInfo> list = new ArrayList<EpisodeInfo>();
        int count = cursor.getCount();
        cursor.moveToFirst();
        while (count > 0){

            list.add(makeEpisodeFromCursor(cursor,db)) ;
            count--;
            cursor.moveToNext();

        }
        db.close();
        return list;
    }

    public List<EpisodeInfo> getEpisodeInRange(Calendar start, Calendar end){

        SQLiteDatabase db = this.getReadableDatabase();

        String startString = Long.toString(start.getTimeInMillis()-10);
        String endString = Long.toString(end.getTimeInMillis()+10);
        String query = String.format(QUERY_ALL_EPISODE_BY_DATE_RANGE,startString,endString);
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<EpisodeInfo> list = new ArrayList<EpisodeInfo>();
        int count = cursor.getCount();
        Log.d("Show Tracker", " count = "+ count + ", query = "+query);
        cursor.moveToFirst();
        while (count > 0){

            list.add(makeEpisodeFromCursor(cursor,db)) ;
            count--;
            cursor.moveToNext();

        }
        db.close();
        return list;

    }

    public List<EpisodeInfo> getEpisodeOfShow(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(String.format(QUERY_ALL_EPISODE_BY_ID,Integer.toString(id)), null);
        ArrayList<EpisodeInfo> list = new ArrayList<EpisodeInfo>();
        int count = cursor.getCount();
        cursor.moveToFirst();
        while (count < 0){

            list.add(makeEpisodeFromCursor(cursor,db)) ;
            count--;
            cursor.moveToNext();

        }
        db.close();
        return list;

    }
    public EpisodeInfo getEpisode(int showId , int epNum){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(String.format(QUERY_GET_EPISODE_BY_PRIM ,Integer.toString(showId),Integer.toString(epNum)), null);
       cursor.moveToFirst();
        EpisodeInfo episodeInfo = makeEpisodeFromCursor(cursor,db);
        db.close();
        return episodeInfo;

    }

    private EpisodeInfo makeEpisodeFromCursor( Cursor cursor, SQLiteDatabase db ){
        EpisodeInfo ep = new EpisodeInfo();

        ep.setShowId(cursor.getInt(0));
        ep.setEpNum(cursor.getInt(1));
        ep.setSeasonNum(cursor.getInt(2));
        ep.setProdNum(cursor.getString(3));
        if(cursor.getLong(4) == -1)
            ep.setAirDate(null);
        else {
            Calendar cal = new GregorianCalendar();
            cal.setTimeInMillis(cursor.getLong(4));
            ep.setAirDate(cal);
        }
        ep.setLink(cursor.getString(5));
        ep.setTitle(cursor.getString(6));
        ep.setSeason(cursor.getInt(7));
        ep.setSeen(cursor.getInt(8)==1);
        ep.setShow(getShow(cursor.getString(0),db));
        return ep;
    }

    private ShowInfo makeShowFromCursor(Cursor cursor){

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

    public ShowInfo getShow(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ShowInfo showInfo = getShow(id, db);
        db.close();
        return  showInfo;
    }

    private ShowInfo getShow(String id,SQLiteDatabase db) {

        Cursor cursorShow = db.rawQuery(String.format(QUERY_GET_SHOW_BY_ID, id), null);
        if (cursorShow.moveToFirst()) {
            ShowInfo show = makeShowFromCursor(cursorShow);
            Cursor cursorAka = db.rawQuery(String.format(QUERY_GET_AKA_BY_ID, id), null);
            int count = cursorAka.getCount() ;
            cursorAka.moveToFirst();
            while ( count > 0 ){
                show.addAka(cursorAka.getString(1), cursorAka.getString(2));
                count--;
                cursorAka.moveToNext();
            }
            Cursor cursorGenre = db.rawQuery(String.format(QUERY_GET_GENRE_BY_ID, id), null);
            count = cursorGenre.getCount();
            cursorGenre.moveToFirst();
            while (count > 0){
                show.addGenre(cursorGenre.getString(1));
                count--;
                cursorGenre.moveToNext();
            }
            return show;
        }
        return null;
    }
}
