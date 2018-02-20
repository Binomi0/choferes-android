package com.contenedoressatur.android.choferesandroid;

/**
 * Created by user on 18/02/2018.
 */

class MiSingleton {
    private static final MiSingleton ourInstance = new MiSingleton();

    static MiSingleton getInstance() {
        return ourInstance;
    }

    private MiSingleton() {
    }
}
