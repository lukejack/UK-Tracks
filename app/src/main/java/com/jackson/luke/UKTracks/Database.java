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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Tutorial from http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Database extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArtistTracks3.db";

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
                    "largeIMG" + TEXT_TYPE + " )";

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
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
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

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c.getTime());

        ContentValues values = new ContentValues();
        values.put("artist", track.getArtist());
        values.put("name", track.getTitle());
        values.put("position", track.getPosition());
        values.put("date", date);
        long track_id = db.insert(TRACKS_TABLE, null, values);

        db.close();
    }

    public List<Track> getTracks(Date date){
        String selectQuery = "SELECT * FROM " + TRACKS_TABLE + " WHERE date = Date('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        List<Track> tracks= new ArrayList<>();

        if (c.moveToFirst()){
            do {
                tracks.add(new Track(c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("artist")), c.getString(c.getColumnIndex("position"))));
                String dateofthis = c.getString(c.getColumnIndex("date"));
                int i = 1;
            } while (c.moveToNext());
        }

        c.close();
        return tracks;
    }

    public void updateTracks(List<Track> tracks, Date date){

        SQLiteDatabase db = this.getWritableDatabase();
        List<Track> dbTracks;
        dbTracks = getTracks(date);


        Boolean notSame = false;

        for (int i = 0; i < dbTracks.size(); i++)
        {
                notSame = !dbTracks.get(i).equals(tracks.get(i));
                if (notSame){break;}
        }

        if (notSame)
        {
            Log.v("DATABASE", "Not the same set!");
            //Delete the current day's entries in the database
            //String where = "date = Date('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "')";
            db.delete(TRACKS_TABLE, "date = Date('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "')", null);

            for (Track t : tracks){
                addTrack(t);
            }
        }
        db.close();
    }

    public void addArtist(Artist artist){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", artist.getName());
        values.put("smallIMG", artist.getSmall64());
        values.put("largeURL", artist.getLargeURL());
        values.put("MBID", artist.getMBID());
        values.put("lastFmURL", artist.getLastFmURL());
        long artist_id = db.insert(ARTISTS_TABLE, null, values);

        db.close();
    }

    public void addImageLargeArtist(String name, String image){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "UPDATE " + ARTISTS_TABLE + " SET largeIMG = ";


        ContentValues cv = new ContentValues();
        cv.put("largeIMG", image);
        db.update(ARTISTS_TABLE, cv, "name='"+name + "'", null);
        db.close();
    }

    public Artist getArtist(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT * FROM " + ARTISTS_TABLE + " WHERE name = '" + name + "'";

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()){
            Artist returner;
            returner = new Artist(c.getString(c.getColumnIndex("name")), null, c.getString(c.getColumnIndex("largeURL")), c.getString(c.getColumnIndex("lastFmURL")), c.getString(c.getColumnIndex("MBID")), c.getString(c.getColumnIndexOrThrow("smallIMG")), c.getString(c.getColumnIndexOrThrow("largeIMG")));
            c.close();
            db.close();
            return returner;
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

        if (c.moveToFirst()){
            do {
                dbArtists.add(new Artist(c.getString(c.getColumnIndex("name")), null, c.getString(c.getColumnIndex("largeURL")), c.getString(c.getColumnIndex("lastFmURL")), c.getString(c.getColumnIndex("MBID")), c.getString(c.getColumnIndexOrThrow("smallIMG")), c.getString(c.getColumnIndexOrThrow("largeIMG"))));
            } while (c.moveToNext());
        }

        for (Artist inA : artists) {
            boolean found = false;
            for (Artist dbA : dbArtists) {
                if (dbA.getName().equals(inA.getName())) {
                    found = true;
                    break;
                }
            }
            //Isn't in the database
            if (!found){
                addArtist(inA);
            }
        }

        c.close();
        db.close();
    }



}