package com.contenedoressatur.android.choferesandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * Created by user on 18/02/2018.
 */

public class PedidosAdapter extends ArrayAdapter<Pedido> {

    private static final String TAG = PedidosAdapter.class.getSimpleName();

    public PedidosAdapter (Context context, ArrayList<Pedido> nuevosPedidos) {
        super(context, 0 , nuevosPedidos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lista_pedidos, parent, false);
        }

        Pedido pedido = getItem(position);

        TextView product = (TextView) convertView.findViewById(R.id.product);
        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView fechaPedido = (TextView) convertView.findViewById(R.id.fecha);

        product.setText(pedido.getProduct());
        id.setText(pedido.getOrderId());
        address.setText(pedido.getAddress());
        status.setText(pedido.getStatus());

        String text = "Pedido el " + String.valueOf(pedido.getFechaPedido());
        fechaPedido.setText(text);

        return convertView;
    }














}

