package com.jackson.luke.UKTracks;

public class Pair<Type1, Type2> {
    //Simple tuple
    public final Type1 first;
    public final Type2 second;

    public Pair(Type1 _first, Type2 _second){
        this.first = _first;
        this.second = _second;
    }
}
