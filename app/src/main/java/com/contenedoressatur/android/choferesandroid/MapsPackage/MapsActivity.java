package com.contenedoressatur.android.choferesandroid.MapsPackage;


import com.contenedoressatur.android.choferesandroid.Pedido;
import com.contenedoressatur.android.choferesandroid.PedidosController;
import com.contenedoressatur.android.choferesandroid.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnCameraIdleListener,
        ActivityCompat.OnRequestPermissionsResultCallback

{

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = MapsActivity.class.getSimpleName();
    TextView mCameraTextView;
    private static Pedido pedido;

    GPSTracker gps;
    private static final LatLng base = new LatLng(38.2109726, -0.5749218);
    private static Marker ubicacionContenedor;
    private static LatLng ubicacionContenedorActualizada;
    private static LatLng ubicacionChofer;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    protected LocationManager locationManager;

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        gps = new GPSTracker(MapsActivity.this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle parametros = this.getIntent().getExtras();
        if (parametros != null) {
            Log.i(TAG, "[onCreate] Bundle: " + parametros.getInt("index"));
            int index = parametros.getInt("index");
//            pedido = pedidoArrayList.get(index);
            pedido = PedidosController.getPedido(index);
            Log.i(TAG, "Direccion => " + pedido.getAddress().getString("formatted_address"));
            ubicacionContenedorActualizada = new LatLng(pedido.getAddress().getDouble("lat"), pedido.getAddress().getDouble("lng"));
        } else {
            Log.i(TAG,"No tengo extradata");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mCameraTextView = (TextView) findViewById(R.id.camera_text);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                toast("Iniciando Drag");

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng newPos = marker.getPosition();
                mCameraTextView.setText(newPos.toString());

                ubicacionContenedor.setPosition(newPos);
                Log.i(TAG, "newPos => " + newPos);
                toast("Terminado Drag => ");
            }
        });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (gps.canGetLocation()) {
//            Location location = new Location(gps.getLocation());
            // gps enabled} // return boolean true/false
            Log.i(TAG, "canGetLocation => " + gps.canGetLocation());
            double latitude = gps.getLatitude() == 0.0 ? 38.2109726 : gps.getLatitude();
            double longitude = gps.getLongitude() == 0.0 ? -0.5749218 : gps.getLongitude();
            LatLng coordenadas2 = new LatLng(latitude + 0.1, longitude + 0.1);
            LatLngBounds bounds = new LatLngBounds(base, coordenadas2);
            mMap.setLatLngBoundsForCameraTarget(bounds);
            mMap.setOnCameraIdleListener(this);

            configureMarkers();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                gps.showSettingsAlert();
                Log.i(TAG, "isGPSEnabled => " + gps.isGPSEnabled);
                Log.i(TAG, "isRestricted => " + gps.isRestricted());
                Log.i(TAG, "isNetworkEnabled => " + gps.isNetworkEnabled);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.getLocation();
            gps.showSettingsAlert();
        }
    }

    public void configureMarkers() {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(ubicacionContenedorActualizada)
                .title(pedido.getAddress().getString("formatted_address"))
                .snippet("Id: " + pedido.getOrderId() + " Estado => " + pedido.getStatus())
                .draggable(true);
        ubicacionContenedor = mMap.addMarker(markerOptions);

    }

    @Override
    public void onCameraIdle() {
//        ubicacionContenedorActualizada = ubicacionContenedor.getPosition();
//        ubicacionContenedor.setPosition(ubicacionContenedorActualizada);
        mCameraTextView.setText(ubicacionContenedorActualizada.toString());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        if (hasCapture) {
            toast("tengo captura");
        } else {
            toast("No tengo captura");
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        toast("Actualizando ubicación");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            mPermissionDenied = false;
        }
    }

//    /**
//     * Displays a dialog with error message explaining that the location permission is missing.
//     */
//    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
//    }


    public void toast (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


    }

    public void OnResetClick(View view) {
        mMap.setLatLngBoundsForCameraTarget(null);
    }
}