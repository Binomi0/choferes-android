package com.contenedoressatur.android.choferesandroid;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;
import android.os.StrictMode;

import java.lang.String;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextMessage;
    private TextView mTextEmail;
    private String email;
    ListView listView;
    PedidosAdapter adapter;
    ArrayList<Pedido> pedidoArrayList;

    String[] valores = new String[] { "Nombre", "Direccion", "Pedido", "Fecha" };
    String[] puestas = new String[] { "puestas 1", "puestas 2", "puestas 3", "puestas 4" };
    String[] cambios = new String[] { "cambio 1", "cambio 2", "cambio 3", "cambio 4" };
    String[] retiradas = new String[] { "retiradas 1", "retiradas 2", "retiradas 3", "retiradas 4" };

    private Bundle parametros;

    static final int PICK_CONTACT_REQUEST = 1;  // The request code



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String nombre = "Adolfo";
        PedidosController.cargarTodosPedidos(nombre);

        setTitle("Listado de Pedidos");
        mTextMessage = (TextView) findViewById(R.id.message);
        mTextEmail = (TextView) findViewById(R.id.email);
        listView = (ListView) findViewById(R.id.lista_pedidos);
//        View header = getLayoutInflater().inflate(R.layout.header, null);
//        listView.addHeaderView(header);

        pedidoArrayList = PedidosController.getPedidos();
        adapter = new PedidosAdapter(this, pedidoArrayList);
        listView.setAdapter(adapter);


        parametros = this.getIntent().getExtras();

        // Recoger parametros
        if (parametros != null) {
            Log.i("[MAINACTIVITY] Params", "[ExtraData] $email: " + parametros.getString("email"));
            email = parametros.getString("email");
            mTextEmail.setText(email);
        } else {
            Log.i("[MAINACTIVITY]","No tengo extradata");
            return;
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    private void cargarContenido(String status) {
        if (adapter == null) {
            Log.i(TAG,"actualizarListadoPedidos => Adapter es Null");
            adapter = new PedidosAdapter(this, pedidoArrayList);
            listView.setAdapter(adapter);
        }

        if (status.equals("todos")) {
            Log.i(TAG,"Cargando: " + status);
            pedidoArrayList = PedidosController.getPedidos();

        } else {
            Log.i(TAG,"cargarContenido() => Cargando: " + status);
            pedidoArrayList = PedidosController.getPedidos();

        }

        if (pedidoArrayList.isEmpty()) {
            Log.i(TAG,"cargarContenido() => ArrayList: " + pedidoArrayList.size());
            adapter.clear();
        }

//        adapter = new PedidosAdapter(this, pedidoArrayList);
        Log.i(TAG,"actualizarListadoPedidos => Adapter ok");
        Log.i(TAG,"listaPedidos => " + pedidoArrayList.size());
        adapter.clear();
        adapter.notifyDataSetChanged();
        adapter.addAll(pedidoArrayList);

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_todos:
                    mTextMessage.setText(R.string.title_todos);
                    cargarContenido("todos");
                    return true;
                case R.id.navigation_retiradas:
                    mTextMessage.setText(R.string.title_retiradas);
                    cargarContenido("retirando");
                    return true;
                case R.id.navigation_cambios:
                    mTextMessage.setText(R.string.title_cambios);
                    cargarContenido("cambiando");
                    return true;
                case R.id.navigation_puestas:
                    mTextMessage.setText(R.string.title_puestas);
                    cargarContenido("processing");
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

        if (id == R.id.navigation_todos) {
            Log.i("[MAINACTIVITY] OnOption","Capturamos el clic del boton: " + R.id.navigation_todos);
        }
        return super.onOptionsItemSelected(item);
    }



    void showHomeView(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "adolfo@contenedoressatur.com" });
        startActivity(intent);
    }

    private void pickContact() {
        // Create an intent to "pick" a contact, as defined by the content provider URI
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }



}





