package com.cs465.team_award.accessiblesidewalks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;



import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Observer {

    private static final int REQUEST_FINE_LOCATION = 100;
    private GoogleMap mMap;

    private Location myLocation;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Curb> curbs;

    //Normal UI
    private ImageButton add_obstacle;
    private ImageButton info;

    //for the adding state UI
    private Button choose_location;
    private Button go_back;
    private ImageView pin;

    private View mapView;

    //Reference to the logic that manage the the data
    private Logic logic;


    private boolean add_ob_button_pressed;
    private LatLng camera_center_pos;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ArrayList<Polyline> lowVisLines = new ArrayList<>();

    private static String TAG = "DEBUGGING";

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;

    //For the shaking gesture
    private ShakeGestureDetector shakeDetector;
    private boolean nightMode;

    //For the resize of the curbs
    private ArrayList<Marker> curbsMarkers = new ArrayList<Marker>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ---- CODE TO SWITCH TO ONBOARDING ----//
        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(MapsActivity.this, PREF_USER_FIRST_TIME, "true"));

        Intent introIntent = new Intent(MapsActivity.this, OnboardingActivity.class);
        introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

        if (isUserFirstTime)
            startActivity(introIntent);
        // ---- END ONBOARDING CODE ---- //

        setContentView(R.layout.activity_maps);

        //Initialize the logic
        logic = Logic.getInstance();
        obstacles = logic.getObstacles();
        curbs = logic.getCurbs();


        //shaking gesture
        shakeDetector = new ShakeGestureDetector(this);
        logic.getSync().addObserver(this);


        //Prepare Layout
        initializeUI();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        // Location management methods
        getLastLocation();
        startLocationUpdates();


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(16.0f);


        for(Obstacle o: obstacles) {

            CustomInfoWindow customInfoWindow = new CustomInfoWindow(this);
            googleMap.setInfoWindowAdapter(customInfoWindow);

            Marker tempMarker = googleMap.addMarker(new MarkerOptions().position(o.getLoc())
                    .title(o.getType()).icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle)).anchor(1/2,1/2).snippet(o.getDescription()));

            o.setReferenceMarker(tempMarker);
        }

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }

        //To change the position of the current location button
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {

            //googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_mode));
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_mode));


            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 170, 10, 0);
        }


        //CURBS
        int height = (int) (50);
        int width = (int) (50);

        // Read your drawable from somewhere
        Drawable curbsRes[] = new Drawable[4];
        curbsRes[0] = ResourcesCompat.getDrawable(getResources(), R.drawable.curb_top_left, null);
        curbsRes[1] = ResourcesCompat.getDrawable(getResources(), R.drawable.curb_top_rigt, null);
        curbsRes[2] = ResourcesCompat.getDrawable(getResources(), R.drawable.curb_bottom_left, null);
        curbsRes[3] = ResourcesCompat.getDrawable(getResources(), R.drawable.curb_bottom_right, null);

        Bitmap curbsImages[] = new Bitmap[4];
        for (int i=0; i<curbsImages.length; i++){
            curbsImages[i]= Bitmap.createScaledBitmap(((BitmapDrawable)curbsRes[i]).getBitmap(), width, height, true);
        }

        // add curbs
        for (int i = 0; i < curbs.size(); i++) {
            switch (curbs.get(i).getOrientation()){
                case 0:
                    curbsMarkers.add(mMap.addMarker(new MarkerOptions().position(curbs.get(i).getPosition()).anchor(1f,1f)
                            .flat(true).title("Non accessible curb").icon(BitmapDescriptorFactory.fromBitmap(curbsImages[curbs.get(i).getOrientation()]))));
                    break;
                case 1:
                    curbsMarkers.add(mMap.addMarker(new MarkerOptions().position(curbs.get(i).getPosition()).anchor(0f,1f)
                            .flat(true).title("Non accessible curb").icon(BitmapDescriptorFactory.fromBitmap(curbsImages[curbs.get(i).getOrientation()]))));
                    break;
                case 2:
                    curbsMarkers.add(mMap.addMarker(new MarkerOptions().position(curbs.get(i).getPosition()).anchor(1f,0f)
                            .flat(true).title("Non accessible curb").icon(BitmapDescriptorFactory.fromBitmap(curbsImages[curbs.get(i).getOrientation()]))));
                    break;
                case 3:
                    curbsMarkers.add(mMap.addMarker(new MarkerOptions().position(curbs.get(i).getPosition()).anchor(0f,0f)
                            .flat(true).title("Non accessible curb").icon(BitmapDescriptorFactory.fromBitmap(curbsImages[curbs.get(i).getOrientation()]))));
                    break;
            }
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                float zoom = mMap.getCameraPosition().zoom;
                if(!curbs.isEmpty()){
                    if(zoom >15 && zoom <18.1){
                        for (int i = 0; i < 8; i++) {
                            curbsMarkers.get(i).setVisible(true);
                        }
                    }
                    else{
                        for (int i = 0; i < 8; i++) {
                            curbsMarkers.get(i).setVisible(false);
                        }
                    }
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "Hola mundo");
                for (Obstacle o:obstacles){
                    if(o.getReferenceMarker().equals(marker)){
                        Log.d(TAG, "Type: "+o.getType()+" / Description: "+o.getDescription());
                    }
                }
                return false;
            }
        });

        //draw polylines
        for(int i = 0; i < 5; i++){
            Polyline l = mMap.addPolyline(new PolylineOptions()
                    .add(LowVisStreets.pts.get(i*2), LowVisStreets.pts.get((i*2)+1)).color( ContextCompat.getColor(this, R.color.low_v)));
            //.color(R.color.low_v))
            if(!nightMode){
               l.setVisible(false);
            }

            lowVisLines.add(l);
        }

    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        long UPDATE_INTERVAL = 5000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        long FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        myLocation = location;
        LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));

    }


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(16)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }


    public void initButtonsNormalUI(){
        this.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent introIntent = new Intent(MapsActivity.this, OnboardingActivity.class);
                introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);

                startActivity(introIntent);
            }
        });

        this.add_obstacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AddObstacleActivity.class));
                logic.addNewObstacle();
            }
        });
    }

    public void initButtonsAddUI(){
        this.choose_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // get the current center position of the marker
                camera_center_pos = mMap.getCameraPosition().target;
                // add marker
                logic.getCurrentObstacle().setLoc(camera_center_pos);


                Marker tempMark = mMap.addMarker(new MarkerOptions().position(logic.getCurrentObstacle().getLoc())
                        .title(logic.getCurrentObstacle().getType()).icon(BitmapDescriptorFactory.fromResource(R.drawable.obstacle)).anchor(1/2,1/2));
                logic.getCurrentObstacle().setReferenceMarker(tempMark);


                logic.finishAdding();
                initializeUI();
            }

        });

        this.go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, AddObstacleActivity.class));
                logic.addNewObstacle();
            }
        });
    }

    public void initializeUI(){
        this.info = findViewById(R.id.info);
        this.add_obstacle = findViewById(R.id.add_obstacle);
        this.choose_location = findViewById(R.id.choose_location);
        this.go_back = findViewById(R.id.go_back);
        this.pin = findViewById(R.id.pin);


        if(logic.isAdding()){
            //To show
            this.pin.setVisibility(View.VISIBLE);
            this.choose_location.setVisibility(View.VISIBLE);
            this.go_back.setVisibility(View.VISIBLE);

            //To hide
            this.info.setVisibility(View.GONE);
            this.add_obstacle.setVisibility(View.GONE);

            //clickListeners
            initButtonsAddUI();

        }else{

            //To show
            this.info.setVisibility(View.VISIBLE);
            this.add_obstacle.setVisibility(View.VISIBLE);

            //To hide
            this.pin.setVisibility(View.GONE);
            this.choose_location.setVisibility(View.GONE);
            this.go_back.setVisibility(View.GONE);


            initButtonsNormalUI();

        }
    }

    public void changeMode() {
        if (nightMode) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_mode));

            for (Polyline l : lowVisLines) {
                l.setVisible(true);
            }

        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_mode));

            for (Polyline l : lowVisLines) {
                l.setVisible(false);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onResume() {
        super.onResume();
            if (shakeDetector != null) {
              shakeDetector.registerSensorListeners();
            }
    }

    protected void onPause(){
        super.onPause();
        if (shakeDetector != null) {
            shakeDetector.unRegisterSensorListeners();
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        nightMode = !nightMode;
        changeMode();
    }
}
