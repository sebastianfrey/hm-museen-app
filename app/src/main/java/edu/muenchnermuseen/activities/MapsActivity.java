package edu.muenchnermuseen.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import edu.muenchnermuseen.R;

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
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import edu.muenchnermuseen.db.DataBaseHelper;
import edu.muenchnermuseen.db.dao.CategoryDAO;
import edu.muenchnermuseen.db.dao.MuseumDAO;
import edu.muenchnermuseen.entities.Category;
import edu.muenchnermuseen.entities.Museum;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        LocationListener, SearchView.OnSuggestionListener {

    private final static int LOCATION_PERMISSION = 1;

    private GoogleMap map;
    private GeoApiContext context;
    private Museum museum;
    SearchView searchView;
    MenuItem searchMenuItem;
    DataBaseHelper db;
    MuseumDAO museumDAO;
    CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DataBaseHelper(this);
        categoryDAO = new CategoryDAO(db);
        museumDAO = new MuseumDAO(db);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String key = getResources().getString(R.string.google_directions_api_key);
        context = new GeoApiContext().setApiKey(key);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            museum = (Museum) b.getSerializable("museum");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(this);

        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position)
    {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        CursorAdapter c = searchView.getSuggestionsAdapter();

        Cursor cur = c.getCursor();
        cur.moveToPosition(position);
        int id = cur.getInt(cur.getColumnIndex(BaseColumns._ID));

        Museum museum = museumDAO.getMuseumById(id);

        if (museum != null)
        {
            searchMenuItem.collapseActionView();
            searchView.clearFocus();
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("museum", museum);
            intent.putExtras(b);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        return false;
*/
//         Handle action bar item clicks here. The action bar will
//         automatically handle clicks on the Home/Up button, so long
//         as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int categoryId = -1;

        switch (id)
        {
            case R.id.nav_category_technology:
                categoryId = 0;
                break;

            case R.id.nav_category_history:
                categoryId = 1;
                break;

            case R.id.nav_category_nature:
                categoryId = 2;
                break;

            case R.id.nav_category_art:
                categoryId = 3;
                break;

        }

        if (categoryId > -1)
        {
            Category category = categoryDAO.getCategory(categoryId);
            Intent intent = new Intent(this, MuseumActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("category", category);
            intent.putExtras(b);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

            int resId;
            switch (museum.getCategory().getId())
            {
                case 0:
                    resId = R.mipmap.mp_category_technology;
                    break;

                case 1:
                    resId = R.mipmap.mp_category_history;
                    break;

                case 2:
                    resId = R.mipmap.mp_category_nature;
                    break;

                case 3:
                    resId = R.mipmap.mp_category_art;
                    break;

                default:
                    resId = R.mipmap.ic_img_not_found;
            }

            map.addMarker(
                    new MarkerOptions()
                            .position(polyline.get(polyline.size() - 1))
                            .icon(BitmapDescriptorFactory.fromResource(resId))
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
