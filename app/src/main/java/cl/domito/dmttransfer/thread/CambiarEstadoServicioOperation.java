package cl.domito.dmttransfer.thread;

import android.os.AsyncTask;

import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;

public class CambiarEstadoServicioOperation extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        String idServicio = strings[0];
        String estado = strings[1];
        String obs = strings[2];
        RequestConductor.cambiarEstadoServicio(idServicio,estado,obs);
        return null;
    }


}