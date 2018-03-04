package com.contenedoressatur.android.choferesandroid.Choferes;

import android.os.CountDownTimer;

import java.util.HashMap;
import java.util.Map;

/**
 * Creado por user el 23/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class Chofer {

    private static final Map<String, String> choferes = new HashMap<>();

    public String nombre;
    private String email;
    private Map<String, String> tareas = new HashMap<>();
    public int tareasPendientes;
    public int tareasCompletadas;
    private Boolean ocupado;
    public long tiempoOcupado;
    private CountDownTimer countDownTimer;
    private static final int intervalo = 60000;

    public Chofer(String newEmail) {
        choferes.put("adolfo@onrubia.es", "Adolfo");
        choferes.put("otro@email.com", "Adolfo1");
        choferes.put("otro2@email.com", "Adolfo2");
        choferes.put("jose@contenedoressatur.com", "Jose");

        choferes.put("antoniosatur1@gmail.com", "Antonio");
        choferes.put("rogeliosatur@gmail.com", "Rogelio");
        choferes.put("contenedoressatur@gmail.com", "Satur");
        choferes.put("bernalsalgal@gmail.com", "Alejandro");
        choferes.put("resitur9@gmail.com", "Dario");

        if (choferes.containsKey(newEmail)) {
            email = newEmail;
        }
        email = newEmail;
        nombre = choferes.get(email);
        tareasCompletadas = 0;
        ocupado = false;
        tiempoOcupado = 0;
        tareasPendientes = 0;
    }


    public void setTareas_pendientes(String nuevaTarea, String direccion, String newOrderId) {
        Boolean alicante = direccion.contains("Alicante");
        Boolean elche = direccion.contains("Elche");
        Boolean santapola = direccion.contains("Santa Pola");

        if (!tareas.containsKey(newOrderId)) {
            if (santapola) {
                tiempoOcupado += 2700000;
            } else if (elche) {
                tiempoOcupado += 3600000;
            } else if (alicante) {
                tiempoOcupado += 5400000;
            }
            tareas.put(newOrderId, nuevaTarea);
            iniciarCuentaAtras();
        }
        tareasPendientes = tareas.size();
        ocupado = true;
    }

    public void setTareas_realizadas(String completedId) {
        if (tareas.containsKey(completedId)) {
            tareas.remove(String.valueOf(completedId));
            tareasCompletadas += 1;
            if (tareas.size() == 0) {
                tiempoOcupado = 0;
                countDownTimer.cancel();
                ocupado = false;
            }
        }
    }

    private void iniciarCuentaAtras() {
        countDownTimer = new CountDownTimer(tiempoOcupado, intervalo) {
            @Override
            public void onTick(long l) {
                tiempoOcupado = l;
            }

            @Override
            public void onFinish() {
                tiempoOcupado = 0;
            }
        };
        countDownTimer.start();
    }

    /**
     *
     * @return
     * True -> el chofer esta ocupado
     * False -> el chofer está libre
     */
    public Boolean getOcupado() {
        return ocupado;
    }


    public String getEmail() {
        return email;
    }
}

