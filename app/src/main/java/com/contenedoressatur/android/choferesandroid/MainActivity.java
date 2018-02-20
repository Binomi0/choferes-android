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

    private TextView mTextMessage;
    private TextView mTextEmail;
    private String nombre;
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    cargarContenido("processing");
                    return true;
                case R.id.navigation_retiradas:
                    mTextMessage.setText(R.string.title_retiradas);
                    cargarContenido("retirando");
                    return true;
                case R.id.navigation_cambios:
                    mTextMessage.setText(R.string.title_cambios);
                    cargarContenido("cambiando");
                    return true;
            }
            return false;
        }
    };


    private void actualizarListadoPedidos(ArrayAdapter adapterPedidos, ArrayList<Pedido> listPedidos) {
        adapter.clear();
        adapter.addAll(listPedidos);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapterPedidos);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast toast = Toast.makeText(getApplicationContext(), "Posicion: "+position, Toast.LENGTH_SHORT );
//                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
//                toast.show();
//            }
//        });
    }

//    private void clearAdapter() {
//        adapter.clear();
//    }

    private void cargarContenido(String status) {

        System.out.println("Cargando: " + status);
        pedidoArrayList = PedidosController.getPedidos(status);

        if (pedidoArrayList.isEmpty()) {
            return;
        }

        actualizarListadoPedidos(adapter, pedidoArrayList);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setTitle("Contenedores SATUR");
        mTextMessage = (TextView) findViewById(R.id.message);
        mTextEmail = (TextView) findViewById(R.id.email);

        listView = (ListView) findViewById(R.id.lista_pedidos);
        pedidoArrayList = new ArrayList<>(PedidosController.getPedidos(null));
        adapter = new PedidosAdapter(this, pedidoArrayList);
        listView.setAdapter(adapter);

//        View header = getLayoutInflater().inflate(R.layout.header, null);
//        listView.addHeaderView(header);

        parametros = this.getIntent().getExtras();

        // Recoger parametros
        if (parametros != null) {
            Log.i("Parametros de login", "[ExtraData] $email: " + parametros.getString("email"));
            email = parametros.getString("email");
            mTextEmail.setText(email);
        } else {
            System.out.println("No tengo extradata");
            return;
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        cargarContenido("pending");
    }


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
            System.out.println("Capturamos el clic del boton: " + R.id.navigation_home);
        }
        return super.onOptionsItemSelected(item);
    }







}





