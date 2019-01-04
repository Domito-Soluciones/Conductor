package cl.domito.conductor.thread;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import cl.domito.conductor.activity.MapsActivity;

public class DesAsignarServicioOperation  extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;

    public DesAsignarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }



    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_SERVICIO + "DesAsignarServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id",Utilidades.USER));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Utilidades.enviarPost(url,params);
                Utilidades.TIEMPO_ESPERA = 30;
            }
        });
        thread.start();
        return null;
    }

}




