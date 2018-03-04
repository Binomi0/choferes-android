package com.contenedoressatur.android.choferesandroid.woocommerce;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Creado por user el 23/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class HttpController  {
    private static final String TAG = HttpController.class.getSimpleName();

    private String type;
    private static String url;

    public HttpController(String query, String type) {
        url = query;
        type = type;
    }

    static public void containerChanged(String orderId, String chofer) {
        url = "https://contenedoressatur.es/wp-json/pedidos/v1/cambiado/" + chofer + "/" + orderId;
        new JSONTask().execute(url);
    }

    static public void containerRemoved(String orderId, String chofer) {
        url = "https://contenedoressatur.es/wp-json/pedidos/v1/retirado/" + chofer + "/" + orderId;
        new JSONTask().execute(url);
    }

    static public void containerPlaced(String orderId, String chofer) {
        url = "https://contenedoressatur.es/wp-json/pedidos/v1/puesto/" + chofer + "/" + orderId;
        new JSONTask().execute(url);
    }

    static public void updateOrderLocationFromMarker(LatLng coords, String orderId) {
        url = "https://contenedoressatur.es/wp-json/pedidos/v1/updatelocation/" + orderId + "/" + coords.latitude + "/" + coords.longitude;
        new JSONTask().execute(url);
    }

    static class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i(TAG,"Se ha cancelado la peticion");
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.i(TAG,"Empezando request en background");
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Basic Y2tfZjFlYmQ1OWRkMDc0NThhZWEwYzQyOTBhNGE4OGRjNmRjOWExZDNiYjpjc183OTc5OGIwYWE2OTQwYTAxY2NhODNlY2I5YmJlMWRlOTM4YzM2MmU0");
                connection.setAllowUserInteraction(true);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(5000);
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                String jsonData = buffer.toString();
                if (jsonData.length() > 0 ) {
                    Log.i(TAG, "Response es un String de " + jsonData.length() + " elementos.");
                    Log.i(TAG, "JSONArray => " + jsonData
                    );
                    return connection.getResponseMessage();

                } else {
                    Log.i(TAG, "Response es un JSONObject => " + jsonData);
                    JSONObject dataObject = new JSONObject(jsonData);
                    int orderId = dataObject.getInt("orderId");
                }


                Log.i(TAG, "Response => " + connection.getResponseMessage());
                return connection.getResponseMessage();

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
        protected void onPostExecute(String done) {
            super.onPostExecute(done);
            Log.i("[JSONTask] onPostExecut","Recibida la respuesta de la request en background");

        }
    }

}