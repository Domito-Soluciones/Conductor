package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class DatosConductorOperation  extends AsyncTask<Void, Void, Void> {

    private WeakReference<MapsActivity> context;
    TextView textViewError;

    public DatosConductorOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_CONDUCTOR + "GetConductor.php";
        try {
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("id",conductor.getNick()));
            JSONObject jsonObject = RequestConductor.datosConductor(url,params);
            context.get().runOnUiThread(ActivityUtils.mensajeError(context.get()));
            if(jsonObject != null) {
                conductor.setNombre(jsonObject.getString("conductor_nombre"));
                conductor.setCantidadViajes(jsonObject.getInt("conductor_viajes"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
