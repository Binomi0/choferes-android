package com.contenedoressatur.android.choferesandroid.Pedidos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.contenedoressatur.android.choferesandroid.MainActivity;
import com.contenedoressatur.android.choferesandroid.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Set;

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
    private static ArrayList<Pedido> pedidos = new ArrayList<>();
    private Context context;
    static PedidosAdapter pedidosAdapter;

    public PedidosController(Context context) {
        this.context = context;
        Log.i("PedidoController", "El estatus del controlador es ");
    }

    public ArrayList<Pedido> cargarTodosPedidos(String chofer) {
        String request = "https://contenedoressatur.es/wp-json/pedidos/v1/chofer/" + chofer;
        new JSONTask().execute(request);
        return pedidos;
    }

    public static ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public static Pedido getPedido(int index) {
        return pedidos.get(index);
    }

    private void parseResponseFromRequest(ArrayList<Pedido> response) {
//        Set<Pedido> mySet = new ArraySet<>();

        if ( response != null ) {
            Log.i(TAG,"El array contiene " + response.size() + " elementos");
            pedidosAdapter = new PedidosAdapter(this.context, response);
            pedidosAdapter.notifyDataSetChanged();
            pedidosAdapter.addAll(response);
            if (pedidos.equals(response) ) {
                Log.i(TAG,"El pedido es igual a la respuesta");

            } else {
//                for (Pedido pedido: response ) {
//                    if ( ! mySet.contains(pedido)) {
//                        mySet.add(pedido);
//                    }
//                }
                pedidos = response;
            }

        } else {
            Log.i("[JSONTask] => pedidos","El array NO contiene el pedido");
        }
    }

    private class JSONTask extends AsyncTask<String, String, ArrayList<Pedido>> {
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
                        JSONObject pedido = dataArray.getJSONObject(i);
                        int id = pedido.getInt("id");
                        JSONObject fecha = pedido.getJSONObject("fecha");
                        String fechaPedido = fecha.getString("date");
                        String producto = pedido.getString("product");
                        String status = pedido.getString("status");
                        JSONArray addressArray = pedido.getJSONArray("address");

                        String address = "";
                        LatLng coords = new LatLng(35.2, -0.5);
                        if (addressArray.length() == 1) {
                            JSONObject fullAddress = addressArray.getJSONObject(0);
                            coords = new LatLng(fullAddress.getDouble("lat"), fullAddress.getDouble("lng") );
                            address = fullAddress.getString("formatted_address");

                        }

                        nuevospedidos.add(new Pedido(producto, id, address, coords, fechaPedido, status));

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
