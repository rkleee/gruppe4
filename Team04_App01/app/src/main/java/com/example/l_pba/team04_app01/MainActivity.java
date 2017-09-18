package com.example.l_pba.team04_app01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    public LocationManager lmanager;
    public LocationListener llistner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidGVhbTA0IiwiYSI6ImNqN3B5cTVxdTNqMnIzM3FwYmhoeWczczgifQ._hVIhv2V3zLw8TNOCeLc7A");
        setContentView(R.layout.activity_main);

        /** Initialize a mapView  */
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // set MapBox streets style.
        mapView.setStyleUrl(Style.MAPBOX_STREETS);

        //Marker
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(48.13863, 11.57603))
                        .title("test_title")
                        .snippet("test_snippet"));
            }
        });


        //Location Manager
        lmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        llistner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!=null){
                    final double lat = location.getLatitude();
                    final double lon = location.getLongitude();

                    /** Camera jumps to actual position **/

                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {
                            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                    .target(new LatLng(lat, lon))
                                    .build());
                        }
                    });
                }
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
        };

        //for API 23+
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        try{    //updates every 2 secs
            lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, llistner);
        }
        catch (SecurityException e){
            //---
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        lmanager.removeUpdates(llistner);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if ( (grantResults.length>0) && (grantResults[0]== PackageManager.PERMISSION_GRANTED) ){
                    try{
                        lmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, llistner);
                    }
                    catch (SecurityException e){
                        //--
                    }
                }
                break;
            default:

        }
    }
}
