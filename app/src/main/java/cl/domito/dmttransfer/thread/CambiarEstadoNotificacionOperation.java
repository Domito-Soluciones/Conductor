package cl.domito.dmttransfer.thread;

import android.os.AsyncTask;

import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.http.RequestConductor;

public class CambiarEstadoNotificacionOperation extends AsyncTask<String,Void,Void> {


    @Override
    protected Void doInBackground(String... strings) {
        if(strings[0] != null && !strings[0].equals("")) {
            RequestConductor.cambiarEstadoNotificacion(strings[0]);
        }
        return null;
    }
}
