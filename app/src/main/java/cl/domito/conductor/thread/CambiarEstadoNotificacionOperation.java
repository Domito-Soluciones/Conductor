package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class CambiarEstadoNotificacionOperation extends AsyncTask<String,Void,Void> {


    @Override
    protected Void doInBackground(String... strings) {
        if(strings[0] != null && !strings[0].equals("")) {
            RequestConductor.cambiarEstadoNotificacion(strings[0]);
        }
        return null;
    }
}
