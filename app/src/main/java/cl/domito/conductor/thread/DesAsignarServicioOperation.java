package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

public class DesAsignarServicioOperation  extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;

    public DesAsignarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    public DesAsignarServicioOperation() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        String url = Utilidades.URL_BASE_SERVICIO + "ModConductorServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String idServicio = null;
        try {
            idServicio = Conductor.getInstance().getServicio().getString("servicio_id");
            params.add(new BasicNameValuePair("id",idServicio));
            params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getNick()));
            Utilidades.enviarPost(url,params);
            Conductor.getInstance().setTiempoEspera(30);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConstraintLayout constraintLayout = context.get().findViewById(R.id.constrainLayoutServicio);
                    constraintLayout.setVisibility(View.GONE);
                }
            });
        }
    }
}




