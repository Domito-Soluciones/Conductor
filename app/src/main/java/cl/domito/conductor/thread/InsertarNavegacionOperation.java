package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

public class InsertarNavegacionOperation extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_SERVICIO + "AddServicioDetalleReal.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("servicio",conductor.getServicioActual()));
        params.add(new BasicNameValuePair("lat",conductor.getLocation().getLatitude()+""));
        params.add(new BasicNameValuePair("lon",conductor.getLocation().getLatitude()+""));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
