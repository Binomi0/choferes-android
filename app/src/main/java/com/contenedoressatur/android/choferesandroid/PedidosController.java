package com.contenedoressatur.android.choferesandroid;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * Creado por user el 19/02/2018.
 *
 * Todos los derechos reservados.
 * #adolfoonrubia
 * adolfo.onrubia.es
 */

public class PedidosController {
    private static final String TAG = PedidosController.class.getSimpleName();

    static ArrayList<Pedido> pedidos = new ArrayList<>();

    static PedidosAdapter adapter;

    public PedidosController() {
        Log.i("PedidoController", "El estatus del controlador es ");
    }

    static void cargarTodosPedidos(String chofer) {
        Log.i("PedidoController", "cargarTodosPedidos()");
//        new JSONTask().execute("https://contenedoressatur.es/wp-json/wc/v2/orders?per_page=100&orderby=date", "all");
        new JSONTask().execute("https://contenedoressatur.es/wp-json/pedidos/v1/chofer/" + chofer);
    }

    static ArrayList<Pedido> getPedidos() {
        return pedidos;
    }
    public static Pedido getPedido(int index) {
        return pedidos.get(index);
    }

    private static void setPedidos(ArrayList<Pedido> nuevosPedidos) {
        pedidos = nuevosPedidos;
    }


    private static void parseResponseFromRequest(ArrayList<Pedido> response) {

        if (pedidos.contains(response)) {
            Log.i("[JSONTask] => pedidos","El array contiene " + response.size() + " elementos");
        } else {
            for (Pedido pedido: response
                 ) {
                Log.i("[PEDIDOS CONTROLLER","status => " + pedido.getStatus());
                Log.i("[PEDIDOS CONTROLLER","ID => " + pedido.getOrderId());
            }
            Log.i("[JSONTask] => pedidos","El array NO contiene el pedido");
            setPedidos(response);
        }

    }

    static class JSONTask extends AsyncTask<String, String, ArrayList<Pedido>> {
        @Override
        protected void onCancelled() {
            super.onCancelled();
            System.out.println("Se ha cancelado la peticion");
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected ArrayList<Pedido> doInBackground(String... strings) {
            Log.i("[JSONTask] doInBackgro","Empezando request en background");
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic Y2tfZjFlYmQ1OWRkMDc0NThhZWEwYzQyOTBhNGE4OGRjNmRjOWExZDNiYjpjc183OTc5OGIwYWE2OTQwYTAxY2NhODNlY2I5YmJlMWRlOTM4YzM2MmU0");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                Bundle addressBundle = new Bundle(3);
                ArrayList<Pedido> nuevospedidos = new ArrayList<>();
                String jsonData = buffer.toString();
//                JSONObject dataObject = new JSONObject(jsonData);
                JSONArray dataArray = new JSONArray(jsonData);
                if (dataArray.length() > 0) {
                    for (int i=0; i<dataArray.length(); i++) {
                        JSONObject pedido = dataArray.getJSONObject(i);
                        int id = pedido.getInt("id");
                        JSONObject fecha = pedido.getJSONObject("fecha");
                        String fechaPedido = fecha.getString("date");
                        String producto = pedido.getString("product");
                        String status = pedido.getString("status");
                        JSONArray address = pedido.getJSONArray("address");

                        if (address.length() > 0) {
                            JSONObject fullAddress = address.getJSONObject(0);
                            addressBundle.putString("formatted_address", fullAddress.getString("formatted_address"));
                            addressBundle.putDouble("lat", fullAddress.getDouble("lat"));
                            addressBundle.putDouble("lng", fullAddress.getDouble("lng"));
                        }

                        nuevospedidos.add(new Pedido(producto, id, addressBundle, fechaPedido, status));

                    }
                }

                return nuevospedidos;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Pedido> data) {
            super.onPostExecute(data);
            Log.i("[JSONTask] onPostExecut","Recibida la respuesta de la request en background");
            parseResponseFromRequest(data);

        }
    }




}
