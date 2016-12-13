package com.jackson.luke.UKTracks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity implements BasicImageDownloader.OnImageLoaderListener {
    private Artist artist;
    private Track track;
    ImageView artHolder;
    Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button lastFM = (Button) findViewById(R.id.lastFM);
        lastFM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(artist.getLastFmURL()));
                startActivity(i);
            }
        });
        artist = (Artist) getIntent().getSerializableExtra("artist");
        track = (Track) getIntent().getSerializableExtra("track");


        getSupportActionBar().setTitle("Number " + track.getPosition());

        TextView titleHolder = (TextView) findViewById(R.id.title);
        titleHolder.setText(track.getTitle());
        TextView artistHolder = (TextView) findViewById(R.id.artist);
        artistHolder.setText(artist.getName());
        artHolder = (ImageView)findViewById(R.id.art);

        Bitmap fromDB = db.getImage(artist.getName(), "largeIMG");
        if (fromDB == null)
            new BasicImageDownloader(this).download(artist.getLargeURL(), false, 0);
        else
            artHolder.setImageBitmap(fromDB);


    }

    @Override
    public void onImageDownload(Bitmap result, int position) {
        db.addImageLargeArtist(artist.getName(), Bitmap64.to64(result));
        artHolder.setImageBitmap(result);
    }

    @Override
    public void onError(BasicImageDownloader.ImageError error) {

    }

    @Override
    public void onProgressChange(int percent) {

    }
}
