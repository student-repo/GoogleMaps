package com.example.ubuntu_master.googlemaps;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.level;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<CityInfo> citiesInfo;
    private PolylineOptions poption;
    private List<Polyline> polylines = new ArrayList<Polyline>();
    private LatLng addCityLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        citiesInfo = getCityInfo();
        poption = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
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

    private void updateCitiesToSpinner() {
        Spinner s = (Spinner) findViewById(R.id.spinner);

        String[] arraySpinner = updateCitiesNames();

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

    private String[] updateCitiesNames(){
        String[] arraySpinner = new String[citiesInfo.size()];


        for(int i = 0; i < citiesInfo.size(); i++ ){
            arraySpinner[i] = citiesInfo.get(i).getName();
        }
        return arraySpinner;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                final LinearLayout l = (LinearLayout)findViewById(R.id.appendNewCityView);
                final Spinner s = (Spinner) findViewById(R.id.spinner);
                s.setVisibility(View.GONE);
                l.setVisibility(View.VISIBLE);
                addCityLatLng = latLng;
            }
        });

        for (CityInfo c : citiesInfo) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(c.getCoordinateX(), c.getCoordinateY())).title(c.getName()));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(citiesInfo.get(0).getCoordinateX(), citiesInfo.get(0).getCoordinateY())));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), level));
                poption.add(marker.getPosition());
                polylines.add(mMap.addPolyline(poption));
                return false;
            }
        });


    }

    public void handleAddCity(View v) {
        final LinearLayout l = (LinearLayout)findViewById(R.id.appendNewCityView);
        final Spinner s = (Spinner) findViewById(R.id.spinner);
        EditText et = (EditText)findViewById(R.id.addCityEditText);
            citiesInfo.add(0, new CityInfo(et.getText().toString(), addCityLatLng.latitude, addCityLatLng.longitude));
            mMap.addMarker(new MarkerOptions().position(new LatLng(addCityLatLng.latitude, addCityLatLng.longitude)).title(et.getText().toString()));
            et.setText("");
            updateCitiesToSpinner();
            s.setVisibility(View.VISIBLE);
            l.setVisibility(View.GONE);

    }

    public void handleExitAddCity(View v){
        final LinearLayout l = (LinearLayout)findViewById(R.id.appendNewCityView);
        final Spinner s = (Spinner) findViewById(R.id.spinner);

        s.setVisibility(View.VISIBLE);
        l.setVisibility(View.GONE);
    }

    public void removePath(View v){
        for(Polyline line : polylines)
        {
            line.remove();
        }
        polylines.clear();
        poption = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

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
