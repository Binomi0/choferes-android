package com.contenedoressatur.android.choferesandroid.Pedidos;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 18/02/2018.
 */

public class Pedido {
    private static final String TAG = Pedido.class.getSimpleName();

    private String product;
    private int orderId;
    private String address;
    private LatLng coords;
    private String fechaPedido;
    private String status;

    Pedido(String product, Integer id, String address, LatLng coords, String fechaPedido, String status) {
        this.product = product;
        this.orderId = id;
        this.coords = coords;
        this.address = address;
        this.status = status;
        this.fechaPedido = getFormattedDate(fechaPedido);

    }

    public String getProduct() {
        return product;
    }

    public String getOrderId() {
        return String.valueOf(orderId);
    }

    public String getAddress() {
        return address;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public String getStatus() {
        return status;
    }

    private String getFormattedDate(String dateToFormat) {

        SimpleDateFormat outputPattern = new SimpleDateFormat("EEEE, d 'de' MMMM 'a las' kk:mm", new Locale("es"));
        SimpleDateFormat inputPatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.000000", new Locale("es"));

        Date date = null;
        String fecha = null;
        try {
            date = inputPatter.parse(dateToFormat);
            fecha = outputPattern.format(date);
            Log.i(TAG, "La fecha formateada es" + fecha);
        } catch (ParseException e) {
            Log.i(TAG, "No se ha podido parsear la fecha" + fechaPedido);
            e.printStackTrace();
        }
        return fecha;
    }

}
