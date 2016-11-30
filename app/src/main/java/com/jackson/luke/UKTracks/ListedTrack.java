package com.jackson.luke.UKTracks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ListedTrack {


    private String title;
    private String artist;
    private String position;
    private Bitmap image;


    public ListedTrack(String _title, String _artist, String _position){
        title = _title;
        artist = _artist;
        position = _position;
    }

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getPosition(){return position;}
    public Bitmap getSmallImg(){return image;}
    public void setImg(Bitmap _image){
        image = _image;
    }
    public void setTitle(String _title){title = _title;}


}
