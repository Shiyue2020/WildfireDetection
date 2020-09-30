package com.example.wildfiredetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnList;
    Button btnLogout;
    Button btnSOS;
    ImageButton btnCurrentLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    //represents the refresh interval in ms
    private final long MIN_TIME = 1000;
    //represents the distance in meters to update a location
    private final long MIN_DIST = 5;

    private LatLng latLng;
    ArrayList<LatLng> location = new ArrayList<LatLng>();
    private static final String TAG = "MapActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnList = findViewById(R.id.showList);
        btnLogout = findViewById(R.id.logout);
        btnSOS = findViewById(R.id.SOS);
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
                    //location requests
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        FirebaseDatabase myRef = FirebaseDatabase.getInstance();
        DatabaseReference ref = myRef.getReference();

        //Read from the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
	    //Create a datasnapshot object and get its data
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<LatLng> point = new ArrayList<LatLng>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Get values of latitude and longitude
                    LatLng latLng = new LatLng(Double.valueOf(snapshot.getKey()), Double.valueOf(snapshot.getKey()));

                    point.add(latLng);

                    //Loop through the fire data
                    for (int i = 0; i < point.size(); i++) {
                        //Add markers
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location.get(i)));
                        Toast.makeText(MapActivity.this, "Data retrieved successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    //generate address
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
                    // Check Permissions Now
                    ActivityCompat.requestPermissions(MapActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            0);

                    //Show settings to allow configuration of current location sources
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        };
    }

    public void onZoom(View view){
        if (view.getId() == R.id.zoomIn) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());

        }
        if (view.getId() == R.id.zoomOut) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }
}