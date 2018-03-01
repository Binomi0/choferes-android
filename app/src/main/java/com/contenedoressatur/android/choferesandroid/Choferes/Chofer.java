package com.contenedoressatur.android.choferesandroid.Choferes;

import android.os.Bundle;

import com.contenedoressatur.android.choferesandroid.R;

/**
 * Creado por user el 23/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class Chofer {

    private static final Bundle choferes = new Bundle();

    private String nombre;
    private int id;

    public void Chofer(String nombre) {
        choferes.putString("adolfo@onrubia.es", "Adolfo");
        choferes.putString("otro@email.com", "Adolfo1");
        choferes.putString("otro2@email.com", "Adolfo2");
        choferes.putString("jose@contenedoressatur.com", "Jose");

        choferes.putString("antoniosatur1@gmail.com", "Antonio");
        choferes.putString("rogeliosatur@gmail.com", "Rogelio");
        choferes.putString("contenedoressatur@gmail.com", "Satur");
        choferes.putString("bernalsalgal@gmail.com", "Alejandro");
        choferes.putString("resitur9@gmail.com", "Dario");

        if (choferes.containsKey(nombre)) {
            this.nombre = nombre;

        }
        this.nombre = nombre;
        this.id = this.id + 1;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }
}
