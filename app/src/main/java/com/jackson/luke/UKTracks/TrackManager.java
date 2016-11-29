package com.jackson.luke.UKTracks;


import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class TrackManager implements ReceiveString, BasicImageDownloader.OnImageLoaderListener{

    private static boolean initialised = false;
    private static ReceiveTrack caller;
    private static Track[] tracks;
    private int imageCount = 0;

    public TrackManager(ReceiveTrack _caller) {
        this.caller = _caller;
    }

    public void getInstance(Boolean networkAvailable) {
        //Pull data if it is not initialised
        if (!initialised) {
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
                caller.postToast("Unable to find a network connection");
            }
        } else {
            //The data is recieved and formatted successfully
            //caller.onReturn(tracks);
        }
    }

    public void onPullComplete(String data){
        if (data.equals("FAIL")) {
            //Pulling data failed, notify user
            caller.postToast("Unable to connect to web server");
        } else {
            try {
                //XML check; parse as JSON
                if (!data.startsWith("<")) {
                    JSONArray wholeJSON = new JSONObject(data).getJSONObject("tracks").getJSONArray("track");
                    tracks = new Track[wholeJSON.length()];

                    for (int i = 0; i < wholeJSON.length(); i++) {
                        BasicImageDownloader downloader = new BasicImageDownloader(this);
                        //Iterate through each track in the JSON and construct them into tracks for tracks[]
                        JSONObject trackData = wholeJSON.getJSONObject(i);
                        String trackName = trackData.getString("name");
                        String artistName = trackData.getJSONObject("artist").getString("name");
                        JSONArray imageLinks = trackData.getJSONArray("image");
                        String smallImgURL = imageLinks.getJSONObject(1).getString("#text");
                        String largeImgURL = imageLinks.getJSONObject(3).getString("#text");

                        tracks[i] = new Track(trackName, artistName, Integer.toString(i + 1), smallImgURL, largeImgURL);
                        downloader.download(smallImgURL, false, i);
                    }
                    //Data exists in tracks[]
                    initialised = true;

                    //Pass back to calling activity

                }
            }catch (Exception e){
                caller.postToast("Unexpected data format received");
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

    public void onComplete(Bitmap result, int position){
        tracks[position].setSmallImg(result);
        if (++imageCount == tracks.length - 1){
            caller.onReturn(tracks);
        }
    }

    public void onProgressChange(int percent){

    }
}
