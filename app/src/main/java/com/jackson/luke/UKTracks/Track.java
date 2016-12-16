package com.jackson.luke.UKTracks;

import java.io.Serializable;

public class Track implements Serializable{

    //Attributes of a track
    private String title;
    private String artist;
    private String position;

    public Track(String _title, String _artist, String _position){
        title = _title;
        artist = _artist;
        position = _position;
    }

    public boolean equals(Track track){
        return (title.equals(track.getTitle()) && artist.equals(track.getArtist()) && position.equals(track.getPosition()));
    }

    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getPosition(){return position;}
    public void setTitle(String _title){title = _title;}

}
