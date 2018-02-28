package com.contenedoressatur.android.choferesandroid.Pedidos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.contenedoressatur.android.choferesandroid.R;

import java.util.ArrayList;

/**
 * Created by user on 18/02/2018.
 */

public class PedidosAdapter extends ArrayAdapter<Pedido> {

    private static final String TAG = PedidosAdapter.class.getSimpleName();

    public PedidosAdapter (Context context, ArrayList<Pedido> nuevosPedidos) {
        super(context, 0 , nuevosPedidos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

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
        status.setText(getStatus(pedido.getStatus()));

        String text = "Pedido el " + String.valueOf(pedido.getFechaPedido());
        fechaPedido.setText(text);

        return convertView;
    }

    public String getStatus(String status) {
        switch (status) {
            case "pending":
                return "pendiente";
            case "on-hold":
                return "en espera";
            case "processing":
                return "puesta";
            case "cambiando":
                return "cambio";
            case "retirando":
                return "retirada";
            default:
                return "estado";
        }
    }














}

