package cl.domito.dmttransfer.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.http.RequestConductor;

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
        if(!context.get().isDestroyed()) {
            //dialog.dismiss();
        }
    }

}