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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mad.lab1.Database.LocalDB;
import mad.lab1.R;
import mad.lab1.Database.UserInfo;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton cancelBtn, borrowBtn;
    //private Location location;
    //private MapView mapView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager mLocationManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ValueEventListener userPositionListener;
    private List<UserInfo> users;
    private TextView descriptionTextView;
    private Marker selectedMarker = null;
    private Map<Marker, UserInfo> markUserMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(mLocationManager == null){
            Toast.makeText(getApplicationContext(), "null location manager", Toast.LENGTH_SHORT).show();
        }
        // set view
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        cancelBtn = findViewById(R.id.cancelButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

        borrowBtn = findViewById(R.id.borrowButton);
        borrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedMarker == null){
                    Toast.makeText(MapsActivity.this, "Please select a book", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MapsActivity.this, "Borrowed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        descriptionTextView = findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(" select a book and borrow it ");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device.
     * This method will only be triggered once the user has installed
     Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get permissions
        checkLocationPermission();



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // todo create a view to specify profile name, distance, ecc
                // todo or open the bookInfoPage
                if(selectedMarker != marker)
                   selectedMarker = marker;

                Toast.makeText(getApplicationContext(), ""+markUserMap.get(marker).getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    public void addMarkers(){
        // add a list of all book titles of a user and the distance between curr position and users
        Location location = getLastKnownLocation();
        users = new LinkedList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");//FirebaseDatabase.getInstance().getReference("users");
        UserInfo userInfo = LocalDB.getUserInfo(this);
        //markerList = new LinkedList<>();
        if(location != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Integer minDistanceFound = new Integer(1000);
                    Marker  minDistanceMarker = null;
                    List<Marker> markerList = new LinkedList<>();

                    for(DataSnapshot user : dataSnapshot.getChildren()) {

                        UserInfo u = user.getValue(UserInfo.class);
                        if (!u.getName().equals(userInfo.getName())){
                            users.add(u);
                            //Toast.makeText(getApplicationContext(), u.getName()+" "+u.getLatitude(), Toast.LENGTH_SHORT).show();
                            Log.d("ADD", u.getName() + " " + u.getLatitude() + " " + users.size());
                            if (u.getLatitude() != null && u.getLongitude() != null) {
                                Double lat = new Double(u.getLatitude());
                                Double lng = new Double(u.getLongitude());
                                LatLng myLatLng = new LatLng(lat, lng);

                                Location l = new Location("");
                                l.setLatitude(lat);
                                l.setLongitude(lng);

                                Float distanceInMt = location.distanceTo(l);
                                Float distanceInKm = location.distanceTo(l) / 1000;
                                String distKm = String.format("%.2f", distanceInKm);
                                Integer distanceInKmInt = new Integer(distanceInKm.intValue());

                                Marker m;
                                if (distanceInKm < 0.1)
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(distanceInMt + " m"));
                                if (distanceInKm < 1)
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(distKm + " km"));
                                if (distanceInKm < 5)
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(distanceInKmInt.toString() + " km"));
                                else
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(distanceInKmInt.toString() + " km"));

                                if(minDistanceFound > distanceInKmInt){
                                    minDistanceFound = distanceInKmInt;
                                    minDistanceMarker = m;
                                }
                                if (distanceInKm < 1000)
                                    markerList.add(m);

                                // store correspondance between user and map
                                markUserMap.put(m, u);
                            }
                        }
                    }

                    // now I have the minimum distance of a marker. I can set an appropriate zoom
                    if(minDistanceMarker != null) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        for(Marker m : markerList)
                            boundsBuilder.include(new LatLng(m.getPosition().latitude, m.getPosition().longitude));
                        //boundsBuilder.include(new LatLng(minDistanceMarker.getPosition().latitude, minDistanceMarker.getPosition().longitude));
                        final LatLngBounds bounds = boundsBuilder.build();
                        int padding = 400; // offset from edges of the map in pixels
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,padding);
                        mMap.animateCamera(cameraUpdate);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else
            Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
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
                                ActivityCompat.requestPermissions(MapsActivity.this,
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
            Log.d("POS", "false");
            return false;
        } else {
            mMap.setMyLocationEnabled(true);
            Log.d("POS", "true");
            // once you have the permission, here you have to call initialization() and zoomLevel()
            //setZoomLevel();
            addMarkers();
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

                        // get current location enabled -> blue pointer on the map
                        mMap.setMyLocationEnabled(true);

                        //setZoomLevel();

                        addMarkers();
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

    private void setZoomLevel(){
        Location l = getLastKnownLocation();
        double curlat = l.getLatitude(); // l is null
        double curlon = l.getLongitude();
        LatLng currentpos = new LatLng(curlat, curlon);

        float zoomLevel = 10.0f; //This goes up to 21

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curlat, curlon), zoomLevel));
    }

    private Location getLastKnownLocation() {

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
