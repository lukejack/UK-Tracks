package com.jackson.luke.UKTracks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

//Tutorial from http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
public class Database extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ArtistTracks.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ARTISTS =
            "CREATE TABLE " + "artists" + " (" +
                    "id" + " INTEGER PRIMARY KEY," +
                    "artist" + TEXT_TYPE + COMMA_SEP +
                    "smallURL" + TEXT_TYPE + COMMA_SEP +
                    "largeURL" + TEXT_TYPE + COMMA_SEP +
                    "smallIMG" + TEXT_TYPE + COMMA_SEP +
                    "largeIMG" + TEXT_TYPE + " )";
    private static final String SQL_CREATE_TRACKS =
            "CREATE TABLE " + "tracks" + " (" +
                    "id" + " INTEGER PRIMARY KEY," +
                    "artist" + TEXT_TYPE + COMMA_SEP +
                    "track" + TEXT_TYPE + COMMA_SEP +
                    "position" + INT_TYPE + COMMA_SEP +
                    "date" + DATE_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES_ARTISTS =
            "DROP TABLE IF EXISTS " + "artists";
    private static final String SQL_DELETE_ENTRIES_TRACKS =
            "DROP TABLE IF EXISTS " + "tracks";

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

    public long addTrack(Track track) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("artist", track.getArtist());
        values.put("track", track.getTitle());
        values.put("position", track.getPosition());
        Date current = new Date();
        values.put("date", current.getTime());

        // insert row
        long track_id = db.insert("tracks", null, values);

        /*
        for (long tag_id : tag_ids) {
            createTodoTag(todo_id, tag_id);
        }*/

        return track_id;
    }
}