package com.jackson.luke.UKTracks;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ReceiveTrack {

    private ListView listView = null;
    private MainActivity activity = this;

    //Construct track manager with activity reference for data return
    private TrackManager manager = new TrackManager(activity, this);
    private Artist[] artists;
    private Track[] tracks;
    private Database db = new Database(this);


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get UI components
        listView = (ListView) findViewById(R.id.tweetList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                Intent intent = new Intent(activity, DetailActivity.class);

                Track selected = tracks[position];
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
        //Database db = new Database(activity);
        //this.deleteDatabase("ArtistTracks3.db");
        //Artist[] test = new Artist[1];
        //test[0] = new Artist("aa", "ab", "ac", "ad", "ae");
        //db.updateArtists(test);
        //db.addArtist(new Artist("a", "small", "large", "last", "mbid"));
        //Artist test = db.getArtist("name");
        //db.addTrack(new Track("a", "b", "c"));
        //List<Track> tracks = new ArrayList<>();
        //tracks = db.getTracks(Calendar.getInstance().getTime());
    }

    public void onReturn(Pair<Track[], Artist[]> data){
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
        db.updateArtists(artists);
        Artist first = db.getArtist("The Weeknd");
        Artist second = db.getArtist("The xx");
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void postToast(String text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public boolean isNetworkAvailable(final Context context) {
        //Code found at http://stackoverflow.com/questions/9570237/android-check-internet-connection
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

