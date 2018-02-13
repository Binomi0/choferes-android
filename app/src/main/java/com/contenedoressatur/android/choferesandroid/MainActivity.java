package com.contenedoressatur.android.choferesandroid;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;

import java.lang.String;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView mTextEmail;
    private String nombre;
    private String email;

    private Bundle parametros;

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    System.out.println("EMAIL: " + email);
//                    mTextMessage.setText(email);
                    mTextMessage.setText(R.string.title_home);

                    return true;
                case R.id.navigation_dashboard:
//                    mTextMessage.setText(email);
                    mTextEmail.setText("");

                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextEmail.setText("");

                    mTextMessage.setText(R.string.title_notifications);

                    return true;
            }
            return false;
        }
    };

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
        setTitle("Contenedores SATUR");
        mTextMessage = (TextView) findViewById(R.id.message);
        mTextEmail = (TextView) findViewById(R.id.email);

        parametros = this.getIntent().getExtras();

        // Recoger parametros
        if (parametros != null) {
            System.out.println("Tengo ExtraData " + parametros);
            System.out.println("Tengo ExtraData " + parametros.getString("email"));
            email = parametros.getString("email");
            mTextEmail.setText(email);
        } else {
            return;
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        if (id == R.id.navigation_dashboard) {
            System.out.println("Capturamos el clic del boton: " + R.id.navigation_dashboard);
        }
        return super.onOptionsItemSelected(item);
    }
}
