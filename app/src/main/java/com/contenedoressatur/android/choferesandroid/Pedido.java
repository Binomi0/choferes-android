package com.contenedoressatur.android.choferesandroid;

/**
 * Created by user on 18/02/2018.
 */

public class Pedido {

    private String product;
    private int orderId;
    private String address;
    private String fechaPedido;
    private String status;

    public Pedido(String product, Integer id, String address, String fechaPedido, String status) {
        this.product = product;
        this.orderId = id;
        this.address = address;
        this.fechaPedido = fechaPedido;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

}
