package cl.domito.conductor.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.PasajeroActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class FinalizarRutaPasajerosOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<Activity> context;
    private Conductor conductor;
    private AlertDialog dialog;

    public FinalizarRutaPasajerosOperation(Activity activity)
    {
        context = new WeakReference<Activity>(activity);
        conductor = Conductor.getInstance();
        dialog = ActivityUtils.setProgressDialog(activity);
    }
    @Override
    protected Void doInBackground(String... strings) {
        RequestConductor.cambiarEstadoPasajeros(strings[0]);
        return null;
    }

    @Override
    protected void onPreExecute() {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //dialog.show();
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(conductor.servicioActualRuta.contains("RG"))
                {
                    Toast.makeText(context.get(), "Servicio Terminado", Toast.LENGTH_SHORT).show();
                }
                conductor.servicioActual = null;
            }
        });
        dialog.dismiss();
        Toast.makeText(context.get(), "Servicio cancelado", Toast.LENGTH_SHORT).show();


    }

}