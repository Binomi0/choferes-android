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

import com.contenedoressatur.android.choferesandroid.MapsPackage.MapsActivity;
import com.contenedoressatur.android.choferesandroid.Pedidos.Pedido;
import com.contenedoressatur.android.choferesandroid.Pedidos.PedidosAdapter;
import com.contenedoressatur.android.choferesandroid.Pedidos.PedidosController;
import com.google.firebase.iid.FirebaseInstanceId;

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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView mTextMessage;
    ListView listView;
    PedidosAdapter adapter;
    ArrayList<Pedido> pedidoArrayList;
    View mProgressView;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;
    String chofer;
    String email;
    TextView trabajosPendientes;
    ImageView logo;
    static final int PICK_CONTACT_REQUEST = 1;  // The request code

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
        trabajosPendientes.setVisibility(View.INVISIBLE);
        TextView mTextEmail = findViewById(R.id.email);
        Bundle parametros = this.getIntent().getExtras();
        logo = findViewById(R.id.logo);

        // Recoger parametros pasados desde loginactivity
        if (parametros != null) {
            Log.i("[MAINACTIVITY] Params", "[ExtraData] $email: " + parametros.getString("email"));
            chofer = parametros.getString("chofer", "Contenedores Satur");
            email = parametros.getString("email");
            mTextEmail.setText(email);
            setTitle("Chófer " + chofer + "");
            toast("Bienvenido " + chofer);

            ArrayList nuevosPedidos = PedidosController.cargarTodosPedidos(chofer);
            Log.i(TAG, "nuevosPedios => " + nuevosPedidos.size());

        } else {
            setTitle("Chóferes Satur");
            Log.i("[MAINACTIVITY]","No se han cargado pedidos");
            toast("No se han cargado pedidos.");
            return;
        }

        pedidoArrayList = PedidosController.getPedidos();
        adapter = new PedidosAdapter(this, pedidoArrayList);
        listView.setAdapter(adapter);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        cargarContenidoInicio();
        checkChoferToken();

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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
                startActivity(mapaPedido);
            }
        };
        listView.setOnItemClickListener(listener);

        // Read app preferences
        myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    }

    private void cargarContenidoPedidos() {
        trabajosPendientes.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);

        if (adapter == null) {
            Log.i(TAG,"cargarContenidoPedidos => Adapter es Null");
            adapter = new PedidosAdapter(this, pedidoArrayList);
            listView.setAdapter(adapter);
        }

        pedidoArrayList = PedidosController.getPedidos();

        if (pedidoArrayList.isEmpty()) {
            PedidosController.cargarTodosPedidos(chofer);
            Log.i(TAG,"cargarContenido() => ArrayList: " + pedidoArrayList.size());
        } else {
            Log.i(TAG,"actualizarListadoPedidos => Adapter ok");
            Log.i(TAG,"listaPedidos => " + pedidoArrayList.size());
            adapter.clear();
            adapter.notifyDataSetChanged();
            adapter.addAll(pedidoArrayList);
        }




        showProgress(false);
    }
    private void cargarContenidoInicio() {
        trabajosPendientes.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);

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
                    mTextMessage.setText(R.string.title_home);
                    cargarContenidoInicio();
                    return true;
                case R.id.navigation_pedidos:
                    mTextMessage.setText(R.string.title_pedidos);
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


    private void pickContact() {
        // Create an intent to "pick" a contact, as defined by the content provider URI
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    public void mostrarNotificacion(View v) {
        Toast.makeText(this, "Notificacion", Toast.LENGTH_SHORT).show();
    }

    public void checkChoferToken() {

        String token =  myPreferences.getString(chofer, null);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            Log.i(TAG, "Refreshed token: " + refreshedToken);
            Log.i(TAG,"Token: " + token);
            if (token.equals(refreshedToken)) {
                Log.i(TAG, "El token es igual, saliendo...");
                myEditor = myPreferences.edit();
                myEditor.remove("token");
//
//                myEditor = myPreferences.edit();
//                myEditor.remove("token");
//                myEditor.apply();
                return;
            } else {
                updateToken(refreshedToken);
                return;
            }
        } else {
            updateToken(refreshedToken);
        }
        myEditor = myPreferences.edit();
        myEditor.putString(chofer, refreshedToken);
//        myEditor.remove("token");
        myEditor.apply();
    }

    public void updateToken(String token) {

        String query = chofer + "/" + token;
        new UpdateTokenTask().execute(query);

    }

    static public class UpdateTokenTask extends AsyncTask<String, String, Boolean> {
        String request = "https://contenedoressatur.es/wp-json/choferes/v1/update_token/";
        BufferedReader reader = null;

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

                InputStream stream = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();

                String jsonData = buffer.toString();
                if (jsonData.length() > 0){
                    Log.i(TAG, "Response: " + jsonData);
                    return true;
                }

                return false;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
    }


    public void toast (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }




}





