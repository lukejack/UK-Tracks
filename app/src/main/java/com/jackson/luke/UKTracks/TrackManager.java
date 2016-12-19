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

    private static ReceiveTrack caller;
    private MainActivity context;

    private static Track[] tracks;
    private static ArrayList<Artist> artists = new ArrayList<Artist>();
    private static Database db;
    private ProgressBar progressBar;

    private int imageCount = 0; //For image downloading count

    public TrackManager(ReceiveTrack _caller, MainActivity _context) {
        this.caller = _caller;
        context = _context;
        db = new Database(context);
    }

    public void getInstance(Boolean networkAvailable) {
        //Start the loading spinner
        progressBar = (ProgressBar) context.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (networkAvailable){
            try {
                //Call asynchronous network downloading object and pass this reference for message return
                new RestRequest(this).execute(new URL("http://ws.audioscrobbler.com/2.0/?method=chart.gettoptracks&api_key=735d178e0129f10c4058fb1172b36405&format=json"));
            } catch (Exception e) {
                //An unchanging URL will not throw this error
                caller.postToast("Invalid URL for data request");
            }
        } else {
            //Network unavailable; search the database for some data
            List<Date> dates = db.getDaysWithData();
            if (dates.size() != 0) {
                //Select the latest day's data in the database
                DateFormat dbFormat = new SimpleDateFormat("yyy-MM-dd");
                caller.postToast("No internet connection, displaying tracks for " + dbFormat.format(dates.get(0)));
                ArrayList<Track> dbTracks = new ArrayList<>(db.getTracks(dates.get(0)));
                ArrayList<Artist> artists = new ArrayList<>(db.getTrackArtists(dbTracks));
                caller.onReturn(dbTracks, artists, false);
            } else {
                //There is no data in the database, and there is no internet connection
                caller.postToast("Unable to find a network connection or cached tracks");

            }
            //Hide the progressbar
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onPullComplete(String data){
        if (data.equals("FAIL")) {
            //Pulling data failed, notify user
            caller.postToast("Unable to connect to web server");
        } else {
            try {
                //Parse returned JSON
               JSONArray wholeJSON = new JSONObject(data).getJSONObject("tracks").getJSONArray("track");
               tracks = new Track[wholeJSON.length()];
               artists.clear();

               for (int i = 0; i < wholeJSON.length(); i++) {
                   //Iterate through each track in the JSON and construct them into tracks for tracks[]
                   JSONObject trackData = wholeJSON.getJSONObject(i);
                   String trackName = trackData.getString("name");
                   JSONObject artist = trackData.getJSONObject("artist");
                   tracks[i] = new Track(trackName, artist.getString("name"), Integer.toString(i + 1));
                   //If the artist does not exist already, add it to the list
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

               //Get each artist image from the database and download it if it doesn't exist
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
                caller.postToast("Unexpected data format received: ");
            }
        }
    }

    public void onError(BasicImageDownloader.ImageError error, int position){
        caller.postToast("Unable to download image for " + artists.get(position).getName());
        //Increment the counter as this image download is finished
        imageCount++;
    }

    public void onImageDownload(Bitmap result, int position){
        artists.get(position).setSmallIMG(result);
        if (++imageCount == artists.size()){
            //If all images have been downloaded, return the data
            imageCount = 0;
            progressBar.setVisibility(View.INVISIBLE);
            caller.onReturn(new ArrayList<Track>(Arrays.asList(tracks)), artists, true);
        }
    }

    public void onProgressChange(int percent){}

    public boolean artistUnique(List<Artist> artists, String name){
        //Returns whether an artist does not exist in a list
        for (Artist i : artists){
            if (name.equals(i.getName())){return false;}
        }
        return true;
    }
}
