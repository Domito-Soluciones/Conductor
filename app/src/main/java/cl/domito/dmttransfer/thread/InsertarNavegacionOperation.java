package cl.domito.dmttransfer.thread;

import android.os.AsyncTask;

import cl.domito.dmttransfer.http.RequestConductor;

public class InsertarNavegacionOperation extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        RequestConductor.insertarNavegacion();
        return null;
    }
}
