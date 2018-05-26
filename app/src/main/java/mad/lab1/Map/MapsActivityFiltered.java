package mad.lab1.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mad.lab1.Database.Book;
import mad.lab1.Database.LocalDB;
import mad.lab1.FinalBookingConfirmationActivity;
import mad.lab1.Fragments.ShowSelectedBookInfo;
import mad.lab1.R;
import mad.lab1.Database.UserInfo;


public class MapsActivityFiltered extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton cancelBtn;
    private FloatingActionButton borrowBtn;
    //private Location location;
    //private MapView mapView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager mLocationManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ValueEventListener userPositionListener;
    private List<UserInfo> users;
    private List<String> userIds;
    private String isbn;
    private Book book;
    private Map<Marker, UserInfo> markUserMap = new HashMap<>();
    private Marker selectedMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isbn = extras.getString("isbn");
        }

        getBook();

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
        runFirstTimeTutotrial();
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

        Location currentLocation =  getLastKnownLocation();
        LatLng latLng;
        if(currentLocation != null) {
            latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }else{
            latLng = new LatLng(0, 0);
        }
        float zoomLevel = 10.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        addMarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //todo create a view to specify profile name, distance, ecc
                // todo or open the bookInfoPage
                if(selectedMarker != marker)
                    selectedMarker = marker;


                Bundle arg = new Bundle();
                arg.putParcelable("book", book);
                arg.putParcelable("user", markUserMap.get(marker));
                Intent i = new Intent(getApplicationContext(), FinalBookingConfirmationActivity.class);
                i.putExtra("argument", arg);
                startActivity(i);


                //Toast.makeText(getApplicationContext(), "Book borrowed , "+markUserMap.get(marker).getName(), Toast.LENGTH_SHORT).show();
                //finish();
                return false;
            }
        });

    }

    public void addMarkers(){
        Log.d("here", "addmarkers()");
        // add a list of all book titles of a user and the distance between curr position and users
        Location location = getLastKnownLocation();
        users = new LinkedList<>();
        userIds = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("isbnOwners").child(isbn);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    //select the keys of owners of this book
                    userIds.add(user.getKey());
                }

                placeMarkers(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("");

            }
        });


    }

    private void runFirstTimeTutotrial(){
        //run first time tutorial
        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("FilteredMapFirstStart", true);

        if (isFirstStart) {
            showcase("confirm");
            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();

            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("FilteredMapFirstStart", false);

            //  Apply changes
            e.apply();
        }
    }

    private void showcase(String btn){

        switch(btn){
            case "cancel":

                    new ShowcaseView.Builder(this)
                            .withMaterialShowcase()
                            .setStyle(R.style.CustomShowcaseTheme2)
                            .setTarget(new ViewTarget(cancelBtn))
                            .setContentTitle("cancel")
                            .setContentText("press this button to come back to book description")
                            .setShowcaseEventListener(
                                    new SimpleShowcaseEventListener(){
                                        @Override
                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                            //showcase("confirm");
                                        }
                                    }
                            )
                            .build();

                    break;

            case "confirm":
                new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setTarget(new ViewTarget(borrowBtn))
                        .setContentTitle("borrow")
                        .setContentText("press this button to borrow the selected book on the map")
                        .setShowcaseEventListener(
                                new SimpleShowcaseEventListener(){
                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                        showcase("cancel");
                                    }
                                }
                        )
                        .build();

                break;
        }

    }

    private void placeMarkers(Location location){



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");//FirebaseDatabase.getInstance().getReference("users");
        Log.d("here", "place markers");
        //markerList = new LinkedList<>();
        if(location != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //DataSnapshot userSnapshot = dataSnapshot.child("users");
                    //Iterable<DataSnapshot> usersChildren = userSnapshot.getChildren();

                    //Toast.makeText(MapsActivity.this, "bauuuuuu", Toast.LENGTH_SHORT).show();
                    List<Marker> markerList = new LinkedList<>();
                    for(DataSnapshot user : dataSnapshot.getChildren()) {

                        UserInfo u = user.getValue(UserInfo.class);
                        if(userIds.contains(u.getUid()) && !u.getName().equals(LocalDB.getUserInfo(getApplicationContext()).getName())){
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
                                /*if (distanceInKm < 0.1)
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(distanceInMt + " m"));
                                */
                                if (distanceInKm < 1)
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(distKm + " km"));
                                else if (distanceInKm < 5)
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(distanceInKmInt.toString() + " km"));
                                else
                                    m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(distanceInKmInt.toString() + " km"));

                                markerList.add(m);

                                //store correspondance between user and marker
                                markUserMap.put(m, u);
                            }
                        }
                    }

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    if(markerList.size()==0){
                        setZoomLevel();
                    }
                    else {
                        Log.d("here", "add markers");

                        for (Marker m : markerList)
                            boundsBuilder.include(new LatLng(m.getPosition().latitude, m.getPosition().longitude));

                        boundsBuilder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                        final LatLngBounds bounds = boundsBuilder.build();
                        int padding = 100; // offset from edges of the map in pixels
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cameraUpdate);



                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else{
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //DataSnapshot userSnapshot = dataSnapshot.child("users");
                    //Iterable<DataSnapshot> usersChildren = userSnapshot.getChildren();

                    //Toast.makeText(MapsActivity.this, "bauuuuuu", Toast.LENGTH_SHORT).show();
                    List<Marker> markerList = new LinkedList<>();
                    for(DataSnapshot user : dataSnapshot.getChildren()) {

                        UserInfo u = user.getValue(UserInfo.class);
                        if(userIds.contains(u.getUid()) && !u.getName().equals(LocalDB.getUserInfo(getApplicationContext()).getName())){
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

                                Marker m;

                                m = mMap.addMarker(new MarkerOptions().position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                markerList.add(m);

                                //store correspondance between user and marker
                                markUserMap.put(m, u);
                            }
                        }
                    }

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    if(markerList.size()==0){
                        setZoomLevel();
                    }
                    else {
                        Log.d("here", "add markers");
                        //for (Marker m : markerList)
                            //boundsBuilder.include(new LatLng(m.getPosition().latitude, m.getPosition().longitude));

                        boundsBuilder.include(putDefaultLocations("Torino"));
                        //boundsBuilder.include(putDefaultLocations("Milano"));
                        //boundsBuilder.include(putDefaultLocations("Roma"));
                        //boundsBuilder.include(putDefaultLocations("Palermo"));
                        boundsBuilder.include(putDefaultLocations("Taranto"));
                        final LatLngBounds bounds = boundsBuilder.build();
                        int padding = 200; // offset from edges of the map in pixels
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cameraUpdate);
                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Toast.makeText(this, R.string.location_not_available, Toast.LENGTH_LONG).show();
        }
    }


    public LatLng putDefaultLocations(String desideredLocation){
        if(Geocoder.isPresent()){
            try {
                String location = desideredLocation;
                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(location, 5); // get the found Address Objects

                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                if(ll.size()!= 0)
                    return ll.get(0);
                else
                    return null;
            } catch (IOException e) {
                // handle the exception
            }
        }
        return null;
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
                                ActivityCompat.requestPermissions(MapsActivityFiltered.this,
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
            setZoomLevel();
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

                        setZoomLevel();

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
        if(l!= null) {
            double curlat = l.getLatitude(); // l is null
            double curlon = l.getLongitude();
            LatLng currentpos = new LatLng(curlat, curlon);

            float zoomLevel = 10.0f; //This goes up to 21

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curlat, curlon), zoomLevel));
        }
        //else
            //Toast.makeText(this, R.string.location_not_available, Toast.LENGTH_LONG).show();
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

    private void getBook(){
        //gets a book with the specified isbn. Doesn't use the specified user yet.
        //TODO: link marker to user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("bookID");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot bookDS : dataSnapshot.getChildren()){
                    Book currentBook = bookDS.getValue(Book.class);
                    if(currentBook.getIsbn().equals(isbn)){
                        book = currentBook;
                        book.setBookId(bookDS.getKey());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
