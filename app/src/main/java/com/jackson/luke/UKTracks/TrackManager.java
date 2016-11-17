package com.jackson.luke.UKTracks;


import android.graphics.Bitmap;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackManager implements ReceiveString, BasicImageDownloader.OnImageLoaderListener{

    private static boolean initialised = false;
    private static ReceiveTrack caller;
    private Track[] tracks = new Track[40];

    public TrackManager(ReceiveTrack _caller) {
        this.caller = _caller;
    }

    public void getInstance(Boolean networkAvailable) {
        //Pull data if it is not initialised
        if (!initialised) {
            if (networkAvailable){
                try {
                    //Call asynchronous network downloading object and pass this reference for message return
                    new RestRequest(this).execute(new URL("http://www.webcoding.co.uk/top40.json"));
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
            caller.onReturn(tracks);
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
                    JSONObject wholeJSON = new JSONObject(data);
                    for (int i = 0; i < 39; i++) {
                        //Iterate through each track in the JSON and construct them into tracks for tracks[]
                        JSONObject trackData = wholeJSON.getJSONObject(Integer.toString(i + 1));
                        tracks[i] = new Track(trackData.getString("song_title"), trackData.getString("song_artist"), trackData.getString("position"));
                    }
                    //Data exists in tracks[]
                    initialised = true;

                    //Pass back to calling activity
                    caller.onReturn(tracks);
                }
            }catch (Exception e){
                caller.postToast("Unexpected data format recieved");
            }
        }
    }

    //Image downloading implementation
    public void onError(BasicImageDownloader.ImageError error){

    }

    public void onComplete(Bitmap result){

    }

    public void onProgressChange(int percent){

    }
}
