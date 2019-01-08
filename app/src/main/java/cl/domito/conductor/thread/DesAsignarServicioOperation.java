package cl.domito.conductor.thread;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

public class DesAsignarServicioOperation  extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;

    public DesAsignarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_SERVICIO + "DesAsignarServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id",Conductor.getInstance().getNick()));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Utilidades.enviarPost(url,params);
                Conductor.getInstance().setTiempoEspera(30);
            }
        });
        thread.start();
        return null;
    }

}




