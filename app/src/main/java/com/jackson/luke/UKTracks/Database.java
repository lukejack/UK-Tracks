package com.jackson.luke.UKTracks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Date;

//Tutorial from http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Database extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArtistTracks.db";

    private static final String ARTISTS_TABLE = "artists";
    private static final String TRACKS_TABLE = "tracks";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ARTISTS =
            "CREATE TABLE " + ARTISTS_TABLE + " (" +
                    "artist" + " TEXT PRIMARY KEY," +
                    "largeURL" + TEXT_TYPE + COMMA_SEP +
                    "smallIMG" + TEXT_TYPE + COMMA_SEP +
                    "MBID" + TEXT_TYPE + COMMA_SEP +
                    "lastFmURL" + TEXT_TYPE + COMMA_SEP +
                    "largeIMG" + TEXT_TYPE + " )";
    private static final String SQL_CREATE_TRACKS =
            "CREATE TABLE " + TRACKS_TABLE + " (" +
                    "track" + " TEXT PRIMARY KEY," +
                    "artist" + TEXT_TYPE + COMMA_SEP +
                    "position" + INT_TYPE + COMMA_SEP +
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

    public long addTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("artist", track.getArtist());
        values.put("track", track.getTitle());
        values.put("position", track.getPosition());
        Date current = new Date();
        values.put("date", current.getTime());
        // insert row
        long track_id = db.insert(TRACKS_TABLE, null, values);

        /*
        for (long tag_id : tag_ids) {
            createTodoTag(todo_id, tag_id);
        }*/

        return track_id;
    }

    public long addArtist(Artist artist){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("artist", artist.getName());
        values.put("smallIMG", bitmapToString(artist.getSmallIMG()));
        values.put("largeURL", artist.getLargeURL());
        values.put("MBID", artist.getMBID());
        values.put("lastFmURL", artist.getLastFmURL());

        long artist_id = db.insert(ARTISTS_TABLE, null, values);
        return artist_id;
    }

    public Artist getArtist(Artist artist){
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT * FROM " + ARTISTS_TABLE + " WHERE artist = '" + artist.getName() + "'";


        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()){
            do {
                Artist returnArtist = new Artist(artist);
                String base64enc = c.getString(c.getColumnIndex("smallIMG"));
                byte[] decodedString = Base64.decode(base64enc, Base64.DEFAULT);
                returnArtist.setSmallIMG(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                return returnArtist;
            }while (c.moveToNext());
        } else {return null;}
    }

    private String bitmapToString(Bitmap image){
        //Found at http://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /*
    public Track[] getAllTracks(Date day){

        String selectQuery = "SELECT * FROM " + ARTISTS_TABLE + " WHERE date=" + day.getTime();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do {
                //Track thisTrack = new Track(c.getString(c.getColumnIndex("track")), c.getString(c.getColumnIndex("artist")), c.getInt(c.getColumnIndex("position")), );
            } while (false);
        }
        //Track[] tracks= new Track[];
    }*/

}