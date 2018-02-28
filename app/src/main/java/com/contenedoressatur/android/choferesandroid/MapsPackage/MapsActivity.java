package com.contenedoressatur.android.choferesandroid.MapsPackage;


import com.contenedoressatur.android.choferesandroid.Pedidos.Pedido;
import com.contenedoressatur.android.choferesandroid.Pedidos.PedidosController;
import com.contenedoressatur.android.choferesandroid.R;
import com.contenedoressatur.android.choferesandroid.woocommerce.HttpController;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.firebase.iid.FirebaseInstanceId;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
//        OnMarkerDragListener,
        OnCameraIdleListener,
        OnInfoWindowClickListener,
        OnInfoWindowLongClickListener,
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

    private GPSTracker gps;
    private static final LatLng base = new LatLng(38.2109726, -0.5749218);
    private static Marker markerUbicacionContenedor;
    private static LatLng coordsUbicacionContenedor;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

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
            pedido = PedidosController.getPedido(index);
            setTitle("Pedido " + pedido.getOrderId());
            coordsUbicacionContenedor = pedido.getCoords();

            configureButtons();

        } else {
            Log.i(TAG,"No tengo extradata");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mCameraTextView = findViewById(R.id.camera_text);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                toast("Buscando ubicación");

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng newPos = marker.getPosition();
                mCameraTextView.setText(pedido.getAddress());

                markerUbicacionContenedor.setPosition(newPos);
                Log.i(TAG, "newPos => " + newPos);
                toast("Actualizando posición...");
            }
        });

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Nueva Ubicación")
                .setMessage("¿Quieres actualizar la ubicación?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpController.updateOrderLocationFromMarker(markerUbicacionContenedor.getPosition(), pedido.getOrderId());

                        toast("Actualizando ubicación, por favor espera...");
                        onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (gps.canGetLocation()) {
            Log.i(TAG, "canGetLocation => " + gps.canGetLocation());

            LatLng posicionChofer = new LatLng(gps.getLatitude(), gps.getLongitude());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(posicionChofer);
            builder.include(coordsUbicacionContenedor);
            LatLngBounds bounds = builder.build();
            mMap.setLatLngBoundsForCameraTarget(bounds);

            configureMarkers();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                gps.showSettingsAlert();
                Log.i(TAG, "isGPSEnabled => " + gps.isGPSEnabled);
                Log.i(TAG, "isNetworkEnabled => " + gps.isNetworkEnabled);
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setOnCameraIdleListener(this);
            mMap.setMyLocationEnabled(true);
            toast("Localización encontrada - \nLat: " + gps.getLatitude() + "\nLong: " + gps.getLongitude());
        } else {
            gps.getLocation();
            gps.showSettingsAlert();
        }
    }

    public void configureMarkers() {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(coordsUbicacionContenedor)
                .title(pedido.getAddress())
                .snippet("Id: " + pedido.getOrderId() + " Estado => " + pedido.getStatus())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true);
        markerUbicacionContenedor = mMap.addMarker(markerOptions);

    }

    @Override
    public void onCameraIdle() {
//        ubicacionContenedorActualizada = markerUbicacionContenedor.getPosition();
//        markerUbicacionContenedor.setPosition(ubicacionContenedorActualizada);
        mCameraTextView.setText(pedido.getAddress());
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
        toast("Ubicación actual:\n" + location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            gps.showSettingsAlert();
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
    public void onInfoWindowLongClick(Marker marker) {

        toast("Info Window long click");
    }

    public void OnContenedorPuesto (View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmar Puesta")
                .setMessage("¿Está puesto el contenedor y ubicado correctamente?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpController.containerPlaced(pedido.getOrderId());
                        toast("Actualizando pedido, por favor espera...");
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void OnContenedorCambiado (View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmar Cambio")
                .setMessage("¿Está cambiado el contenedor?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpController.containerChanged(pedido.getOrderId());
                        toast("Actualizando pedido, por favor espera...");
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void OnContenedorRetirado(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmar Retirada")
                .setMessage("¿Está retirado el contenedor?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpController.containerRemoved(pedido.getOrderId());
                        toast("Actualizando pedido, por favor espera...");
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void configureButtons() {
        // Set up button from ui
        Button contenedor_puesto = findViewById(R.id.contenedor_puesto);
        Button contenedor_retirado = findViewById(R.id.contenedor_retirado);
        Button contenedor_cambiado = findViewById(R.id.contenedor_cambiado);
        int color = getResources().getColor(R.color.colorPrimary);
        contenedor_retirado.setBackgroundColor(Color.parseColor("#cccccc"));
        contenedor_cambiado.setBackgroundColor(Color.parseColor("#cccccc"));
        contenedor_puesto.setBackgroundColor(Color.parseColor("#cccccc"));
        contenedor_retirado.setEnabled(false);
        contenedor_cambiado.setEnabled(false);
        contenedor_puesto.setEnabled(false);

        switch (pedido.getStatus()) {
            case "processing":
                contenedor_puesto.setEnabled(true);
                contenedor_puesto.setBackgroundColor(color);
                break;
            case "cambiando":
                contenedor_cambiado.setEnabled(true);
                contenedor_cambiado.setBackgroundColor(color);
                break;
            case "retirando":
                contenedor_retirado.setEnabled(true);
                contenedor_retirado.setBackgroundColor(color);
        }


    }




}