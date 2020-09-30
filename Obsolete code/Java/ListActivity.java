package com.example.wildfiredetection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    Button btnBack;

    private String TAG = ListActivity.class.getSimpleName();
    private ListView listView;

    //Create fireList to store all fire objects after parsing json
    ArrayList<HashMap<String, String>> fireList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fireList = new ArrayList<>();
        btnBack = findViewById(R.id.button7);
        listView = findViewById(R.id.list);

	//Execute the method
        new GetFires().execute();

        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBack = new Intent(ListActivity.this, MapActivity.class);
                startActivity(goBack);
            }
        });
    }

    private class GetFires extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
	    //Display the message before doInBackground() is retrieving data
            Toast.makeText(ListActivity.this,"Downloading fires data",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Void... params) {
	    //The json string to be returned
            String jsonString = "";

            try {
                //Make request to url
                //URL url = new URL("https://wildfiredetection-fdd86.firebaseio.com/";
                URL url = new URL("http://localhost:8000/australia_jan.json");

                //Create connection
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                //Read data from stream url
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while(line != null) {
                    line = bufferedReader.readLine();
		    //lines are added to the string
                    jsonString = jsonString + line; 
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
         	    //Get json object
                    JSONObject jsonObj = new JSONObject(jsonString);

                    //Parsing json data
                    JSONArray Australia = jsonObj.getJSONArray("Australia");

                    //Iterate over all elements in fires data
                    for (int i = 0; i < Australia.length(); i++) {

			//Get all elements in each json object
                        JSONObject obj = Australia.getJSONObject(i); 
                        String acq_date = obj.getString("acq_date");
                        String acq_time = obj.getString("acq_time");
                        String latitude = obj.getString("latitude");
                        String longitude = obj.getString("longitude");
                        String location = obj.getString("location");

                        //Hash map for a single fire object
                        HashMap<String, String> fire = new HashMap<>();

                        //Add elements to fire object 
                        fire.put("acq_date", acq_date);
                        fire.put("acq_time", acq_time);
                        fire.put("latitude", latitude);
                        fire.put("longitude", longitude);
                        fire.put("location", location);

                        //Add each fire object to fireList
                        fireList.add(fire);
                    }
                }
                catch (final JSONException e) {
		    //Display error message
                    Log.e(TAG, "Failed to parse data: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Failed to parse data: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else {
                Log.e(TAG, "Couldn't get json from server, check if the url is correct");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            //Return data to onPostExecute
            return jsonString;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Add list items to list
            ListAdapter adapter = new SimpleAdapter(ListActivity.this, fireList,
                    R.layout.list_item, new String[]{ "acq_date","acq_time","latitude","location","longitude"},
                    new int[]{R.id.acq_date, R.id.acq_time, R.id.latitude, R.id.location,R.id.longitude});
            listView.setAdapter(adapter);
        }
    }
}

