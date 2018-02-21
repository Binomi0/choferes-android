package com.contenedoressatur.android.choferesandroid;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 18/02/2018.
 */

public class PedidosAdapter extends ArrayAdapter<Pedido> {

    private static final String TAG = PedidosAdapter.class.getSimpleName();
    static ArrayList<Pedido> pedidos;

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
        TextView getFechaPedido = (TextView) convertView.findViewById(R.id.fecha);

        product.setText(pedido.getProduct());
        id.setText(pedido.getOrderId());
        address.setText(pedido.getAddress());
        status.setText(pedido.getStatus());
        getFechaPedido.setText(pedido.getFechaPedido());

        return convertView;
    }














}

