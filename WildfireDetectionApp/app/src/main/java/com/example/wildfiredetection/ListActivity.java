package com.example.wildfiredetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    //Url for fires data
    //private static final String url = "https://wildfiredetection-fdd86.firebaseio.com/";

    private static final String url = "http://localhost:8000/australia_jan.json";

    Button btnBack;
    ListView listView;
    //The fire list to store all fire objects after parsing json
    List<Fire> fireList;
    //Create a requestQueue
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        btnBack = findViewById(R.id.button7);
        listView = findViewById(R.id.list);
        fireList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBack = new Intent(ListActivity.this, MapActivity.class);
                startActivity(goBack);
            }
        });

        //Call getFireList() to fetch and parse data
        getFireList();
    }

    private void getFireList(){

        //Request a response from url as string
        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Get json object from the response
                            JSONObject jsonObject = new JSONObject(response);

                            //Get the "Australia" array from the json file stored in url 'http'
                            JSONArray fireJsonArray = jsonObject.getJSONArray("Australia");

                            //Iterate over all elements in json array
                            for (int i = 0; i < fireJsonArray.length(); i++) {
                                //Get the json object at a particular index of json array
                                JSONObject fireJsonObject = fireJsonArray.getJSONObject(i);

                                //Create a fire object and give them the values from json object
                                Fire fire = new Fire(fireJsonObject.getString("acq_date"),
                                        fireJsonObject.getString("acq_time"),
                                        fireJsonObject.getString("location"),
                                        fireJsonObject.getDouble("latitude"),
                                        fireJsonObject.getDouble("longitude"));

                                //Add fire object to fireList
                                fireList.add(fire);
                            }

                            //Create an adapter object
                            ListViewAdapter adapter = new ListViewAdapter(fireList, getApplicationContext());

                            //Add adapter to listView
                            listView.setAdapter(adapter);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Display error message
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        //Toast.makeText(ListActivity.this, "Server not running", Toast.LENGTH_SHORT).show();
                    }
                });
        //Add the string request to request queue
        requestQueue.add(strRequest);
    }
}



