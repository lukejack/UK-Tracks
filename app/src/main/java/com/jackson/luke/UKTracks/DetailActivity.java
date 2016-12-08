package com.jackson.luke.UKTracks;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity implements BasicImageDownloader.OnImageLoaderListener {
    private String artistName;
    private Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        artistName = (String) getIntent().getSerializableExtra("artistName");
        track = (Track) getIntent().getSerializableExtra("track");

        getSupportActionBar().setTitle("Number " + track.getPosition());
        TextView titleHolder = (TextView) findViewById(R.id.title);
        titleHolder.setText(track.getTitle());
        TextView artistHolder = (TextView) findViewById(R.id.artist);
        artistHolder.setText(artistName);

        BasicImageDownloader downloader = new BasicImageDownloader(this);
        String url = (String) getIntent().getSerializableExtra("imageURL");
        downloader.download(url, false, 0);
    }

    @Override
    public void onImageDownload(Bitmap result, int position) {
        ImageView artHolder = (ImageView )findViewById(R.id.art);
        artHolder.setImageBitmap(result);
    }

    @Override
    public void onError(BasicImageDownloader.ImageError error) {

    }

    @Override
    public void onProgressChange(int percent) {

    }
}
