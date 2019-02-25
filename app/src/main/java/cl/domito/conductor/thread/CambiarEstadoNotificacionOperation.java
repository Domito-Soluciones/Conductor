package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.http.Utilidades;

public class CambiarEstadoNotificacionOperation extends AsyncTask<String,Void,Void> {


    @Override
    protected Void doInBackground(String... strings) {
        if(strings[0] != null && !strings[0].equals("")) {
            String url = Utilidades.URL_BASE_NOTIFICACION + "ModEstadoNotificacion.php";
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("id", strings[0]));
            Utilidades.enviarPost(url, params);
        }
        return null;
    }
}
