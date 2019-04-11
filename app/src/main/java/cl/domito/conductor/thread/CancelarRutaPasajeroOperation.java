package cl.domito.conductor.thread;

import android.os.AsyncTask;

import cl.domito.conductor.http.RequestConductor;

public class CancelarRutaPasajeroOperation extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        String observacion = strings[0];
        RequestConductor.cambiarEstadoPasajero("2",observacion);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}