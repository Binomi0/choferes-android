package com.contenedoressatur.android.choferesandroid.Choferes;

import com.contenedoressatur.android.choferesandroid.R;

/**
 * Creado por user el 23/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class Chofer {

    private static final String[] choferes = { "Adolfo", "Rogelio", "Alejandro", "Dario", "Satur" };
    private String nombre;
    private int id;

    public void Chofer(String nombre) {
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
