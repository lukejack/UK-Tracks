package com.jackson.luke.UKTracks;

import android.graphics.Bitmap;

/**
 * Created by Luke on 29/11/2016.
 */

public class Artist {
    private String name;
    private String smallURL;
    private String largeURL;
    private String lastFmURL;
    private String MBID;

    private Bitmap smallIMG;
    private Bitmap largeIMG;

    public Artist(String _name, String _smallURL, String _largeURL, String _lastFmURL, String _MBID){
        this.name = _name;
        this.smallURL = _smallURL;
        this.largeURL = _largeURL;
        this.lastFmURL = _lastFmURL;
        this.MBID = _MBID;
    }

    public Artist(Artist artist){
        this.name = artist.getName();
        this.smallURL = artist.getSmallURL();
        this.largeURL = artist.getLargeURL();
        this.lastFmURL = artist.getLastFmURL();
        this.MBID = artist.getMBID();
    }

    public String getName(){return name;}
    public String getSmallURL(){return smallURL;}
    public String getLargeURL(){return largeURL;}
    public String getLastFmURL(){return lastFmURL;}
    public String getMBID(){return MBID;}
    public Bitmap getSmallIMG(){return smallIMG;}
    public Bitmap getLargeIMG(){return largeIMG;}

    public void setSmallIMG(Bitmap image){smallIMG = image;}
    public void setLargeIMG(Bitmap image){largeIMG = image;}
}
