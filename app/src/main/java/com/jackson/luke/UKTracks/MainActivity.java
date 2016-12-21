package com.jackson.luke.UKTracks;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ReceiveTrack {

    private ListView listView = null; //Primary view of tracks
    private MainActivity activity = this;

    //Construct track manager with activity reference for data return
    private TrackManager manager = new TrackManager(activity, this);
    private ArrayList<Artist> artists = new ArrayList<>();
    private ArrayList<Track> tracks = new ArrayList<>();
    private Database db = new Database(this);

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        //Initialise the interface
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Bind elements to code
        listView = (ListView) findViewById(R.id.trackList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                //Open track detail activity
                Intent intent = new Intent(activity, DetailActivity.class);
                Track selected = tracks.get(position);
                //Find the artist for that track
                for (Artist a : artists){
                    if (a.getName().equals(selected.getArtist())){
                        intent.putExtra("artist", a);
                        break;
                    }
                }
                intent.putExtra("track", selected);
                startActivity(intent);
            }
        });

        //Get the track data from the track manager
        manager.getInstance(isNetworkAvailable(getApplicationContext()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2: {
                //Speech return
                if ((data != null) && (resultCode == RESULT_OK)) {
                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //Start a web browser with the wikipedia search
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://www.last.fm/search?q=" + speech.get(0)));
                    startActivity(i);
                } else {
                    postToast("Unable to recognise speech");
                }
                break;
            }
        }
    }

    public void onReturn(ArrayList<Track> _tracks, ArrayList<Artist> _artists, boolean newData){

        List<ListedTrack> listData = new ArrayList<>();
        tracks = _tracks;
        artists = _artists;

        //Bind artists with tracks and list them
        for (Track t : _tracks){
            for(Artist a : _artists){
                if (t.getArtist().equals(a.getName())){
                    ListedTrack thisOne = new ListedTrack(t.getTitle(), a.getName(), t.getPosition(), a.getSmallIMG());
                    listData.add(thisOne);
                    break;
                }
            }
        }
        TrackAdapter adapter2 = new TrackAdapter(this, listData.toArray(new ListedTrack[listData.size()]));
        listView.setAdapter(adapter2);

        //Update the database if the data is new
        if (newData) {
            db.updateArtists(artists);
            db.updateTracks(tracks, Calendar.getInstance().getTime());
        }

    }

    public void drawDateSelection(){
        //Get the unique days from the database
        final ArrayList<Date> dates = new ArrayList<>(db.getDaysWithData());
        String[] textDates = new String[dates.size()];

        //Convert these dates into strings
        for (int i = 0; i < dates.size(); i++){
            DateFormat dbFormat = new SimpleDateFormat("yyy-MM-dd");
            textDates[i] = dbFormat.format(dates.get(i));
        }

        //Draw the date selection popup list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, textDates);
        final ListPopupWindow dateSelection = new ListPopupWindow(activity);
        dateSelection.setAdapter(adapter);
        dateSelection.setWidth(400);
        dateSelection.setHeight(400);
        Space anchorPoint = (Space) findViewById(R.id.anchor);
        dateSelection.setAnchorView(anchorPoint);

        dateSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                //Get the data of the selection from the database
                ArrayList<Track> dbTracks = db.getTracks(dates.get(position));
                ArrayList<Artist> dbArtists = db.getTrackArtists(dbTracks);
                onReturn(dbTracks, dbArtists, false);
                dateSelection.dismiss();
            }
        });
        dateSelection.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Set up menu bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                //Get list data
                manager.getInstance(isNetworkAvailable(getApplicationContext()));
                return true;
            case R.id.selectDay:
                drawDateSelection();
                return true;
            case R.id.search:
                //Create an intent for speech recognition
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //Set the language as the local language
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                try {
                    //Response code is 2
                    startActivityForResult(intent, 2);
                } catch (Exception a) {
                    //The user probably does not have the required google application
                    postToast("Download the Google app for voice search");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.googlequicksearchbox")));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void postToast(String text){
        //Display a notification to the user
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public boolean isNetworkAvailable(final Context context) {
        //Code found at http://stackoverflow.com/questions/9570237/android-check-internet-connection
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

