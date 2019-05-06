package cl.domito.conductor.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class CancelarRutaPasajeroOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<Activity> context;
    private AlertDialog dialog;

    public CancelarRutaPasajeroOperation(Activity activity)
    {
        context = new WeakReference<Activity>(activity);
        dialog = ActivityUtils.setProgressDialog(activity);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String observacion = strings[0];
        RequestConductor.cambiarEstadoPasajero("2",observacion);
        return null;
    }

    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ActivityUtils.recargarPasajeros(context.get());
        dialog.dismiss();
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.get(), "Pasajero cancelado", Toast.LENGTH_SHORT).show();

            }
        });
    }

}