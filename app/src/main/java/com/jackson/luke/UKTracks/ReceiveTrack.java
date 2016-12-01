package com.jackson.luke.UKTracks;

//Implements a function to receive tracks from the TrackManager
//implements a function to post toast in context

public interface ReceiveTrack {
    void onReturn(Pair<Track[], Artist[]> items);
    void postToast(String text);
}
