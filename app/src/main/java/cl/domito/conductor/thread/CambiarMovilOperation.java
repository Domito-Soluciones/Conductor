package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.json.JSONObject;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class CambiarMovilOperation extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        JSONObject jsonObject = RequestConductor.cambiarServicioMovil(strings[0]);
        conductor.estado = 0;
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}