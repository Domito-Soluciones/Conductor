package cl.domito.dmttransfer.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.http.RequestConductor;

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
                if(!context.get().isDestroyed()) {
                    try {
                        dialog.show();
                    }
                    catch(Exception e){

                    }
                }
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ActivityUtils.recargarPasajeros(context.get());
        if(!context.get().isDestroyed()) {
            dialog.dismiss();
        }
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.get(), "Pasajero cancelado", Toast.LENGTH_SHORT).show();

            }
        });
    }

}