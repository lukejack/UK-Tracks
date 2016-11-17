package com.jackson.luke.UKTracks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements ReceiveTrack {

    private ListView listView = null;
    private MainActivity activity = this;

    //Construct track manager with activity reference for data return
    private TrackManager manager = new TrackManager(activity);

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get UI components
        listView = (ListView) findViewById(R.id.tweetList);
        Button pull = (Button) findViewById(R.id.pull);

        pull.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Check network availability and request data
                manager.getInstance(isNetworkAvailable(getApplicationContext()));
            }});

    }

    public void onReturn(Track[] tracks){
        //Interface method for TrackManager's returned data binding
        TrackAdapter adapter2 = new TrackAdapter(this, tracks);
        listView.setAdapter(adapter2);
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


    public String[] jkeyStrings(String input, String key) {
        //Take JSON values from key into String array
        String[] items = {};
        JSONArray data;
        try {
            data = new JSONArray(input);
            items = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                JSONObject json_message = data.getJSONObject(i);
                if (json_message != null) {
                    items[i] = json_message.getString(key);
                }
            }
        } catch (Exception e) {
            //If the JSON parse failed to object
        }
        return items;
    }

    public void logStringArray(String[] data){
        for (int i = 0; i < data.length; i++) {
            Log.v("logarray", data[i]);
        }
    }

}

