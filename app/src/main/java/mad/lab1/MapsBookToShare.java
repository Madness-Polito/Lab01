package mad.lab1;

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
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import mad.lab1.madFragments.PlaceholderFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerFlag, new PlaceholderFragment()).commit();

        }

        // todo retrieve lat and lng and put in Boundle and get the last location

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
                    Toast.makeText(getApplicationContext(), "ERROR, FINAL POSITION NULL", Toast.LENGTH_LONG).show();
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
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get permissions
        checkLocationPermission();

        Location l = getLastKnownLocation();

        if(l == null) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            checkLocationPermission();
            l = getLastKnownLocation();
        }

        else {

            if(l == null){
                //checkLocationPermission();
                l = getLastKnownLocation();
            }

            gps = new GPSTracker(getApplicationContext(), this);

            double curlat = l.getLatitude(); // l is null
            double curlon = l.getLongitude();
            LatLng currentpos = new LatLng(curlat, curlon);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curlat, curlon), 10));
            //getLastKnownLocation();
            m = mMap.addMarker(new MarkerOptions().position(currentpos)
                    .title("Draggable Marker")
                    .snippet("Long press and move the marker if needed.")
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
                    Toast.makeText(MapsBookToShare.this, markerLocation.toString(), Toast.LENGTH_LONG).show();
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

    public void addMarkers(){
        /*
        Location location = getLastKnownLocation();
        //markerList = new LinkedList<>();
        if(location != null) {
                //Marker m;
                LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                //m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                // todo add title with some info on the marker if necessary
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLatLng) // set to Center
                    .build();                   // Creates a CameraPosition
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
            //for(Marker m1 : markerList)
            //    m1.showInfoWindow();
         */
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
        } else {
            mMap.setMyLocationEnabled(true);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        getLastKnownLocation();
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
}
