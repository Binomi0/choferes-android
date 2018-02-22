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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;
import android.os.StrictMode;

import com.contenedoressatur.android.choferesandroid.MapsPackage.MapsActivity;

import java.lang.String;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextMessage;
    ListView listView;
    PedidosAdapter adapter;
    ArrayList<Pedido> pedidoArrayList;

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String nombre = "Adolfo";
        PedidosController.cargarTodosPedidos(nombre);

        setTitle("Contenedores Satur");
        mTextMessage = findViewById(R.id.message);
        TextView mTextEmail = findViewById(R.id.email);
        listView = findViewById(R.id.lista_pedidos);

        pedidoArrayList = PedidosController.getPedidos();
        adapter = new PedidosAdapter(this, pedidoArrayList);
        listView.setAdapter(adapter);


        Bundle parametros = this.getIntent().getExtras();

        // Recoger parametros pasados desde loginactivity
        if (parametros != null) {
            Log.i("[MAINACTIVITY] Params", "[ExtraData] $email: " + parametros.getString("email"));
            String email = parametros.getString("email");
            mTextEmail.setText(email);
        } else {
            Log.i("[MAINACTIVITY]","No tengo extradata");
            return;
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onStart() {
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
    }

    private void cargarContenido() {
        if (adapter == null) {
            Log.i(TAG,"actualizarListadoPedidos => Adapter es Null");
            adapter = new PedidosAdapter(this, pedidoArrayList);
            listView.setAdapter(adapter);
        }

        pedidoArrayList = PedidosController.getPedidos();


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
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    cargarContenido();
                    return true;
                case R.id.navigation_pedidos:
                    mTextMessage.setText(R.string.title_pedidos);
                    cargarContenido();
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





