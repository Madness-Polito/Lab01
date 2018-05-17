package mad.lab1.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import mad.lab1.Database.LocalDB;
import mad.lab1.Database.UserInfo;
import mad.lab1.R;
import mad.lab1.Fragments.PlaceholderFragment;

public class MapsBookToShare extends AppCompatActivity {


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager mLocationManager;
    private FloatingActionButton btn_done;
    private FloatingActionButton btn_cancel;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Marker m; // only one that must be returned to the editProfile activity
    private GPSTracker gps;
    private LatLng finalPosition = null;
    Double curlat = null, curlon = null;
    Location currentLocation = null;
    private TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MARKER", "onCreate()");
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerFlag, new PlaceholderFragment()).commit();
            //
            getSavedLocation();
            //

        }
        else{
            Log.d("MARKER", "savedInstanceState != null");
            curlat = new Double(savedInstanceState.get("lat").toString()); // l is null
            curlon = new Double(savedInstanceState.get("lng").toString());
            currentLocation = new Location("");
            currentLocation.setLatitude(curlat);
            currentLocation.setLongitude(curlon);
        }

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(mLocationManager == null){
            Toast.makeText(getApplicationContext(), "null location manager", Toast.LENGTH_SHORT).show();
        }
        // set view
        setContentView(R.layout.activity_maps_book_to_share);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this::onMapReady);
        //mapFragment.getMapAsync(this);


        // set location done and cancel button -> return null or Location chosen
        btn_done = findViewById(R.id.doneButton);
        btn_cancel = findViewById(R.id.cancelLocationButton);

        btn_done.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng markerLocation = m.getPosition(); // todo m is null
                if(markerLocation != null){
                    finalPosition = markerLocation;
                    Intent intent = new Intent();
                    intent.putExtra("LatLng", finalPosition);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else
                    Log.d("ERROR", "ERROR, FINAL POSITION NULL");//Toast.makeText(getApplicationContext(), "ERROR, FINAL POSITION NULL", Toast.LENGTH_LONG).show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.putExtra("LatLng", userInfo);
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

        Toast.makeText(getApplicationContext(), "choose a location", Toast.LENGTH_LONG).show();

        descriptionText = findViewById(R.id.descriptionTextView);
        descriptionText.setText(" drag the marker and choose your location ");
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get permissions
        checkLocationPermission();



    }

    public void getSavedLocation(){

        // if lat and long are registered on firebase fill them
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        UserInfo userInfo = LocalDB.getUserInfo(this);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot user : dataSnapshot.getChildren()) {
                    UserInfo u = user.getValue(UserInfo.class);
                    // if user found, check if lat and long is null
                    //Log.d("MARKER", "user "+u.getName()+" / "+userInfo.getName());
                    if (u.getName().equals(userInfo.getName())){
                        if (u.getLatitude() != null && u.getLongitude() != null){
                            //Log.d("MARKER", "user found!");
                            curlat = new Double(u.getLatitude());
                            curlon = new Double(u.getLongitude());
                            currentLocation = new Location("");
                            currentLocation.setLatitude(curlat);
                            currentLocation.setLongitude(curlon);
                            Log.d("MARKER", "currLocation set");
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MARKER", "onCancelled() called");
            }
        });
        //Log.d("MARKER", "return currentLocation");
        //return currentLocation;
    }

    public void initialize(){


        if(curlat != null && curlon != null) {
            currentLocation = new Location("");
            currentLocation.setLatitude(curlat);
            currentLocation.setLongitude(curlon);
        }

        else {
            getSavedLocation();
            Log.d("MARKER", "currLoc == "+currentLocation);
            if(currentLocation == null)
                currentLocation = getLastKnownLocation();

        }




        if(currentLocation == null) {
            Toast.makeText(this, R.string.location_not_available, Toast.LENGTH_SHORT).show();
            //finish();
        }

        else {

            gps = new GPSTracker(getApplicationContext(), this);

            LatLng currentpos = setZoomLevel();

            //getLastKnownLocation();
            Log.d("MARKER", "--> added");
            m = mMap.addMarker(new MarkerOptions().position(currentpos)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDrag(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("Marker", "Dragging");
                }

                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    // TODO Auto-generated method stub
                    LatLng markerLocation = m.getPosition();
                    //Toast.makeText(MapsBookToShare.this, markerLocation.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Marker", "finished");
                }

                @Override
                public void onMarkerDragStart(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("Marker", "Started");

                }
            });
        }
    }

    private LatLng setZoomLevel(){
        Location l = null;
        if(currentLocation == null)
            l = getLastKnownLocation();
        else
            l = currentLocation;
        double curlat = l.getLatitude(); // l is null
        double curlon = l.getLongitude();
        LatLng currentpos = new LatLng(curlat, curlon);
        // must be set according to markers location
        float zoomLevel = 10.0f; //This goes up to 21

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curlat, curlon), zoomLevel));
        return currentpos;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsBookToShare.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
            // then it calls callback down
        } else {
            // get current location enabled -> blue pointer on the map
            mMap.setMyLocationEnabled(true);
            Log.d("MARKER", "no request permission ret true");
            // when you have the location permissions, this method returns true and initialize() is called from here
            initialize();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.d("MARKER", "permission requesting");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        // first time is asked for permission so initialize() must be called here, the other times in checkLocationPermission where true is returned
                        // get current location enabled -> blue pointer on the map
                        mMap.setMyLocationEnabled(true);
                        initialize();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "location permission not granted", Toast.LENGTH_SHORT);

                }
                return;
            }
        }
    }

    private Location getLastKnownLocation() {
        //checkLocationPermission();
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        Location l = null;
        for (String provider : providers) {
            try {
                l = mLocationManager.getLastKnownLocation(provider);
            } catch (SecurityException e){
                e.printStackTrace();
            }


            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //ALog.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        if(m != null) {
            b.putString("lat", new Double(m.getPosition().latitude).toString());
            b.putString("lng", new Double(m.getPosition().longitude).toString());
        }
    }

    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);

    }
}
