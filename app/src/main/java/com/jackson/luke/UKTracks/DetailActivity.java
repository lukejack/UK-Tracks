package com.jackson.luke.UKTracks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.pm.PackageManager.GET_ACTIVITIES;

public class DetailActivity extends AppCompatActivity implements BasicImageDownloader.OnImageLoaderListener, ReceiveString {
    private Artist artist;
    private Track track;
    ImageView artHolder;
    Database db = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        //Set up UI elements we have
        TextView artistHolder = (TextView) findViewById(R.id.artist);
        artistHolder.setText("Loading...");
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
                try {
                    //Try opening in YouTube app first
                    Intent intent = new Intent(Intent.ACTION_SEARCH);
                    intent.setPackage("com.google.android.youtube");
                    intent.putExtra("query", track.getArtist() + " " + track.getTitle());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //Takes a while to start, so remind the user that something is happening
                    Toast toast = Toast.makeText(getApplicationContext(), "Opening the YouTube app...", Toast.LENGTH_SHORT);
                    toast.show();
                }catch(Exception e){
                    //Use the web browser as a fallback
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.youtube.com/results?search_query=" + artist.getName().replace(" ", "+") + "+" + track.getTitle().replace(" ", "+")));
                    startActivity(intent);
                }

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



        Button google = (Button) findViewById(R.id.google);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.google.co.uk/search?q=" + artist.getName().replace(" ", "+") + "+" + track.getTitle().replace(" ", "+")));
                startActivity(i);
            }
        });


        artist = (Artist) getIntent().getSerializableExtra("artist");
        track = (Track) getIntent().getSerializableExtra("track");
        getSupportActionBar().setTitle("Number " + track.getPosition());
        artHolder = (ImageView)findViewById(R.id.art);

        Bitmap dbImage = db.getImage(artist.getName(), "largeIMG");
        if (dbImage == null) {
            new BasicImageDownloader(this).download(artist.getLargeURL(), false, 0);
        }
            else
            artHolder.setImageBitmap(dbImage);

        Artist fromDB = db.getArtist(artist.getName());
        if (fromDB.getBegin() == null) {
            try {
                new RestRequest(this).execute(new URL("http://musicbrainz.org/ws/2/artist/" + artist.getMBID() + "?inc=aliases&fmt=json"));
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to download artist detail", Toast.LENGTH_SHORT);
                toast.show();
                artist = fromDB;
                bindElements();
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
        dates.setText("Since: " + artist.getBegin());

        TextView country = (TextView) findViewById(R.id.country);
        country.setText(artist.getCountry());


    }

    @Override
    public void onPullComplete(String result){

        if (result.equals("FAil"))
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Unable to download artist detail", Toast.LENGTH_SHORT);
            toast.show();
        }
        try {
            JSONObject wholeJSON = new JSONObject(result);
            String country = wholeJSON.getString("country");
            String begin = wholeJSON.getJSONObject("life-span").getString("begin");
            String ended = wholeJSON.getJSONObject("life-span").getString("end");
            artist.setDetail(begin, ended, country);
            db.addArtistDetail(artist.getName(), begin, ended, country);
        }catch(JSONException j)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Unable to parse downloaded artist detail", Toast.LENGTH_SHORT);
            toast.show();
        }
        bindElements();
    }

    @Override
    public void onImageDownload(Bitmap result, int position) {
        db.addImageLargeArtist(artist.getName(), Bitmap64.to64(result));
        artHolder.setImageBitmap(result);
    }

    @Override
    public void onError(BasicImageDownloader.ImageError error, int position) {
        Toast toast = Toast.makeText(getApplicationContext(), "Unable to download artist image", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onProgressChange(int percent) {

    }
}
