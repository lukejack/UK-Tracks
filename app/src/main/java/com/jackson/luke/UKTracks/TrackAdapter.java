package com.jackson.luke.UKTracks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.jackson.luke.UKTracks.R.layout.listview_item_row;

public class TrackAdapter extends ArrayAdapter<ListedTrack> {

    private ListedTrack data[];

    public TrackAdapter(Context _context, ListedTrack[] _data){
        super(_context, 0, _data);
        data = _data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Use row layout
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(listview_item_row, parent, false);

        //Get layout elements
        TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView txtArtist = (TextView) convertView.findViewById(R.id.txtArtist);
        TextView txtPosition = (TextView) convertView.findViewById(R.id.txtPosition);
        ImageView imgArt = (ImageView) convertView.findViewById(R.id.imageView);

        //Assign values to elements
        if (data[position] == null) {
            return convertView;
        }

        //set the small image if it exists.
        if (data[position].getSmallImg() != null){
            imgArt.setImageBitmap(data[position].getSmallImg());
        }

        //Bind text to UI
        txtTitle.setText(data[position].getTitle());
        txtArtist.setText(data[position].getArtist());
        txtPosition.setText(data[position].getPosition());

        return convertView;
    }
}
