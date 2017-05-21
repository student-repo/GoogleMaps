package com.example.ubuntu_master.googlemaps;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.R.attr.level;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<CityInfo> citiesInfo;
    private PolylineOptions poption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        citiesInfo = getCityInfo();
        poption = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addCitiesToSpinner();


    }

    private void addCitiesToSpinner() {
        Spinner s = (Spinner) findViewById(R.id.spinner);

        String[] arraySpinner = getCitiesNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(citiesInfo.get(position).getCoordinateX(),
                        citiesInfo.get(position).getCoordinateY()), level));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private String[] getCitiesNames(){
        ArrayList<CityInfo> citiesInfo = getCityInfo();
        String[] arraySpinner = new String[citiesInfo.size()];


        for(int i = 0; i < citiesInfo.size(); i++ ){
            arraySpinner[i] = citiesInfo.get(i).getName();
        }
        return arraySpinner;
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);

        for (CityInfo c : citiesInfo) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(c.getCoordinateX(), c.getCoordinateY())).title(c.getName()));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(citiesInfo.get(0).getCoordinateX(), citiesInfo.get(0).getCoordinateY())));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), level));
                poption.add(marker.getPosition());
                mMap.addPolyline(poption);
                return false;
            }
        });
        mMap.addPolyline(poption);


    }

    public void removePath(View v){
//        poption = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        mMap.addPolyline(poption);

    }

    public String loadJSONFromRaw() {
        String json = null;
        try {
            Resources res = getResources();
            InputStream is = res.openRawResource(R.raw.cities);

            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            json = new String(b, "UTF-8");
        } catch (Exception e) {
            System.out.println("Fail during loading data from JSON ");

        }
        return json;

    }
    private ArrayList<CityInfo> getCityInfo() {
        ArrayList<CityInfo> citiesInfo = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(loadJSONFromRaw());
            JSONArray arr = json.getJSONArray("cities");
            for(int i = 0; i < arr.length(); i++) {
                JSONObject json2 = new JSONObject(arr.getString(i));
                citiesInfo.add(new CityInfo(json2.getString("city"), json2.getDouble("coordinateX"), json2.getDouble("coordinateY")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return citiesInfo;
    }
}
