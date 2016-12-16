package com.jackson.luke.UKTracks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.awt.font.TextAttribute;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Tutorial from http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Database extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArtistTracks.db";

    //SQL execution strings
    private static final String ARTISTS_TABLE = "artists";
    private static final String TRACKS_TABLE = "tracks";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ARTISTS =
            "CREATE TABLE " + ARTISTS_TABLE + " (" +
                    "name" + " TEXT PRIMARY KEY, " +
                    "largeURL" + TEXT_TYPE + COMMA_SEP +
                    "smallIMG" + TEXT_TYPE + COMMA_SEP +
                    "MBID" + TEXT_TYPE + COMMA_SEP +
                    "lastFmURL" + TEXT_TYPE + COMMA_SEP +
                    "largeIMG" + TEXT_TYPE + COMMA_SEP +
                    "begin" + TEXT_TYPE + COMMA_SEP +
                    "end" + TEXT_TYPE + COMMA_SEP +
                    "country" + TEXT_TYPE + " )";

    private static final String SQL_CREATE_TRACKS =
            "CREATE TABLE " + TRACKS_TABLE + " (" +
                    //"id" + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name" + TEXT_TYPE + COMMA_SEP +
                    "artist" + TEXT_TYPE + COMMA_SEP +
                    "position" + TEXT_TYPE + COMMA_SEP +
                    "date" + DATE_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES_ARTISTS =
            "DROP TABLE IF EXISTS " + ARTISTS_TABLE;
    private static final String SQL_DELETE_ENTRIES_TRACKS =
            "DROP TABLE IF EXISTS " + TRACKS_TABLE;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ARTISTS);
        db.execSQL(SQL_CREATE_TRACKS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_ARTISTS);
        db.execSQL(SQL_DELETE_ENTRIES_TRACKS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public void addTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Get the current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c.getTime());

        //Parse object data into ContentValues
        ContentValues values = new ContentValues();
        values.put("artist", track.getArtist());
        values.put("name", track.getTitle());
        values.put("position", track.getPosition());
        values.put("date", date);
        db.insert(TRACKS_TABLE, null, values);
        db.close();
    }

    public ArrayList<Track> getTracks(Date date){

        //Get all tracks for the input date
        String selectQuery = "SELECT * FROM " + TRACKS_TABLE + " WHERE date = Date('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //Add all tracks to return list
        ArrayList<Track> tracks= new ArrayList<>();
        if (c.moveToFirst()){
            do {
                tracks.add(new Track(c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("artist")), c.getString(c.getColumnIndex("position"))));
            } while (c.moveToNext());
        }
        c.close();
        return tracks;
    }

    public List<Date> getDaysWithData(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Date> dates = new ArrayList<>();

        //Get unique dates with data
        String query = "SELECT DISTINCT date FROM " + TRACKS_TABLE;
        Cursor c = db.rawQuery(query, null);

        //Add all these dates to the return list
        if (c.moveToFirst()){
            do {
                DateFormat dbFormat = new SimpleDateFormat("yyy-MM-dd");
                try {
                    dates.add(dbFormat.parse(c.getString(c.getColumnIndex("date"))));
                } catch (Exception e)
                {
                    return dates;
                }
            } while (c.moveToNext());
        }
        db.close();
        c.close();
        return dates;
    }

    public void updateTracks(List<Track> tracks, Date date){

        SQLiteDatabase db = this.getWritableDatabase();
        List<Track> dbTracks;
        dbTracks = getTracks(date);

        Boolean notSame = false;
        if (dbTracks.size() == 0)
            notSame = true; //No tracks, so update is required
        else {
            for (int i = 0; i < dbTracks.size(); i++)
            {   //If an element is not the same, update
                notSame = !dbTracks.get(i).equals(tracks.get(i));
                if (notSame){break;}
            }
        }

        if (notSame) //The database needs updating
        {
            //Delete the current day's entries in the database
            db.delete(TRACKS_TABLE, "date = Date('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "')", null);
            //Add each track into the database
            for (Track t : tracks){addTrack(t);}
        }

        db.close();
    }

    public void addArtist(Artist artist){
        SQLiteDatabase db = this.getWritableDatabase();

        //Parse object attributes into ContentValues and insert into the database
        ContentValues values = new ContentValues();
        values.put("name", artist.getName());
        values.put("smallIMG", artist.getSmall64());
        values.put("largeURL", artist.getLargeURL());
        values.put("MBID", artist.getMBID());
        values.put("lastFmURL", artist.getLastFmURL());
        long artist_id = db.insert(ARTISTS_TABLE, null, values);

        db.close();
    }

    public void addImageLargeArtist(String name, String data){
        SQLiteDatabase db = this.getReadableDatabase();

        //Set the image for this artist in the database
        ContentValues cv = new ContentValues();
        cv.put("largeIMG", data);
        db.update(ARTISTS_TABLE, cv, "name='"+name + "'", null);
        db.close();
    }

    public void addArtistDetail(String name, String began, String ended, String country){
        SQLiteDatabase db = this.getReadableDatabase();

        //Insert details into the record matching the artist name
        ContentValues cv = new ContentValues();
        cv.put("begin", began);
        cv.put("end", ended);
        cv.put("country", country);
        db.update(ARTISTS_TABLE, cv, "name='"+name + "'", null);
        db.close();
    }

    public Artist getArtist(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "SELECT * FROM " + ARTISTS_TABLE + " WHERE name = '" + name + "' LIMIT 1";
        Cursor c = db.rawQuery(query, null);

        //Get the only element in the list if it exists
        if (c.moveToFirst()){
            //Construct returning Artist with database values
            Artist returner = new Artist(c.getString(c.getColumnIndex("name")), null, c.getString(c.getColumnIndex("largeURL")), c.getString(c.getColumnIndex("lastFmURL")), c.getString(c.getColumnIndex("MBID")), c.getString(c.getColumnIndexOrThrow("smallIMG")), c.getString(c.getColumnIndexOrThrow("largeIMG")));
            if (c.getString(c.getColumnIndex("country")) != null)
            {
                //If the detail is available, set the object values
                returner.setDetail(c.getString(c.getColumnIndex("begin")), c.getString(c.getColumnIndex("end")), c.getString(c.getColumnIndex("country")));
            }
            c.close();
            db.close();
            return returner;
        } else {
            db.close();
            return null;
        }
    }

    public ArrayList<Artist> getTrackArtists(ArrayList<Track> tracks){
        ArrayList<Artist> artists = new ArrayList<>();

        //Get all unique artists from a list of tracks
        for (Track t : tracks)
        {
            boolean alreadyExists = false;
            for (Artist a : artists)
            {
                if (a.getName().equals(t.getArtist())){
                 alreadyExists = true;
                }
            }
            if (!alreadyExists)
                artists.add(getArtist(t.getArtist()));
        }
        return artists;
    }

    public Bitmap getImage(String name, String imageType){
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "SELECT " + imageType + " FROM " + ARTISTS_TABLE + " WHERE name='" + name + "' LIMIT 1";
        Cursor c = db.rawQuery(query, null);

        //If the data exists in the database, then return it
        if (c.moveToFirst() && (c.getString(c.getColumnIndex(imageType)) != null)){
            db.close();
            //Return as a bitmap
            return Bitmap64.toBitmap(c.getString(c.getColumnIndex(imageType)));
        } else {
            db.close();
            return null;}
    }

    public void updateArtists(List<Artist> artists){
        String query =
                "SELECT * FROM " + ARTISTS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        List<Artist> dbArtists = new ArrayList<>();

        //Add all the artist from the database into a list
        if (c.moveToFirst()){
            do {
                dbArtists.add(new Artist(c.getString(c.getColumnIndex("name")), null, c.getString(c.getColumnIndex("largeURL")), c.getString(c.getColumnIndex("lastFmURL")), c.getString(c.getColumnIndex("MBID")), c.getString(c.getColumnIndexOrThrow("smallIMG")), c.getString(c.getColumnIndexOrThrow("largeIMG"))));
            } while (c.moveToNext());
        }

        //Find whether an artist exists i the database list
        for (Artist inA : artists) {
            boolean found = false;
            for (Artist dbA : dbArtists) {
                if (dbA.getName().equals(inA.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found){
                //Add it if it doesn't exist in the database list
                addArtist(inA);
            }
        }

        c.close();
        db.close();
    }
}