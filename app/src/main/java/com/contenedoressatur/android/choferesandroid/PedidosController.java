package com.contenedoressatur.android.choferesandroid;

import android.app.ActivityManager;
import android.os.AsyncTask;
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
import java.util.ArrayList;

/**
 *
 * Creado por user el 19/02/2018.
 *
 * Todos los derechos reservados.
 * #adolfoonrubia
 * adolfo.onrubia.es
 */

public class PedidosController {
    static private String status;

    static ArrayList<Pedido> pedidos = new ArrayList<>();
    static ArrayList<Pedido> cambios = new ArrayList<>();
    static ArrayList<Pedido> puestas = new ArrayList<>();
    static ArrayList<Pedido> retiradas = new ArrayList<>();

    public PedidosController(String status) {
        this.status = status;
        Log.i("PedidoController", "El estatus del controlador es " + status);
    }

    static ArrayList<Pedido> getPedidos(String newStatus) {
        if (status == null) {
            Log.i("PedidoController", "El estatus del pedido es " + status + " y estas pasando " + newStatus);
            status = newStatus;
        } else {
            if (!status.equals(newStatus)) {
                Log.i("PedidoController", "El estatus del pedido es " + status + " y estas pasando " + newStatus);
                pedidos = new ArrayList<>();
                if (pedidos.isEmpty()) {
                    Log.i("PedidoController", "pedidos esta vacio, lanzando consulta HTTPS");
                    new JSONTask().execute("https://contenedoressatur.es/wp-json/wc/v2/orders?status=" + status);
                }
                status = newStatus;
            }
        }

        return pedidos;
    }

    private static void setPedidos(ArrayList<Pedido> nuevosPedidos) {
        pedidos = nuevosPedidos;

    }


    static void parseResponseFromRequest(ArrayList<Pedido> pedidos) {

        if (pedidos.contains(pedidos)) {
            System.out.println("El array ya contiene el pedido");
            setPedidos(pedidos);
        } else {
            System.out.println("El array NO contiene el pedido");
            pedidos.add(new Pedido("Contenedor 5m", 6794, "Calle Ganaderos 32 SAnta Pola", "retirando"));
            setPedidos(pedidos);
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
            System.out.println("Empezando request en background");
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

                ArrayList<Pedido> nuevospedidos = new ArrayList<>();
                String jsonData = buffer.toString();
//                JSONObject dataObject = new JSONObject(jsonData);
                JSONArray dataArray = new JSONArray(jsonData);
                if (dataArray.length() > 0) {
                    for (int i=0; i<dataArray.length(); i++) {
                        String product;
                        JSONObject pedido = dataArray.getJSONObject(i);
                        int id = pedido.getInt("id");
                        String fechaPedido = pedido.getString("date_created");

                        JSONArray lineItems = pedido.getJSONArray("line_items");
                        if (!lineItems.isNull(0)) {
                            JSONObject productos = lineItems.getJSONObject(0);
                            product = productos.getString("name");
                        } else {
                            product = "No hay productos en el pedido";
                        }

                        JSONObject shipping = pedido.getJSONObject("shipping");
                        String address = shipping.getString("address_1");

                        if (pedido.getString("status").equals("processing")) {
                            puestas.add(new Pedido(product, id, address, fechaPedido));
                            return puestas;
                        }
                        if (pedido.getString("status").equals("cambiando")) {
                            cambios.add(new Pedido(product, id, address, fechaPedido));
                            return cambios;
                        }
                        if (pedido.getString("status").equals("retirando")) {
                            retiradas.add(new Pedido(product, id, address, fechaPedido));
                            return retiradas;
                        }


//                        pedidos.add(new Pedido(product, id, address, fechaPedido));
                        nuevospedidos.add(new Pedido(product, id, address, fechaPedido));
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
            System.out.println("Recibida la respuesta de la request en background");
            parseResponseFromRequest(data);

        }
    }




}
