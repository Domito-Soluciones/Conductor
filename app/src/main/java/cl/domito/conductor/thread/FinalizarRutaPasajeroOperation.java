package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class FinalizarRutaPasajeroOperation extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        RequestConductor.finalizarServicio("1");
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}