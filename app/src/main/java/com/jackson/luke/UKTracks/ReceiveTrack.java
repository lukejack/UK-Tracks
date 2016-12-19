package com.jackson.luke.UKTracks;

//Implements a function to receive tracks from the TrackManager
//implements a function to post toast in context

import java.util.ArrayList;

public interface ReceiveTrack {
    void onReturn(ArrayList<Track> tracks, ArrayList<Artist> artists, boolean newData);
    void postToast(String text);
}
