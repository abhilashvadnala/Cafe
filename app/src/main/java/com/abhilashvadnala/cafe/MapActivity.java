package com.abhilashvadnala.cafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import java.util.Arrays;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap googleMap;

    LatLng myLocation;


    class Cafe extends CafeBasic  {
        LatLng location;
        boolean searchResult;
        Marker marker;
        String icon;
    }

    ArrayList<Cafe> cafes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        cafes = new ArrayList<Cafe>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().toString().equals("Recycler Activity")) {
                    Intent i =new Intent(MapActivity.this, RecyclerActivity.class);
                    //EXTRA_CAFE
                    ArrayList<CafeBasic> cb = new ArrayList<CafeBasic>();
                    for(Cafe c:cafes)cb.add(new CafeBasic(c.name,c.address,c.rating));
                    i.putExtra("EXTRA_CAFE",cb);
                    startActivity(i);
                    finish();
                }
                return false;
            }
        });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(MapActivity.this.getLocalClassName(), "Place: " + place.getName() + ", " + place.getId());
                //Toast.makeText(MapActivity.this ,place.getLatLng().toString(), Toast.LENGTH_LONG).show();
                queryNearbyCafes(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(MapActivity.this.getLocalClassName(), "An error occurred: " + status);
            }
        });
    }

    private void queryNearbyCafes(final LatLng latLng) {
        final HashMap<String,String> map = new HashMap<String,String>();
        map.put("key", getResources().getString(R.string.google_maps_key));
        map.put("location",latLng.latitude+","+latLng.longitude);
        map.put("radius", "1000");
        map.put("type", "cafe");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = getRequest("https://maps.googleapis.com/maps/api/place/nearbysearch/json",map);
                try {
                    loadCafesFromResponse(response,latLng);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshMarkers();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void refreshMarkers() {
        LatLngBounds.Builder latLngBounds = LatLngBounds.builder();
        for(int i=0;i<cafes.size();i++){
            if(cafes.get(i).marker!=null){
                cafes.get(i).marker.remove();
                cafes.remove(i);
                i--;
            }else{
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(cafes.get(i).name)
                        .position(cafes.get(i).location)
                        .icon(BitmapDescriptorFactory.defaultMarker(cafes.get(i).searchResult?BitmapDescriptorFactory.HUE_RED:BitmapDescriptorFactory.HUE_ORANGE));

                cafes.get(i).marker = googleMap.addMarker(markerOptions);
                latLngBounds.include(cafes.get(i).location);
                System.out.println(cafes.get(i).location);
            }
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(),50));

    }

    private void loadCafesFromResponse(String response,LatLng searchLatLng) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray results = jsonObject.getJSONArray("results");
        cafes.clear();
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);

            Cafe cafe = new Cafe();

            cafe.name = result.has("name")?result.getString("name"):"Unknown";
            cafe.address = result.has("vicinity") ? result.getString("vicinity") : "Unknown";
            cafe.rating = result.has("rating") ? result.getDouble("rating") : 0;
            cafe.icon = result.has("icon") ? result.getString("icon") : "Unknown";
            JSONObject loc = result.getJSONObject("geometry").getJSONObject("location");
            cafe.location = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
            cafe.searchResult = cafe.location.equals(searchLatLng);
            cafes.add(cafe);
        }
    }

    private static String getRequest(String url, HashMap<String, String> requestParams) {
        String prepend="?";
        for(Entry<String,String> entry:requestParams.entrySet()) {
            url += prepend+ entry.getKey()+"="+entry.getValue();
            prepend="&";
        }
        try {
            System.out.println(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            StringBuilder sb=new StringBuilder();
            String str;
            while((str=br.readLine())!=null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    boolean first=true;
    public void showMyLocation() {
        if(googleMap == null || myLocation == null)
            return;
        if(!first)
            return;
        first=false;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,18));
        MarkerOptions markerOptions = new MarkerOptions().title("Me").position(myLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        showMyLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.myLocation = new LatLng(location.getLatitude(),location.getLongitude());
        showMyLocation();
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
