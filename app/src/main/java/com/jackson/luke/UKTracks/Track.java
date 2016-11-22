package com.jackson.luke.UKTracks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class Track {


    private String title;
    private String artist;
    private String position;
    private String smallImgURL;
    private String largeImgURL;
    private Bitmap smallImg;


    public Track(String _title, String _artist, String _position, String _smallImgURL, String _largeImgURL){
        title = _title;
        artist = _artist;
        position = _position;
        smallImgURL = _smallImgURL;
        largeImgURL = _largeImgURL;
    }

    /* UNFINISHED IMAGE DOWNLOADING
    public void onPullComplete(String data){
        try {
            JSONArray results = new JSONObject(data).getJSONArray("results");
            String imageURL = results.getJSONObject(0).getString("artworkUrl30");
        }catch (Exception e){
            //Empty catch block
        }
    }*/

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getPosition(){return position;}
    public String getSmallImgURL(){return smallImgURL;}
    public String getLargeImgURL(){return largeImgURL;}
    public Bitmap getSmallImg(){return smallImg;}
    public void setSmallImg(Bitmap image){
        smallImg = image;
    }



    public Bitmap StringToBitMap(String encodedString){
        //Function from http://androidtrainningcenter.blogspot.co.uk/2012/03/how-to-convert-string-to-bitmap-and.html
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
