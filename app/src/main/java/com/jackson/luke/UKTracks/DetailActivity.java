package com.jackson.luke.UKTracks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity implements BasicImageDownloader.OnImageLoaderListener, ReceiveString {
    private Artist artist;
    private Track track;
    ImageView artHolder;
    Database db = new Database(this);
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
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
        Button youtube = (Button) findViewById(R.id.youtube);
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", track.getArtist() + " " + track.getTitle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //Image from https://en.wikipedia.org/w/index.php?search=%3Blokjasjhdflkjhsdafgphlkjsdfg
        Button wikipedia = (Button) findViewById(R.id.wikipedia);
        wikipedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://en.wikipedia.org/w/index.php?search=" + artist.getName()));
                startActivity(i);
            }
        });

        artist = (Artist) getIntent().getSerializableExtra("artist");
        track = (Track) getIntent().getSerializableExtra("track");
        getSupportActionBar().setTitle("Number " + track.getPosition());
        artHolder = (ImageView)findViewById(R.id.art);

        Bitmap dbImage = db.getImage(artist.getName(), "largeIMG");
        if (dbImage == null)
            new BasicImageDownloader(this).download(artist.getLargeURL(), false, 0);
        else
            artHolder.setImageBitmap(dbImage);

        Artist fromDB = db.getArtist(artist.getName());
        if (fromDB.getBegin() == null) {
            try {
                new RestRequest(this).execute(new URL("http://musicbrainz.org/ws/2/artist/" + artist.getMBID() + "?inc=aliases&fmt=json"));
            } catch (MalformedURLException e) {

            }
        }
        else{
            artist = fromDB;
            bindElements();
        }

    }

    public void bindElements(){
        TextView titleHolder = (TextView) findViewById(R.id.title);
        titleHolder.setText(track.getTitle());
        TextView artistHolder = (TextView) findViewById(R.id.artist);
        artistHolder.setText(artist.getName());

        TextView dates = (TextView) findViewById(R.id.dates);
        if (artist.getBegin().equals("null"))
            dates.setText("Since: Unknown");
        else
            dates.setText("Since: " + artist.getBegin());

        TextView country = (TextView) findViewById(R.id.country);
        if (artist.getCountry().equals("null"))
            country.setText("Unknown Origin");
        else
            country.setText(artist.getCountry());

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPullComplete(String result){
        try {
            JSONObject wholeJSON = new JSONObject(result);
            String country = wholeJSON.getString("country");
            String begin = wholeJSON.getJSONObject("life-span").getString("begin");
            String ended = wholeJSON.getJSONObject("life-span").getString("end");
            artist.setDetail(begin, ended, country);
            db.addArtistDetail(artist.getName(), begin, ended, country);
        }catch(JSONException j)
        {

        }
        bindElements();
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
