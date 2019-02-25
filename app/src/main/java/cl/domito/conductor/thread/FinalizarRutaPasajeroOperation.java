package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

public class FinalizarRutaPasajeroOperation extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicioPasajero.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idServicio",conductor.getServicioActual()));
        params.add(new BasicNameValuePair("idPasajero",conductor.getPasajeroActual()));
        Utilidades.enviarPost(url,params);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

}