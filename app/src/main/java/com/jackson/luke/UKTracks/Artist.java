package com.jackson.luke.UKTracks;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Luke on 29/11/2016.
 */

public class Artist implements Serializable{
    private String name;
    private String smallURL;
    private String largeURL;
    private String lastFmURL;
    private String MBID;
    private String smallIMG;
    private String largeIMG;
    private String begin;
    private String end;
    private String country;

    public Artist(String _name, String _smallURL, String _largeURL, String _lastFmURL, String _MBID){
        this.name = _name;
        this.smallURL = _smallURL;
        this.largeURL = _largeURL;
        this.lastFmURL = _lastFmURL;
        this.MBID = _MBID;
    }

    public Artist(String _name, String _smallURL, String _largeURL, String _lastFmURL, String _MBID, String smallIMG, String largeIMG){
        this.name = _name;
        this.smallURL = _smallURL;
        this.largeURL = _largeURL;
        this.lastFmURL = _lastFmURL;
        this.MBID = _MBID;
        this.smallIMG = smallIMG;
        this.largeIMG = largeIMG;
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
    public Bitmap getSmallIMG(){return (smallIMG == null) ? null : Bitmap64.toBitmap(smallIMG);}
    public Bitmap getLargeIMG(){return Bitmap64.toBitmap(largeIMG);}
    public String getSmall64(){return smallIMG;}
    public String getLarge64(){return largeIMG;}
    public String getBegin(){return begin;}
    public String getEnd(){return end;}
    public String getCountry(){return country;}

    public void setDetail(String _begin, String _end, String _country){
        begin = _begin;
        end = _end;
        country = _country;
    }
    public void setSmallIMG(Bitmap image){smallIMG = Bitmap64.to64(image);}
    public void setSmallIMG(String image){smallIMG = image;}
    public void setLargeIMG(Bitmap image){largeIMG = Bitmap64.to64(image);}
    public void setLargeIMG(String image){largeIMG = image;}
}
