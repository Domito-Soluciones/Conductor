package cl.domito.conductor.thread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.LoginActivity;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;

public class ObtenerServicioOperation extends AsyncTask<Void, Void, JSONObject> {

    @Override
        protected JSONObject doInBackground(Void... voids) {
            Conductor conductor = Conductor.getInstance();
            List<NameValuePair> params = new ArrayList();
            String url = Utilidades.URL_BASE_SERVICIO + "GetServicioConductor.php";
            params.add(new BasicNameValuePair("user",conductor.getNick()));
            JSONObject servicio = RequestConductor.obtenerServicioAsignado(url,params);
            return servicio;
    }
}
