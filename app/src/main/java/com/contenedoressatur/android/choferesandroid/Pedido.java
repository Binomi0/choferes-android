package com.contenedoressatur.android.choferesandroid;

/**
 * Created by user on 18/02/2018.
 */

public class Pedido {

    private String product;
    private int orderId;
    private String address;
    private String fechaPedido;

    public Pedido(String product, Integer id, String address, String fechaPedido) {
        this.product = product;
        this.orderId = id;
        this.address = address;
        this.fechaPedido = fechaPedido;
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

    public String getFechaPedido() {
        return fechaPedido;
    }

}
