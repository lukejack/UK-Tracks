package com.jackson.luke.UKTracks;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ReceiveTrack {

    private ListView listView = null;
    private MainActivity activity = this;

    //Construct track manager with activity reference for data return
    private TrackManager manager = new TrackManager(activity, this);
    private ArrayList<Artist> artists = new ArrayList<>();
    private ArrayList<Track> tracks = new ArrayList<>();
    private Database db = new Database(this);
    private ProgressBar progressBar;


    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        List<Date> dates = db.getDaysWithData();
        //Get UI components
        listView = (ListView) findViewById(R.id.tweetList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                Intent intent = new Intent(activity, DetailActivity.class);
                Track selected = tracks.get(position);
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
        manager.getInstance(isNetworkAvailable(getApplicationContext()));
        //this.deleteDatabase("ArtistTracksa.db");
        //Artist[] test = new Artist[1];
        //test[0] = new Artist("aa", "ab", "ac", "ad", "ae");
        //db.updateArtists(test);
        //db.addArtist(new Artist("a", "small", "large", "last", "mbid"));
        //Artist test = db.getArtist("name");
        //List<Track> test = new ArrayList<>();


        //for (Track t : test)
        //    db.addTrack(t);

        //test.clear();
        //test.add(new Track("a", "b", "c"));
        //test.add(new Track("a", "132123123", "d"));
        //test.add(new Track("097214309874320987", "b", "f"));

        //db.updateTracks(test, Calendar.getInstance().getTime());
        //db.addImageLargeArtist("a", "IMAGE OVER HERE");

        //List<Track> tracks = new ArrayList<>();
        //tracks = db.getTracks(Calendar.getInstance().getTime());
    }

    public void onReturn(Pair<ArrayList<Track>, ArrayList<Artist>> data, boolean newData){
        List<ListedTrack> listData = new ArrayList<>();
        tracks = data.first;
        artists = data.second;
        for (Track t : data.first){
            for(Artist a : data.second){
                if (t.getArtist().equals(a.getName())){
                    ListedTrack thisOne = new ListedTrack(t.getTitle(), a.getName(), t.getPosition(), a.getSmallIMG());
                    listData.add(thisOne);
                    break;
                }
            }
        }
        TrackAdapter adapter2 = new TrackAdapter(this, listData.toArray(new ListedTrack[listData.size()]));
        listView.setAdapter(adapter2);

        if (newData) {
            db.updateArtists(artists);
            db.updateTracks(tracks, Calendar.getInstance().getTime());
        }

    }

    public void drawDateSelection(){
        final ArrayList<Date> dates = new ArrayList<>(db.getDaysWithData());
        String[] textDates = new String[dates.size()];
        for (int i = 0; i < dates.size(); i++){
            DateFormat dbFormat = new SimpleDateFormat("yyy-MM-dd");
            textDates[i] = dbFormat.format(dates.get(i));
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, textDates);
        final ListPopupWindow dateSelection = new ListPopupWindow(activity);
        dateSelection.setAdapter(adapter);
        dateSelection.setWidth(400);
        dateSelection.setHeight(600);
        Space anchorPoint = (Space) findViewById(R.id.anchor);
        dateSelection.setAnchorView(anchorPoint);

        dateSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                Log.v("Position", Integer.toString(position));
                ArrayList<Track> dbTracks = db.getTracks(dates.get(position));
                ArrayList<Artist> dbArtists = db.getTrackArtists(dbTracks);
                onReturn(new Pair<>(dbTracks, dbArtists), false);
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
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                manager.getInstance(isNetworkAvailable(getApplicationContext()));
                return true;
            case R.id.selectDay:
                drawDateSelection();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void postToast(String text){
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

