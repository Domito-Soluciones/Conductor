package cl.domito.conductor.thread;

import android.os.AsyncTask;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class CambiarEstadoServicioOperation extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        RequestConductor.cambiarEstadoServicio(strings[0],strings[1]);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //if()

    }

}