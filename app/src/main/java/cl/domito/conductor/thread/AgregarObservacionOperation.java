package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.FinServicioActivity;
import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class AgregarObservacionOperation extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        String idServicio = strings[0];
        String texto = strings[1];
        RequestConductor.actualizarComentarioAdicional(idServicio,texto);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}