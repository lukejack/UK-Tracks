package com.jackson.luke.UKTracks;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Date;


public class TrackManager implements ReceiveString, BasicImageDownloader.OnImageLoaderListener{

    private static boolean initialised = false;
    private static ReceiveTrack caller;
    private static MainActivity context;
    private static Track[] tracks;
    private static ArrayList<Artist> artists = new ArrayList<Artist>();
    private static Database db;
    private ProgressBar progressBar;


    private static List<ListedTrack> returnList = new ArrayList<ListedTrack>();
    private int imageCount = 0;

    public TrackManager(ReceiveTrack _caller, MainActivity _context) {
        this.caller = _caller;
        context = _context;
        db = new Database(context);
    }

    public void getInstance(Boolean networkAvailable) {
        progressBar = (ProgressBar) context.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        //Pull data if it is not initialised
            if (networkAvailable){
                try {
                    //Call asynchronous network downloading object and pass this reference for message return
                    new RestRequest(this).execute(new URL("http://ws.audioscrobbler.com/2.0/?method=chart.gettoptracks&api_key=735d178e0129f10c4058fb1172b36405&format=json&limit=25"));
                } catch (Exception e) {
                    //An unchanging URL will not throw this error
                    caller.postToast("Invalid URL for data request");
                }
            } else {
                //Network unavailable

                List<Date> dates = db.getDaysWithData();

                if (dates.size() != 0) {
                    DateFormat dbFormat = new SimpleDateFormat("yyy-MM-dd");
                    caller.postToast("No internet connection, displaying tracks for " + dbFormat.format(dates.get(0)));
                    ArrayList<Track> dbTracks = new ArrayList<>(db.getTracks(dates.get(0)));
                    ArrayList<Artist> artists = new ArrayList<>(db.getTrackArtists(dbTracks));


                    caller.onReturn(new Pair<>(dbTracks, artists), false);
                } else {
                    caller.postToast("Unable to find a network connection or cached tracks");

                }
                progressBar.setVisibility(View.INVISIBLE);
            }
    }

    public void onPullComplete(String data){
        if (data.equals("FAIL")) {
            //Pulling data failed, notify user
            caller.postToast("Unable to connect to web server");
        } else {
            try {
               JSONArray wholeJSON = new JSONObject(data).getJSONObject("tracks").getJSONArray("track");
               tracks = new Track[wholeJSON.length()];
               artists.clear();
               for (int i = 0; i < wholeJSON.length(); i++) {

                   //Iterate through each track in the JSON and construct them into tracks for tracks[]
                   JSONObject trackData = wholeJSON.getJSONObject(i);
                   String trackName = trackData.getString("name");
                   JSONObject artist = trackData.getJSONObject("artist");
                   tracks[i] = new Track(trackName, artist.getString("name"), Integer.toString(i + 1));

                   if (artistUnique(artists, artist.getString("name"))){
                       JSONArray imageLinks = trackData.getJSONArray("image");
                       Artist thisArtist = new Artist(artist.getString("name")
                               , imageLinks.getJSONObject(0).getString("#text")
                               , imageLinks.getJSONObject(3).getString("#text")
                               , artist.getString("url")
                               , artist.getString("mbid"));


                       artists.add(thisArtist);
                   }
               }


               int artistCount = -1;
               for(Artist a : artists)
               {
                   artistCount++;
                   BasicImageDownloader downloader = new BasicImageDownloader(this);
                   Bitmap fromDB = db.getImage(a.getName(), "smallIMG");
                   if (fromDB == null){
                       downloader.download(a.getSmallURL(), false, artistCount);
                   }
                   else
                       onImageDownload(fromDB, artistCount);
               }
            }catch (Exception e){
                caller.postToast("Unexpected data format received: " + e.getMessage());
                Log.e("thrown", "error", e);
            }
        }
    }

    private int JSONKeyCount(JSONObject jObject){
        //contents from http://stackoverflow.com/questions/9151619/how-to-iterate-over-a-jsonobject
        int count = 0;
        try{
            Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if ( jObject.get(key) instanceof JSONObject ) {
                count++;
            }
        }} catch (Exception e){

        }
        return count;
    }

    //Image downloading implementation
    public void onError(BasicImageDownloader.ImageError error){

    }

    public void onImageDownload(Bitmap result, int position){
        artists.get(position).setSmallIMG(result);
        if (++imageCount == artists.size()){
            imageCount = 0;
            progressBar.setVisibility(View.INVISIBLE);
            caller.onReturn(new Pair<>(new ArrayList<>(Arrays.asList(tracks)), artists), true);
        }
    }

    public void onProgressChange(int percent){

    }

    public boolean artistUnique(List<Artist> artists, String name){
        for (Artist i : artists){
            if (name.equals(i.getName())){return false;}
        }
        return true;
    }
}
