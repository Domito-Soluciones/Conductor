package cl.domito.conductor.thread;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;

public class RealizarServicioOperation extends AsyncTask<Object, Void, Void> {

    private WeakReference<MapsActivity> context;
    private TextView textView;
    private ConstraintLayout servicioLayout;


    public RealizarServicioOperation(MapsActivity activity) {
        context = new WeakReference<MapsActivity>(activity);
    }

    @Override
    protected Void doInBackground(Object... objects) {
        GoogleMap map = (GoogleMap) objects[0];
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicio.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        textView = context.get().findViewById(R.id.textViewIdServicioValor);
        servicioLayout = context.get().findViewById(R.id.constrainLayoutServicio);
        params.add(new BasicNameValuePair("id",textView.getText().toString()));
        params.add(new BasicNameValuePair("estado","3"));
        Utilidades.enviarPost(url,params);
        Conductor.getInstance().setTiempoEspera(30);
        context.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AsignacionServicioService.LAYOUT_SERVICIO_VISIBLE) {
                    servicioLayout.setVisibility(View.GONE);
                }
            }
        });
        try {
            JSONObject servicio = Conductor.getInstance().getServicio();
            String url2 = Utilidades.URL_BASE_SERVICIO + "GetDetalleServicio.php";
            List<NameValuePair> params2 = new ArrayList();
            params2.add(new BasicNameValuePair("id", servicio.getString("servicio_id")));
            JSONArray route = RequestConductor.getRoute(url2,params2);
            ActivityUtils.dibujarRuta(context.get(),map,route);
            //Conductor.getInstance().setServicio(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button buttonConfirmar = context.get().findViewById(R.id.buttonConfirmar);
                    buttonConfirmar.setText("Confirmando...");
                }
            });
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(context != null) {
            context.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   servicioLayout.setVisibility(View.GONE);
                   Button buttonNavegar = context.get().findViewById(R.id.buttonNavegar);
                   buttonNavegar.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
