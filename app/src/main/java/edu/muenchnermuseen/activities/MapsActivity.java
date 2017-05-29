package edu.muenchnermuseen.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.muenchnermuseen.R;
import edu.muenchnermuseen.entities.Museum;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private final static int LOCATION_PERMISSION = 1;

    private Location location;
    private GoogleMap map;
    private GeoApiContext context;
    private Museum museum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String key = getResources().getString(R.string.google_maps_key);
        context = new GeoApiContext().setApiKey("AIzaSyBJFp8TrR1JYC9wW3hRykD1zAXCk2QzyGg");

        Bundle b = getIntent().getExtras();
        if(b != null) {
            museum = (Museum) b.getSerializable("museum");
        }
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
        map = googleMap;
        map.setMyLocationEnabled(true);

        if (checkLocationPermission())
        {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                initLocationListener();
            }
        }
    }


    private boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Get permission for location.")
                        .setMessage("Do it.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION);
                            }
                        })
                        .create()
                        .show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION);
            }

            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) throws SecurityException {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initLocationListener();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    private void initLocationListener() throws SecurityException
    {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        locationManager.requestSingleUpdate(criteria, this, null);
    }


    private void doDirection(Location location)
    {
        if (museum == null || location == null) return;

        DirectionsResult result = null;

        String origin = MessageFormat.format("{0},{1}", location.getLatitude(), location.getLongitude());
        String destination = MessageFormat.format("{0},{1}", museum.getLat(), museum.getLon());

        try {
            result = DirectionsApi.newRequest(context)
                    .origin(origin)
                    .destination(destination)
                    .mode(TravelMode.WALKING)
                    .await();
        }
        catch (Exception e)
        {
            Log.e("MapsActivity", e.getMessage(), e);
        }

        if (result != null && result.routes.length > 0)
        {
            DirectionsRoute route = result.routes[0];
            List<LatLng> polyline = new ArrayList<>();

            polyline.add(new LatLng(location.getLatitude(), location.getLongitude()));

            for (com.google.maps.model.LatLng vertex : route.overviewPolyline.decodePath())
            {
                polyline.add(new LatLng(vertex.lat, vertex.lng));
            }

            map.addMarker(
                    new MarkerOptions()
                            .position(polyline.get(0))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );
            map.addPolyline(
                    new PolylineOptions()
                            .addAll(polyline)
            );
            map.addMarker(
                    new MarkerOptions()
                            .position(polyline.get(polyline.size() - 1))
            );

            map.moveCamera(getCameraUpdate(route));
        }
    }


    private CameraUpdate getCameraUpdate(DirectionsRoute route)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(new LatLng(route.bounds.northeast.lat, route.bounds.northeast.lng));
        builder.include(new LatLng(route.bounds.southwest.lat, route.bounds.southwest.lng));

        return CameraUpdateFactory.newLatLngBounds(builder.build(), 200);
    }

    @Override
    public void onLocationChanged(Location location) {
        doDirection(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
