package com.example.wildfiredetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnList;
    Button btnLogout;
    Button btnSOS;
    ImageButton btnShare;
    ImageButton btnCurrentLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    //Represents the refresh interval in ms
    private final long MIN_TIME = 1000;
    //Represents the distance in meters to update a location
    private final long MIN_DIST = 5;

    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnList = findViewById(R.id.showList);
        btnLogout = findViewById(R.id.logout);
        btnSOS = findViewById(R.id.SOS);
        btnShare = findViewById(R.id.shareOnTwitter);
        btnCurrentLocation = findViewById(R.id.locationButton);

        //Get notified when the map is ready to use
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        btnList.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Intent showList = new Intent(MapActivity.this, ListActivity.class);
                startActivity(showList);
            }


        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(MapActivity.this, MainActivity.class);
                startActivity(register);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                PackageManager packageManager = getPackageManager();
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareText = "This is from the WildfireDetection app. Check the link for Australia's wildfires " +
                            "data in January 2020: http://localhost:8000/australia_jan.json";

                    PackageInfo info = packageManager.getPackageInfo("com.twitter.android", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    shareIntent.setPackage("com.twitter.android");

                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, "Share to Twitter"));
                }
                catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MapActivity.this, "Twitter not Installed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }


        });

        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Australia's emergency number
                String number = "000";

                //Enable calls
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel: " + number));
                //Requires permission checking
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                else if (number != null) {
                    startActivity(intent);
                }
            }
        });

        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MapActivity.this, "Please check your settings to enable location access", Toast.LENGTH_SHORT).show();

                    return;
                }
                else {
                    //Get GPS update
                    locationManager = (LocationManager) (getSystemService(LOCATION_SERVICE));
                    //Location requests
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    //Generate address
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    String address = addresses.get(0).getAddressLine(0);

                    mMap.addMarker(new MarkerOptions().position(latLng).title(address)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomIn());

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Empty
            }

            @Override
            public void onProviderEnabled(String provider) {
                //Empty
            }

            @Override
            public void onProviderDisabled(String provider) {

                if (ActivityCompat.checkSelfPermission(MapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MapActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            0);

                    //Show settings to allow configuration for current location
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        };
        //Execute the method
        new AsyncTaskGetMarker().execute();
    }


    private class AsyncTaskGetMarker extends AsyncTask<String , String, JSONArray> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            //Create a string to store json retrieved from assets
            String firesJsonString = getJsonFromAssets();
            try {
                //Store JsonString into JsonArray
                JSONArray firesJsonArray = new JSONArray(firesJsonString);
                return firesJsonArray;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            //If there's an exception above, return null
            return null;
        }

        //Iterate over the json file and assign markers to the map
        protected void onPostExecute (JSONArray result){
            if (result != null){
                for (int i =0; i <result.length(); i++){

                    try {
                        //Get the json object at a particular index of json array
                        JSONObject jsonObject= result.getJSONObject(i);
                        String location = jsonObject.getString("location");
                        String latitude = jsonObject.getString("latitude");
                        String longitude = jsonObject.getString("longitude");

                        latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng).title(location)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mMap.addMarker(markerOptions);
                        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(latLng,5);
                        mMap.animateCamera(zoom);

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Get json from assets
    public String getJsonFromAssets() {
        String json;//null
        try {
            //Read australia_january.json from assets
            InputStream inputData = getAssets().open("australia_january.json");
            int size = inputData.available();
            byte[] buffer = new byte[size];
            inputData.read(buffer);
            inputData.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    //Zoom in and out
    public void onZoom(View view){
        if (view.getId() == R.id.zoomIn) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if (view.getId() == R.id.zoomOut) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }
}