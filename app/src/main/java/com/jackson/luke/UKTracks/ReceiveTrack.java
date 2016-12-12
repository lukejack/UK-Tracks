package com.jackson.luke.UKTracks;

//Implements a function to receive tracks from the TrackManager
//implements a function to post toast in context

import java.util.ArrayList;

public interface ReceiveTrack {
    void onReturn(Pair<ArrayList<Track>, ArrayList<Artist>> items);
    void postToast(String text);
}
