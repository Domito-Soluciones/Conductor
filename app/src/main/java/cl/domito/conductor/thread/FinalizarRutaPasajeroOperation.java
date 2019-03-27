package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.activity.PasajeroActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class FinalizarRutaPasajeroOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<PasajeroActivity> context;

    public FinalizarRutaPasajeroOperation(PasajeroActivity activity)
    {
        context = new WeakReference<PasajeroActivity>(activity);
    }
    @Override
    protected Void doInBackground(String... strings) {
        RequestConductor.cambiarEstadoPasajero("3");
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.get(),"Pasajero Entregado",Toast.LENGTH_SHORT).show();
            }
        });

    }

}