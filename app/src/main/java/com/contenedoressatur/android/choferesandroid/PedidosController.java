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

    static void cargarTodosPedidos() {
        Log.i("PedidoController", "cargarTodosPedidos()");
//        new JSONTask().execute("https://contenedoressatur.es/wp-json/wc/v2/orders?per_page=100&orderby=date", "all");
        new JSONTask().execute("https://contenedoressatur.es/wp-json/wc/v2/orders?filter[meta_key]=_assigned_chofer_field&filter[meta_value]=adolfo");
    }

    static ArrayList<Pedido> getPedidos(String requestStatus) {
        Log.i("PedidoController", "El estatus del pedido es " + status + " y estas pasando " + requestStatus);
        if (requestStatus != null) {
            status = requestStatus;
            if (requestStatus.equals("retirando")) {
                Log.i("PedidoController", "getPedidos() => " + requestStatus);
                return retiradas;
            }
            if (requestStatus.equals("cambiando")) {
                Log.i("PedidoController", "getPedidos() => " + requestStatus);
                return cambios;
            }
            if (requestStatus.equals("processing")) {
                Log.i("PedidoController", "getPedidos() => " + requestStatus);
                return puestas;
            }
        }

        return pedidos;
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
                Log.i("[PEDIDOS CONTROLLER","response => " + pedido.getStatus());
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

                        status = pedido.getString("status");

                        if (status.equals("processing")) {
                            puestas.add(new Pedido(product, id, address, fechaPedido, status));
//                            return puestas;
                        } else
                        if (status.equals("cambiando")) {
                            cambios.add(new Pedido(product, id, address, fechaPedido, status));
//                            return cambios;
                        } else
                        if (status.equals("retirando")) {
                            retiradas.add(new Pedido(product, id, address, fechaPedido, status));
//                            return retiradas;
                        } else {
                            nuevospedidos.add(new Pedido(product, id, address, fechaPedido, status));
                        }


//                        pedidos.add(new Pedido(product, id, address, fechaPedido));
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
