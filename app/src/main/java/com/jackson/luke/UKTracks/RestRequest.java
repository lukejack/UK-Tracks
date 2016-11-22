package com.jackson.luke.UKTracks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestRequest extends AsyncTask<URL, Integer, String> {

    private ReceiveString activity;


    public RestRequest(ReceiveString a){
        this.activity = a;
    }

    protected String doInBackground(URL... sites){
        String result;
        try {
            //Open connection and GET request
            HttpURLConnection urlConnection = (HttpURLConnection) sites[0].openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

            //Convert the data input stream to a string
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            result = streamString(in);
        } catch (Exception e){
            //Return connection failure to the calling activity
            Log.v("Error message", e.getMessage());
            return "FAIL";
        }
        return result;
    }

    protected void onPostExecute(String result){
        //Pass the data back to the calling activity
        activity.onPullComplete(result);
    }

    public String streamString(InputStream is){
        //Function from https://www.mkyong.com/java/how-to-convert-inputstream-to-string-in-java/
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }




}
