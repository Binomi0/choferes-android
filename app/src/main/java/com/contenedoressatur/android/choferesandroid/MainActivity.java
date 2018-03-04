package com.contenedoressatur.android.choferesandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.Toast;

import com.contenedoressatur.android.choferesandroid.Choferes.Chofer;
import com.contenedoressatur.android.choferesandroid.MapsPackage.MapsActivity;
import com.contenedoressatur.android.choferesandroid.Pedidos.Pedido;
import com.contenedoressatur.android.choferesandroid.Pedidos.PedidosAdapter;
import com.contenedoressatur.android.choferesandroid.Pedidos.PedidosController;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView mTextMessage;
    ListView listView;
    PedidosAdapter adapter;
    ArrayList<Pedido> pedidoArrayList;
    View mProgressView;
    Chofer chofer;
    String email;
    TextView trabajosPendientes;
    ImageView logo;
    FloatingActionButton fab;
    TextView tareasRealizadas;
    TextView tareasPendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Cargamos el indicador de progreso de la carga de los datos y los componentes de la vista
        mProgressView = findViewById(R.id.pedidos_progress);
        mTextMessage = findViewById(R.id.message);
        listView = findViewById(R.id.lista_pedidos);
        trabajosPendientes = findViewById(R.id.trabajos_pendientes);
        TextView mTextEmail = findViewById(R.id.email);
        Bundle parametros = this.getIntent().getExtras();
        logo = findViewById(R.id.logo);
        fab = findViewById(R.id.fab);
        tareasRealizadas = findViewById(R.id.tareas_realizadas);
        tareasPendientes = findViewById(R.id.tareas_pendientes);


        // Recoger parametros pasados desde loginactivity
        if (parametros != null) {
            Log.i("[MAINACTIVITY] Params", "[ExtraData] $email: " + parametros.getString("email"));
            email = parametros.getString("email");
            chofer = new Chofer(email);
            mTextEmail.setText(email);
            setTitle("Chófer " + chofer.nombre);
            sincronizarPedidos(chofer.nombre);
            toast("Bienvenido " + chofer.nombre);

        } else {
            setTitle("No identificado");
            toast("No se han cargado pedidos.");
            return;
        }

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                showProgress(true);
                toast("Actualizando Pedidos");
                sincronizarPedidos(chofer.nombre);
            }
        });

        pedidoArrayList = PedidosController.getPedidos();
        adapter = new PedidosAdapter(this, pedidoArrayList);
        listView.setAdapter(adapter);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        cargarContenidoInicio();

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (show){
            if (adapter != null) {
                adapter.clear();
            }
        }
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {

        // TODO insertar campos telefono entrega y persona de contacto en los detalles del pedido y empresa
        super.onStart();

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int i, long l) {
                Intent mapaPedido = new Intent(MainActivity.this, MapsActivity.class);
                mapaPedido.putExtra("index", i);
                mapaPedido.putExtra("chofer", chofer.nombre);
                startActivity(mapaPedido);
            }
        };
        listView.setOnItemClickListener(listener);

        // Read app preferences
        cargarContenidoInicio();
        sincronizarPedidos(chofer.nombre);
        checkChoferToken();

    }

    private void cargarContenidoPedidos() {
        trabajosPendientes.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.VISIBLE);
        tareasRealizadas.setVisibility(View.INVISIBLE);
        tareasPendientes.setVisibility(View.INVISIBLE);
        if (adapter == null) {
            Log.i(TAG,"cargarContenidoPedidos => Adapter es Null");
            adapter = new PedidosAdapter(this, pedidoArrayList);
            listView.setAdapter(adapter);
        }

        Log.i(TAG,"actualizarListadoPedidos => Adapter ok");
        Log.i(TAG,"listaPedidos => " + pedidoArrayList.size());
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter.addAll(pedidoArrayList);
        showProgress(false);
    }

    private void cargarContenidoInicio() {
        trabajosPendientes.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
        fab.setVisibility(View.INVISIBLE);
        tareasPendientes.setVisibility(View.VISIBLE);
        tareasRealizadas.setVisibility(View.VISIBLE);
        String stringTareasRealizadas = "Realizadas " + String.valueOf(chofer.tareasCompletadas);
        String stringTareasPendientes = "Pendientes " + String.valueOf(chofer.tareasPendientes);
        tareasRealizadas.setText(stringTareasRealizadas);
        tareasPendientes.setText(stringTareasPendientes);
        long horasRestantes = TimeUnit.MILLISECONDS.toHours(chofer.tiempoOcupado);
        long minutosRestantes = TimeUnit.MILLISECONDS.toMinutes(chofer.tiempoOcupado);
        String stringTiempoRestante = String.valueOf(horasRestantes) + " horas";

        if (horasRestantes < 1) {
            stringTiempoRestante = String.valueOf(minutosRestantes) + " minutos";
        }
        if (chofer.getOcupado()) {
            mTextMessage.setText(stringTiempoRestante);
        } else {
            mTextMessage.setText(R.string.sin_tareas);
        }

        adapter.clear();
        // TODO Añadir contenido seccion home
        showProgress(false);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    cargarContenidoInicio();
                    return true;
                case R.id.navigation_pedidos:
//                    mTextMessage.setText(R.string.title_pedidos);
                    cargarContenidoPedidos();
                    return true;
            }
            return false;

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_home) {
            Log.i("[MAINACTIVITY] OnOption","Capturamos el clic del boton: " + R.id.navigation_home);
        }
        return super.onOptionsItemSelected(item);
    }


    public void checkChoferToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken != null) {
            Log.i(TAG, "Refreshed token: " + refreshedToken);
            updateToken(refreshedToken);
        }
    }

    public void updateToken(String token) {

        String query = chofer.nombre + "/" + token;
        new UpdateTokenTask().execute(query);

    }

    static public class UpdateTokenTask extends AsyncTask<String, String, Boolean> {
        String request = "https://contenedoressatur.es/wp-json/choferes/v1/update_token/";

        @Override
        protected Boolean doInBackground(String... strings) {
            request += strings[0];
            try {
                URL url = new URL(request);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Accept", "application/json");
                conn.connect();

                int responseCode = conn.getResponseCode();
                return responseCode < 400;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

        }
    }


    public void toast (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void sincronizarPedidos(String chofer) {
        showProgress(true);
        String request = "https://contenedoressatur.es/wp-json/pedidos/v1/chofer/" + chofer;
        new JSONTask().execute(request);
    }

    private void parseResponseFromRequest(ArrayList<Pedido> response) {
        Log.i(TAG,"cargarContenido() => ArrayList: " + response.size());
        pedidoArrayList = response;
        cargarContenidoPedidos();
        for (Pedido pedido: response) {
            chofer.setTareas_pendientes(pedido.getStatus(), pedido.getAddress(), pedido.getOrderId());
        }
        showProgress(false);
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

//                StringBuilder buffer = new StringBuilder();
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line).append("\n");
//                }

                ArrayList<Pedido> nuevospedidos = new ArrayList<>();
                String jsonData = reader.readLine();
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





