package cl.domito.conductor.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.FinServicioActivity;
import cl.domito.conductor.activity.MainActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;

public class AgregarObservacionOperation extends AsyncTask<String, Void, Void> {

    private WeakReference<Activity> context;
    private AlertDialog dialog;

    public AgregarObservacionOperation(Activity activity)
    {
        context = new WeakReference<Activity>(activity);
        dialog = ActivityUtils.setProgressDialog(activity);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String idServicio = strings[0];
        String texto = strings[1];
        RequestConductor.actualizarComentarioAdicional(idServicio,texto);
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
        dialog.dismiss();
    }

}