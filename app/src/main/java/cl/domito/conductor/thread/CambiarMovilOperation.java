package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class CambiarMovilOperation extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        JSONObject jsonObject = RequestConductor.cambiarServicioMovil(strings[0]);
        conductor.setEstado(0);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}