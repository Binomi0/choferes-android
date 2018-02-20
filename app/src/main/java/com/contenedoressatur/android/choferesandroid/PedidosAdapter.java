package com.contenedoressatur.android.choferesandroid;

import android.content.Context;
import android.support.annotation.Nullable;
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

    static ArrayList<Pedido> pedidos;

    public PedidosAdapter (Context context, ArrayList<Pedido> nuevosPedidos) {
        super(context, 0 , nuevosPedidos);
        pedidos = nuevosPedidos;
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        super.setNotifyOnChange(notifyOnChange);
        System.out.println("[PedidosAdapter](setNotifyOnChange) El objeto ha cambiado");
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
        TextView getFechaPedido = (TextView) convertView.findViewById(R.id.status);

        product.setText(pedido.getProduct());
        id.setText(pedido.getOrderId());
        address.setText(pedido.getAddress());
        getFechaPedido.setText(pedido.getFechaPedido());

        return convertView;
    }














}

